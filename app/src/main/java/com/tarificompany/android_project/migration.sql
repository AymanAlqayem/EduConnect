CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('student', 'teacher', 'register') NOT NULL
);


CREATE TABLE students (
    user_id INT PRIMARY KEY,
    parent_contact VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE teachers (
    user_id INT PRIMARY KEY,
    specialization VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE courses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE class_groups (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE course_assignments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    teacher_id INT NOT NULL,
    class_group_id INT NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (teacher_id) REFERENCES teachers(user_id),
    FOREIGN KEY (class_group_id) REFERENCES class_groups(id)
);

CREATE TABLE student_courses (
    student_id INT NOT NULL,
    course_assignment_id INT NOT NULL,
    PRIMARY KEY (student_id, course_assignment_id),
    FOREIGN KEY (student_id) REFERENCES students(user_id),
    FOREIGN KEY (course_assignment_id) REFERENCES course_assignments(id)
);

CREATE TABLE schedule (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_assignment_id INT NOT NULL,
    day_of_week ENUM('Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday'),
    start_time TIME,
    end_time TIME,
    FOREIGN KEY (course_assignment_id) REFERENCES course_assignments(id)
);

CREATE TABLE assignments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_assignment_id INT NOT NULL,
    title VARCHAR(255),
    description TEXT,
    due_date DATE,
    FOREIGN KEY (course_assignment_id) REFERENCES course_assignments(id)
);

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

CREATE TABLE messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_assignment_id INT NOT NULL,
    sender_id INT NOT NULL,
    message_text TEXT,
    sent_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_assignment_id) REFERENCES course_assignments(id),
    FOREIGN KEY (sender_id) REFERENCES teachers(user_id)
);

CREATE TABLE attendance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_assignment_id INT NOT NULL,
    date DATE,
    status ENUM('present', 'absent') DEFAULT 'present',
    FOREIGN KEY (student_id) REFERENCES students(user_id),
    FOREIGN KEY (course_assignment_id) REFERENCES course_assignments(id)
);


INSERT INTO users (id, name, email, password, role) VALUES

(1, 'Alice Johnson', 'alice@student.com', 'pass123', 'student'),
(2, 'Bob Smith', 'bob@student.com', 'pass123', 'student'),
(3, 'Carol Lee', 'carol@student.com', 'pass123', 'student'),
(4, 'David Kim', 'david@student.com', 'pass123', 'student'),
(5, 'Eva Green', 'eva@student.com', 'pass123', 'student'),
(6, 'Tom Ford', 'tom@teacher.com', 'pass123', 'teacher'),
(7, 'Sara White', 'sara@teacher.com', 'pass123', 'teacher'),
(8, 'Rami Register', 'rami@school.com', 'pass123', 'register');


INSERT INTO students (user_id, parent_contact) VALUES
(1, '123-456-7890'),
(2, '234-567-8901'),
(3, '345-678-9012'),
(4, '456-789-0123'),
(5, '567-890-1234');


INSERT INTO teachers (user_id, specialization) VALUES
(6, 'Mathematics'),
(7, 'Literature');


INSERT INTO courses (id, name, description) VALUES
(1, 'Algebra', 'Advanced algebra for high school'),
(2, 'Biology', 'Introduction to biological sciences'),
(3, 'Literature', 'World literature study'),
(4, 'Physics', 'High school level physics'),
(5, 'History', 'Modern world history'),
(6, 'Chemistry', 'Basic chemistry concepts'),
(7, 'English', 'English reading and writing'),
(8, 'Computer Science', 'Basics of computing');


INSERT INTO class_groups (id, name) VALUES
(1, '10th'),
(2, '11th literature'),
(3, '11th science'),
(4, '12th 11th literature'),
(5, '12th 11th science');


INSERT INTO course_assignments (id, course_id, teacher_id, class_group_id) VALUES
(1, 1, 6, 1),  -- Algebra for 10th
(2, 2, 6, 3),  -- Biology for 11th science
(3, 3, 7, 2),  -- Literature for 11th literature
(4, 4, 6, 5),  -- Physics for 12th 11th science
(5, 5, 7, 1),  -- History for 10th
(6, 6, 6, 5),  -- Chemistry for 12th 11th science
(7, 7, 7, 4),  -- English for 12th 11th literature
(8, 8, 6, 3);  -- CS for 11th science

INSERT INTO student_courses (student_id, course_assignment_id) VALUES
(1, 1), (1, 5),
(2, 2), (2, 8),
(3, 3), (3, 7),
(4, 4), (4, 6),
(5, 1), (5, 5);


INSERT INTO schedule (id, course_assignment_id, day_of_week, start_time, end_time) VALUES
(1, 1, 'Monday', '08:00:00', '09:30:00'),
(2, 2, 'Tuesday', '09:00:00', '10:30:00'),
(3, 3, 'Wednesday', '08:00:00', '09:30:00'),
(4, 4, 'Thursday', '10:00:00', '11:30:00'),
(5, 5, 'Friday', '08:00:00', '09:30:00'),
(6, 6, 'Monday', '10:00:00', '11:30:00'),
(7, 7, 'Tuesday', '11:00:00', '12:30:00'),
(8, 8, 'Wednesday', '13:00:00', '14:30:00');


INSERT INTO assignments (id, course_assignment_id, title, description, due_date) VALUES
(1, 1, 'Algebra Homework 1', 'Solve problems 1-10', '2025-05-10'),
(2, 2, 'Biology Report', 'Write a report on ecosystems', '2025-05-12'),
(3, 3, 'Literature Essay', 'Analyze Shakespeare', '2025-05-15'),
(4, 4, 'Physics Lab', 'Lab on Newton\'s Laws', '2025-05-18'),
(5, 5, 'History Timeline', 'Create a timeline of WWII', '2025-05-20'),
(6, 6, 'Chemistry Worksheet', 'Balance chemical equations', '2025-05-22'),
(7, 7, 'English Poem', 'Write an original poem', '2025-05-25'),
(8, 8, 'CS Project', 'Create a basic website', '2025-05-28');


INSERT INTO submissions (id, assignment_id, student_id, submission_text, submission_date, grade) VALUES
(1, 1, 1, 'Completed HW1', '2025-05-09 19:00:00', 90.0),
(2, 2, 2, 'Report on ecosystems', '2025-05-11 16:00:00', 85.5),
(3, 3, 3, 'Essay on Hamlet', '2025-05-14 13:00:00', 88.0),
(4, 4, 4, 'Physics lab report', '2025-05-17 10:30:00', 92.0),
(5, 5, 1, 'WWII timeline', '2025-05-19 20:00:00', 89.5),
(6, 6, 4, 'Balanced equations', '2025-05-21 17:00:00', 93.0),
(7, 7, 3, 'Poem about nature', '2025-05-24 14:00:00', 87.0),
(8, 8, 2, 'HTML website', '2025-05-27 21:00:00', 91.0);


INSERT INTO messages (id, course_assignment_id, sender_id, message_text, sent_at) VALUES
(1, 1, 6, 'Don’t forget your homework!', '2025-05-08 08:00:00'),
(2, 2, 6, 'Lab will be in room 5 today.', '2025-05-09 09:30:00'),
(3, 3, 7, 'Essay deadline extended.', '2025-05-10 10:15:00'),
(4, 4, 6, 'Bring calculators tomorrow.', '2025-05-11 14:00:00'),
(5, 5, 7, 'Test next Monday.', '2025-05-12 16:00:00'),
(6, 6, 6, 'Chemistry quiz announced.', '2025-05-13 11:00:00'),
(7, 7, 7, 'Read chapter 4 tonight.', '2025-05-14 17:30:00'),
(8, 8, 6, 'Final project requirements uploaded.', '2025-05-15 18:45:00');


INSERT INTO attendance (id, student_id, course_assignment_id, date, status) VALUES
(1, 1, 1, '2025-05-01', 'present'),
(2, 2, 2, '2025-05-01', 'absent'),
(3, 3, 3, '2025-05-01', 'present'),
(4, 4, 4, '2025-05-01', 'present'),
(5, 1, 5, '2025-05-01', 'present'),
(6, 4, 6, '2025-05-01', 'absent'),
(7, 3, 7, '2025-05-01', 'present'),
(8, 2, 8, '2025-05-01', 'present');





