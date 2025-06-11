<?php
header('Content-Type: application/json');

require_once("db.config.php");

try {
    // Get teacher_id from request
    $teacher_id = isset($_GET['teacher_id']) ? (int)$_GET['teacher_id'] : 0;

    if ($teacher_id <= 0) {
        echo json_encode(array("status" => "error", "message" => "Invalid teacher ID"));
        exit();
    }

    $pdo = getPDOConnection();
    
    // Query to fetch assigned classes and sections for the teacher
    $query = "
        SELECT c.class_name, s.section_name
        FROM teacher_sections ts
        JOIN classes c ON ts.class_id = c.class_id
        JOIN sections s ON ts.section_id = s.section_id
        WHERE ts.teacher_id = ?
        ORDER BY c.class_name, s.section_name
    ";

    $stmt = $pdo->prepare($query);
    $stmt->execute([$teacher_id]);
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $assignments = array();
    foreach ($result as $row) {
        $assignments[] = array(
            "class_name" => $row['class_name'],
            "section_name" => $row['section_name']
        );
    }

    // Return JSON response
    echo json_encode(array(
        "status" => "success",
        "assignments" => $assignments
    ));

} catch (Exception $e) {
    if (isset($pdo) && $pdo->inTransaction()) {
        $pdo->rollBack();
    }
    echo json_encode(array(
        "status" => "error",
        "message" => "Error: " . $e->getMessage()
    ));
}
?>