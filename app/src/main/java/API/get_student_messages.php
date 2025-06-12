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
   $sql = "SELECT
            m.message_id,
            m.subject,
            m.content,
            m.sent_at,
            m.sender_role,
            CASE
                WHEN m.sender_role = 'Teacher' THEN t.name
                WHEN m.sender_role = 'Student' THEN s.name
                ELSE 'Unknown'
            END AS sender_name
        FROM messages m
        LEFT JOIN teachers t ON m.sender_id = t.teacher_id AND m.sender_role = 'Teacher'
        LEFT JOIN students s ON m.sender_id = s.student_id AND m.sender_role = 'Student'
        INNER JOIN message_recipients mr ON m.message_id = mr.message_id
        WHERE
            (
                mr.recipient_role = 'Student'
                AND mr.recipient_id = :student_id
            )
            OR
            (
                mr.class_id = (SELECT class_id FROM students WHERE student_id = :student_id)
                OR mr.section_id = (SELECT section_id FROM students WHERE student_id = :student_id)
            )
        ORDER BY m.sent_at DESC";

$stmt = $pdo->prepare($sql);
$stmt->execute([':student_id' => $student_id]);
$messages = $stmt->fetchAll(PDO::FETCH_ASSOC);


    echo json_encode($messages);

} catch (PDOException $e) {
    echo json_encode(['error' => 'Failed to fetch messages: ' . $e->getMessage()]);
}
?>