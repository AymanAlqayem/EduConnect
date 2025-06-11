<?php
header('Content-Type: application/json');
require_once("db.config.php");

$student_id = $_GET['student_id'] ?? 0;

if (!$student_id || !is_numeric($student_id)) {
    echo json_encode(['error' => 'Invalid or missing student ID']);
    exit;
}

try {
    $pdo = getPDOConnection();

    // Get unread messages where student is recipient or part of recipient class
    $sql = "SELECT m.message_id, m.content, m.sent_at, 
                   CASE 
                       WHEN m.sender_type = 'Teacher' THEN t.name
                       WHEN m.sender_type = 'Student' THEN s.name
                   END as sender_name
            FROM messages m
            LEFT JOIN teachers t ON m.sender_id = t.teacher_id AND m.sender_type = 'Teacher'
            LEFT JOIN students s ON m.sender_id = s.student_id AND m.sender_type = 'Student'
            WHERE (m.recipient_id = :student_id AND m.recipient_type = 'Student' AND m.is_read = 0)
               OR (m.recipient_type = 'Class' AND m.recipient_id = 
                   (SELECT class_id FROM students WHERE student_id = :student_id))
            ORDER BY m.sent_at DESC";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([':student_id' => $student_id]);
    $messages = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($messages);

} catch (PDOException $e) {
    echo json_encode(['error' => 'Failed to fetch messages: ' . $e->getMessage()]);
}
?>