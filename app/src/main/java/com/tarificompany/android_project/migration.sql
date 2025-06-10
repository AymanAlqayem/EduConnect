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
    notes         TEXT
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



# ===========================   DATA  ===================================


# ===========================   Registrar  ===================================

INSERT INTO registrars (name, email, password_hash)
VALUES ('Rania Khalil', 'rania.khalil@example.com', 'hashed_pw_1'),
       ('Omar Saeed', 'omar.saeed@example.com', 'hashed_pw_2'),
       ('Ayman', 'ayman@example.com', '1220040');


# ===========================   Teachers  ===================================
-- Teachers
INSERT INTO teachers (name, email, password_hash, DOB)
VALUES ('Nadia Salem', 'nadia.salem@example.com', 'hashed_pw_3', '1980-04-12'),
       ('Khaled Nasser', 'khaled.nasser@example.com', 'hashed_pw_4', '1975-11-22'),
       ('Ayman Teacher', 'ayman@example.com', '1220040', '1975-11-22');


# ===========================   Classes  ===================================

-- Insert classes
INSERT INTO classes (class_name, created_by)
VALUES ('10th', 1),
       ('11th literature', 1),
       ('11th science', 1),
       ('12th literature', 1),
       ('12th science', 1);


# ===========================   sections  ===================================
INSERT INTO sections (class_id, section_name, created_by)
VALUES (1, 'Section A', 1),
       (1, 'Section B', 1),

       (2, 'Section A', 1),
       (2, 'Section B', 1),

       (3, 'Section A', 1),
       (3, 'Section B', 1),

       (4, 'Section A', 1),
       (4, 'Section B', 1),

       (5, 'Section A', 1),
       (5, 'Section B', 1);


# ==================================   Students  ==========================================
-- Insert students
INSERT INTO students (first_name, middle_name, last_name, parent_phone, email, password_hash, DOB, class_id, section_id)
VALUES
-- Class 1: 10th (Section A: section_id=1, Section B: section_id=2)
('Amit', 'Kumar', 'Sharma', '9876543210', 'amit.sharma@example.com', 'hashed_password_123', '2009-03-15', 1, 1),
('Priya', 'Anil', 'Verma', '9876543211', 'priya.verma@example.com', 'hashed_password_123', '2009-07-22', 1, 1),
('Rahul', 'Suresh', 'Patel', '9876543212', 'rahul.patel@example.com', 'hashed_password_123', '2009-05-10', 1, 2),
('Sneha', 'Ravi', 'Mehta', '9876543213', 'sneha.mehta@example.com', 'hashed_password_123', '2009-09-18', 1, 2),

-- Class 2: 11th literature (Section A: section_id=3, Section B: section_id=4)
('Vikram', 'Arun', 'Singh', '9876543214', 'vikram.singh@example.com', 'hashed_password_123', '2008-11-05', 2, 3),
('Ananya', 'Vijay', 'Gupta', '9876543215', 'ananya.gupta@example.com', 'hashed_password_123', '2008-12-12', 2, 3),
('Rohan', 'Kishore', 'Joshi', '9876543216', 'rohan.joshi@example.com', 'hashed_password_123', '2008-10-20', 2, 4),
('Kavya', 'Mohan', 'Nair', '9876543217', 'kavya.nair@example.com', 'hashed_password_123', '2008-08-30', 2, 4),

-- Class 3: 11th science (Section A: section_id=5, Section B: section_id=6)
('Arjun', 'Deepak', 'Reddy', '9876543218', 'arjun.reddy@example.com', 'hashed_password_123', '2008-06-25', 3, 5),
('Neha', 'Prakash', 'Kumar', '9876543219', 'neha.kumar@example.com', 'hashed_password_123', '2008-04-15', 3, 5),
('Siddharth', 'Vikas', 'Rao', '9876543220', 'siddharth.rao@example.com', 'hashed_password_123', '2008-07-10', 3, 6),
('Pooja', 'Sanjay', 'Desai', '9876543221', 'pooja.desai@example.com', 'hashed_password_123', '2008-09-05', 3, 6),

-- Class 4: 12th literature (Section A: section_id=7, Section B: section_id=8)
('Aditya', 'Ramesh', 'Iyer', '9876543222', 'aditya.iyer@example.com', 'hashed_password_123', '2007-02-28', 4, 7),
('Shreya', 'Ajay', 'Pandey', '9876543223', 'shreya.pandey@example.com', 'hashed_password_123', '2007-03-17', 4, 7),
('Karan', 'Vinod', 'Malhotra', '9876543224', 'karan.malhotra@example.com', 'hashed_password_123', '2007-05-22', 4, 8),
('Riya', 'Sunil', 'Chopra', '9876543225', 'riya.chopra@example.com', 'hashed_password_123', '2007-06-30', 4, 8),

-- Class 5: 12th science (Section A: section_id=9, Section B: section_id=10)
('Vishal', 'Nitin', 'Bhatia', '9876543226', 'vishal.bhatia@example.com', 'hashed_password_123', '2007-01-10', 5, 9),
('Meera', 'Rajesh', 'Saxena', '9876543227', 'meera.saxena@example.com', 'hashed_password_123', '2007-04-20', 5, 9),
('Nikhil', 'Anand', 'Kapoor', '9876543228', 'nikhil.kapoor@example.com', 'hashed_password_123', '2007-08-15', 5, 10),
('Tanya', 'Mukesh', 'Agarwal', '9876543229', 'tanya.agarwal@example.com', 'hashed_password_123', '2007-09-25', 5, 10);


# ======================================= Teachers =====================================================

INSERT INTO teachers (name, email, password_hash, DOB)
VALUES ('Dr. Anil Sharma', 'anil.sharma@example.com', 'hashed_password_123', '1978-04-12'),   -- General (10th)
       ('Prof. Meena Gupta', 'meena.gupta@example.com', 'hashed_password_123', '1982-07-19'), -- Literature
       ('Ms. Priya Nair', 'priya.nair@example.com', 'hashed_password_123', '1985-02-25'),     -- Science
       ('Mr. Rajesh Kumar', 'rajesh.kumar@example.com', 'hashed_password_123', '1980-11-03'), -- Literature
       ('Dr. Vikram Singh', 'vikram.singh@example.com', 'hashed_password_123', '1976-09-10'), -- Science
       ('Mrs. Sunita Patel', 'sunita.patel@example.com', 'hashed_password_123', '1983-12-15'); -- General (10th)


INSERT INTO teacher_sections (teacher_id, class_id, section_id)
VALUES
-- Class 1: 10th (Section A: section_id=1, Section B: section_id=2)

(1, 1, 1), -- Dr. Anil Sharma for 10th Section A
(1, 1, 2), -- Dr. Anil Sharma for 10th Section B
(6, 1, 1), -- Mrs. Sunita Patel also for 10th Section A

-- Class 2: 11th literature (Section A: section_id=3, Section B: section_id=4)
(2, 2, 3), -- Prof. Meena Gupta for 11th literature Section A
(2, 2, 4), -- Prof. Meena Gupta for 11th literature Section B
(4, 2, 4), -- Mr. Rajesh Kumar also for 11th literature Section B

-- Class 3: 11th science (Section A: section_id=5, Section B: section_id=6)
(3, 3, 5), -- Ms. Priya Nair for 11th science Section A
(3, 3, 6), -- Ms. Priya Nair for 11th science Section B

-- Class 4: 12th literature (Section A: section_id=7, Section B: section_id=8)
(4, 4, 7), -- Mr. Rajesh Kumar for 12th literature Section A
(2, 4, 8), -- Prof. Meena Gupta for 12th literature Section B

-- Class 5: 12th science (Section A: section_id=9, Section B: section_id=10)
(5, 5, 9), -- Dr. Vikram Singh for 12th science Section A
(5, 5, 10);
-- Dr. Vikram Singh for 12th science Section B


# ============================= Subjects ==============================================

-- 10th Grade
INSERT INTO subjects (subject_code, subject_name, teacher_id, class_id, section_id, created_by)
VALUES ('MTH10A', 'Mathematics', 1, 1, 1, 1),
       ('SCI10A', 'Science', 2, 1, 1, 1),
       ('ENG10A', 'English', 3, 1, 1, 1),
       ('MTH10B', 'Mathematics', 1, 1, 2, 1),
       ('SCI10B', 'Science', 2, 1, 2, 1),
       ('ENG10B', 'English', 3, 1, 2, 1);

-- 11th Literature
INSERT INTO subjects (subject_code, subject_name, teacher_id, class_id, section_id, created_by)
VALUES ('LIT11A', 'Literature', 4, 2, 3, 1),
       ('HIS11A', 'History', 5, 2, 3, 1),
       ('ENG11A', 'English', 3, 2, 3, 1),
       ('LIT11B', 'Literature', 4, 2, 4, 1),
       ('HIS11B', 'History', 5, 2, 4, 1),
       ('ENG11B', 'English', 3, 2, 4, 1);

-- 11th Science
INSERT INTO subjects (subject_code, subject_name, teacher_id, class_id, section_id, created_by)
VALUES ('PHY11A', 'Physics', 6, 3, 5, 1),
       ('CHE11A', 'Chemistry', 7, 3, 5, 1),
       ('BIO11A', 'Biology', 8, 3, 5, 1),
       ('PHY11B', 'Physics', 6, 3, 6, 1),
       ('CHE11B', 'Chemistry', 7, 3, 6, 1),
       ('BIO11B', 'Biology', 8, 3, 6, 1);

-- 12th Literature
INSERT INTO subjects (subject_code, subject_name, teacher_id, class_id, section_id, created_by)
VALUES ('LIT12A', 'Literature', 4, 4, 7, 1),
       ('PHI12A', 'Philosophy', 9, 4, 7, 1),
       ('ENG12A', 'English', 3, 4, 7, 1),
       ('LIT12B', 'Literature', 4, 4, 8, 1),
       ('PHI12B', 'Philosophy', 9, 4, 8, 1),
       ('ENG12B', 'English', 3, 4, 8, 1);

-- 12th Science
INSERT INTO subjects (subject_code, subject_name, teacher_id, class_id, section_id, created_by)
VALUES ('PHY12A', 'Physics', 6, 5, 9, 1),
       ('CHE12A', 'Chemistry', 7, 5, 9, 1),
       ('BIO12A', 'Biology', 8, 5, 9, 1),
       ('PHY12B', 'Physics', 6, 5, 10, 1),
       ('CHE12B', 'Chemistry', 7, 5, 10, 1),
       ('BIO12B', 'Biology', 8, 5, 10, 1);


# ===========================   Schedules  ===================================

-- Schedule for Math for 10th A by Teacher 1
INSERT INTO schedules (subject_id, teacher_id, class_id, section_id, day_of_week, start_time, end_time, room,
                       created_by, created_at)
VALUES (1, 1, 1, 1, 'Monday', '08:00:00', '09:00:00', 'Room A1', 1, '2025-06-08 08:00:00');

-- Schedule for Physics for 11th Science A by Teacher 2
INSERT INTO schedules (subject_id, teacher_id, class_id, section_id, day_of_week, start_time, end_time, room,
                       created_by, created_at)
VALUES (2, 2, 2, 2, 'Tuesday', '09:00:00', '10:00:00', 'Room B1', 1, '2025-06-08 08:05:00');

-- Schedule for Literature for 11th Literature B by Teacher 3
INSERT INTO schedules (subject_id, teacher_id, class_id, section_id, day_of_week, start_time, end_time, room,
                       created_by, created_at)
VALUES (3, 3, 3, 3, 'Wednesday', '10:00:00', '11:00:00', 'Room C1', 1, '2025-06-08 08:10:00');

-- Schedule for Chemistry for 12th Science A by Teacher 4
INSERT INTO schedules (subject_id, teacher_id, class_id, section_id, day_of_week, start_time, end_time, room,
                       created_by, created_at)
VALUES (4, 4, 4, 4, 'Thursday', '11:00:00', '12:00:00', 'Room D1', 1, '2025-06-08 08:15:00');

-- Schedule for Biology for 12th Literature A by Teacher 5
INSERT INTO schedules (subject_id, teacher_id, class_id, section_id, day_of_week, start_time, end_time, room,
                       created_by, created_at)
VALUES (5, 5, 5, 1, 'Friday', '12:00:00', '13:00:00', 'Room E1', 1, '2025-06-08 08:20:00');

-- Schedule for Math for 10th B by Teacher 6
INSERT INTO schedules (subject_id, teacher_id, class_id, section_id, day_of_week, start_time, end_time, room,
                       created_by, created_at)
VALUES (6, 6, 1, 2, 'Saturday', '13:00:00', '14:00:00', 'Room F1', 1, '2025-06-08 08:25:00');

-- Schedule for Physics for 11th Science B by Teacher 7
INSERT INTO schedules (subject_id, teacher_id, class_id, section_id, day_of_week, start_time, end_time, room,
                       created_by, created_at)
VALUES (7, 7, 2, 3, 'Monday', '08:00:00', '09:00:00', 'Room A1', 1, '2025-06-08 08:30:00');

-- Schedule for Literature for 11th Literature A by Teacher 8
INSERT INTO schedules (subject_id, teacher_id, class_id, section_id, day_of_week, start_time, end_time, room,
                       created_by, created_at)
VALUES (8, 8, 3, 4, 'Tuesday', '09:00:00', '10:00:00', 'Room B1', 1, '2025-06-08 08:35:00');

-- Schedule for Chemistry for 12th Science B by Teacher 9
INSERT INTO schedules (subject_id, teacher_id, class_id, section_id, day_of_week, start_time, end_time, room,
                       created_by, created_at)
VALUES (9, 9, 4, 1, 'Wednesday', '10:00:00', '11:00:00', 'Room C1', 1, '2025-06-08 08:40:00');


# ======================================= enrollments  =====================================================

-- Enrollments
INSERT INTO enrollments (student_id, subject_id, enrollment_date)
VALUES (1, 10, '2025-06-01'),
       (1, 9, '2025-06-01'),
       (1, 5, '2025-06-01'),
       (2, 5, '2025-06-01'),
       (2, 9, '2025-06-01');


# ======================================= marks  =====================================================

-- Exam Marks
INSERT INTO marks (student_id, subject_id, teacher_id, exam_name, score, published_at)
VALUES (1, 10, 1, 'Mid Term Exam', 87.27, '2025-06-08 20:56:38'),
       (1, 9, 9, 'Mid Term Exam', 64.85, '2025-06-08 20:56:38'),
       (1, 5, 5, 'Mid Term Exam', 58.69, '2025-06-08 20:56:38'),
       (2, 5, 5, 'Mid Term Exam', 93.13, '2025-06-08 20:56:38'),
       (2, 9, 9, 'Mid Term Exam', 72.75, '2025-06-08 20:56:38');


# ======================================= assignments  =====================================================

INSERT INTO assignments (subject_id, teacher_id, title, description, due_date, max_score, created_at)
VALUES (1, 1, 'Assignment for Subject 1', 'Complete the exercises for subject 1.', '2025-06-15 20:56:38', 100.00,
        '2025-06-08 20:56:38'),
       (2, 2, 'Assignment for Subject 2', 'Complete the exercises for subject 2.', '2025-06-15 20:56:38', 100.00,
        '2025-06-08 20:56:38'),
       (3, 3, 'Assignment for Subject 3', 'Complete the exercises for subject 3.', '2025-06-15 20:56:38', 100.00,
        '2025-06-08 20:56:38');

# ======================================= assignment_submissions  =====================================================
-- Assignment Submissions
INSERT INTO assignment_submissions (assignment_id, student_id, submission_file, submission_text, submitted_at, score,
                                    graded_by)
VALUES (1, 1, 'file_1_1.pdf', 'Submitted via portal.', '2025-06-08 20:56:38', 96.71, 1),
       (1, 2, 'file_1_2.pdf', 'Submitted via portal.', '2025-06-08 20:56:38', 94.89, 1),
       (1, 3, 'file_1_3.pdf', 'Submitted via portal.', '2025-06-08 20:56:38', 89.47, 1);


# ======================================= announcements  =====================================================
INSERT INTO announcements (title, content, created_by, target_role)
VALUES ('Exam Notice', 'Mid-term exams will start from next week.', 1, 'All'),
       ('Assignment Deadline', 'Submit your assignments by the due date.', 1, 'Student'),
       ('Meeting', 'Staff meeting scheduled on Friday.', 1, 'Teacher');


# =================================================================================================================

INSERT INTO teachers (name, email, password_hash, DOB)
VALUES ('Dr. Ayman Nabil', 'ayman2004@example.com', '1220040', '2004-06-2');



SELECT *
FROM registrars;
SELECT *
FROM classes;
SELECT *
FROM sections;
SELECT *
FROM teachers;

SELECT *
FROM subjects;



SELECT *
FROM students;
SELECT *
FROM teacher_sections;



SELECT DISTINCT subject_name
FROM subjects










