<?php
header('Content-Type: application/json');
require_once("db.config.php");

try {
    $pdo = getPDOConnection();

    // Check if teacher_id is provided
    if (!isset($_POST['teacher_id'])) {
        echo json_encode([
            "status" => "error",
            "message" => "Teacher ID is required"
        ]);
        exit;
    }

    $teacher_id = $_POST['teacher_id'];

    // Update is_active to FALSE for the specified teacher
    $stmt = $pdo->prepare("SET FOREIGN_KEY_CHECKS = 0; DELETE from teachers WHERE teacher_id = ?; SET FOREIGN_KEY_CHECKS = 1;
");
    $stmt->execute([$teacher_id]);

    // Check if any row was affected
    if ($stmt->rowCount() > 0) {
        echo json_encode([
            "status" => "success",
            "message" => "Teacher deleted successfully"
        ]);
    } else {
        echo json_encode([
            "status" => "error",
            "message" => "Teacher not found"
        ]);
    }
} catch (PDOException $e) {
    echo json_encode([
        "status" => "error",
        "message" => $e->getMessage()
    ]);
}
