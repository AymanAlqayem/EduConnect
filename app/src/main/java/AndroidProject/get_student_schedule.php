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
        SELECT subject_name, day_of_week, start_time, end_time
        FROM students s
        JOIN enrollments e ON s.student_id = e.student_id
        JOIN subjects sub ON e.subject_id = sub.subject_id
        JOIN schedules sch ON sub.subject_id = sch.subject_id
        WHERE s.student_id = ?
        ORDER BY 
            FIELD(sch.day_of_week, 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'),
            sch.start_time
    ";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([$student_id]);
    $schedules = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode([
        'status' => 'success',
        'data' => $schedules
    ]);
} catch (PDOException $e) {
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage()
    ]);
}
