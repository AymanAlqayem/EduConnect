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
            a.assignment_id,
            a.title,
            sub.subject_name,
            a.due_date,
            a.description,
            a.max_score,
            IFNULL(ass.status, 'Pending') AS submission_status,
            t.name AS teacher_name,
            c.class_name
        FROM assignments a
        JOIN subjects sub ON a.subject_id = sub.subject_id
        JOIN enrollments e ON sub.subject_id = e.subject_id
        JOIN students s ON e.student_id = s.student_id
        JOIN classes c ON s.class_id = c.class_id
        JOIN teachers t ON a.teacher_id = t.teacher_id
        LEFT JOIN assignment_submissions ass ON a.assignment_id = ass.assignment_id AND ass.student_id = e.student_id
        WHERE e.student_id = ?
        ORDER BY a.due_date ASC
    ";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([$student_id]);
    $assignments = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode([
        'status' => 'success',
        'data' => $assignments
    ]);
} catch (PDOException $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Failed to fetch assignments: ' . $e->getMessage()
    ]);
}
?>