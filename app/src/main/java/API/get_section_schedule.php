<?php
header('Content-Type: application/json');
require_once 'db.config.php';

$pdo = getPDOConnection();

$student_id = $_GET['student_id'] ?? null;

if (!$student_id) {
    echo json_encode(['error' => 'student_id is required']);
    exit;
}

// 1. Get the section_id for the student
$stmt = $pdo->prepare("SELECT section_id FROM students WHERE student_id = ?");
$stmt->execute([$student_id]);
$section_id = $stmt->fetchColumn();

if (!$section_id) {
    echo json_encode(['error' => 'Student not found or has no section assigned']);
    exit;
}

// 2. Get section name
$stmt = $pdo->prepare("SELECT section_name FROM sections WHERE section_id = ?");
$stmt->execute([$section_id]);
$sectionName = $stmt->fetchColumn();

if (!$sectionName) {
    echo json_encode(['error' => 'Section not found']);
    exit;
}

// 3. Get schedule entries
$stmt = $pdo->prepare("
    SELECT ts.day, ts.period, sub.subject_code AS subject, t.name AS teacher
    FROM schedules sc
    JOIN timeslots ts ON ts.timeslot_id = sc.timeslot_id
    JOIN subjects sub ON sub.subject_id = sc.subject_id
    JOIN teachers t ON t.teacher_id = sc.teacher_id
    WHERE sc.section_id = ?
");
$stmt->execute([$section_id]);
$entries = $stmt->fetchAll(PDO::FETCH_ASSOC);

$days = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY'];
$periods = range(1, 7);
$schedule = [];

foreach ($days as $day) {
    foreach ($periods as $period) {
        $schedule[$day][$period] = "Free";
    }
}

foreach ($entries as $e) {
    $schedule[$e['day']][$e['period']] = $e['subject'] . " (" . $e['teacher'] . ")";
}

echo json_encode([
    'section' => $sectionName,
    'schedule' => $schedule
], JSON_PRETTY_PRINT);