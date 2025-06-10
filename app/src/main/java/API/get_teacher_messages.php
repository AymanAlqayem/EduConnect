<?php
header('Content-Type: application/json');

require_once("db.config.php");

$teacher_id = $_GET['teacher_id'] ?? 0;

if (!$teacher_id) {
    echo json_encode(['error' => 'Invalid teacher ID']);
    $conn->close();
    exit;
}

$sql = "SELECT CONCAT(s.first_name, ' ', s.last_name) as sender_name, m.subject, m.content, m.sent_at
        FROM messages m
        JOIN students s ON m.sender_id = s.student_id
        WHERE m.recipient_id = ? AND m.recipient_type = 'Teacher'";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $teacher_id);
$stmt->execute();
$result = $stmt->get_result();

$messages = [];
while ($row = $result->fetch_assoc()) {
    $messages[] = $row;
}

echo json_encode($messages);

$stmt->close();
$conn->close();
?>