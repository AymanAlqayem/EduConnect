<?php
header('Content-Type: application/json');
require_once("db.config.php");

$message_id = $_GET['message_id'] ?? 0;

if (!$message_id) {
    echo json_encode(['error' => 'Invalid message ID']);
    exit;
}

try {
    $pdo = getPDOConnection();
    
    $sql = "SELECT m.*, 
                   CASE 
                       WHEN m.sender_type = 'Teacher' THEN t.name
                       WHEN m.sender_type = 'Student' THEN s.name
                   END as sender_name
            FROM messages m
            LEFT JOIN teachers t ON m.sender_id = t.teacher_id AND m.sender_type = 'Teacher'
            LEFT JOIN students s ON m.sender_id = s.student_id AND m.sender_type = 'Student'
            WHERE m.message_id = :message_id";
    
    $stmt = $pdo->prepare($sql);
    $stmt->execute([':message_id' => $message_id]);
    $message = $stmt->fetch(PDO::FETCH_ASSOC);
    
    if (!$message) {
        echo json_encode(['error' => 'Message not found']);
        exit;
    }
    
    echo json_encode($message);
    
} catch (PDOException $e) {
    echo json_encode(['error' => 'Failed to get message details: ' . $e->getMessage()]);
}
?>