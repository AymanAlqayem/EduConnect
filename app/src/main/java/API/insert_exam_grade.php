<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");

$input = file_get_contents("php://input");
$data = json_decode($input, true);

if (
    !isset($data['subject_id']) ||
    !isset($data['teacher_id']) ||
    !isset($data['exam_name']) ||
    !isset($data['grades']) ||
    !is_array($data['grades'])
) {
    echo json_encode(["error" => "Missing required fields"]);
    exit;
}

$host = "localhost";
$dbname = "education";
$username = "root";
$password = "";

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    $subject_id = $data['subject_id'];
    $teacher_id = $data['teacher_id'];
    $exam_name = $data['exam_name'];
    $grades = $data['grades'];

    $stmt = $pdo->prepare("INSERT INTO marks (student_id, subject_id, teacher_id, exam_name, score) VALUES (:student_id, :subject_id, :teacher_id, :exam_name, :score)");

    foreach ($grades as $grade) {
        if (!isset($grade['student_id']) || !isset($grade['score'])) {
            echo json_encode(["error" => "Each grade must include student_id and score"]);
            exit;
        }

        $stmt->execute([
            ':student_id' => $grade['student_id'],
            ':subject_id' => $subject_id,
            ':teacher_id' => $teacher_id,
            ':exam_name' => $exam_name,
            ':score' => $grade['score']
        ]);
    }

    echo json_encode(["success" => true]);

} catch (PDOException $e) {
    echo json_encode(["error" => $e->getMessage()]);
}
?>
