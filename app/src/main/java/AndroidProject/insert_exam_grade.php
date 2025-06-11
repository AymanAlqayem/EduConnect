<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(["error" => "Only POST method allowed"]);
    exit;
}

require_once "db.config.php";

$input = file_get_contents("php://input");
$data = json_decode($input, true);

if (
    !isset($data['class_id']) ||
    !isset($data['teacher_id']) ||
    !isset($data['exam_name']) ||
    !isset($data['grades']) ||
    !is_array($data['grades'])
) {
    echo json_encode(["error" => "Missing required fields"]);
    exit;
}

$class_id = $data['class_id'];
$teacher_id = $data['teacher_id'];
$exam_name = $data['exam_name'];
$grades = $data['grades'];

if (!is_numeric($class_id) || !is_numeric($teacher_id) || !is_string($exam_name)) {
    echo json_encode(["error" => "Invalid input types"]);
    exit;
}

try {
    $pdo = getPDOConnection();

    // Get subject_id
    $stmt = $pdo->prepare("SELECT subject_id FROM teacher_class_subject WHERE teacher_id = :teacher_id AND class_id = :class_id LIMIT 1");
    $stmt->execute([':teacher_id' => $teacher_id, ':class_id' => $class_id]);
    $row = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$row) {
        echo json_encode(["error" => "No subject found for this teacher and class"]);
        exit;
    }

    $subject_id = $row['subject_id'];

    $insertStmt = $pdo->prepare("INSERT INTO marks (student_id, subject_id, teacher_id, exam_name, score) VALUES (:student_id, :subject_id, :teacher_id, :exam_name, :score)");

    $pdo->beginTransaction();

    foreach ($grades as $grade) {
        if (!isset($grade['student_id']) || !isset($grade['score']) || !is_numeric($grade['student_id']) || !is_numeric($grade['score'])) {
            $pdo->rollBack();
            echo json_encode(["error" => "Each grade must include numeric student_id and score"]);
            exit;
        }

        // Optional: validate score range, e.g., 0-100
        if ($grade['score'] < 0 || $grade['score'] > 100) {
            $pdo->rollBack();
            echo json_encode(["error" => "Score must be between 0 and 100"]);
            exit;
        }

        $insertStmt->execute([
            ':student_id' => $grade['student_id'],
            ':subject_id' => $subject_id,
            ':teacher_id' => $teacher_id,
            ':exam_name' => $exam_name,
            ':score' => $grade['score']
        ]);
    }

    $pdo->commit();

    echo json_encode(["success" => true]);

} catch (PDOException $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    echo json_encode(["error" => $e->getMessage()]);
}
?>
