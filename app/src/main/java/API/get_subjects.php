<?php
header('Content-Type: application/json');

require_once("db.config.php");

try {
    // Establish a PDO connection
    $pdo = getPDOConnection();

    // Prepare and execute the SQL query
    $stm = $pdo->prepare("SELECT DISTINCT subject_name FROM subjects");
    $stm->execute();

    // Fetch all results
    $subjects = $stm->fetchAll(PDO::FETCH_COLUMN);

    // Return JSON response
    echo json_encode([
        'status' => 'success',
        'subjects' => $subjects
    ]);

} catch (PDOException $e) {
    // Return error as JSON
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage()
    ]);
}
?>
