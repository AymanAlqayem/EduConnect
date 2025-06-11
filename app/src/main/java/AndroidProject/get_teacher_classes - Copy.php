<?php
header('Content-Type: application/json');
require_once("db.config.php");

try {
    $pdo = getPDOConnection();

    if (!isset($_GET['teacher_id'])) {
        echo json_encode(["error" => "Missing teacher_id"]);
        exit;
    }

    $teacher_id = $_GET['teacher_id'];

    $sql = "SELECT DISTINCT c.class_id, c.class_name
            FROM classes c
            JOIN subjects s ON c.class_id = s.class_id
            WHERE s.teacher_id = ?";
    $stmt = $pdo->prepare($sql);
    $stmt->execute([$teacher_id]);

    $classes = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($classes);
} catch (PDOException $e) {
    echo json_encode(["error" => $e->getMessage()]);
}
?>
