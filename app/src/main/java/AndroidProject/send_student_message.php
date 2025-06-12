<?php
require_once 'db.config.php';

header('Content-Type: application/json');

try {
    $pdo = getPDOConnection();
    if (!$pdo) {
        echo json_encode(["error" => "Failed to connect to database"]);
        exit;
    }
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // قراءة محتوى JSON من جسم الطلب
    $rawInput = file_get_contents('php://input');

    if (!$rawInput) {
        echo json_encode(["error" => "Empty request body"]);
        exit;
    }

    // فك ترميز JSON
    $input = json_decode($rawInput, true);

    if (json_last_error() !== JSON_ERROR_NONE) {
        echo json_encode(["error" => "Invalid JSON: " . json_last_error_msg()]);
        exit;
    }

    // التحقق من الحقول المطلوبة
    if (!isset($input['sender_id'], $input['sender_type'], $input['recipient_id'], $input['recipient_type'], $input['content'])) {
        echo json_encode(["error" => "Missing required fields: sender_id, sender_type, recipient_id, recipient_type, or content"]);
        exit;
    }

    // استخراج المتغيرات من JSON
    $sender_id      = $input['sender_id'];
    $sender_role    = $input['sender_type'];
    $recipient_id   = $input['recipient_id'];
    $recipient_role = $input['recipient_type'];
    $subject        = $input['subject'] ?? '';
    $content        = $input['content'];
    $parent_id      = $input['parent_id'] ?? null;

    // بدء معاملة لضمان الاتساق
    $pdo->beginTransaction();

    // إدخال الرسالة في جدول messages
    $sqlMessage = "INSERT INTO messages (sender_id, sender_role, subject, content, parent_id)
                   VALUES (:sender_id, :sender_role, :subject, :content, :parent_id)";
    $stmtMessage = $pdo->prepare($sqlMessage);
    $stmtMessage->execute([
        ':sender_id' => $sender_id,
        ':sender_role' => $sender_role,
        ':subject' => $subject,
        ':content' => $content,
        ':parent_id' => $parent_id
    ]);

    $message_id = $pdo->lastInsertId();

    // إدخال المستلم في جدول message_recipients
    $sqlRecipient = "INSERT INTO message_recipients (message_id, recipient_id, recipient_role)
                     VALUES (:message_id, :recipient_id, :recipient_role)";
    $stmtRecipient = $pdo->prepare($sqlRecipient);
    $stmtRecipient->execute([
        ':message_id' => $message_id,
        ':recipient_id' => $recipient_id,
        ':recipient_role' => $recipient_role
    ]);

    // إنهاء المعاملة
    $pdo->commit();

    echo json_encode([
        "success" => true,
        "message_id" => $message_id
    ]);

} catch (PDOException $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    echo json_encode(["error" => "Database error: " . $e->getMessage()]);
} catch (Exception $e) {
    echo json_encode(["error" => "Error: " . $e->getMessage()]);
}
?>
