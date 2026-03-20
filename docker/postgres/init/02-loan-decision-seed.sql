INSERT INTO loan_products (id, product_code, display_name, min_amount, max_amount, default_term_min, default_term_max)
VALUES
    (1, 'PERSONAL_LOAN', 'Personal Loan', 5000, 50000, 12, 72),
    (2, 'CAR_LOAN', 'Car Loan', 5000, 50000, 12, 72),
    (3, 'CASH_LOAN', 'Cash Loan', 5000, 30000, 12, 48)
ON CONFLICT (id) DO UPDATE SET display_name = EXCLUDED.display_name;

INSERT INTO customers (id, customer_type, full_name, pesel, vat_id, email, phone, date_of_birth)
VALUES
    (1, 'INDIVIDUAL', 'Anna Nowak', '90010112345', NULL, 'anna.nowak@example.test', '+48111000111', '1990-01-01'),
    (2, 'INDIVIDUAL', 'Bartosz Kowalski', '88020223456', NULL, 'bartosz.kowalski@example.test', '+48111000222', '1988-02-02'),
    (3, 'INDIVIDUAL', 'Celina Zielinska', '92030334567', NULL, 'celina.zielinska@example.test', '+48111000333', '1992-03-03'),
    (4, 'INDIVIDUAL', 'Damian Wisniewski', '85040445678', NULL, 'damian.wisniewski@example.test', '+48111000444', '1985-04-04'),
    (5, 'INDIVIDUAL', 'Ewa Maj', '96050556789', NULL, 'ewa.maj@example.test', '+48111000555', '1996-05-05'),
    (6, 'INDIVIDUAL', 'Filip Duda', '94060667890', NULL, 'filip.duda@example.test', '+48111000666', '1994-06-06'),
    (7, 'INDIVIDUAL', 'Gabriela Piotrowska', '93070778901', NULL, 'gabriela.piotrowska@example.test', '+48111000777', '1993-07-07'),
    (8, 'INDIVIDUAL', 'Hubert Wojcik', '91080889012', NULL, 'hubert.wojcik@example.test', '+48111000888', '1991-08-08'),
    (9, 'COMPANY', 'Inwest Trade Sp. z o.o.', NULL, 'PL5210001112', 'office@inwesttrade.example.test', '+48221000999', NULL),
    (10, 'COMPANY', 'Metro Cars SA', NULL, 'PL5210002223', 'contact@metrocars.example.test', '+48221000123', NULL)
ON CONFLICT (id) DO UPDATE SET full_name = EXCLUDED.full_name;

INSERT INTO customer_addresses (id, customer_id, city, country, postal_code)
VALUES
    (1, 1, 'Warsaw', 'Poland', '00-001'),
    (2, 2, 'Krakow', 'Poland', '30-002'),
    (3, 3, 'Gdansk', 'Poland', '80-003'),
    (4, 4, 'Wroclaw', 'Poland', '50-004'),
    (5, 5, 'Poznan', 'Poland', '60-005'),
    (6, 6, 'Lodz', 'Poland', '90-006'),
    (7, 7, 'Szczecin', 'Poland', '70-007'),
    (8, 8, 'Lublin', 'Poland', '20-008'),
    (9, 9, 'Warsaw', 'Poland', '00-009'),
    (10, 10, 'Katowice', 'Poland', '40-010')
ON CONFLICT (id) DO NOTHING;

INSERT INTO customer_financial_profiles (id, customer_id, employment_status, employment_months, monthly_income_net, monthly_expenses, existing_liabilities_total, has_income_verification, credit_history_length_months)
VALUES
    (1, 1, 'FULL_TIME', 48, 8500, 2600, 500, TRUE, 60),
    (2, 2, 'FULL_TIME', 36, 6200, 2500, 1800, TRUE, 48),
    (3, 3, 'SELF_EMPLOYED', 18, 6200, 2400, 300, FALSE, 30),
    (4, 4, 'FULL_TIME', 30, 7000, 2500, 1000, TRUE, 48),
    (5, 5, 'FULL_TIME', 4, 5400, 2200, 400, TRUE, 8),
    (6, 6, 'FULL_TIME', 24, 4900, 2100, 0, TRUE, 24),
    (7, 7, NULL, 12, 5800, 2300, 900, TRUE, 18),
    (8, 8, 'FULL_TIME', 20, 7200, 2800, 600, TRUE, 12),
    (9, 9, 'COMPANY', 60, 14000, 6000, 2500, TRUE, 72),
    (10, 10, 'COMPANY', 72, 18000, 7000, 3500, TRUE, 84)
ON CONFLICT (id) DO UPDATE SET monthly_income_net = EXCLUDED.monthly_income_net;

INSERT INTO repayment_history (id, customer_id, late_payments_12m, delinquency_flag, last_delinquency_date, notes)
VALUES
    (1, 1, 0, FALSE, NULL, 'Strong applicant scenario'),
    (2, 2, 1, FALSE, NULL, 'High debt burden scenario'),
    (3, 3, 0, FALSE, NULL, 'Missing income verification scenario'),
    (4, 4, 3, TRUE, '2025-11-10', 'Prior repayment issues scenario'),
    (5, 5, 0, FALSE, NULL, 'Borderline but recoverable scenario'),
    (6, 6, 0, FALSE, NULL, 'Zero liabilities edge case'),
    (7, 7, 0, FALSE, NULL, 'Missing employment status edge case'),
    (8, 8, 1, FALSE, NULL, 'High amount verification case'),
    (9, 9, 0, FALSE, NULL, 'Company VAT lookup case'),
    (10, 10, 2, TRUE, '2025-09-05', 'Company delinquency case')
ON CONFLICT (id) DO UPDATE SET notes = EXCLUDED.notes;
