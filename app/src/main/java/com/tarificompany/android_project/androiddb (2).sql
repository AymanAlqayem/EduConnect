-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 13, 2025 at 05:42 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `androiddb`
--

-- --------------------------------------------------------

--
-- Table structure for table `announcements`
--

CREATE TABLE `announcements` (
  `announcement_id` int(11) NOT NULL,
  `title` varchar(100) NOT NULL,
  `content` text NOT NULL,
  `created_by` int(11) NOT NULL,
  `target_role` enum('Student','Teacher','All') NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `announcements`
--

INSERT INTO `announcements` (`announcement_id`, `title`, `content`, `created_by`, `target_role`, `created_at`) VALUES
(1, 'School Holiday', 'No classes on June 15th due to holiday.', 1, 'All', '2025-06-11 16:46:49'),
(2, 'Exam Schedule', 'Midterm exams start on June 20th.', 2, 'Student', '2025-06-11 16:46:49'),
(3, 'Teacher Meeting', 'Staff meeting on June 12th at 3 PM.', 3, 'Teacher', '2025-06-11 16:46:49');

-- --------------------------------------------------------

--
-- Table structure for table `assignments`
--

CREATE TABLE `assignments` (
  `assignment_id` int(11) NOT NULL,
  `subject_id` int(11) NOT NULL,
  `teacher_id` int(11) NOT NULL,
  `title` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `due_date` datetime NOT NULL,
  `max_score` decimal(5,2) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `assignments`
--

INSERT INTO `assignments` (`assignment_id`, `subject_id`, `teacher_id`, `title`, `description`, `due_date`, `max_score`, `created_at`) VALUES
(1, 1, 1, 'Math Assignment 1', 'Solve quadratic equations', '2025-06-20 23:59:00', 100.00, '2025-06-11 16:46:49'),
(2, 5, 2, 'Physics Lab Report', 'Write a report on motion experiment', '2025-06-25 23:59:00', 100.00, '2025-06-11 16:46:49'),
(3, 8, 3, 'Literature Essay', 'Analyze a poem', '2025-06-22 23:59:00', 100.00, '2025-06-11 16:46:49'),
(4, 12, 4, 'Chemistry Lab', 'Complete chemical reaction analysis', '2025-06-23 23:59:00', 100.00, '2025-06-11 16:46:49'),
(5, 21, 3, 'English Essay', 'Write a 500-word essay on a topic of your choice.', '2025-06-30 23:59:00', 100.00, '2025-06-11 16:46:49'),
(6, 2, 1, 'Math Homework', 'Complete exercises 1-10 from Chapter 3.', '2025-06-18 23:59:00', 50.00, '2025-06-11 16:46:49');

-- --------------------------------------------------------

--
-- Table structure for table `assignment_submissions`
--

CREATE TABLE `assignment_submissions` (
  `submission_id` int(11) NOT NULL,
  `assignment_id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `submission_file` varchar(255) DEFAULT NULL,
  `submission_text` text DEFAULT NULL,
  `submitted_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `status` enum('Submitted','Graded') DEFAULT 'Submitted',
  `score` decimal(5,2) DEFAULT NULL,
  `graded_by` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `assignment_submissions`
--

INSERT INTO `assignment_submissions` (`submission_id`, `assignment_id`, `student_id`, `submission_file`, `submission_text`, `submitted_at`, `status`, `score`, `graded_by`) VALUES
(1, 1, 1, NULL, 'Submitted quadratic equations solutions', '2025-06-11 16:46:49', 'Graded', 90.00, 1),
(2, 2, 7, NULL, 'Submitted motion experiment report', '2025-06-11 16:46:49', 'Graded', 85.00, 2),
(3, 3, 3, NULL, 'Submitted poem analysis', '2025-06-11 16:46:49', 'Graded', 88.00, 3),
(4, 4, 8, NULL, 'Submitted chemical reaction analysis', '2025-06-11 16:46:49', 'Graded', 92.00, 4);

-- --------------------------------------------------------

--
-- Table structure for table `classes`
--

CREATE TABLE `classes` (
  `class_id` int(11) NOT NULL,
  `class_name` enum('10th','11th literature','11th science','12th literature','12th science') NOT NULL,
  `created_by` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `classes`
--

INSERT INTO `classes` (`class_id`, `class_name`, `created_by`, `created_at`) VALUES
(1, '10th', 1, '2025-06-11 16:46:49'),
(2, '11th literature', 1, '2025-06-11 16:46:49'),
(3, '11th science', 1, '2025-06-11 16:46:49'),
(4, '12th literature', 2, '2025-06-11 16:46:49'),
(5, '12th science', 2, '2025-06-11 16:46:49');

-- --------------------------------------------------------

--
-- Table structure for table `enrollments`
--

CREATE TABLE `enrollments` (
  `enrollment_id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `subject_id` int(11) NOT NULL,
  `enrollment_date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `enrollments`
--

INSERT INTO `enrollments` (`enrollment_id`, `student_id`, `subject_id`, `enrollment_date`) VALUES
(1, 1, 1, '2025-01-10'),
(2, 1, 21, '2025-01-10'),
(3, 2, 2, '2025-01-10'),
(4, 2, 22, '2025-01-10'),
(5, 3, 3, '2025-01-10'),
(6, 3, 8, '2025-01-10'),
(7, 4, 9, '2025-01-10'),
(8, 5, 10, '2025-01-10'),
(9, 6, 11, '2025-01-10'),
(10, 7, 5, '2025-01-10'),
(11, 7, 12, '2025-01-10'),
(12, 8, 6, '2025-01-10'),
(13, 8, 12, '2025-01-10'),
(14, 9, 7, '2025-01-10'),
(15, 9, 13, '2025-01-10'),
(16, 9, 19, '2025-01-10'),
(17, 10, 14, '2025-01-10'),
(18, 10, 20, '2025-01-10');

-- --------------------------------------------------------

--
-- Table structure for table `marks`
--

CREATE TABLE `marks` (
  `mark_id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `subject_id` int(11) NOT NULL,
  `teacher_id` int(11) NOT NULL,
  `exam_name` varchar(100) NOT NULL,
  `score` decimal(5,2) NOT NULL,
  `published_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `marks`
--

INSERT INTO `marks` (`mark_id`, `student_id`, `subject_id`, `teacher_id`, `exam_name`, `score`, `published_at`) VALUES
(1, 1, 1, 1, 'Midterm', 85.50, '2025-06-11 16:46:49'),
(2, 2, 2, 1, 'Midterm', 78.00, '2025-06-11 16:46:49'),
(3, 3, 3, 1, 'Midterm', 92.00, '2025-06-11 16:46:49'),
(4, 4, 9, 3, 'Midterm', 88.50, '2025-06-11 16:46:49'),
(5, 5, 10, 3, 'Midterm', 90.00, '2025-06-11 16:46:49'),
(6, 7, 5, 2, 'Midterm', 79.50, '2025-06-11 16:46:49'),
(7, 9, 7, 2, 'Midterm', 82.00, '2025-06-11 16:46:49');

-- --------------------------------------------------------

--
-- Table structure for table `messages`
--

CREATE TABLE `messages` (
  `message_id` int(11) NOT NULL,
  `sender_id` int(11) NOT NULL,
  `sender_type` enum('Teacher','Student') NOT NULL,
  `subject` varchar(255) DEFAULT NULL,
  `content` text NOT NULL,
  `sent_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `parent_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `messages`
--

INSERT INTO `messages` (`message_id`, `sender_id`, `sender_type`, `subject`, `content`, `sent_at`, `parent_id`) VALUES
(1, 1, 'Teacher', 'Upcoming Math Test', 'Reminder: Math test on Monday.', '2025-06-11 16:46:49', NULL),
(2, 2, 'Teacher', 'Literature Class Prep', 'Bring your literature books tomorrow.', '2025-06-11 16:46:49', NULL),
(3, 3, 'Teacher', 'Lab Report Due', 'Lab report due Friday.', '2025-06-11 16:46:49', NULL),
(4, 4, 'Teacher', 'Poetry Reading Activity', 'Poetry reading in next class.', '2025-06-11 16:46:49', NULL),
(5, 5, 'Teacher', 'Science Project Submission', 'Don’t forget to submit your project.', '2025-06-11 16:46:49', NULL),
(6, 1, 'Student', 'Test', 'Thank you for the reminder!', '2025-06-11 16:46:49', 1),
(7, 2, 'Student', 'Test2', 'Will bring it. Thanks.', '2025-06-11 16:46:49', 2),
(8, 3, 'Student', 'Test3', 'Is it okay to submit in PDF format?', '2025-06-11 16:46:49', 3),
(9, 4, 'Student', 'Test4', 'Can we prepare a group performance?', '2025-06-11 16:46:49', 4),
(10, 5, 'Student', 'Test5', 'Project is almost done!', '2025-06-11 16:46:49', 5),
(11, 6, 'Student', 'Quick Question', 'Can I meet you after class tomorrow?', '2025-06-11 16:46:49', NULL),
(12, 7, 'Student', 'Class Absence Notice', 'I will be absent tomorrow.', '2025-06-11 16:46:49', NULL),
(13, 8, 'Student', 'Homework Help', 'Can I get help with the homework?', '2025-06-11 16:46:49', NULL),
(14, 9, 'Student', 'Notebook Issue', 'I lost my literature notebook.', '2025-06-11 16:46:49', NULL),
(15, 10, 'Student', 'Lab Book Check', 'Will you be checking our lab books today?', '2025-06-11 16:46:49', NULL),
(16, 1, 'Student', 'Assignments 1', 'Rodddd law sama7et', '2025-06-12 23:04:26', NULL),
(17, 1, 'Student', 'dfs', 'sdfsd', '2025-06-13 08:34:08', NULL),
(18, 1, 'Teacher', 'Midterm', 'Ok your are Fine', '2025-06-13 09:00:06', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `message_recipients`
--

CREATE TABLE `message_recipients` (
  `id` int(11) NOT NULL,
  `message_id` int(11) NOT NULL,
  `recipient_id` int(11) DEFAULT NULL,
  `recipient_role` enum('Teacher','Student') DEFAULT NULL,
  `class_id` int(11) DEFAULT NULL,
  `section_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `message_recipients`
--

INSERT INTO `message_recipients` (`id`, `message_id`, `recipient_id`, `recipient_role`, `class_id`, `section_id`) VALUES
(1, 1, NULL, NULL, 1, 1),
(2, 2, NULL, NULL, 2, 5),
(3, 3, NULL, NULL, 3, 9),
(4, 4, NULL, NULL, 4, 10),
(5, 5, NULL, NULL, 5, 14),
(6, 6, 1, 'Teacher', NULL, NULL),
(7, 7, 2, 'Teacher', NULL, NULL),
(8, 8, 3, 'Teacher', NULL, NULL),
(9, 9, 4, 'Teacher', NULL, NULL),
(10, 10, 5, 'Teacher', NULL, NULL),
(11, 11, 1, 'Teacher', NULL, NULL),
(12, 12, 2, 'Teacher', NULL, NULL),
(13, 13, 3, 'Teacher', NULL, NULL),
(14, 14, 4, 'Teacher', NULL, NULL),
(15, 15, 5, 'Teacher', NULL, NULL),
(16, 16, 7, 'Teacher', NULL, NULL),
(17, 17, 1, 'Teacher', NULL, NULL),
(18, 18, 1, 'Student', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `registrars`
--

CREATE TABLE `registrars` (
  `registrar_id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `registrars`
--

INSERT INTO `registrars` (`registrar_id`, `name`, `email`, `password_hash`, `created_at`) VALUES
(1, 'John Smith', 'john.smith@school.com', 'hashed_password_1', '2025-06-11 16:46:49'),
(2, 'Emma Wilson', 'emma.wilson@school.com', 'hashed_password_2', '2025-06-11 16:46:49'),
(3, 'Michael Brown', 'michael.brown@school.com', 'hashed_password_3', '2025-06-11 16:46:49');

-- --------------------------------------------------------

--
-- Table structure for table `schedules`
--

CREATE TABLE `schedules` (
  `schedule_id` int(11) NOT NULL,
  `subject_id` int(11) NOT NULL,
  `teacher_id` int(11) NOT NULL,
  `class_id` int(11) NOT NULL,
  `section_id` int(11) NOT NULL,
  `room` varchar(50) DEFAULT NULL,
  `created_by` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `timeslot_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `schedules`
--

INSERT INTO `schedules` (`schedule_id`, `subject_id`, `teacher_id`, `class_id`, `section_id`, `room`, `created_by`, `created_at`, `timeslot_id`) VALUES
(891, 1, 1, 1, 1, NULL, 1, '2025-06-13 15:35:58', 32),
(892, 1, 1, 1, 1, NULL, 1, '2025-06-13 15:35:58', 5),
(893, 1, 1, 1, 1, NULL, 1, '2025-06-13 15:35:58', 13),
(894, 1, 1, 1, 1, NULL, 1, '2025-06-13 15:35:58', 28),
(895, 1, 1, 1, 1, NULL, 1, '2025-06-13 15:35:58', 34),
(896, 2, 1, 1, 1, NULL, 1, '2025-06-13 15:35:58', 17),
(897, 2, 1, 1, 1, NULL, 1, '2025-06-13 15:35:58', 31),
(898, 2, 1, 1, 1, NULL, 1, '2025-06-13 15:35:58', 22),
(899, 2, 1, 1, 1, NULL, 1, '2025-06-13 15:35:58', 4),
(900, 15, 5, 1, 1, NULL, 1, '2025-06-13 15:35:58', 26),
(901, 15, 5, 1, 1, NULL, 1, '2025-06-13 15:35:58', 9),
(902, 15, 5, 1, 1, NULL, 1, '2025-06-13 15:35:58', 15),
(903, 15, 5, 1, 1, NULL, 1, '2025-06-13 15:35:58', 14),
(904, 21, 3, 1, 1, NULL, 1, '2025-06-13 15:35:58', 3),
(905, 21, 3, 1, 1, NULL, 1, '2025-06-13 15:35:58', 25),
(906, 21, 3, 1, 1, NULL, 1, '2025-06-13 15:35:58', 27),
(907, 21, 3, 1, 1, NULL, 1, '2025-06-13 15:35:58', 7),
(908, 22, 3, 1, 1, NULL, 1, '2025-06-13 15:35:58', 29),
(909, 22, 3, 1, 1, NULL, 1, '2025-06-13 15:35:58', 30),
(910, 22, 3, 1, 1, NULL, 1, '2025-06-13 15:35:58', 35),
(911, 22, 3, 1, 1, NULL, 1, '2025-06-13 15:35:58', 1),
(912, 3, 7, 2, 4, NULL, 1, '2025-06-13 15:35:58', 32),
(913, 3, 7, 2, 4, NULL, 1, '2025-06-13 15:35:58', 5),
(914, 3, 7, 2, 4, NULL, 1, '2025-06-13 15:35:58', 13),
(915, 3, 7, 2, 4, NULL, 1, '2025-06-13 15:35:58', 28),
(916, 8, 3, 2, 4, NULL, 1, '2025-06-13 15:35:58', 34),
(917, 8, 3, 2, 4, NULL, 1, '2025-06-13 15:35:58', 17),
(918, 8, 3, 2, 4, NULL, 1, '2025-06-13 15:35:58', 31),
(919, 8, 3, 2, 4, NULL, 1, '2025-06-13 15:35:58', 22),
(920, 9, 3, 2, 4, NULL, 1, '2025-06-13 15:35:58', 4),
(921, 9, 3, 2, 4, NULL, 1, '2025-06-13 15:35:58', 26),
(922, 9, 3, 2, 4, NULL, 1, '2025-06-13 15:35:58', 9),
(923, 9, 3, 2, 4, NULL, 1, '2025-06-13 15:35:58', 15),
(924, 9, 3, 2, 4, NULL, 1, '2025-06-13 15:35:58', 14),
(925, 16, 5, 2, 4, NULL, 1, '2025-06-13 15:35:58', 3),
(926, 16, 5, 2, 4, NULL, 1, '2025-06-13 15:35:58', 25),
(927, 16, 5, 2, 4, NULL, 1, '2025-06-13 15:35:58', 27),
(928, 16, 5, 2, 4, NULL, 1, '2025-06-13 15:35:58', 7),
(929, 4, 7, 3, 7, NULL, 1, '2025-06-13 15:35:58', 34),
(930, 4, 7, 3, 7, NULL, 1, '2025-06-13 15:35:58', 17),
(931, 4, 7, 3, 7, NULL, 1, '2025-06-13 15:35:58', 31),
(932, 4, 7, 3, 7, NULL, 1, '2025-06-13 15:35:58', 22),
(933, 5, 2, 3, 7, NULL, 1, '2025-06-13 15:35:58', 32),
(934, 5, 2, 3, 7, NULL, 1, '2025-06-13 15:35:58', 5),
(935, 5, 2, 3, 7, NULL, 1, '2025-06-13 15:35:58', 13),
(936, 5, 2, 3, 7, NULL, 1, '2025-06-13 15:35:58', 28),
(937, 5, 2, 3, 7, NULL, 1, '2025-06-13 15:35:58', 4),
(938, 6, 2, 3, 7, NULL, 1, '2025-06-13 15:35:58', 26),
(939, 6, 2, 3, 7, NULL, 1, '2025-06-13 15:35:58', 9),
(940, 6, 2, 3, 7, NULL, 1, '2025-06-13 15:35:58', 15),
(941, 6, 2, 3, 7, NULL, 1, '2025-06-13 15:35:58', 14),
(942, 6, 2, 3, 7, NULL, 1, '2025-06-13 15:35:58', 3),
(943, 12, 4, 3, 7, NULL, 1, '2025-06-13 15:35:58', 25),
(944, 12, 4, 3, 7, NULL, 1, '2025-06-13 15:35:58', 27),
(945, 12, 4, 3, 7, NULL, 1, '2025-06-13 15:35:58', 7),
(946, 12, 4, 3, 7, NULL, 1, '2025-06-13 15:35:58', 29),
(947, 18, 6, 3, 7, NULL, 1, '2025-06-13 15:35:58', 30),
(948, 18, 6, 3, 7, NULL, 1, '2025-06-13 15:35:58', 35),
(949, 18, 6, 3, 7, NULL, 1, '2025-06-13 15:35:58', 1),
(950, 18, 6, 3, 7, NULL, 1, '2025-06-13 15:35:58', 12),
(951, 10, 8, 4, 10, NULL, 1, '2025-06-13 15:35:58', 32),
(952, 10, 8, 4, 10, NULL, 1, '2025-06-13 15:35:58', 5),
(953, 10, 8, 4, 10, NULL, 1, '2025-06-13 15:35:58', 13),
(954, 10, 8, 4, 10, NULL, 1, '2025-06-13 15:35:58', 28),
(955, 10, 8, 4, 10, NULL, 1, '2025-06-13 15:35:58', 34),
(956, 11, 8, 4, 10, NULL, 1, '2025-06-13 15:35:58', 17),
(957, 11, 8, 4, 10, NULL, 1, '2025-06-13 15:35:58', 31),
(958, 11, 8, 4, 10, NULL, 1, '2025-06-13 15:35:58', 22),
(959, 11, 8, 4, 10, NULL, 1, '2025-06-13 15:35:58', 4),
(960, 11, 8, 4, 10, NULL, 1, '2025-06-13 15:35:58', 26),
(961, 17, 5, 4, 10, NULL, 1, '2025-06-13 15:35:58', 29),
(962, 17, 5, 4, 10, NULL, 1, '2025-06-13 15:35:58', 30),
(963, 17, 5, 4, 10, NULL, 1, '2025-06-13 15:35:58', 35),
(964, 17, 5, 4, 10, NULL, 1, '2025-06-13 15:35:58', 1),
(965, 7, 2, 5, 13, NULL, 1, '2025-06-13 15:35:58', 34),
(966, 7, 2, 5, 13, NULL, 1, '2025-06-13 15:35:58', 17),
(967, 7, 2, 5, 13, NULL, 1, '2025-06-13 15:35:58', 31),
(968, 7, 2, 5, 13, NULL, 1, '2025-06-13 15:35:58', 22),
(969, 13, 4, 5, 13, NULL, 1, '2025-06-13 15:35:58', 32),
(970, 13, 4, 5, 13, NULL, 1, '2025-06-13 15:35:58', 5),
(971, 13, 4, 5, 13, NULL, 1, '2025-06-13 15:35:58', 13),
(972, 13, 4, 5, 13, NULL, 1, '2025-06-13 15:35:58', 28),
(973, 14, 9, 5, 13, NULL, 1, '2025-06-13 15:35:58', 4),
(974, 14, 9, 5, 13, NULL, 1, '2025-06-13 15:35:58', 26),
(975, 14, 9, 5, 13, NULL, 1, '2025-06-13 15:35:58', 9),
(976, 14, 9, 5, 13, NULL, 1, '2025-06-13 15:35:58', 15),
(977, 19, 6, 5, 13, NULL, 1, '2025-06-13 15:35:58', 14),
(978, 19, 6, 5, 13, NULL, 1, '2025-06-13 15:35:58', 3),
(979, 19, 6, 5, 13, NULL, 1, '2025-06-13 15:35:58', 25),
(980, 19, 6, 5, 13, NULL, 1, '2025-06-13 15:35:58', 27),
(981, 20, 9, 5, 13, NULL, 1, '2025-06-13 15:35:58', 7),
(982, 20, 9, 5, 13, NULL, 1, '2025-06-13 15:35:58', 29),
(983, 20, 9, 5, 13, NULL, 1, '2025-06-13 15:35:58', 30),
(984, 20, 9, 5, 13, NULL, 1, '2025-06-13 15:35:58', 35);

-- --------------------------------------------------------

--
-- Table structure for table `sections`
--

CREATE TABLE `sections` (
  `section_id` int(11) NOT NULL,
  `class_id` int(11) NOT NULL,
  `section_name` varchar(50) NOT NULL,
  `created_by` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `sections`
--

INSERT INTO `sections` (`section_id`, `class_id`, `section_name`, `created_by`, `created_at`) VALUES
(1, 1, 'Section A', 1, '2025-06-11 16:46:49'),
(4, 2, 'Section A', 1, '2025-06-11 16:46:49'),
(7, 3, 'Section A', 1, '2025-06-11 16:46:49'),
(10, 4, 'Section A', 2, '2025-06-11 16:46:49'),
(13, 5, 'Section A', 2, '2025-06-11 16:46:49');

-- --------------------------------------------------------

--
-- Table structure for table `students`
--

CREATE TABLE `students` (
  `student_id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `parent_phone` varchar(10) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `DOB` date DEFAULT NULL,
  `class_id` int(11) NOT NULL,
  `section_id` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `students`
--

INSERT INTO `students` (`student_id`, `name`, `parent_phone`, `email`, `password_hash`, `DOB`, `class_id`, `section_id`, `created_at`) VALUES
(1, 'Emily Taylor', '7890123457', 'emily.taylor@school.com', 'hashed_password_10', '2008-06-14', 1, 1, '2025-06-11 16:46:49'),
(2, 'Noah Wilson', '8901234567', 'noah.wilson@school.com', 'hashed_password_11', '2008-06-15', 1, 2, '2025-06-11 16:46:49'),
(3, 'Sophia Clark', '9012345678', 'sophia.clark@school.com', 'hashed_password_12', '2007-09-20', 2, 1, '2025-06-11 16:46:49'),
(4, 'Liam Lewis', '0123456789', 'liam.lewis@school.com', 'hashed_password_13', '2007-02-10', 2, 2, '2025-06-11 16:46:49'),
(5, 'Olivia Walker', '1234567891', 'olivia.walker@school.com', 'hashed_password_14', '2006-11-05', 4, 1, '2025-06-11 16:46:49'),
(6, 'Ethan Hall', '2345678902', 'ethan.hall@school.com', 'hashed_password_15', '2006-03-22', 4, 2, '2025-06-11 16:46:49'),
(7, 'Ava Allen', '3456789013', 'ava.allen@school.com', 'hashed_password_16', '2007-07-18', 3, 1, '2025-06-11 16:46:49'),
(8, 'Mason Young', '4567890124', 'mason.young@school.com', 'hashed_password_17', '2007-01-30', 3, 2, '2025-06-11 16:46:49'),
(9, 'Isabella King', '5678901235', 'isabella.king@school.com', 'hashed_password_18', '2006-05-25', 5, 1, '2025-06-11 16:46:49'),
(10, 'Jacob Wright', '6789012346', 'jacob.wright@school.com', 'hashed_password_19', '2006-08-14', 5, 2, '2025-06-11 16:46:49'),
(11, 'Tyson Kid', '0597531594', 'tysonkid@edu.com', 'kid123', '2004-07-29', 4, 10, '2025-06-12 20:21:18');

-- --------------------------------------------------------

--
-- Table structure for table `subjects`
--

CREATE TABLE `subjects` (
  `subject_id` int(11) NOT NULL,
  `subject_code` varchar(20) NOT NULL,
  `subject_name` varchar(100) NOT NULL,
  `teacher_id` int(11) NOT NULL,
  `class_id` int(11) NOT NULL,
  `section_id` int(11) NOT NULL,
  `created_by` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `subjects`
--

INSERT INTO `subjects` (`subject_id`, `subject_code`, `subject_name`, `teacher_id`, `class_id`, `section_id`, `created_by`, `created_at`) VALUES
(1, 'MATH101', 'Mathematics', 1, 1, 1, 1, '2025-06-11 16:46:49'),
(2, 'MATH102', 'Mathematics', 1, 1, 2, 1, '2025-06-11 16:46:49'),
(3, 'MATH201', 'Mathematics', 7, 2, 1, 1, '2025-06-11 16:46:49'),
(4, 'MATH301', 'Mathematics', 7, 3, 1, 1, '2025-06-11 16:46:49'),
(5, 'PHY301', 'Physics', 2, 3, 1, 1, '2025-06-11 16:46:49'),
(6, 'PHY302', 'Physics', 2, 3, 2, 1, '2025-06-11 16:46:49'),
(7, 'PHY501', 'Physics', 2, 5, 1, 2, '2025-06-11 16:46:49'),
(8, 'LIT201', 'Literature', 3, 2, 1, 1, '2025-06-11 16:46:49'),
(9, 'LIT202', 'Literature', 3, 2, 2, 1, '2025-06-11 16:46:49'),
(10, 'LIT401', 'Literature', 8, 4, 1, 2, '2025-06-11 16:46:49'),
(11, 'LIT402', 'Literature', 8, 4, 2, 2, '2025-06-11 16:46:49'),
(12, 'CHEM302', 'Chemistry', 4, 3, 2, 1, '2025-06-11 16:46:49'),
(13, 'CHEM501', 'Chemistry', 4, 5, 2, 2, '2025-06-11 16:46:49'),
(14, 'CHEM502', 'Chemistry', 9, 5, 3, 2, '2025-06-11 16:46:49'),
(15, 'HIST103', 'History', 5, 1, 3, 1, '2025-06-11 16:46:49'),
(16, 'HIST203', 'History', 5, 2, 3, 1, '2025-06-11 16:46:49'),
(17, 'HIST403', 'History', 5, 4, 3, 2, '2025-06-11 16:46:49'),
(18, 'BIO303', 'Biology', 6, 3, 3, 1, '2025-06-11 16:46:49'),
(19, 'BIO501', 'Biology', 6, 5, 1, 2, '2025-06-11 16:46:49'),
(20, 'BIO502', 'Biology', 9, 5, 3, 2, '2025-06-11 16:46:49'),
(21, 'ENG101', 'English', 3, 1, 1, 1, '2025-06-11 16:46:49'),
(22, 'ENG102', 'English', 3, 1, 2, 1, '2025-06-11 16:46:49');

-- --------------------------------------------------------

--
-- Table structure for table `subject_loads`
--

CREATE TABLE `subject_loads` (
  `id` int(11) NOT NULL,
  `subject_id` int(11) NOT NULL,
  `weekly_periods` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `teachers`
--

CREATE TABLE `teachers` (
  `teacher_id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `DOB` date DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `gender` varchar(10) DEFAULT NULL,
  `phone` varchar(10) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `teachers`
--

INSERT INTO `teachers` (`teacher_id`, `name`, `email`, `password_hash`, `DOB`, `created_at`, `gender`, `phone`, `notes`, `is_active`) VALUES
(1, 'Alice Johnson', 'alice.johnson@school.com', 'hashed_password_4', '1975-03-15', '2025-06-11 16:46:49', 'Female', '1234567890', 'Math specialist', 1),
(2, 'Robert Davis', 'robert.davis@school.com', 'hashed_password_5', '1980-07-22', '2025-06-11 16:46:49', 'Male', '2345678901', 'Physics expert', 1),
(3, 'Sarah Lee', 'sarah.lee@school.com', 'hashed_password_6', '1978-11-30', '2025-06-11 16:46:49', 'Female', '3456789012', 'Literature enthusiast', 1),
(4, 'David Kim', 'david.kim@school.com', 'hashed_password_7', '1985-05-10', '2025-06-11 16:46:49', 'Male', '4567890123', 'Chemistry teacher', 1),
(5, 'Laura Chen', 'laura.chen@school.com', 'hashed_password_8', '1972-09-18', '2025-06-11 16:46:49', 'Female', '5678901234', 'History specialist', 1),
(6, 'James Patel', 'james.patel@school.com', 'hashed_password_9', '1983-01-25', '2025-06-11 16:46:49', 'Male', '6789012345', 'Biology teacher', 1),
(7, 'Mustafa Altaweel', 'afam396@gmail.com', 'firefly1122', '2004-07-29', '2025-06-12 15:28:44', 'Male', '0595237163', 'nothing', 1),
(8, 'Rahaf Badwan', 'rahaf@edu.com', 'pwd123', '2004-08-20', '2025-06-12 15:28:44', 'Female', '0591234567', 'nothing', 1),
(9, 'Saleh Shawer', 'saleh@edu.com', 'saddam2006', '2006-12-31', '2025-06-12 15:40:19', 'Male', '0565237162', 'Saddam Hussien Almajeed', 1);

-- --------------------------------------------------------

--
-- Table structure for table `teacher_class_subject`
--

CREATE TABLE `teacher_class_subject` (
  `id` int(11) NOT NULL,
  `teacher_id` int(11) NOT NULL,
  `class_id` int(11) NOT NULL,
  `section_id` int(11) NOT NULL,
  `subject_id` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `teacher_class_subject`
--

INSERT INTO `teacher_class_subject` (`id`, `teacher_id`, `class_id`, `section_id`, `subject_id`, `created_at`, `updated_at`) VALUES
(1, 1, 1, 1, 1, '2025-06-11 16:46:49', '2025-06-11 16:46:49'),
(2, 1, 1, 2, 2, '2025-06-11 16:46:49', '2025-06-11 16:46:49'),
(3, 1, 2, 1, 3, '2025-06-11 16:46:49', '2025-06-11 16:46:49'),
(4, 1, 3, 1, 4, '2025-06-11 16:46:49', '2025-06-11 16:46:49'),
(5, 2, 3, 1, 5, '2025-06-11 16:46:49', '2025-06-11 16:46:49'),
(6, 2, 3, 2, 6, '2025-06-11 16:46:49', '2025-06-11 16:46:49'),
(7, 2, 5, 1, 7, '2025-06-11 16:46:49', '2025-06-11 16:46:49'),
(8, 3, 2, 1, 8, '2025-06-11 16:46:49', '2025-06-11 16:46:49'),
(9, 3, 2, 2, 9, '2025-06-11 16:46:49', '2025-06-11 16:46:49'),
(10, 3, 4, 1, 10, '2025-06-11 16:46:49', '2025-06-11 16:46:49'),
(11, 3, 4, 2, 11, '2025-06-11 16:46:49', '2025-06-11 16:46:49'),
(12, 4, 3, 2, 12, '2025-06-11 16:46:49', '2025-06-11 16:46:49'),
(13, 4, 5, 2, 13, '2025-06-11 16:46:49', '2025-06-11 16:46:49'),
(14, 4, 5, 3, 14, '2025-06-11 16:46:49', '2025-06-11 16:46:49'),
(15, 5, 1, 3, 15, '2025-06-11 16:46:49', '2025-06-11 16:46:49'),
(16, 5, 2, 3, 16, '2025-06-11 16:46:49', '2025-06-11 16:46:49'),
(17, 5, 4, 3, 17, '2025-06-11 16:46:49', '2025-06-11 16:46:49'),
(18, 6, 3, 3, 18, '2025-06-11 16:46:49', '2025-06-11 16:46:49'),
(19, 6, 5, 1, 19, '2025-06-11 16:46:49', '2025-06-11 16:46:49'),
(20, 6, 5, 3, 20, '2025-06-11 16:46:49', '2025-06-11 16:46:49'),
(21, 3, 1, 1, 21, '2025-06-11 16:46:49', '2025-06-11 16:46:49'),
(22, 3, 1, 2, 22, '2025-06-11 16:46:49', '2025-06-11 16:46:49');

-- --------------------------------------------------------

--
-- Table structure for table `teacher_sections`
--

CREATE TABLE `teacher_sections` (
  `id` int(11) NOT NULL,
  `teacher_id` int(11) NOT NULL,
  `class_id` int(11) NOT NULL,
  `section_id` int(11) NOT NULL,
  `assigned_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `teacher_sections`
--

INSERT INTO `teacher_sections` (`id`, `teacher_id`, `class_id`, `section_id`, `assigned_at`) VALUES
(1, 1, 1, 1, '2025-06-11 16:46:49'),
(2, 1, 1, 2, '2025-06-11 16:46:49'),
(3, 1, 2, 1, '2025-06-11 16:46:49'),
(4, 1, 3, 1, '2025-06-11 16:46:49'),
(5, 2, 3, 1, '2025-06-11 16:46:49'),
(6, 2, 3, 2, '2025-06-11 16:46:49'),
(7, 2, 5, 1, '2025-06-11 16:46:49'),
(8, 3, 2, 1, '2025-06-11 16:46:49'),
(9, 3, 2, 2, '2025-06-11 16:46:49'),
(10, 3, 4, 1, '2025-06-11 16:46:49'),
(11, 3, 4, 2, '2025-06-11 16:46:49'),
(12, 4, 3, 2, '2025-06-11 16:46:49'),
(13, 4, 5, 2, '2025-06-11 16:46:49'),
(14, 4, 5, 3, '2025-06-11 16:46:49'),
(15, 5, 1, 3, '2025-06-11 16:46:49'),
(16, 5, 2, 3, '2025-06-11 16:46:49'),
(17, 5, 4, 3, '2025-06-11 16:46:49'),
(18, 6, 3, 3, '2025-06-11 16:46:49'),
(19, 6, 5, 1, '2025-06-11 16:46:49'),
(20, 6, 5, 3, '2025-06-11 16:46:49');

-- --------------------------------------------------------

--
-- Table structure for table `timeslots`
--

CREATE TABLE `timeslots` (
  `timeslot_id` int(11) NOT NULL,
  `day` varchar(10) DEFAULT NULL,
  `period` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `timeslots`
--

INSERT INTO `timeslots` (`timeslot_id`, `day`, `period`) VALUES
(1, 'SUNDAY', 1),
(2, 'SUNDAY', 2),
(3, 'SUNDAY', 3),
(4, 'SUNDAY', 4),
(5, 'SUNDAY', 5),
(6, 'SUNDAY', 6),
(7, 'SUNDAY', 7),
(8, 'MONDAY', 1),
(9, 'MONDAY', 2),
(10, 'MONDAY', 3),
(11, 'MONDAY', 4),
(12, 'MONDAY', 5),
(13, 'MONDAY', 6),
(14, 'MONDAY', 7),
(15, 'TUESDAY', 1),
(16, 'TUESDAY', 2),
(17, 'TUESDAY', 3),
(18, 'TUESDAY', 4),
(19, 'TUESDAY', 5),
(20, 'TUESDAY', 6),
(21, 'TUESDAY', 7),
(22, 'WEDNESDAY', 1),
(23, 'WEDNESDAY', 2),
(24, 'WEDNESDAY', 3),
(25, 'WEDNESDAY', 4),
(26, 'WEDNESDAY', 5),
(27, 'WEDNESDAY', 6),
(28, 'WEDNESDAY', 7),
(29, 'THURSDAY', 1),
(30, 'THURSDAY', 2),
(31, 'THURSDAY', 3),
(32, 'THURSDAY', 4),
(33, 'THURSDAY', 5),
(34, 'THURSDAY', 6),
(35, 'THURSDAY', 7);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `announcements`
--
ALTER TABLE `announcements`
  ADD PRIMARY KEY (`announcement_id`),
  ADD KEY `created_by` (`created_by`);

--
-- Indexes for table `assignments`
--
ALTER TABLE `assignments`
  ADD PRIMARY KEY (`assignment_id`),
  ADD KEY `subject_id` (`subject_id`),
  ADD KEY `teacher_id` (`teacher_id`);

--
-- Indexes for table `assignment_submissions`
--
ALTER TABLE `assignment_submissions`
  ADD PRIMARY KEY (`submission_id`),
  ADD KEY `assignment_id` (`assignment_id`),
  ADD KEY `student_id` (`student_id`),
  ADD KEY `graded_by` (`graded_by`);

--
-- Indexes for table `classes`
--
ALTER TABLE `classes`
  ADD PRIMARY KEY (`class_id`),
  ADD KEY `created_by` (`created_by`);

--
-- Indexes for table `enrollments`
--
ALTER TABLE `enrollments`
  ADD PRIMARY KEY (`enrollment_id`),
  ADD UNIQUE KEY `student_id` (`student_id`,`subject_id`),
  ADD KEY `subject_id` (`subject_id`);

--
-- Indexes for table `marks`
--
ALTER TABLE `marks`
  ADD PRIMARY KEY (`mark_id`),
  ADD KEY `student_id` (`student_id`),
  ADD KEY `subject_id` (`subject_id`),
  ADD KEY `teacher_id` (`teacher_id`);

--
-- Indexes for table `messages`
--
ALTER TABLE `messages`
  ADD PRIMARY KEY (`message_id`),
  ADD KEY `parent_id` (`parent_id`);

--
-- Indexes for table `message_recipients`
--
ALTER TABLE `message_recipients`
  ADD PRIMARY KEY (`id`),
  ADD KEY `message_id` (`message_id`),
  ADD KEY `class_id` (`class_id`),
  ADD KEY `section_id` (`section_id`);

--
-- Indexes for table `registrars`
--
ALTER TABLE `registrars`
  ADD PRIMARY KEY (`registrar_id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `schedules`
--
ALTER TABLE `schedules`
  ADD PRIMARY KEY (`schedule_id`),
  ADD KEY `subject_id` (`subject_id`),
  ADD KEY `teacher_id` (`teacher_id`),
  ADD KEY `class_id` (`class_id`),
  ADD KEY `section_id` (`section_id`),
  ADD KEY `created_by` (`created_by`);

--
-- Indexes for table `sections`
--
ALTER TABLE `sections`
  ADD PRIMARY KEY (`section_id`),
  ADD UNIQUE KEY `class_id` (`class_id`,`section_name`),
  ADD KEY `created_by` (`created_by`);

--
-- Indexes for table `students`
--
ALTER TABLE `students`
  ADD PRIMARY KEY (`student_id`),
  ADD UNIQUE KEY `parent_phone` (`parent_phone`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `class_id` (`class_id`),
  ADD KEY `section_id` (`section_id`);

--
-- Indexes for table `subjects`
--
ALTER TABLE `subjects`
  ADD PRIMARY KEY (`subject_id`),
  ADD UNIQUE KEY `subject_code` (`subject_code`),
  ADD UNIQUE KEY `class_id` (`class_id`,`section_id`,`subject_name`),
  ADD KEY `section_id` (`section_id`),
  ADD KEY `created_by` (`created_by`),
  ADD KEY `fk_subject_teacher` (`teacher_id`);

--
-- Indexes for table `subject_loads`
--
ALTER TABLE `subject_loads`
  ADD PRIMARY KEY (`id`),
  ADD KEY `subject_id` (`subject_id`);

--
-- Indexes for table `teachers`
--
ALTER TABLE `teachers`
  ADD PRIMARY KEY (`teacher_id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `teacher_class_subject`
--
ALTER TABLE `teacher_class_subject`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_teacher` (`teacher_id`),
  ADD KEY `fk_class` (`class_id`),
  ADD KEY `fk_section` (`section_id`),
  ADD KEY `fk_subject` (`subject_id`);

--
-- Indexes for table `teacher_sections`
--
ALTER TABLE `teacher_sections`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `teacher_id` (`teacher_id`,`class_id`,`section_id`),
  ADD KEY `class_id` (`class_id`),
  ADD KEY `section_id` (`section_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `announcements`
--
ALTER TABLE `announcements`
  MODIFY `announcement_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `assignments`
--
ALTER TABLE `assignments`
  MODIFY `assignment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `assignment_submissions`
--
ALTER TABLE `assignment_submissions`
  MODIFY `submission_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `classes`
--
ALTER TABLE `classes`
  MODIFY `class_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `enrollments`
--
ALTER TABLE `enrollments`
  MODIFY `enrollment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `marks`
--
ALTER TABLE `marks`
  MODIFY `mark_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `messages`
--
ALTER TABLE `messages`
  MODIFY `message_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `message_recipients`
--
ALTER TABLE `message_recipients`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `registrars`
--
ALTER TABLE `registrars`
  MODIFY `registrar_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `schedules`
--
ALTER TABLE `schedules`
  MODIFY `schedule_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=985;

--
-- AUTO_INCREMENT for table `sections`
--
ALTER TABLE `sections`
  MODIFY `section_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `students`
--
ALTER TABLE `students`
  MODIFY `student_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `subjects`
--
ALTER TABLE `subjects`
  MODIFY `subject_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT for table `subject_loads`
--
ALTER TABLE `subject_loads`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=41;

--
-- AUTO_INCREMENT for table `teachers`
--
ALTER TABLE `teachers`
  MODIFY `teacher_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `teacher_class_subject`
--
ALTER TABLE `teacher_class_subject`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT for table `teacher_sections`
--
ALTER TABLE `teacher_sections`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `announcements`
--
ALTER TABLE `announcements`
  ADD CONSTRAINT `announcements_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `registrars` (`registrar_id`);

--
-- Constraints for table `assignments`
--
ALTER TABLE `assignments`
  ADD CONSTRAINT `assignments_ibfk_1` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`subject_id`),
  ADD CONSTRAINT `assignments_ibfk_2` FOREIGN KEY (`teacher_id`) REFERENCES `teachers` (`teacher_id`);

--
-- Constraints for table `assignment_submissions`
--
ALTER TABLE `assignment_submissions`
  ADD CONSTRAINT `assignment_submissions_ibfk_1` FOREIGN KEY (`assignment_id`) REFERENCES `assignments` (`assignment_id`),
  ADD CONSTRAINT `assignment_submissions_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`),
  ADD CONSTRAINT `assignment_submissions_ibfk_3` FOREIGN KEY (`graded_by`) REFERENCES `teachers` (`teacher_id`);

--
-- Constraints for table `classes`
--
ALTER TABLE `classes`
  ADD CONSTRAINT `classes_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `registrars` (`registrar_id`);

--
-- Constraints for table `enrollments`
--
ALTER TABLE `enrollments`
  ADD CONSTRAINT `enrollments_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`),
  ADD CONSTRAINT `enrollments_ibfk_2` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`subject_id`);

--
-- Constraints for table `marks`
--
ALTER TABLE `marks`
  ADD CONSTRAINT `marks_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`),
  ADD CONSTRAINT `marks_ibfk_2` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`subject_id`),
  ADD CONSTRAINT `marks_ibfk_3` FOREIGN KEY (`teacher_id`) REFERENCES `teachers` (`teacher_id`);

--
-- Constraints for table `messages`
--
ALTER TABLE `messages`
  ADD CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `messages` (`message_id`);

--
-- Constraints for table `message_recipients`
--
ALTER TABLE `message_recipients`
  ADD CONSTRAINT `message_recipients_ibfk_1` FOREIGN KEY (`message_id`) REFERENCES `messages` (`message_id`),
  ADD CONSTRAINT `message_recipients_ibfk_2` FOREIGN KEY (`class_id`) REFERENCES `classes` (`class_id`),
  ADD CONSTRAINT `message_recipients_ibfk_3` FOREIGN KEY (`section_id`) REFERENCES `sections` (`section_id`);

--
-- Constraints for table `schedules`
--
ALTER TABLE `schedules`
  ADD CONSTRAINT `schedules_ibfk_1` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`subject_id`),
  ADD CONSTRAINT `schedules_ibfk_2` FOREIGN KEY (`teacher_id`) REFERENCES `teachers` (`teacher_id`),
  ADD CONSTRAINT `schedules_ibfk_3` FOREIGN KEY (`class_id`) REFERENCES `classes` (`class_id`),
  ADD CONSTRAINT `schedules_ibfk_4` FOREIGN KEY (`section_id`) REFERENCES `sections` (`section_id`),
  ADD CONSTRAINT `schedules_ibfk_5` FOREIGN KEY (`created_by`) REFERENCES `registrars` (`registrar_id`);

--
-- Constraints for table `sections`
--
ALTER TABLE `sections`
  ADD CONSTRAINT `sections_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `classes` (`class_id`),
  ADD CONSTRAINT `sections_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `registrars` (`registrar_id`);

--
-- Constraints for table `students`
--
ALTER TABLE `students`
  ADD CONSTRAINT `students_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `classes` (`class_id`),
  ADD CONSTRAINT `students_ibfk_2` FOREIGN KEY (`section_id`) REFERENCES `sections` (`section_id`);

--
-- Constraints for table `subjects`
--
ALTER TABLE `subjects`
  ADD CONSTRAINT `fk_subject_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `teachers` (`teacher_id`),
  ADD CONSTRAINT `subjects_ibfk_1` FOREIGN KEY (`teacher_id`) REFERENCES `teachers` (`teacher_id`),
  ADD CONSTRAINT `subjects_ibfk_2` FOREIGN KEY (`class_id`) REFERENCES `classes` (`class_id`),
  ADD CONSTRAINT `subjects_ibfk_3` FOREIGN KEY (`section_id`) REFERENCES `sections` (`section_id`),
  ADD CONSTRAINT `subjects_ibfk_4` FOREIGN KEY (`created_by`) REFERENCES `registrars` (`registrar_id`);

--
-- Constraints for table `subject_loads`
--
ALTER TABLE `subject_loads`
  ADD CONSTRAINT `subject_loads_ibfk_1` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`subject_id`);

--
-- Constraints for table `teacher_class_subject`
--
ALTER TABLE `teacher_class_subject`
  ADD CONSTRAINT `fk_class` FOREIGN KEY (`class_id`) REFERENCES `classes` (`class_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_section` FOREIGN KEY (`section_id`) REFERENCES `sections` (`section_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_subject` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`subject_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `teachers` (`teacher_id`) ON DELETE CASCADE;

--
-- Constraints for table `teacher_sections`
--
ALTER TABLE `teacher_sections`
  ADD CONSTRAINT `teacher_sections_ibfk_1` FOREIGN KEY (`teacher_id`) REFERENCES `teachers` (`teacher_id`),
  ADD CONSTRAINT `teacher_sections_ibfk_2` FOREIGN KEY (`class_id`) REFERENCES `classes` (`class_id`),
  ADD CONSTRAINT `teacher_sections_ibfk_3` FOREIGN KEY (`section_id`) REFERENCES `sections` (`section_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
