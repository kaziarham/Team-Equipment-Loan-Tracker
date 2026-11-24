INSERT INTO equipment (name, category, serial_number) VALUES
    ('Canon EOS R6', 'Camera', 'DEMO-CAM-001'),
    ('Rode VideoMic Pro', 'Audio', 'DEMO-AUD-001'),
    ('Dell XPS 13', 'Laptop', 'DEMO-LAP-001'),
    ('Logitech Brio', 'Webcam', 'DEMO-WEB-001');

-- Overdue: due dates are in the past
INSERT INTO loan (equipment_id, borrower_name, loan_date, due_date, returned_date)
SELECT id, 'Alex Morgan', DATE '2026-11-01', DATE '2026-11-08', NULL
FROM equipment WHERE serial_number = 'DEMO-CAM-001';

INSERT INTO loan (equipment_id, borrower_name, loan_date, due_date, returned_date)
SELECT id, 'Priya Shah', DATE '2026-10-20', DATE '2026-10-27', NULL
FROM equipment WHERE serial_number = 'DEMO-AUD-001';

-- On loan, not yet overdue (move the due date forward if it ever passes)
INSERT INTO loan (equipment_id, borrower_name, loan_date, due_date, returned_date)
SELECT id, 'Sam Lee', DATE '2026-11-20', DATE '2030-01-31', NULL
FROM equipment WHERE serial_number = 'DEMO-LAP-001';