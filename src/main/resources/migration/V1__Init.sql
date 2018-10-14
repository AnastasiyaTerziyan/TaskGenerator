CREATE TABLE tasks
(
    id             long AUTO_INCREMENT PRIMARY KEY,
    task_full_text varchar2(10000),
    task_solution  varchar2(512),
    unknown VARCHAR2(512)
);