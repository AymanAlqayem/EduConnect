<?php
header('Content-Type: application/json');
require_once("db.config.php");

try {
    $pdo = getPDOConnection();
    $stm = $pdo->prepare("SELECT class_id, class_name FROM classes");
    $stm->execute();
    $classes = $stm->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode([
        "status" => "success",
        "classes" => $classes
    ]);
} catch (PDOException $e) {
    echo json_encode([
        "status" => "error",
        "message" => $e->getMessage()
    ]);
}
?>
