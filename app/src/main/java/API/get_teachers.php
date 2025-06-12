<?php
header('Content-Type: application/json');
require_once("db.config.php");

try {
    $pdo = getPDOConnection();

    $stmt = $pdo->prepare("
SELECT
    t.teacher_id AS id,
    t.name AS full_name,
    t.email,
    t.phone,
    t.gender,
    t.notes,
    DATE(t.created_at) AS joining_date,
    MAX(s.subject_name) AS subject
FROM teachers t
LEFT JOIN subjects s ON t.teacher_id = s.teacher_id
WHERE t.is_active = TRUE
GROUP BY t.teacher_id, t.name, t.email, t.phone, t.gender, t.notes, t.created_at
    ");
    $stmt->execute();
    $teachers = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode([
        "status" => "success",
        "teachers" => $teachers
    ]);
} catch (PDOException $e) {
    echo json_encode([
        "status" => "error",
        "message" => $e->getMessage()
    ]);
}