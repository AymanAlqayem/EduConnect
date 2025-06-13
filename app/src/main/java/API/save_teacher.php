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
    

    // Validate required fields
    if (empty($fullname) || empty($email) || empty($password) || empty($subject)) {
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