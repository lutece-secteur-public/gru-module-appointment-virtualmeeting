--
-- Table structure for workflow task config
--
CREATE TABLE IF NOT EXISTS workflow_task_addvirtualmeeting_config (
    id_task INT NOT NULL,
    provider VARCHAR(255) NOT NULL DEFAULT '',
    access_level VARCHAR(50) NOT NULL DEFAULT '',
    id_entry_guest_link INT NOT NULL DEFAULT 0,
    id_entry_host_link INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id_task)
);

--
-- Table structure for virtual meeting rooms
--
CREATE TABLE IF NOT EXISTS virtualmeeting_room (
    id_virtualmeeting INT AUTO_INCREMENT,
    id_resource VARCHAR(255) NOT NULL DEFAULT '',
    resource_type VARCHAR(255) NOT NULL DEFAULT '',
    room_name VARCHAR(255) NOT NULL,
    provider VARCHAR(255) NOT NULL,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_virtualmeeting)
);

--
-- Table structure for task information (host URL shown in workflow history)
--
CREATE TABLE IF NOT EXISTS workflow_task_virtualmeeting_information (
    id_history INT NOT NULL,
    id_task INT NOT NULL,
    host_url VARCHAR(2000) NOT NULL DEFAULT '',
    error_message VARCHAR(2000) NOT NULL DEFAULT '',
    PRIMARY KEY (id_history, id_task)
);
