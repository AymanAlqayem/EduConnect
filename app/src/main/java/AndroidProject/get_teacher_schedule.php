<?php
header('Content-Type: application/json');
require_once("db.config.php");

$teacher_id = $_GET['teacher_id'] ?? 0;

if (!$teacher_id) {
    echo json_encode(['error' => 'Invalid teacher ID']);
    exit;
}

try {
    $pdo = getPDOConnection();

    $sql = "SELECT s.subject_name, sc.start_time, sc.end_time, c.class_name, sc.room, sc.day_of_week AS day,
                   (SELECT COUNT(e.student_id) FROM enrollments e WHERE e.subject_id = s.subject_id) AS student_count
            FROM schedules sc
            JOIN subjects s ON sc.subject_id = s.subject_id
            JOIN classes c ON s.class_id = c.class_id
            WHERE sc.teacher_id = ?";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([$teacher_id]);
    $schedules = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($schedules);
} catch (PDOException $e) {
    echo json_encode(['error' => 'Failed to fetch schedules: ' . $e->getMessage()]);
}
?>