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
    $subject_id = isset($_POST['subject']) ? trim($_POST['subject']) : '';

    // Validate required fields
    if (empty($fullname) || empty($email) || empty($password) || empty($subject_id)) {
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
        ':password_hash' => $password,
        ':dob' => $dob,
        ':gender' => $gender,
        ':phone' => $phone,
        ':notes' => $notes,
        ':is_active' => true
    ]);

    $teacherId = $pdo->lastInsertId();

    // Update the selected subject with the new teacher ID
    $updateSubject = $pdo->prepare("UPDATE subjects SET teacher_id = :teacher_id WHERE subject_id = :subject_id");
    $updateSubject->execute([
        ':teacher_id' => $teacherId,
        ':subject_id' => $subject_id
    ]);

    $pdo->commit();

    echo json_encode(['status' => 'success', 'message' => 'Teacher saved and assigned to subject successfully']);

} catch (PDOException $e) {
    if ($pdo->inTransaction()) $pdo->rollBack();
    echo json_encode(['status' => 'error', 'message' => 'Database error: ' . $e->getMessage()]);
} catch (Exception $e) {
    if ($pdo->inTransaction()) $pdo->rollBack();
    echo json_encode(['status' => 'error', 'message' => 'Error: ' . $e->getMessage()]);
}
?>
