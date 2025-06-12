<?php
header('Content-Type: application/json');

try {
    require_once __DIR__ . "/db.config.php";
    $pdo = getPDOConnection();

    if (!$pdo) {
        echo json_encode(['status' => 'error', 'message' => 'Database connection not established']);
        exit;
    }

    $data = json_decode(file_get_contents("php://input"), true);

    $message_id = $data['message_id'] ?? null;
    $recipient_id = $data['recipient_id'] ?? null;

    if (!$message_id || !$recipient_id) {
        echo json_encode(['status' => 'error', 'message' => 'Missing message_id or recipient_id']);
        exit;
    }

    $sql = "UPDATE message_recipients 
            SET is_read = 1 
            WHERE message_id = ? AND recipient_id = ? AND recipient_role = 'Teacher'";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([$message_id, $recipient_id]);

    if ($stmt->rowCount() > 0) {
        echo json_encode(['status' => 'success', 'message' => 'Message marked as read']);
    } else {
        echo json_encode(['status' => 'info', 'message' => 'No update made. Either already read or not found.']);
    }

} catch (PDOException $e) {
    echo json_encode(['status' => 'error', 'message' => 'Database error: ' . $e->getMessage()]);
} catch (Exception $e) {
    echo json_encode(['status' => 'error', 'message' => 'Error: ' . $e->getMessage()]);
}
?>
