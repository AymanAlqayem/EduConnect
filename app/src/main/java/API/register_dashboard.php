<?php
header('Content-Type: application/json');
require_once 'db.config.php';

// Function to get dashboard counts and announcements
function getDashboardData($pdo) {
    try {
        $data = array();

        // Count total students
        $stmt = $pdo->query("SELECT COUNT(*) AS count FROM students");
        $data['total_students'] = $stmt->fetch(PDO::FETCH_ASSOC)['count'];

        // Count total active teachers
        $stmt = $pdo->query("SELECT COUNT(*) AS count FROM teachers WHERE is_active = TRUE");
        $data['total_teachers'] = $stmt->fetch(PDO::FETCH_ASSOC)['count'];

        // Count total classes
        $stmt = $pdo->query("SELECT COUNT(*) AS count FROM classes");
        $data['total_classes'] = $stmt->fetch(PDO::FETCH_ASSOC)['count'];

        // Count students per class
        $stmt = $pdo->query("
            SELECT c.class_name, COUNT(s.student_id) AS student_count
            FROM classes c
            LEFT JOIN students s ON c.class_id = s.class_id
            GROUP BY c.class_id, c.class_name
        ");
        $class_counts = $stmt->fetchAll(PDO::FETCH_ASSOC);

        // Initialize counts for each class
        $data['class_counts'] = array(
            '10th' => 0,
            '11th_literature' => 0,
            '11th_science' => 0,
            '12th_literature' => 0,
            '12th_science' => 0
        );

        // Map database class names to response keys
        foreach ($class_counts as $row) {
            $class_key = str_replace(' ', '_', strtolower($row['class_name']));
            $data['class_counts'][$class_key] = (int)$row['student_count'];
        }

        // Get announcements (concatenate title and content)
        $stmt = $pdo->query("
            SELECT title, content
            FROM announcements
            ORDER BY created_at DESC
            LIMIT 5
        ");
        $announcements = $stmt->fetchAll(PDO::FETCH_ASSOC);
        $announcements_text = '';
        foreach ($announcements as $ann) {
            $announcements_text .= $ann['title'] . ': ' . $ann['content'] . "\n";
        }
        $data['announcements'] = trim($announcements_text);

        return array(
            'status' => 'success',
            'data' => $data
        );
    } catch (PDOException $e) {
        return array(
            'status' => 'error',
            'message' => 'Database error: ' . $e->getMessage()
        );
    }
}

// Get PDO connection
$pdo = getPDOConnection();

// Get dashboard data
$response = getDashboardData($pdo);

// Output JSON response
echo json_encode($response);
?>