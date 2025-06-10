<?php
header('Content-Type: application/json');

require_once("db.config.php");

$data = json_decode(file_get_contents('php://input'), true);

$sender_id = $data['sender_id'];
$sender_type = $data['sender_type'];
$recipient_id = $data['recipient_id'];
$recipient_type = $data['recipient_type'];
$subject = $data['subject'];
$content = $data['content'];

$sql = "INSERT INTO messages (sender_id, sender_type, recipient_id, recipient_type, subject, content) VALUES (?, ?, ?, ?, ?, ?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ississ", $sender_id, $sender_type, $recipient_id, $recipient_type, $subject, $content);

if ($stmt->execute()) {
    echo json_encode(['success' => true]);
} else {
    echo json_encode(['error' => 'Failed to send message']);
}

$stmt->close();
$conn->close();
?>