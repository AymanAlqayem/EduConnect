<?php
header('Content-Type: application/json');
require_once 'db.config.php';

$pdo = getPDOConnection();

$selectedTeacherId = $_GET['teacher_id'] ?? $_POST['teacher_id'] ?? null;

if (!$selectedTeacherId || !is_numeric($selectedTeacherId)) {
    http_response_code(400);
    echo json_encode(['error' => 'Invalid or missing teacher_id']);
    exit;
}

$today = date('l');

$classStmt = $pdo->prepare("
    SELECT COUNT(*) AS class_count
    FROM schedules sc
    JOIN timeslots ts ON ts.timeslot_id = sc.timeslot_id
    WHERE sc.teacher_id = ? AND ts.day = ?
");
$classStmt->execute([$selectedTeacherId, $today]);
$classCount = (int) $classStmt->fetchColumn();

$studentStmt = $pdo->prepare("
    SELECT COUNT(DISTINCT st.student_id) AS student_count
    FROM schedules sc
    JOIN sections sec ON sec.section_id = sc.section_id
    JOIN students st ON st.section_id = sec.section_id
    WHERE sc.teacher_id = ?
");
$studentStmt->execute([$selectedTeacherId]);
$studentCount = (int) $studentStmt->fetchColumn();

echo json_encode([
    'class_count' => $classCount,
    'student_count' => $studentCount
]);
