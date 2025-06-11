<?php
header('Content-Type: application/json');
require_once("db.config.php");

try {
    $pdo = getPDOConnection();

    if (!isset($_POST['student_id'])) {
        echo json_encode([
            "status" => "error",
            "message" => "Teacher ID is required"
        ]);
        exit;
    }

    $student_id = $_POST['student_id'];

    $stmt = $pdo->prepare("DELETE from students WHERE student_id = ?");
    $stmt->execute([$student_id]);

    if ($stmt->rowCount() > 0) {
        echo json_encode([
            "status" => "success",
            "message" => "student deleted successfully"
        ]);
    } else {
        echo json_encode([
            "status" => "error",
            "message" => "Student not found"
        ]);
    }
} catch (PDOException $e) {
    echo json_encode([
        "status" => "error",
        "message" => $e->getMessage()
    ]);
}
?>