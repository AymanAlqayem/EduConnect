<?php
header('Content-Type: application/json');
require_once("db.config.php");

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(['status' => 'error', 'message' => 'Invalid request method']);
    exit;
}

$input = json_decode(file_get_contents('php://input'), true);
$assignment_id = $input['assignment_id'] ?? 0;
$student_id = $input['student_id'] ?? 0;
$submission_text = $input['submission_text'] ?? '';
$submission_file = $input['submission_file'] ?? null;

if (!$assignment_id || !$student_id) {
    echo json_encode(['status' => 'error', 'message' => 'Missing required fields']);
    exit;
}

try {
    $pdo = getPDOConnection();

    $sql = "SELECT due_date FROM assignments WHERE assignment_id = ?";
    $stmt = $pdo->prepare($sql);
    $stmt->execute([$assignment_id]);
    $assignment = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$assignment) {
        echo json_encode(['status' => 'error', 'message' => 'Assignment not found']);
        exit;
    }

    if (strtotime($assignment['due_date']) < time()) {
        echo json_encode(['status' => 'error', 'message' => 'Assignment submission is past due']);
        exit;
    }

    $sql = "SELECT submission_id FROM assignment_submissions WHERE assignment_id = ? AND student_id = ?";
    $stmt = $pdo->prepare($sql);
    $stmt->execute([$assignment_id, $student_id]);
    if ($stmt->fetch()) {
        echo json_encode(['status' => 'error', 'message' => 'Assignment already submitted']);
        exit;
    }

    $sql = "
        INSERT INTO assignment_submissions (assignment_id, student_id, submission_text, submission_file, status)
        VALUES (?, ?, ?, ?, 'Submitted')
    ";
    $stmt = $pdo->prepare($sql);
    $stmt->execute([$assignment_id, $student_id, $submission_text, $submission_file]);

    echo json_encode(['status' => 'success', 'message' => 'Assignment submitted successfully']);
} catch (PDOException $e) {
    echo json_encode(['status' => 'error', 'message' => 'Failed to submit assignment: ' . $e->getMessage()]);
}
?>