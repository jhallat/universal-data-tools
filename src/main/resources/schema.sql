
CREATE TABLE IF NOT EXISTS connection_property_values (
    connection_id INT NOT NULL,
    property_id INT NOT NULL,
    value varchar(250) NOT NULL
);

CREATE TABLE IF NOT EXISTS connections (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type_label varchar(50),
    description varchar(250) NOT NULL
);

CREATE TABLE IF NOT EXISTS connection_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    connection varchar(25),
    log_type varchar(10),
    log_timestamp timestamp,
    message varchar(1000)
);