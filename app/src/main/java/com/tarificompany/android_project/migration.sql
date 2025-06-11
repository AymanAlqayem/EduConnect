drop database androiddb;
create database androiddb;
use androiddb;


-- Table for Registrars
CREATE TABLE registrars
(
    registrar_id  INT PRIMARY KEY AUTO_INCREMENT,
    name          VARCHAR(50)         NOT NULL,
    email         VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255)        NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE classes
(
    class_id   INT PRIMARY KEY AUTO_INCREMENT,
    class_name ENUM ('10th', '11th literature', '11th science', '12th literature', '12th science') NOT NULL,
    created_by INT                                                                                 NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES registrars (registrar_id)
);

CREATE TABLE sections
(
    section_id   INT PRIMARY KEY AUTO_INCREMENT,
    class_id     INT         NOT NULL,
    section_name VARCHAR(50) NOT NULL,
    created_by   INT         NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES classes (class_id),
    FOREIGN KEY (created_by) REFERENCES registrars (registrar_id),
    UNIQUE (class_id, section_name)
);

CREATE TABLE students
(
    student_id    INT PRIMARY KEY AUTO_INCREMENT,
    first_name    VARCHAR(50)         NOT NULL,
    middle_name   VARCHAR(50)         NOT NULL,
    last_name     VARCHAR(50)         NOT NULL,
    parent_phone  VARCHAR(10) UNIQUE  NOT NULL,
    email         VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255)        NOT NULL,
    DOB           DATE,
    class_id      INT                 NOT NULL,
    section_id    INT                 NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES classes (class_id),
    FOREIGN KEY (section_id) REFERENCES sections (section_id)
);

-- Table for Teachers
CREATE TABLE teachers
(
    teacher_id    INT PRIMARY KEY AUTO_INCREMENT,
    name          VARCHAR(50)         NOT NULL,
    email         VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255)        NOT NULL,
    DOB           DATE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    gender        VARCHAR(10),
    phone         VARCHAR(10),
    notes         TEXT,
    is_active     BOOLEAN   DEFAULT TRUE -- Added status column for soft delete
);

CREATE TABLE teacher_sections
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    teacher_id  INT NOT NULL,
    class_id    INT NOT NULL,
    section_id  INT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES teachers (teacher_id),
    FOREIGN KEY (class_id) REFERENCES classes (class_id),
    FOREIGN KEY (section_id) REFERENCES sections (section_id),
    UNIQUE (teacher_id, class_id, section_id)
);

CREATE TABLE subjects
(
    subject_id   INT PRIMARY KEY AUTO_INCREMENT,
    subject_code VARCHAR(20) UNIQUE NOT NULL,
    subject_name VARCHAR(100)       NOT NULL,
    teacher_id   INT                NOT NULL,
    class_id     INT                NOT NULL,
    section_id   INT                NOT NULL,
    created_by   INT                NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES teachers (teacher_id),
    FOREIGN KEY (class_id) REFERENCES classes (class_id),
    FOREIGN KEY (section_id) REFERENCES sections (section_id),
    FOREIGN KEY (created_by) REFERENCES registrars (registrar_id),
    UNIQUE (class_id, section_id, subject_name)
);


CREATE TABLE schedules
(
    schedule_id INT PRIMARY KEY AUTO_INCREMENT,
    subject_id  INT                                                                                 NOT NULL,
    teacher_id  INT                                                                                 NOT NULL,
    class_id    INT                                                                                 NOT NULL,
    section_id  INT                                                                                 NOT NULL,
    day_of_week ENUM ('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday') NOT NULL,
    start_time  TIME                                                                                NOT NULL,
    end_time    TIME                                                                                NOT NULL,
    room        VARCHAR(50),
    created_by  INT                                                                                 NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (subject_id) REFERENCES subjects (subject_id),
    FOREIGN KEY (teacher_id) REFERENCES teachers (teacher_id),
    FOREIGN KEY (class_id) REFERENCES classes (class_id),
    FOREIGN KEY (section_id) REFERENCES sections (section_id),
    FOREIGN KEY (created_by) REFERENCES registrars (registrar_id)
);


-- Table for Student Enrollment in Subjects
CREATE TABLE enrollments
(
    enrollment_id   INT PRIMARY KEY AUTO_INCREMENT,
    student_id      INT  NOT NULL,
    subject_id      INT  NOT NULL,
    enrollment_date DATE NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students (student_id),
    FOREIGN KEY (subject_id) REFERENCES subjects (subject_id),
    UNIQUE (student_id, subject_id)
);

-- Table for Exam Marks
CREATE TABLE marks
(
    mark_id      INT PRIMARY KEY AUTO_INCREMENT,
    student_id   INT           NOT NULL,
    subject_id   INT           NOT NULL,
    teacher_id   INT           NOT NULL,
    exam_name    VARCHAR(100)  NOT NULL,
    score        DECIMAL(5, 2) NOT NULL,
    published_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students (student_id),
    FOREIGN KEY (subject_id) REFERENCES subjects (subject_id),
    FOREIGN KEY (teacher_id) REFERENCES teachers (teacher_id)
);

-- Table for Assignments
CREATE TABLE assignments
(
    assignment_id INT PRIMARY KEY AUTO_INCREMENT,
    subject_id    INT           NOT NULL,
    teacher_id    INT           NOT NULL,
    title         VARCHAR(100)  NOT NULL,
    description   TEXT,
    due_date      DATETIME      NOT NULL,
    max_score     DECIMAL(5, 2) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (subject_id) REFERENCES subjects (subject_id),
    FOREIGN KEY (teacher_id) REFERENCES teachers (teacher_id)
);

-- Table for Assignment Submissions
CREATE TABLE assignment_submissions
(
    submission_id   INT PRIMARY KEY AUTO_INCREMENT,
    assignment_id   INT NOT NULL,
    student_id      INT NOT NULL,
    submission_file VARCHAR(255),
    submission_text TEXT,
    submitted_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    score           DECIMAL(5, 2),
    graded_by       INT,
    FOREIGN KEY (assignment_id) REFERENCES assignments (assignment_id),
    FOREIGN KEY (student_id) REFERENCES students (student_id),
    FOREIGN KEY (graded_by) REFERENCES teachers (teacher_id)
);

-- Table for Announcements
CREATE TABLE announcements
(
    announcement_id INT PRIMARY KEY AUTO_INCREMENT,
    title           VARCHAR(100)                       NOT NULL,
    content         TEXT                               NOT NULL,
    created_by      INT                                NOT NULL,
    target_role     ENUM ('Student', 'Teacher', 'All') NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES registrars (registrar_id)
);


CREATE TABLE messages
(
    message_id   INT PRIMARY KEY AUTO_INCREMENT,
    sender_id    INT                         NOT NULL,
    sender_role  ENUM ('Teacher', 'Student') NOT NULL,
    subject      VARCHAR(255),               -- ✅ New: subject or title
    content      TEXT                        NOT NULL,
    sent_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    parent_id    INT,                        -- Optional: for reply threads
    FOREIGN KEY (parent_id) REFERENCES messages (message_id)
);



CREATE TABLE message_recipients
(
    id             INT PRIMARY KEY AUTO_INCREMENT,
    message_id     INT NOT NULL,
    recipient_id   INT,                         -- Nullable for section-wide messages
    recipient_role ENUM ('Teacher', 'Student'), -- Optional; helps you know who to notify
    class_id       INT,                         -- Nullable
    section_id     INT,                         -- Nullable
    FOREIGN KEY (message_id) REFERENCES messages (message_id),
    FOREIGN KEY (class_id) REFERENCES classes (class_id),
    FOREIGN KEY (section_id) REFERENCES sections (section_id)
);
CREATE TABLE teacher_class_subject (
    id INT AUTO_INCREMENT PRIMARY KEY,
    teacher_id INT NOT NULL,
    class_id INT NOT NULL,
    section_id INT NOT NULL,
    subject_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_teacher FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE,
    CONSTRAINT fk_class FOREIGN KEY (class_id) REFERENCES classes(id) ON DELETE CASCADE,
    CONSTRAINT fk_section FOREIGN KEY (section_id) REFERENCES sections(id) ON DELETE CASCADE,
    CONSTRAINT fk_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE
);



# ===========================   DATA  ===================================


-- Insert Registrars
INSERT INTO registrars (name, email, password_hash)
VALUES ('John Smith', 'john.smith@school.com', 'hashed_password_1'),
       ('Emma Wilson', 'emma.wilson@school.com', 'hashed_password_2'),
       ('Michael Brown', 'michael.brown@school.com', 'hashed_password_3');

-- Insert Classes
INSERT INTO classes (class_name, created_by)
VALUES ('10th', 1),
       ('11th literature', 1),
       ('11th science', 1),
       ('12th literature', 2),
       ('12th science', 2);

-- Insert Sections
INSERT INTO sections (class_id, section_name, created_by)
VALUES (1, 'Section A', 1),
       (1, 'Section B', 1),
       (1, 'Section C', 1),
       (2, 'Section A', 1),
       (2, 'Section B', 1),
       (2, 'Section C', 1),
       (3, 'Section A', 1),
       (3, 'Section B', 1),
       (3, 'Section C', 1),
       (4, 'Section A', 2),
       (4, 'Section B', 2),
       (4, 'Section C', 2),
       (5, 'Section A', 2),
       (5, 'Section B', 2),
       (5, 'Section C', 2);

-- Insert Teachers
INSERT INTO teachers (name, email, password_hash, DOB, gender, phone, notes, is_active)
VALUES ('Alice Johnson', 'alice.johnson@school.com', 'hashed_password_4', '1975-03-15', 'Female', '1234567890',
        'Math specialist', TRUE),
       ('Robert Davis', 'robert.davis@school.com', 'hashed_password_5', '1980-07-22', 'Male', '2345678901',
        'Physics expert', TRUE),
       ('Sarah Lee', 'sarah.lee@school.com', 'hashed_password_6', '1978-11-30', 'Female', '3456789012',
        'Literature enthusiast', TRUE),
       ('David Kim', 'david.kim@school.com', 'hashed_password_7', '1985-05-10', 'Male', '4567890123',
        'Chemistry teacher', TRUE),
       ('Laura Chen', 'laura.chen@school.com', 'hashed_password_8', '1972-09-18', 'Female', '5678901234',
        'History specialist', TRUE),
       ('James Patel', 'james.patel@school.com', 'hashed_password_9', '1983-01-25', 'Male', '6789012345',
        'Biology teacher', TRUE);

-- Insert Teacher Sections
INSERT INTO teacher_sections (teacher_id, class_id, section_id)
VALUES (1, 1, 1),
       (1, 1, 2),
       (1, 2, 1),
       (1, 3, 1), -- Alice: Math across multiple classes
       (2, 3, 1),
       (2, 3, 2),
       (2, 5, 1), -- Robert: Physics for science classes
       (3, 2, 1),
       (3, 2, 2),
       (3, 4, 1),
       (3, 4, 2), -- Sarah: Literature classes
       (4, 3, 2),
       (4, 5, 2),
       (4, 5, 3), -- David: Chemistry for science classes
       (5, 1, 3),
       (5, 2, 3),
       (5, 4, 3), -- Laura: History across classes
       (6, 3, 3),
       (6, 5, 1),
       (6, 5, 3);
-- James: Biology for science classes

-- Insert Subjects
INSERT INTO subjects (subject_code, subject_name, teacher_id, class_id, section_id, created_by)
VALUES ('MATH101', 'Mathematics', 1, 1, 1, 1),
       ('MATH102', 'Mathematics', 1, 1, 2, 1),
       ('MATH201', 'Mathematics', 1, 2, 1, 1),
       ('MATH301', 'Mathematics', 1, 3, 1, 1),
       ('PHY301', 'Physics', 2, 3, 1, 1),
       ('PHY302', 'Physics', 2, 3, 2, 1),
       ('PHY501', 'Physics', 2, 5, 1, 2),
       ('LIT201', 'Literature', 3, 2, 1, 1),
       ('LIT202', 'Literature', 3, 2, 2, 1),
       ('LIT401', 'Literature', 3, 4, 1, 2),
       ('LIT402', 'Literature', 3, 4, 2, 2),
       ('CHEM302', 'Chemistry', 4, 3, 2, 1),
       ('CHEM501', 'Chemistry', 4, 5, 2, 2),
       ('CHEM502', 'Chemistry', 4, 5, 3, 2),
       ('HIST103', 'History', 5, 1, 3, 1),
       ('HIST203', 'History', 5, 2, 3, 1),
       ('HIST403', 'History', 5, 4, 3, 2),
       ('BIO303', 'Biology', 6, 3, 3, 1),
       ('BIO501', 'Biology', 6, 5, 1, 2),
       ('BIO502', 'Biology', 6, 5, 3, 2),
       ('ENG101', 'English', 3, 1, 1, 1),
       ('ENG102', 'English', 3, 1, 2, 1);

-- Insert Schedules (No classes on Sunday or Friday)
INSERT INTO schedules (subject_id, teacher_id, class_id, section_id, day_of_week, start_time, end_time, room,
                       created_by)
VALUES (1, 1, 1, 1, 'Monday', '08:00:00', '09:00:00', 'Room 101', 1),
       (1, 1, 1, 1, 'Wednesday', '08:00:00', '09:00:00', 'Room 101', 1),
       (2, 1, 1, 2, 'Tuesday', '09:00:00', '10:00:00', 'Room 102', 1),
       (2, 1, 1, 2, 'Thursday', '09:00:00', '10:00:00', 'Room 102', 1),
       (3, 1, 2, 1, 'Monday', '10:00:00', '11:00:00', 'Room 201', 1),
       (4, 1, 3, 1, 'Tuesday', '08:00:00', '09:00:00', 'Room 301', 1),
       (5, 2, 3, 1, 'Monday', '09:00:00', '10:00:00', 'Room 302', 1),
       (6, 2, 3, 2, 'Wednesday', '10:00:00', '11:00:00', 'Room 302', 1),
       (7, 2, 5, 1, 'Thursday', '08:00:00', '09:00:00', 'Room 501', 2),
       (8, 3, 2, 1, 'Tuesday', '10:00:00', '11:00:00', 'Room 202', 1),
       (9, 3, 2, 2, 'Thursday', '10:00:00', '11:00:00', 'Room 203', 1),
       (10, 3, 4, 1, 'Monday', '11:00:00', '12:00:00', 'Room 401', 2),
       (11, 3, 4, 2, 'Wednesday', '11:00:00', '12:00:00', 'Room 402', 2),
       (12, 4, 3, 2, 'Tuesday', '11:00:00', '12:00:00', 'Room 303', 1),
       (13, 4, 5, 2, 'Monday', '12:00:00', '13:00:00', 'Room 502', 2),
       (14, 4, 5, 3, 'Thursday', '11:00:00', '12:00:00', 'Room 503', 2),
       (15, 5, 1, 3, 'Monday', '09:00:00', '10:00:00', 'Room 103', 1),
       (16, 5, 2, 3, 'Wednesday', '09:00:00', '10:00:00', 'Room 204', 1),
       (17, 5, 4, 3, 'Tuesday', '12:00:00', '13:00:00', 'Room 403', 2),
       (18, 6, 3, 3, 'Thursday', '09:00:00', '10:00:00', 'Room 304', 1),
       (19, 6, 5, 1, 'Monday', '10:00:00', '11:00:00', 'Room 504', 2),
       (20, 6, 5, 3, 'Wednesday', '12:00:00', '13:00:00', 'Room 505', 2),
       (21, 3, 1, 1, 'Tuesday', '08:00:00', '09:00:00', 'Room 104', 1),
       (22, 3, 1, 2, 'Thursday', '08:00:00', '09:00:00', 'Room 105', 1);

-- Insert Students
INSERT INTO students (first_name, middle_name, last_name, parent_phone, email, password_hash, DOB, class_id, section_id)
VALUES ('Emily', 'Jane', 'Taylor', '7890123456', 'emily.taylor@school.com', 'hashed_password_10', '2008-04-12', 1, 1),
       ('Noah', 'James', 'Wilson', '8901234567', 'noah.wilson@school.com', 'hashed_password_11', '2008-06-15', 1, 2),
       ('Sophia', 'Marie', 'Clark', '9012345678', 'sophia.clark@school.com', 'hashed_password_12', '2007-09-20', 2, 1),
       ('Liam', 'Thomas', 'Lewis', '0123456789', 'liam.lewis@school.com', 'hashed_password_13', '2007-02-10', 2, 2),
       ('Olivia', 'Grace', 'Walker', '1234567891', 'olivia.walker@school.com', 'hashed_password_14', '2006-11-05', 4,
        1),
       ('Ethan', 'Michael', 'Hall', '2345678902', 'ethan.hall@school.com', 'hashed_password_15', '2006-03-22', 4, 2),
       ('Ava', 'Rose', 'Allen', '3456789013', 'ava.allen@school.com', 'hashed_password_16', '2007-07-18', 3, 1),
       ('Mason', 'David', 'Young', '4567890124', 'mason.young@school.com', 'hashed_password_17', '2007-01-30', 3, 2),
       ('Isabella', 'Anne', 'King', '5678901235', 'isabella.king@school.com', 'hashed_password_18', '2006-05-25', 5, 1),
       ('Jacob', 'Paul', 'Wright', '6789012346', 'jacob.wright@school.com', 'hashed_password_19', '2006-08-14', 5, 2);

-- Insert Enrollments
INSERT INTO enrollments (student_id, subject_id, enrollment_date)
VALUES (1, 1, '2025-01-10'),
       (1, 21, '2025-01-10'),
       (2, 2, '2025-01-10'),
       (2, 22, '2025-01-10'),
       (3, 3, '2025-01-10'),
       (3, 8, '2025-01-10'),
       (4, 9, '2025-01-10'),
       (5, 10, '2025-01-10'),
       (6, 11, '2025-01-10'),
       (7, 5, '2025-01-10'),
       (7, 12, '2025-01-10'),
       (8, 6, '2025-01-10'),
       (8, 12, '2025-01-10'),
       (9, 7, '2025-01-10'),
       (9, 13, '2025-01-10'),
       (9, 19, '2025-01-10'),
       (10, 14, '2025-01-10'),
       (10, 20, '2025-01-10');

-- Insert Marks
INSERT INTO marks (student_id, subject_id, teacher_id, exam_name, score)
VALUES (1, 1, 1, 'Midterm', 85.50),
       (2, 2, 1, 'Midterm', 78.00),
       (3, 3, 1, 'Midterm', 92.00),
       (4, 9, 3, 'Midterm', 88.50),
       (5, 10, 3, 'Midterm', 90.00),
       (7, 5, 2, 'Midterm', 79.50),
       (9, 7, 2, 'Midterm', 82.00);

-- Insert Assignments
INSERT INTO assignments (subject_id, teacher_id, title, description, due_date, max_score)
VALUES (1, 1, 'Math Assignment 1', 'Solve quadratic equations', '2025-06-20 23:59:00', 100.00),
       (5, 2, 'Physics Lab Report', 'Write a report on motion experiment', '2025-06-25 23:59:00', 100.00),
       (8, 3, 'Literature Essay', 'Analyze a poem', '2025-06-22 23:59:00', 100.00),
       (12, 4, 'Chemistry Lab', 'Complete chemical reaction analysis', '2025-06-23 23:59:00', 100.00);

-- Insert Assignment Submissions
INSERT INTO assignment_submissions (assignment_id, student_id, submission_text, score, graded_by)
VALUES (1, 1, 'Submitted quadratic equations solutions', 90.00, 1),
       (2, 7, 'Submitted motion experiment report', 85.00, 2),
       (3, 3, 'Submitted poem analysis', 88.00, 3),
       (4, 8, 'Submitted chemical reaction analysis', 92.00, 4);

-- Insert Announcements
INSERT INTO announcements (title, content, created_by, target_role)
VALUES ('School Holiday', 'No classes on June 15th due to holiday.', 1, 'All'),
       ('Exam Schedule', 'Midterm exams start on June 20th.', 2, 'Student'),
       ('Teacher Meeting', 'Staff meeting on June 12th at 3 PM.', 3, 'Teacher');





INSERT INTO messages (message_id, sender_id, sender_role, subject, content, parent_id, sent_at) VALUES
-- Top-level messages from teachers to sections
(1, 1, 'Teacher', 'Upcoming Math Test', 'Reminder: Math test on Monday.', NULL, NOW()),
(2, 2, 'Teacher', 'Literature Class Prep', 'Bring your literature books tomorrow.', NULL, NOW()),
(3, 3, 'Teacher', 'Lab Report Due', 'Lab report due Friday.', NULL, NOW()),
(4, 4, 'Teacher', 'Poetry Reading Activity', 'Poetry reading in next class.', NULL, NOW()),
(5, 5, 'Teacher', 'Science Project Submission', 'Don’t forget to submit your project.', NULL, NOW()),

-- Student replies (child messages)
(6, 1, 'Student', 'Test', 'Thank you for the reminder!', 1, NOW()),
(7, 2, 'Student', 'Test2', 'Will bring it. Thanks.', 2, NOW()),
(8, 3, 'Student', 'Test3', 'Is it okay to submit in PDF format?', 3, NOW()),
(9, 4, 'Student', 'Test4', 'Can we prepare a group performance?', 4, NOW()),
(10, 5, 'Student', 'Test5', 'Project is almost done!', 5, NOW()),

-- New messages from students to teachers
(11, 6, 'Student', 'Quick Question', 'Can I meet you after class tomorrow?', NULL, NOW()),
(12, 7, 'Student', 'Class Absence Notice', 'I will be absent tomorrow.', NULL, NOW()),
(13, 8, 'Student', 'Homework Help', 'Can I get help with the homework?', NULL, NOW()),
(14, 9, 'Student', 'Notebook Issue', 'I lost my literature notebook.', NULL, NOW()),
(15, 10, 'Student', 'Lab Book Check', 'Will you be checking our lab books today?', NULL, NOW());



INSERT INTO message_recipients (message_id, recipient_id, recipient_role, class_id, section_id) VALUES
-- Section-wide teacher messages
(1, NULL, NULL, 1, 1),  -- 10th-A
(2, NULL, NULL, 2, 5),  -- 11th literature-B
(3, NULL, NULL, 3, 9),  -- 11th science-C
(4, NULL, NULL, 4, 10), -- 12th literature-A
(5, NULL, NULL, 5, 14), -- 12th science-B

-- Student replies to teachers
(6, 1, 'Teacher', NULL, NULL),
(7, 2, 'Teacher', NULL, NULL),
(8, 3, 'Teacher', NULL, NULL),
(9, 4, 'Teacher', NULL, NULL),
(10, 5, 'Teacher', NULL, NULL),

-- New student-to-teacher direct messages
(11, 1, 'Teacher', NULL, NULL),
(12, 2, 'Teacher', NULL, NULL),
(13, 3, 'Teacher', NULL, NULL),
(14, 4, 'Teacher', NULL, NULL),
(15, 5, 'Teacher', NULL, NULL);
INSERT INTO teacher_class_subject (teacher_id, class_id, section_id, subject_id) VALUES
(1, 1, 1, 1),
(1, 1, 2, 2),
(1, 2, 1, 3),
(1, 3, 1, 4),
(2, 3, 1, 5),
(2, 3, 2, 6),
(2, 5, 1, 7),
(3, 2, 1, 8),
(3, 2, 2, 9),
(3, 4, 1, 10),
(3, 4, 2, 11),
(4, 3, 2, 12),
(4, 5, 2, 13),
(4, 5, 3, 14),
(5, 1, 3, 15),
(5, 2, 3, 16),
(5, 4, 3, 17),
(6, 3, 3, 18),
(6, 5, 1, 19),
(6, 5, 3, 20),
(3, 1, 1, 21),
(3, 1, 2, 22);




SELECT *
FROM messages
WHERE sender_id = 3
  AND sender_role = 'Teacher';


SELECT *
FROM messages
WHERE sender_id = 5
  AND sender_role = 'Student';

SELECT m.message_id, m.sender_id, m.sender_role, m.content, m.sent_at
FROM message_recipients r
         JOIN messages m ON r.message_id = m.message_id
WHERE r.class_id = 1
  AND r.section_id = 1;

SELECT * from assignments


#Student Schedule.
SELECT
    s.day_of_week,
    s.start_time,
    s.end_time,
    subj.subject_name,
    t.name AS teacher_name,
    s.room
FROM students st
JOIN schedules s ON st.class_id = s.class_id AND st.section_id = s.section_id
JOIN subjects subj ON s.subject_id = subj.subject_id
JOIN teachers t ON s.teacher_id = t.teacher_id
WHERE st.student_id = 2
ORDER BY FIELD(s.day_of_week, 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'), s.start_time;

#Teacher Schedule.
SELECT s.day_of_week,
       s.start_time,
       s.end_time,
       c.class_name,
       sec.section_name,
       subj.subject_name,
       s.room
FROM schedules s
         JOIN classes c ON s.class_id = c.class_id
         JOIN sections sec ON s.section_id = sec.section_id
         JOIN subjects subj ON s.subject_id = subj.subject_id
WHERE s.teacher_id = 11 -- Replace with the actual teacher_id
ORDER BY FIELD(s.day_of_week, 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'),
         s.start_time;



SELECT * FROM registrars;
SELECT * FROM teachers;
SELECT * FROM students;