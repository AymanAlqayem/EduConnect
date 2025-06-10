<?php
header('Content-Type: application/json');
require_once("db.config.php");

if (!isset($_GET['class_id'])) {
    echo json_encode(["status" => "error", "message" => "Missing class_id parameter"]);
    exit;
}

$class_id = $_GET['class_id'];

try {
    $pdo = getPDOConnection();
    $stm = $pdo->prepare("SELECT section_id, section_name FROM sections WHERE class_id = ?");
    $stm->execute([$class_id]);
    $sections = $stm->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode([
        "status" => "success",
        "sections" => $sections
    ]);
} catch (PDOException $e) {
    echo json_encode([
        "status" => "error",
        "message" => $e->getMessage()
    ]);
}
?>
