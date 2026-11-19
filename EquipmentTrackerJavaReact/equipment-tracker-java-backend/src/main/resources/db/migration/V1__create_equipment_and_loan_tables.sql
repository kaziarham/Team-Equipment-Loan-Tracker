CREATE TABLE equipment (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    serial_number VARCHAR(255)
);

CREATE TABLE loan (
    id BIGSERIAL PRIMARY KEY,
    equipment_id BIGINT NOT NULL,
    borrower_name VARCHAR(255) NOT NULL,
    loan_date DATE NOT NULL,
    due_date DATE NOT NULL,
    returned_date DATE,
    FOREIGN KEY (equipment_id) REFERENCES equipment(id),
    UNIQUE (equipment_id) WHERE returned_date IS NULL
);