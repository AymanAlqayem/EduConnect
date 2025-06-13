<?php
header('Content-Type: application/json');
require_once("db.config.php");

try {
    $pdo = getPDOConnection();

    $studentId = $_POST['student_id'] ?? '';
    $name = $_POST['name'] ?? '';
    $parentPhone = $_POST['parent_phone'] ?? '';
    $classId = $_POST['class_id'] ?? '';

    if (empty($studentId) || empty($name) || empty($parentPhone) || empty($classId)) {
        echo json_encode(['status' => 'error', 'message' => 'Missing required fields']);
        exit;
    }

    $stmt = $pdo->prepare("UPDATE students SET name = :name, parent_phone = :parent_phone, class_id = :class_id WHERE student_id = :student_id");
    $stmt->execute([
        ':name' => $name,
        ':parent_phone' => $parentPhone,
        ':class_id' => $classId,
        ':student_id' => $studentId
    ]);

    echo json_encode(['status' => 'success', 'message' => 'Student updated successfully']);
} catch (Exception $e) {
    echo json_encode(['status' => 'error', 'message' => 'Update failed: ' . $e->getMessage()]);
}
