<?php
require_once("db.config.php");

header('Content-Type: application/json');

try {
    $pdo = getPDOConnection();

    // Check for student_id
    if (!isset($_GET['student_id'])) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Student ID not provided'
        ]);
        exit;
    }

    $student_id = intval($_GET['student_id']);

    // Fetch student's first name
    $stmt = $pdo->prepare("
        SELECT first_name
        FROM students
        WHERE student_id = :student_id
    ");
    $stmt->execute(['student_id' => $student_id]);
    $student = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$student) {
        echo json_encode([
            'status' => 'error',
            'message' => 'Student not found'
        ]);
        exit;
    }

    // Fetch assignments for the student
    $stmt = $pdo->prepare("
        SELECT a.assignment_id, a.title, s.subject_name AS course, 
               DATE_FORMAT(a.due_date, '%b %d, %Y %H:%i') AS due_date
        FROM assignments a
        JOIN subjects s ON a.subject_id = s.subject_id
        JOIN enrollments e ON s.subject_id = e.subject_id
        WHERE e.student_id = :student_id
        ORDER BY a.due_date ASC
    ");
    $stmt->execute(['student_id' => $student_id]);
    $assignments = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Fetch grades for the student
    $stmt = $pdo->prepare("
        SELECT m.mark_id, m.exam_name AS title, s.subject_name AS course, m.score
        FROM marks m
        JOIN subjects s ON m.subject_id = s.subject_id
        WHERE m.student_id = :student_id
        ORDER BY m.published_at DESC
    ");
    $stmt->execute(['student_id' => $student_id]);
    $grades = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Fetch events (announcements) for the student
    $stmt = $pdo->prepare("
        SELECT announcement_id, title, content, 
               DATE_FORMAT(created_at, '%b %d, %Y %H:%i') AS event_date
        FROM announcements
        WHERE target_role IN ('Student', 'All')
        ORDER BY created_at DESC
    ");
    $stmt->execute();
    $events = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode([
        'status' => 'success',
        'data' => [
            'first_name' => $student['first_name'],
            'assignments' => $assignments,
            'grades' => $grades,
            'events' => $events
        ]
    ]);

} catch (PDOException $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Database error: ' . $e->getMessage()
    ]);
}
?>