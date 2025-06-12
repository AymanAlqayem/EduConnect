<?php
header('Content-Type: application/json');
require_once 'db.config.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $inputJSON = file_get_contents('php://input');
    $input = json_decode($inputJSON, true);

    $subject_id = $input['subject_id'] ?? null;
    $teacher_id = $input['teacher_id'] ?? null;
    $title = $input['title'] ?? null;
    $description = $input['description'] ?? '';
    $due_date = $input['due_date'] ?? null;
    $max_score = $input['max_score'] ?? null;

    if (!$subject_id || !$teacher_id || !$title || !$due_date || !$max_score) {
        echo json_encode([
            "success" => false,
            "message" => "Missing required fields."
        ]);
        exit;
    }

    try {
        $pdo = getPDOConnection();

        $sql = "INSERT INTO assignments (subject_id, teacher_id, title, description, due_date, max_score)
                VALUES (:subject_id, :teacher_id, :title, :description, :due_date, :max_score)";

        $stmt = $pdo->prepare($sql);
        $stmt->execute([
            ':subject_id' => $subject_id,
            ':teacher_id' => $teacher_id,
            ':title' => $title,
            ':description' => $description,
            ':due_date' => $due_date,
            ':max_score' => $max_score
        ]);

        echo json_encode([
            "success" => true,
            "message" => "Assignment added successfully."
        ]);
    } catch (PDOException $e) {
        echo json_encode([
            "success" => false,
            "message" => "Database error: " . $e->getMessage()
        ]);
    }
} else {
    echo json_encode([
        "success" => false,
        "message" => "Invalid request method. Please use POST."
    ]);
}
?>
