<?php
header('Content-Type: application/json');
require_once("db.config.php");

try {
    // make db connection
    $pdo = getPDOConnection();

    $fullname = isset($_POST['fullname']) ? trim($_POST['fullname']) : '';
    $email = isset($_POST['email']) ? trim($_POST['email']) : '';
    $password = isset($_POST['password']) ? trim($_POST['password']) : '';
    $phone = isset($_POST['parentphone']) ? trim($_POST['parentphone']) : '';
    $birthDate = isset($_POST['birthDate']) ? trim($_POST['birthDate']) : '';
    $className = isset($_POST['class']) ? trim($_POST['class']) : '';

    if (empty($fullname) || empty($email) || empty($password) || empty($phone) || empty($birthDate) || empty($className)) {
        echo json_encode(['status' => 'error', 'message' => 'Missing required fields']);
        exit;
    }

    // validate email format
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        echo json_encode(['status' => 'error', 'message' => 'Invalid email format']);
        exit;
    }

    // validate phone (must be 10 digits)
    if (!preg_match('/^\d{10}$/', $phone)) {
        echo json_encode(['status' => 'error', 'message' => 'Invalid phone number']);
        exit;
    }

    // validate DOB format (must be YYYY-MM-DD)
    if (!preg_match('/^\d{4}-\d{2}-\d{2}$/', $birthDate)) {
        echo json_encode(['status' => 'error', 'message' => 'Invalid DOB format']);
        exit;
    }

    // get class_id from class_name
    $classStmt = $pdo->prepare("SELECT class_id FROM classes WHERE class_name = :class_name LIMIT 1");
    $classStmt->execute(['class_name' => $className]);
    $classRow = $classStmt->fetch(PDO::FETCH_ASSOC);

    if (!$classRow) {
        echo json_encode(['status' => 'error', 'message' => 'Class not found']);
        exit;
    }

    $classId = $classRow['class_id'];

    // Max number of students per section
    $maxStudentsPerSection = 30;

    // Try to find a section in this class that has space
    $sectionStmt = $pdo->prepare("
    SELECT s.section_id
    FROM sections s
    LEFT JOIN students st ON s.section_id = st.section_id
    WHERE s.class_id = :class_id
    GROUP BY s.section_id
    HAVING COUNT(st.student_id) < :max
    ORDER BY s.section_id ASC
    LIMIT 1
");
    $sectionStmt->execute([
        'class_id' => $classId,
        'max' => $maxStudentsPerSection
    ]);
    $sectionRow = $sectionStmt->fetch(PDO::FETCH_ASSOC);

    if (!$sectionRow) {
        echo json_encode(['status' => 'error', 'message' => 'All sections are full for this class']);
        exit;
    }

    $sectionId = $sectionRow['section_id'];


    // check if there is a student with email exists
    $checkStmt = $pdo->prepare("SELECT student_id FROM students WHERE email = :email LIMIT 1");
    $checkStmt->execute(['email' => $email]);
    if ($checkStmt->fetch()) {
        echo json_encode(['status' => 'error', 'message' => 'A student with this email already exists']);
        exit;
    }

    $pdo->beginTransaction();

    // add student
    $insertStudentStmt = $pdo->prepare(
        "INSERT INTO students (name, parent_phone, email, password_hash, DOB, class_id, section_id) 
     VALUES (:name, :phone, :email, :password_hash, :dob, :class_id, :section_id)"
    );
    $insertStudentStmt->execute([
        ':name' => $fullname,
        ':email' => $email,
        ':password_hash' => $password,
        ':dob' => $birthDate,
        ':phone' => $phone,
        ':class_id' => $classId,
        ':section_id' => $sectionId
    ]);

    $pdo->commit();

    echo json_encode(['status' => 'success', 'message' => 'Student registered successfully']);
} catch (Exception $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    echo json_encode(['status' => 'error', 'message' => 'Server error: ' . $e->getMessage()]);
}
