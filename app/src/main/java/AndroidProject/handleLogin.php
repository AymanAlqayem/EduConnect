<?php
require_once("db.config.php");

header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $email = $_POST['username'] ?? '';
    $password = $_POST['password'] ?? '';

    if (empty($email) || empty($password)) {
        echo json_encode(["success" => false, "message" => "Please fill all fields."]);
        exit;
    }

    try {
        $pdo = getPDOConnection();

        // Check in Registrars
        $stmt = $pdo->prepare("SELECT password_hash FROM registrars WHERE email = ?");
        $stmt->execute([$email]);
        $user = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($user) {
            if ($password === $user['password_hash']) {
                echo json_encode(["success" => true, "role" => "register", "message" => "Login successful"]);
                exit;
            } else {
                echo json_encode(["success" => false, "message" => "Incorrect password."]);
                exit;
            }
        }

        // Check in Teachers
        $stmt = $pdo->prepare("SELECT teacher_id, password_hash FROM teachers WHERE email = ?");
        $stmt->execute([$email]);
        $user = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($user) {
            if ($password === $user['password_hash']) {
                echo json_encode([
                    "success" => true,
                    "role" => "teacher",
                    "teacher_id" => $user['teacher_id'],
                    "message" => "Login successful"
                ]);
                exit;
            }
        }

            // Check in Students
        $stmt = $pdo->prepare("SELECT student_id, password_hash FROM students WHERE email = ?");
        $stmt->execute([$email]);
        $user = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($user) {
            if ($password === $user['password_hash']) {
                echo json_encode([
                    "success" => true,
                    "role" => "student",
                    "student_id" => $user['student_id'],
                    "message" => "Login successful"
                ]);
                exit;
            } else {
                echo json_encode(["success" => false, "message" => "Incorrect password."]);
                exit;
            }
        }

        // No user found
        echo json_encode(["success" => false, "message" => "This user doesn't exist."]);

    } catch (PDOException $e) {
        echo json_encode(["success" => false, "message" => "Database error: " . $e->getMessage()]);
    }
} else {
    echo json_encode(["success" => false, "message" => "Invalid request method."]);
}
