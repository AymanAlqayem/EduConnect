<?php
header('Content-Type: application/json');

require_once("db.config.php");

try {
    $conn = getPDOConnection();

    $sql = "SELECT class_id, class_name FROM classes";
    $stmt = $conn->query($sql);

    $classes = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($classes);

} catch (PDOException $e) {
    echo json_encode(['error' => $e->getMessage()]);
}

$conn = null;
?>
