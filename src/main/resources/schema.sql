CREATE TABLE IF NOT EXISTS connection_types (
    id INT PRIMARY KEY,
    description varchar(250) NOT NULL,
    label varchar(30) NOT NULL,
    factory varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS connection_properties (
    id INT PRIMARY KEY,
    description varchar(50) NOT NULL,
    masked INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS connection_type_properties (
    type_id INT NOT NULL,
    property_id INT NOT NULL,
    required INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS connection_property_values (
    connection_id INT NOT NULL,
    property_id INT NOT NULL,
    value varchar(250) NOT NULL
);

CREATE TABLE IF NOT EXISTS connections (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type_id INT NOT NULL,
    description varchar(250) NOT NULL
);