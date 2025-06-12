<?php
header('Content-Type: application/json');
require_once("db.config.php"); 

$data = json_decode(file_get_contents('php://input'), true);

$subject_id = isset($data['subject_id']) ? intval($data['subject_id']) : 0;
$teacher_id = isset($data['teacher_id']) ? intval($data['teacher_id']) : 0;
$exam_name = isset($data['exam_name']) ? trim($data['exam_name']) : '';
$grades = isset($data['grades']) && is_array($data['grades']) ? $data['grades'] : [];

if ($subject_id <= 0 || $teacher_id <= 0 || empty($exam_name) || empty($grades)) {
    echo json_encode(['error' => 'Invalid input data']);
    exit;
}

try {
    $pdo->beginTransaction();

    $sql = "INSERT INTO marks (student_id, subject_id, teacher_id, exam_name, score) 
            VALUES (:student_id, :subject_id, :teacher_id, :exam_name, :score)";
    $stmt = $pdo->prepare($sql);

    foreach ($grades as $grade) {
        $student_id = isset($grade['student_id']) ? intval($grade['student_id']) : 0;
        $score = isset($grade['score']) ? floatval($grade['score']) : null;

        if ($student_id <= 0 || $score === null) {
            throw new Exception('Invalid grade data');
        }

        $stmt->execute([
            ':student_id' => $student_id,
            ':subject_id' => $subject_id,
            ':teacher_id' => $teacher_id,
            ':exam_name' => $exam_name,
            ':score' => $score
        ]);
    }

    $pdo->commit();
    echo json_encode(['success' => true]);

} catch (Exception $e) {
    $pdo->rollBack();
    echo json_encode(['error' => 'Failed to submit grades: ' . $e->getMessage()]);
}
?>
