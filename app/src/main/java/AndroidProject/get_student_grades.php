<?php
header('Content-Type: application/json');
require_once("db.config.php");

$student_id = $_GET['student_id'] ?? 0;

if (!$student_id) {
    echo json_encode(['status' => 'error', 'message' => 'Invalid student ID']);
    exit;
}

try {
    $pdo = getPDOConnection();

    $sql = "
        SELECT 
            sub.subject_name,
            sub.subject_code,
            m.exam_name,
            m.score,
            DATE_FORMAT(m.published_at, '%Y-%m-%d %H:%i') AS published_at
        FROM marks m
        JOIN subjects sub ON m.subject_id = sub.subject_id
        WHERE m.student_id = ?
        ORDER BY m.published_at DESC
    ";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([$student_id]);
    $grades = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode([
        'status' => 'success',
        'data' => $grades
    ]);
} catch (PDOException $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ]);
}