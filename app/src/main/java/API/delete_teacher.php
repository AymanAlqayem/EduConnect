<?php
header('Content-Type: application/json');
require_once("db.config.php");

try {
    $pdo = getPDOConnection();

    // Check if teacher_id is provided
    if (!isset($_POST['teacher_id'])) {
        echo json_encode([
            "status" => "error",
            "message" => "Teacher ID is required"
        ]);
        exit;
    }

    $teacher_id = $_POST['teacher_id'];

    $checkTeacher = $pdo->prepare("SELECT * FROM teachers WHERE teacher_id = ?");
    $checkTeacher->execute([$teacher_id]);

    if ($checkTeacher->rowCount() === 0) {
        echo json_encode([
            "status" => "error",
            "message" => "Teacher not found"
        ]);
        exit;
    }

    $pdo->beginTransaction();

    // Fetch all subjects taught by the teacher being deleted
    $subjectsStmt = $pdo->prepare("SELECT subject_id, subject_name FROM subjects WHERE teacher_id = ?");
    $subjectsStmt->execute([$teacher_id]);
    $subjects = $subjectsStmt->fetchAll(PDO::FETCH_ASSOC);

    foreach ($subjects as $subject) {
        $subjectId = $subject['subject_id'];
        $subjectName = $subject['subject_name'];

        // Try to find another teacher teaching the same subject
        $findOtherTeacher = $pdo->prepare("SELECT teacher_id FROM subjects WHERE subject_name = ? AND teacher_id != ? LIMIT 1");
        $findOtherTeacher->execute([$subjectName, $teacher_id]);
        $newTeacher = $findOtherTeacher->fetchColumn();

        // If not found, pick any available teacher
        if (!$newTeacher) {
            $fallbackTeacher = $pdo->prepare("SELECT teacher_id FROM teachers WHERE teacher_id != ? LIMIT 1");
            $fallbackTeacher->execute([$teacher_id]);
            $newTeacher = $fallbackTeacher->fetchColumn();
        }

        // If a fallback teacher is found, assign the subject to them
        if ($newTeacher) {
            $reassign = $pdo->prepare("UPDATE subjects SET teacher_id = ? WHERE subject_id = ?");
            $reassign->execute([$newTeacher, $subjectId]);
        }
    }

    // Delete from schedule where the teacher is referenced
    $deleteSchedule = $pdo->prepare("DELETE FROM schedules WHERE teacher_id = ?");
    $deleteSchedule->execute([$teacher_id]);

    $deleteTeacher = $pdo->prepare("DELETE FROM teachers WHERE teacher_id = ?");
    $deleteTeacher->execute([$teacher_id]);

    $pdo->commit();

    echo json_encode([
        "status" => "success",
        "message" => "Teacher and related subjects deleted successfully."
    ]);
} catch (Exception $e) {
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }

    echo json_encode([
        "status" => "error",
        "message" => "Deletion failed: " . $e->getMessage()
    ]);
}
