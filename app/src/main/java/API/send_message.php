<?php
header('Content-Type: application/json');
require_once("db.config.php");

$data = json_decode(file_get_contents('php://input'), true);

$sender_id = $data['sender_id'] ?? null;
$sender_type = $data['sender_type'] ?? null;
$recipient_id = $data['recipient_id'] ?? null;
$recipient_type = $data['recipient_type'] ?? null;
$class_id = $data['class_id'] ?? null;
$section_id = $data['section_id'] ?? null;
$subject = $data['subject'] ?? null;
$content = $data['content'] ?? null;

$allowed_types = ['Student', 'Class'];
if (!in_array($recipient_type, $allowed_types)) {
    echo json_encode(['error' => 'Invalid recipient type.']);
    exit;
}

if (!$sender_id || !$sender_type || !$subject || !$content) {
    echo json_encode(['error' => 'Missing required fields.']);
    exit;
}

try {
    $pdo = getPDOConnection();

    $sql = "INSERT INTO messages (sender_id, sender_type, subject, content)
            VALUES (:sender_id, :sender_role, :subject, :content)";
    $stmt = $pdo->prepare($sql);
    $stmt->execute([
        ':sender_id' => $sender_id,
        ':sender_role' => $sender_type,
        ':subject' => $subject,
        ':content' => $content
    ]);

    $message_id = $pdo->lastInsertId();

    $sql2 = "INSERT INTO message_recipients (message_id, recipient_id, recipient_role, class_id, section_id)
             VALUES (:message_id, :recipient_id, :recipient_role, :class_id, :section_id)";
    $stmt2 = $pdo->prepare($sql2);
    $stmt2->execute([
        ':message_id' => $message_id,
        ':recipient_id' => $recipient_type === 'Student' ? $recipient_id : null,
        ':recipient_role' => $recipient_type === 'Student' ? 'Student' : null,
        ':class_id' => $recipient_type === 'Class' ? $class_id : null,
        ':section_id' => $recipient_type === 'Class' ? $section_id : null
    ]);

    echo json_encode(['success' => true, 'message_id' => $message_id]);
} catch (PDOException $e) {
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>
