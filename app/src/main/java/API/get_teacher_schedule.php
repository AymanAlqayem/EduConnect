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

$stmt = $pdo->prepare("
    SELECT ts.day, ts.period, s.subject_code AS subject, sec.section_name AS section
    FROM schedules sc
    JOIN timeslots ts ON ts.timeslot_id = sc.timeslot_id
    JOIN subjects s ON s.subject_id = sc.subject_id
    JOIN sections sec ON sec.section_id = sc.section_id
    WHERE sc.teacher_id = ?
");
$stmt->execute([$selectedTeacherId]);
$entries = $stmt->fetchAll(PDO::FETCH_ASSOC);

$schedule = [];
foreach ($entries as $entry) {
    $day = $entry['day'];
    $period = $entry['period'];
    $schedule[$day][$period] = [
        'subject' => $entry['subject'],
        'section' => $entry['section']
    ];
}

// Return schedule as JSON
echo json_encode([
    'teacher_id' => $selectedTeacherId,
    'schedule' => $schedule
]);
