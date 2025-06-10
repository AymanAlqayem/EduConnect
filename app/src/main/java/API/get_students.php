<?php
header('Content-Type: application/json');

require_once("db.config.php");

try {
    $pdo = getPDOConnection();

    $sql = "SELECT student_id, CONCAT(first_name, ' ', last_name) AS name FROM students";
    $stmt = $pdo->query($sql);
    $students = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($students);
} catch (PDOException $e) {
    echo json_encode(['error' => 'Failed to fetch students: ' . $e->getMessage()]);
}
?>