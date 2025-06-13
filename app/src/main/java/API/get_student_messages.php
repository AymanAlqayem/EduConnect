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

    // Get class_id and section_id for the student
    $stmt = $pdo->prepare("SELECT class_id, section_id FROM students WHERE student_id = :student_id");
    $stmt->execute([':student_id' => $student_id]);
    $student = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$student) {
        echo json_encode(['error' => 'Student not found']);
        exit;
    }

    $class_id = $student['class_id'];
    $section_id = $student['section_id'];

    $sql = "
        SELECT 
            m.message_id, 
            m.subject,
            m.content, 
            m.sent_at,
            CASE 
                WHEN m.sender_type = 'Teacher' THEN t.name
                WHEN m.sender_type = 'Student' THEN s.name
                ELSE 'Unknown'
            END AS sender_name
        FROM messages m
        JOIN message_recipients mr ON mr.message_id = m.message_id
        LEFT JOIN teachers t ON m.sender_id = t.teacher_id AND m.sender_type = 'Teacher'
        LEFT JOIN students s ON m.sender_id = s.student_id AND m.sender_type = 'Student'
        WHERE 
            (
                (mr.recipient_id = :student_id AND mr.recipient_role = 'Student') OR
                (mr.class_id = :class_id) OR
                (mr.section_id = :section_id)
            )
        ORDER BY m.sent_at DESC
    ";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([
        ':student_id' => $student_id,
        ':class_id' => $class_id,
        ':section_id' => $section_id
    ]);
    
    $messages = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($messages);

} catch (PDOException $e) {
    echo json_encode(['error' => 'Failed to fetch messages: ' . $e->getMessage()]);
}
?>
