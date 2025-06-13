<?php
header('Content-Type: application/json');
require_once("db.config.php");

try {
    $pdo = getPDOConnection();
    $today = date('m-d'); // current MM-DD

    $stmt = $pdo->prepare("SELECT name FROM students WHERE DATE_FORMAT(DOB, '%m-%d') = :today");
    $stmt->execute([':today' => $today]);

    $students = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode(['status' => 'success', 'birthdays' => $students]);
} catch (PDOException $e) {
    echo json_encode(['status' => 'error', 'message' => 'Database error: ' . $e->getMessage()]);
}
