<?php
header('Content-Type: application/json');

require_once("db.config.php");

$conn = getPDOConnection();

$teacher_id = $_GET['teacher_id'] ?? 0;

if (!$teacher_id) {
    echo json_encode(['error' => 'Missing teacher_id']);
    exit;
}

try {
    $sql = "SELECT subject_id, subject_name FROM subjects WHERE teacher_id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->execute([$teacher_id]);
    $subjects = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($subjects);

} catch (PDOException $e) {
    echo json_encode(['error' => $e->getMessage()]);
}

$conn = null;
?>
