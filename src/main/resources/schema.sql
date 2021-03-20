DROP TABLE IF EXISTS connection_types;
DROP TABLE IF EXISTS connection_properties;
DROP TABLE IF EXISTS connection_type_properties;

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