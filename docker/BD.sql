CREATE DATABASE IF NOT EXISTS sks;
USE sks;
CREATE TABLE Schedule
(id SMALLINT NOT NULL AUTO_INCREMENT, 
 teacher_name VARCHAR(100),
 subject VARCHAR(100),
 day ENUM('M', 'T', 'W', 'S', 'F'),
 group_of_students VARCHAR(8),
 time_of_lesson VARCHAR(10),
 classroom_number VARCHAR(5),
 form_of_studying ENUM ('offline', 'online'),
 week_type ENUM ('знаменник', 'чисельник', 'обидва'),
PRIMARY KEY (id) 
); 
