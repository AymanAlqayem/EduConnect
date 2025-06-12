<?php
header('Content-Type: application/json');
require_once 'db.config.php';

$timeslots = $pdo->query("SELECT * FROM timeslots")->fetchAll(PDO::FETCH_ASSOC);
shuffle($timeslots);

$teacherSchedule = [];
$sectionSchedule = [];

$sections = $pdo->query("SELECT * FROM sections")->fetchAll(PDO::FETCH_ASSOC);

$subjects = $pdo->query("SELECT * FROM subjects")->fetchAll(PDO::FETCH_ASSOC);

$subjectWeeklyLoad = [
    // class 10th
    1 => 5, 2 => 4, 3 => 4, 4 => 4, 5 => 5, 6 => 5, 7 => 4, 8 => 4,
    // class 11S
    9 => 5, 10 => 5, 11 => 5, 12 => 4, 13 => 4, 14 => 4, 15 => 4, 16 => 4,
    // class 11L
    17 => 4, 18 => 4, 19 => 4, 20 => 4, 21 => 4, 22 => 4, 23 => 3, 24 => 8,
    // class 12S
    25 => 5, 26 => 5, 27 => 5, 28 => 4, 29 => 4, 30 => 4, 31 => 4, 32 => 4,
    // class 12L
    33 => 4, 34 => 4, 35 => 4, 36 => 4, 37 => 4, 38 => 4, 39 => 3, 40 => 8
];

$pdo->exec("DELETE FROM schedules");

foreach ($sections as $section) {
    $section_id = $section['section_id'];
    $class_id = $section['class_id'];

    $classSubjects = array_filter($subjects, fn($s) => $s['class_id'] == $class_id);

    foreach ($classSubjects as $subject) {
        $subject_id = $subject['subject_id'];
        $teacher_id = $subject['teacher_id'];
        $periods = $subjectWeeklyLoad[$subject_id] ?? 0;

        for ($i = 0; $i < $periods; $i++) {
            foreach ($timeslots as $slot) {
                $slot_id = $slot['timeslot_id'];

                if (
                    empty($teacherSchedule[$teacher_id][$slot_id]) &&
                    empty($sectionSchedule[$section_id][$slot_id])
                ) {
                    $stmt = $pdo->prepare("INSERT INTO schedules (section_id, subject_id, timeslot_id, teacher_id, class_id, created_by) VALUES (?, ?, ?, ?,?,?)"); //for now just 1
                    $stmt->execute([$section_id, $subject_id, $slot_id, $teacher_id, $class_id, '1']);

                    $teacherSchedule[$teacher_id][$slot_id] = true;
                    $sectionSchedule[$section_id][$slot_id] = true;
                    break;
                }
            }
        }
    }
}
?>
