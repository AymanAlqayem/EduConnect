<?php
error_reporting(0);
header('Content-Type: application/json');
require_once("db.config.php");

$class_id = $_GET['class_id'] ?? 0;

if (!$class_id) {
    echo json_encode([]);
    exit;
}

try {
    $conn = getPDOConnection();

    $sql = "SELECT student_id, name FROM students WHERE class_id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->execute([$class_id]);
    $students = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($students);

} catch (PDOException $e) {
    echo json_encode(['error' => $e->getMessage()]);
}

$conn = null;
?>
