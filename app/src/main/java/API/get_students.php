<?php
header('Content-Type: application/json');
require_once("db.config.php");

try {
    $pdo = getPDOConnection();

    $sql = "
        SELECT 
            s.student_id, 
            s.name, 
            s.email, 
            s.parent_phone, 
            s.DOB, 
            s.class_id,
            c.class_name AS class_name
        FROM students s
        LEFT JOIN classes c ON s.class_id = c.class_id
    ";
    
    $stmt = $pdo->query($sql);
    $students = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode([
        'status' => 'success',
        'students' => $students
    ]);
} catch (PDOException $e) {
    echo json_encode([
        'status' => 'error',
        'message' => 'Failed to fetch students: ' . $e->getMessage()
    ]);
}
?>
