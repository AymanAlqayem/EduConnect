<?php
header('Content-Type: application/json');

try {
    require_once __DIR__ . "/db.config.php";
    $pdo = getPDOConnection();

    if (!$pdo) {
        echo json_encode([['error' => 'Database connection not established']]);
        exit;
    }

    $teacher_id = $_GET['teacher_id'] ?? 0;

    if (!$teacher_id) {
        echo json_encode([['error' => 'Invalid teacher ID']]);
        exit;
    }

    $updateSql = "
        UPDATE message_recipients 
        SET recipient_role = 'Teacher' 
        WHERE recipient_id = ? AND (recipient_role IS NULL OR recipient_role = '')
    ";
    $stmtUpdate = $pdo->prepare($updateSql);
    $stmtUpdate->execute([$teacher_id]);

    $sql = "
        SELECT 
            CASE 
                WHEN m.sender_role = 'Teacher' THEN (SELECT t.name FROM teachers t WHERE t.teacher_id = m.sender_id)
                WHEN m.sender_role = 'Student' THEN (SELECT s.first_name FROM students s WHERE s.student_id = m.sender_id)
                ELSE 'Unknown Sender'
            END AS sender_name,
            m.subject,
            m.content,
            m.sent_at
        FROM messages m
        JOIN message_recipients mr ON m.message_id = mr.message_id
        WHERE mr.recipient_id = ?
          AND mr.recipient_role = 'Teacher'
          AND mr.is_read = 0
        ORDER BY m.sent_at DESC
    ";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([$teacher_id]);
    $messages = $stmt->fetchAll(PDO::FETCH_ASSOC);

    if (empty($messages)) {
        echo json_encode([]); // لا ترجع error إذا لم يكن هناك رسائل غير مقروءة
    } else {
        echo json_encode($messages);
    }

} catch (PDOException $e) {
    echo json_encode([['error' => 'Database error: ' . $e->getMessage()]]);
} catch (Exception $e) {
    echo json_encode([['error' => 'Error: ' . $e->getMessage()]]);
}
?>