ALTER TABLE loan
    ADD CONSTRAINT fk_loan_equipment
    FOREIGN KEY (equipment_id) REFERENCES equipment(id);