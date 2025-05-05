
-- Create users table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('student', 'teacher', 'register') NOT NULL
);

-- Create students table
CREATE TABLE students (
    user_id INT PRIMARY KEY,
    parent_contact VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create teachers table
CREATE TABLE teachers (
    user_id INT PRIMARY KEY,
    specialization VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create courses table
CREATE TABLE courses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

-- Create class_groups table
CREATE TABLE class_groups (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- Create course_assignments table
CREATE TABLE course_assignments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    teacher_id INT NOT NULL,
    class_group_id INT NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(user_id),
    FOREIGN KEY (class_group_id) REFERENCES class_groups(id)
);

-- Create student_courses table
CREATE TABLE student_courses (
    student_id INT NOT NULL,
    course_assignment_id INT NOT NULL,
    PRIMARY KEY (student_id, course_assignment_id),
    FOREIGN KEY (student_id) REFERENCES students(user_id),
    FOREIGN KEY (course_assignment_id) REFERENCES course_assignments(id)
);

-- Create schedule table
CREATE TABLE schedule (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_assignment_id INT NOT NULL,
    day_of_week ENUM('Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday'),
    start_time TIME,
    end_time TIME,
    FOREIGN KEY (course_assignment_id) REFERENCES course_assignments(id)
);

-- Create assignments table
CREATE TABLE assignments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_assignment_id INT NOT NULL,
    title VARCHAR(255),
    description TEXT,
    due_date DATE,
    FOREIGN KEY (course_assignment_id) REFERENCES course_assignments(id)
);

-- Create submissions table
CREATE TABLE submissions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    assignment_id INT NOT NULL,
    student_id INT NOT NULL,
    submission_text TEXT,
    submission_date DATETIME,
    grade FLOAT,
    FOREIGN KEY (assignment_id) REFERENCES assignments(id),
    FOREIGN KEY (student_id) REFERENCES students(user_id)
);

-- Create messages table
CREATE TABLE messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_assignment_id INT NOT NULL,
    sender_id INT NOT NULL,
    message_text TEXT,
    sent_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_assignment_id) REFERENCES course_assignments(id),
    FOREIGN KEY (sender_id) REFERENCES teachers(user_id)
);

-- Create attendance table
CREATE TABLE attendance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_assignment_id INT NOT NULL,
    date DATE,
    status ENUM('present', 'absent') DEFAULT 'present',
    FOREIGN KEY (student_id) REFERENCES students(user_id),
    FOREIGN KEY (course_assignment_id) REFERENCES course_assignments(id)
);



-- USERS
INSERT INTO users (name, email, password, role) VALUES
('Alice Smith', 'alice@student.com', 'pass123', 'student'),
('Bob Jones', 'bob@student.com', 'pass123', 'student'),
('Charlie White', 'charlie@student.com', 'pass123', 'student'),
('Diana Brown', 'diana@student.com', 'pass123', 'student'),
('Evan Black', 'evan@student.com', 'pass123', 'student'),
('Prof. Alan', 'alan@school.com', 'teach123', 'teacher'),
('Prof. Betty', 'betty@school.com', 'teach123', 'teacher'),
('Prof. Carl', 'carl@school.com', 'teach123', 'teacher'),
('Prof. Dana', 'dana@school.com', 'teach123', 'teacher'),
('Prof. Eli', 'eli@school.com', 'teach123', 'teacher'),
('Reg Admin', 'reg@school.com', 'reg123', 'register');

-- STUDENTS (user_ids 1 to 5)
INSERT INTO students (user_id, parent_contact) VALUES
(1, 'mom_alice@gmail.com'),
(2, 'dad_bob@gmail.com'),
(3, 'mom_charlie@gmail.com'),
(4, 'dad_diana@gmail.com'),
(5, 'guardian_evan@gmail.com');

-- TEACHERS (user_ids 6 to 10)
INSERT INTO teachers (user_id, specialization) VALUES
(6, 'Mathematics'),
(7, 'Physics'),
(8, 'Chemistry'),
(9, 'English'),
(10, 'History');

-- COURSES
INSERT INTO courses (name, description) VALUES
('Algebra I', 'Introduction to Algebra'),
('Basic Physics', 'Foundations of physics'),
('Intro to Chemistry', 'Atoms, molecules and reactions'),
('English Grammar', 'Writing and grammar basics'),
('World History', 'History from ancient to modern times');

-- CLASS GROUPS
INSERT INTO class_groups (name) VALUES
('Class A'),
('Class B'),
('Class C'),
('Class D'),
('Class E');

-- COURSE ASSIGNMENTS (each course assigned to one teacher and one class group)
INSERT INTO course_assignments (course_id, teacher_id, class_group_id) VALUES
(1, 6, 1),
(2, 7, 2),
(3, 8, 3),
(4, 9, 4),
(5, 10, 5);

-- STUDENT COURSES (students assigned to different course assignments)
INSERT INTO student_courses (student_id, course_assignment_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4),
(5, 5);

-- SCHEDULE
INSERT INTO schedule (course_assignment_id, day_of_week, start_time, end_time) VALUES
(1, 'Monday', '08:00:00', '09:00:00'),
(2, 'Tuesday', '09:00:00', '10:00:00'),
(3, 'Wednesday', '10:00:00', '11:00:00'),
(4, 'Thursday', '11:00:00', '12:00:00'),
(5, 'Friday', '12:00:00', '13:00:00');

-- ASSIGNMENTS
INSERT INTO assignments (course_assignment_id, title, description, due_date) VALUES
(1, 'Algebra HW1', 'Solve equations', '2025-05-10'),
(2, 'Physics Lab', 'Write lab report', '2025-05-11'),
(3, 'Chem Quiz', 'Prepare for quiz', '2025-05-12'),
(4, 'Essay Writing', '500 words essay', '2025-05-13'),
(5, 'History Timeline', 'Create a timeline', '2025-05-14');

-- SUBMISSIONS
INSERT INTO submissions (assignment_id, student_id, submission_text, submission_date, grade) VALUES
(1, 1, 'All problems solved', '2025-05-09 18:00:00', 95.5),
(2, 2, 'Lab report attached', '2025-05-10 17:30:00', 88.0),
(3, 3, 'Quiz answers ready', '2025-05-11 20:00:00', 92.3),
(4, 4, 'Essay submitted', '2025-05-12 16:45:00', 85.0),
(5, 5, 'Timeline uploaded', '2025-05-13 19:00:00', 90.0);

-- MESSAGES
INSERT INTO messages (course_assignment_id, sender_id, message_text) VALUES
(1, 6, 'Don’t forget to review algebra notes.'),
(2, 7, 'Lab materials have been updated.'),
(3, 8, 'Quiz will be multiple choice.'),
(4, 9, 'Essay topic has changed slightly.'),
(5, 10, 'Extra credit available.');

-- ATTENDANCE
INSERT INTO attendance (student_id, course_assignment_id, date, status) VALUES
(1, 1, '2025-05-05', 'present'),
(2, 2, '2025-05-05', 'absent'),
(3, 3, '2025-05-05', 'present'),
(4, 4, '2025-05-05', 'present'),
(5, 5, '2025-05-05', 'present');





