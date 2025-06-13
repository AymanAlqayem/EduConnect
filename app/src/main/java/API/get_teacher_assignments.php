<?php
header('Content-Type: application/json');
require_once("db.config.php");

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    echo json_encode(['status' => 'error', 'message' => 'Invalid request method']);
    exit;
}

$teacher_id = filter_var($_GET['teacher_id'] ?? '0', FILTER_VALIDATE_INT);

if (!$teacher_id) {
    echo json_encode(['status' => 'error', 'message' => 'Invalid or missing teacher_id']);
    exit;
}

try {
    $pdo = getPDOConnection();

    $sql = "
        SELECT 
            a.assignment_id,
            a.title,
            subj.subject_name AS subject_name,
            a.due_date,
            a.description,
            s.name AS student_name,
            c.class_name,
            sub.submission_text,
            sub.submission_file,
            sub.status AS submission_status
        FROM assignments a
        JOIN assignment_submissions sub ON a.assignment_id = sub.assignment_id
        JOIN subjects subj ON a.subject_id = subj.subject_id
        JOIN students s ON sub.student_id = s.student_id
        JOIN classes c ON subj.class_id = c.class_id
        WHERE a.teacher_id = ?
    ";
    $stmt = $pdo->prepare($sql);
    $stmt->execute([$teacher_id]);
    $assignments = $stmt->fetchAll(PDO::FETCH_ASSOC);

    if (empty($assignments)) {
        echo json_encode(['status' => 'success', 'data' => [], 'message' => 'No assignments found']);
        exit;
    }

    echo json_encode(['status' => 'success', 'data' => $assignments]);
} catch (Exception $e) {
    echo json_encode(['status' => 'error', 'message' => 'Failed to fetch assignments: ' . $e->getMessage()]);
}
?>