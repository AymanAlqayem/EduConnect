<?php
require_once("db.config.php");

header('Content-Type: application/json');

try {
    $pdo = getPDOConnection();

    // Get JSON input
    $input = json_decode(file_get_contents('php://input'), true);
    if (!$input || !isset($input['id']) || !isset($input['full_name']) || !isset($input['email']) || !isset($input['subject'])) {
        echo json_encode(['status' => 'error', 'message' => 'Invalid input data']);
        exit;
    }

    $teacher_id = $input['id'];
    $full_name = $input['full_name'];
    $email = $input['email'];
    $phone = $input['phone'] ?? null;
    $subject_name = $input['subject'];
    $notes = $input['notes'] ?? null;
    $class_id = $input['class_id'] ?? null;
    $section_id = $input['section_id'] ?? null;
    $assignments = $input['assignments'] ?? [];

    // Start transaction
    $pdo->beginTransaction();

    // Update teacher details in teachers table
    $stmt = $pdo->prepare("
        UPDATE teachers 
        SET name = :name, email = :email, phone = :phone, notes = :notes
        WHERE teacher_id = :teacher_id
    ");
    $stmt->execute([
        'name' => $full_name,
        'email' => $email,
        'phone' => $phone,
        'notes' => $notes,
        'teacher_id' => $teacher_id
    ]);

    // If class_id and section_id are provided, update or insert subject
    if ($class_id && $section_id) {
        // Check if subject already exists for this class and section
        $stmt = $pdo->prepare("
            SELECT subject_id 
            FROM subjects 
            WHERE class_id = :class_id AND section_id = :section_id AND subject_name = :subject_name
        ");
        $stmt->execute(['class_id' => $class_id, 'section_id' => $section_id, 'subject_name' => $subject_name]);
        $subject = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($subject) {
            // Update existing subject
            $stmt = $pdo->prepare("
                UPDATE subjects 
                SET teacher_id = :teacher_id, created_by = :created_by
                WHERE subject_id = :subject_id
            ");
            $stmt->execute([
                'teacher_id' => $teacher_id,
                'created_by' => 1, // Assuming registrar_id 1 for now; adjust based on your auth system
                'subject_id' => $subject['subject_id']
            ]);
        } else {
            // Insert new subject
            $subject_code = strtoupper(substr($subject_name, 0, 3)) . rand(100, 999); // Generate unique subject code
            $stmt = $pdo->prepare("
                INSERT INTO subjects (subject_code, subject_name, teacher_id, class_id, section_id, created_by)
                VALUES (:subject_code, :subject_name, :teacher_id, :class_id, :section_id, :created_by)
            ");
            $stmt->execute([
                'subject_code' => $subject_code,
                'subject_name' => $subject_name,
                'teacher_id' => $teacher_id,
                'class_id' => $class_id,
                'section_id' => $section_id,
                'created_by' => 1 // Adjust based on auth system
            ]);
            $subject_id = $pdo->lastInsertId();
        }
    }

    // Handle assignments (class, section, schedule)
    if (!empty($assignments)) {
        // Delete existing teacher_sections and schedules for this teacher to avoid duplicates
        $stmt = $pdo->prepare("DELETE FROM teacher_sections WHERE teacher_id = :teacher_id");
        $stmt->execute(['teacher_id' => $teacher_id]);

        $stmt = $pdo->prepare("DELETE FROM schedules WHERE teacher_id = :teacher_id");
        $stmt->execute(['teacher_id' => $teacher_id]);

        // Insert new assignments
        foreach ($assignments as $assignment) {
            $class_name = $assignment['class'] ?? '';
            $section_name = $assignment['section'] ?? '';
            $day_of_week = $assignment['day_of_week'] ?? 'Monday'; // Default if not provided
            $start_time = $assignment['start_time'] ?? '08:00:00';
            $end_time = $assignment['end_time'] ?? '09:00:00';
            $room = $assignment['room'] ?? '';

            // Get class_id from class_name
            $stmt = $pdo->prepare("SELECT class_id FROM classes WHERE class_name = :class_name");
            $stmt->execute(['class_name' => $class_name]);
            $class = $stmt->fetch(PDO::FETCH_ASSOC);
            if (!$class) {
                throw new Exception("Class not found: $class_name");
            }
            $class_id = $class['class_id'];

            // Get section_id from section_name and class_id
            $stmt = $pdo->prepare("
                SELECT section_id 
                FROM sections 
                WHERE section_name = :section_name AND class_id = :class_id
            ");
            $stmt->execute(['section_name' => $section_name, 'class_id' => $class_id]);
            $section = $stmt->fetch(PDO::FETCH_ASSOC);
            if (!$section) {
                throw new Exception("Section not found: $section_name for class $class_name");
            }
            $section_id = $section['section_id'];

            // Insert into teacher_sections
            $stmt = $pdo->prepare("
                INSERT INTO teacher_sections (teacher_id, class_id, section_id)
                VALUES (:teacher_id, :class_id, :section_id)
            ");
            $stmt->execute([
                'teacher_id' => $teacher_id,
                'class_id' => $class_id,
                'section_id' => $section_id
            ]);

            // Get subject_id for the class, section, and subject_name
            $stmt = $pdo->prepare("
                SELECT subject_id 
                FROM subjects 
                WHERE class_id = :class_id AND section_id = :section_id AND subject_name = :subject_name
            ");
            $stmt->execute([
                'class_id' => $class_id,
                'section_id' => $section_id,
                'subject_name' => $subject_name
            ]);
            $subject = $stmt->fetch(PDO::FETCH_ASSOC);
            if (!$subject) {
                // Insert new subject if not exists
                $subject_code = strtoupper(substr($subject_name, 0, 3)) . rand(100, 999);
                $stmt = $pdo->prepare("
                    INSERT INTO subjects (subject_code, subject_name, teacher_id, class_id, section_id, created_by)
                    VALUES (:subject_code, :subject_name, :teacher_id, :class_id, :section_id, :created_by)
                ");
                $stmt->execute([
                    'subject_code' => $subject_code,
                    'subject_name' => $subject_name,
                    'teacher_id' => $teacher_id,
                    'class_id' => $class_id,
                    'section_id' => $section_id,
                    'created_by' => 1
                ]);
                $subject_id = $pdo->lastInsertId();
            } else {
                $subject_id = $subject['subject_id'];
            }

            // Insert into schedules
            $stmt = $pdo->prepare("
                INSERT INTO schedules (subject_id, teacher_id, class_id, section_id, day_of_week, start_time, end_time, room, created_by)
                VALUES (:subject_id, :teacher_id, :class_id, :section_id, :day_of_week, :start_time, :end_time, :room, :created_by)
            ");
            $stmt->execute([
                'subject_id' => $subject_id,
                'teacher_id' => $teacher_id,
                'class_id' => $class_id,
                'section_id' => $section_id,
                'day_of_week' => $day_of_week,
                'start_time' => $start_time,
                'end_time' => $end_time,
                'room' => $room,
                'created_by' => 1 // Adjust based on auth system
            ]);
        }
    }

    // Commit transaction
    $pdo->commit();
    echo json_encode(['status' => 'success', 'message' => 'Teacher updated successfully']);

} catch (PDOException $e) {
    $pdo->rollBack();
    echo json_encode(['status' => 'error', 'message' => 'Database error: ' . $e->getMessage()]);
} catch (Exception $e) {
    $pdo->rollBack();
    echo json_encode(['status' => 'error', 'message' => 'Error: ' . $e->getMessage()]);
}
?>