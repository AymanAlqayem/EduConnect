<?php
header('Content-Type: application/json');

require_once("db.config.php");

try {
    $pdo = getPDOConnection();

    // Collect and sanitize POST data
    $fullname = isset($_POST['fullname']) ? trim($_POST['fullname']) : '';
    $email = isset($_POST['email']) ? trim($_POST['email']) : '';
    $password = isset($_POST['password']) ? trim($_POST['password']) : '';
    $dob = isset($_POST['dob']) ? trim($_POST['dob']) : null;
    $gender = isset($_POST['gender']) ? trim($_POST['gender']) : '';
    $phone = isset($_POST['phone']) ? trim($_POST['phone']) : '';
    $notes = isset($_POST['notes']) ? trim($_POST['notes']) : '';
    $subject = isset($_POST['subject']) ? trim($_POST['subject']) : '';
    $assignmentsStr = isset($_POST['assignments']) ? trim($_POST['assignments']) : '';

    // Validate required fields
    if (empty($fullname) || empty($email) || empty($password) || empty($subject) || empty($assignmentsStr)) {
        echo json_encode(['status' => 'error', 'message' => 'Missing required fields']);
        exit;
    }

    // Validate email format
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        echo json_encode(['status' => 'error', 'message' => 'Invalid email format']);
        exit;
    }

    // Validate phone (10 digits)
    if (!empty($phone) && !preg_match('/^\d{10}$/', $phone)) {
        echo json_encode(['status' => 'error', 'message' => 'Invalid phone number']);
        exit;
    }

    // Validate DOB format (YYYY-MM-DD)
    if (!empty($dob) && !preg_match('/^\d{4}-\d{2}-\d{2}$/', $dob)) {
        echo json_encode(['status' => 'error', 'message' => 'Invalid DOB format']);
        exit;
    }

    // Check if teacher with email exists
    $checkStmt = $pdo->prepare("SELECT teacher_id FROM teachers WHERE email = :email LIMIT 1");
    $checkStmt->execute(['email' => $email]);
    if ($checkStmt->fetch()) {
        echo json_encode(['status' => 'error', 'message' => 'A teacher with this email already exists']);
        exit;
    }

    // Parse assignments (format: "ClassName - Section SectionName (DayOfWeek, StartTime - EndTime, Room)")
    $assignments = explode(';', $assignmentsStr);
    $validDays = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
    foreach ($assignments as $assignment) {
        $assignment = trim($assignment);
        if (empty($assignment)) continue;
        // Match the assignment format from the app (HH:MM instead of HH:MM:SS)
        if (!preg_match('/^(.*?)\s*-\s*Section\s*([^()]+)\s*\(([^,]+),\s*(\d{2}:\d{2})\s*-\s*(\d{2}:\d{2}),\s*([^)]+)\)$/', $assignment, $matches)) {
            echo json_encode(['status' => 'error', 'message' => 'Invalid assignment format: ' . $assignment]);
            exit;
        }
        $className = trim($matches[1]);
        $sectionName = trim($matches[2]);
        $dayOfWeek = trim($matches[3]);
        $startTime = trim($matches[4]) . ':00'; // Append :00 for HH:MM:SS
        $endTime = trim($matches[5]) . ':00';   // Append :00 for HH:MM:SS
        $room = trim($matches[6]);

        if (!in_array($dayOfWeek, $validDays) ||
            !preg_match('/^\d{2}:\d{2}:\d{2}$/', $startTime) ||
            !preg_match('/^\d{2}:\d{2}:\d{2}$/', $endTime)) {
            echo json_encode(['status' => 'error', 'message' => 'Invalid schedule details in: ' . $assignment]);
            exit;
        }
    }

    // Hash password
    $password_hash = password_hash($password, PASSWORD_BCRYPT);

    // Start transaction
    $pdo->beginTransaction();

    // Insert teacher
    $insertTeacherStmt = $pdo->prepare(
        "INSERT INTO teachers (name, email, password_hash, DOB, gender, phone, notes, is_active) 
         VALUES (:name, :email, :password_hash, :dob, :gender, :phone, :notes, :is_active)"
    );
    $insertTeacherStmt->execute([
        ':name' => $fullname,
        ':email' => $email,
        ':password_hash' => $password_hash,
        ':dob' => $dob,
        ':gender' => $gender,
        ':phone' => $phone,
        ':notes' => $notes,
        ':is_active' => true
    ]);

    $teacherId = $pdo->lastInsertId();

    // Prepare statements
    $insertAssignStmt = $pdo->prepare(
        "INSERT INTO teacher_sections (teacher_id, class_id, section_id) 
         VALUES (:teacher_id, :class_id, :section_id)"
    );
    $insertSubjectStmt = $pdo->prepare(
        "INSERT INTO subjects (subject_code, subject_name, teacher_id, class_id, section_id, created_by) 
         VALUES (:subject_code, :subject_name, :teacher_id, :class_id, :section_id, :created_by)"
    );
    $insertScheduleStmt = $pdo->prepare(
        "INSERT INTO schedules (subject_id, teacher_id, class_id, section_id, day_of_week, start_time, end_time, room, created_by) 
         VALUES (:subject_id, :teacher_id, :class_id, :section_id, :day_of_week, :start_time, :end_time, :room, :created_by)"
    );

    foreach ($assignments as $assignment) {
        $assignment = trim($assignment);
        if (empty($assignment)) continue;

        // Parse assignment: "ClassName - Section SectionName (DayOfWeek, StartTime - EndTime, Room)"
        if (!preg_match('/^(.*?)\s*-\s*Section\s*([^()]+)\s*\(([^,]+),\s*(\d{2}:\d{2})\s*-\s*(\d{2}:\d{2}),\s*([^)]+)\)$/', $assignment, $matches)) {
            $pdo->rollBack();
            echo json_encode(['status' => 'error', 'message' => 'Invalid assignment format: ' . $assignment]);
            exit;
        }

        $className = trim($matches[1]);
        $sectionName = trim($matches[2]);
        $dayOfWeek = trim($matches[3]);
        $startTime = trim($matches[4]) . ':00'; // Convert to HH:MM:SS
        $endTime = trim($matches[5]) . ':00';   // Convert to HH:MM:SS
        $room = trim($matches[6]);

        // Get class_id
        $classStmt = $pdo->prepare("SELECT class_id FROM classes WHERE class_name = :class_name LIMIT 1");
        $classStmt->execute(['class_name' => $className]);
        $classRow = $classStmt->fetch();
        if (!$classRow) {
            $pdo->rollBack();
            echo json_encode(['status' => 'error', 'message' => 'Class not found: ' . $className]);
            exit;
        }
        $classId = $classRow['class_id'];

        // Get section_id
        $sectionStmt = $pdo->prepare(
            "SELECT section_id FROM sections WHERE class_id = :class_id AND section_name = :section_name LIMIT 1"
        );
        $sectionStmt->execute(['class_id' => $classId, 'section_name' => $sectionName]);
        $sectionRow = $sectionStmt->fetch();
        if (!$sectionRow) {
            $pdo->rollBack();
            echo json_encode(['status' => 'error', 'message' => 'Section not found: ' . $sectionName]);
            exit;
        }
        $sectionId = $sectionRow['section_id'];

        // Insert into teacher_sections
        $insertAssignStmt->execute([
            ':teacher_id' => $teacherId,
            ':class_id' => $classId,
            ':section_id' => $sectionId
        ]);

        // Generate subject_code
        $subjectCode = strtoupper(substr($subject, 0, 3)) . $classId . str_replace(' ', '', $sectionName);

        // Check if subject exists
        $checkSubjectStmt = $pdo->prepare(
            "SELECT subject_id FROM subjects 
             WHERE class_id = :class_id AND section_id = :section_id AND subject_name = :subject_name LIMIT 1"
        );
        $checkSubjectStmt->execute([
            ':class_id' => $classId,
            ':section_id' => $sectionId,
            ':subject_name' => $subject
        ]);
        $subjectRow = $checkSubjectStmt->fetch();
        if (!$subjectRow) {
            // Insert into subjects
            $insertSubjectStmt->execute([
                ':subject_code' => $subjectCode,
                ':subject_name' => $subject,
                ':teacher_id' => $teacherId,
                ':class_id' => $classId,
                ':section_id' => $sectionId,
                ':created_by' => 1
            ]);
            $subjectId = $pdo->lastInsertId();
        } else {
            $subjectId = $subjectRow['subject_id'];
        }

        // Insert into schedules
        $insertScheduleStmt->execute([
            ':subject_id' => $subjectId,
            ':teacher_id' => $teacherId,
            ':class_id' => $classId,
            ':section_id' => $sectionId,
            ':day_of_week' => $dayOfWeek,
            ':start_time' => $startTime,
            ':end_time' => $endTime,
            ':room' => $room,
            ':created_by' => 1
        ]);
    }

    // Commit transaction
    $pdo->commit();
    echo json_encode(['status' => 'success', 'message' => 'Teacher saved successfully']);

} catch (PDOException $e) {
    $pdo->rollBack();
    echo json_encode(['status' => 'error', 'message' => 'Database error: ' . $e->getMessage()]);
} catch (Exception $e) {
    $pdo->rollBack();
    echo json_encode(['status' => 'error', 'message' => 'Error: ' . $e->getMessage()]);
}
?>