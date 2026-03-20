CREATE TABLE IF NOT EXISTS customers (
    id BIGINT PRIMARY KEY,
    customer_type VARCHAR(32) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    pesel VARCHAR(32) UNIQUE,
    vat_id VARCHAR(32) UNIQUE,
    email VARCHAR(150),
    phone VARCHAR(50),
    date_of_birth VARCHAR(32),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS customer_addresses (
    id BIGINT PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    city VARCHAR(120) NOT NULL,
    country VARCHAR(120) NOT NULL,
    postal_code VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS customer_financial_profiles (
    id BIGINT PRIMARY KEY,
    customer_id BIGINT NOT NULL UNIQUE REFERENCES customers(id),
    employment_status VARCHAR(64),
    employment_months INTEGER,
    monthly_income_net INTEGER,
    monthly_expenses INTEGER,
    existing_liabilities_total INTEGER,
    has_income_verification BOOLEAN,
    credit_history_length_months INTEGER,
    last_updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS repayment_history (
    id BIGINT PRIMARY KEY,
    customer_id BIGINT NOT NULL UNIQUE REFERENCES customers(id),
    late_payments_12m INTEGER,
    delinquency_flag BOOLEAN,
    last_delinquency_date VARCHAR(32),
    notes TEXT
);

CREATE TABLE IF NOT EXISTS loan_products (
    id BIGINT PRIMARY KEY,
    product_code VARCHAR(64) NOT NULL UNIQUE,
    display_name VARCHAR(150) NOT NULL,
    min_amount INTEGER NOT NULL,
    max_amount INTEGER NOT NULL,
    default_term_min INTEGER NOT NULL,
    default_term_max INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS chat_sessions (
    id VARCHAR(64) PRIMARY KEY,
    employee_id VARCHAR(64) NOT NULL,
    customer_identifier VARCHAR(64),
    customer_identifier_type VARCHAR(32),
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS loan_applications (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    chat_session_id VARCHAR(64) NOT NULL REFERENCES chat_sessions(id),
    loan_product_id BIGINT NOT NULL REFERENCES loan_products(id),
    requested_amount INTEGER NOT NULL,
    requested_term_months INTEGER NOT NULL,
    declared_purpose TEXT NOT NULL,
    submitted_by_employee_id VARCHAR(64) NOT NULL,
    status VARCHAR(64) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    employee_final_action VARCHAR(64),
    employee_override_reason TEXT,
    employee_action_note TEXT,
    employee_action_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS application_form_snapshots (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL REFERENCES loan_applications(id),
    form_version VARCHAR(64) NOT NULL,
    prefilled_json TEXT NOT NULL,
    submitted_json TEXT NOT NULL,
    validation_errors_json TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS decision_results (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT NOT NULL UNIQUE REFERENCES loan_applications(id),
    rule_set_version VARCHAR(64) NOT NULL,
    deterministic_score INTEGER NOT NULL,
    llm_risk_label VARCHAR(64) NOT NULL,
    llm_confidence DOUBLE PRECISION NOT NULL,
    recommendation VARCHAR(64) NOT NULL,
    top_factors_json TEXT NOT NULL,
    explanation_text TEXT NOT NULL,
    next_steps_text TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS audit_events (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT REFERENCES loan_applications(id),
    chat_session_id VARCHAR(64),
    actor_type VARCHAR(32) NOT NULL,
    actor_id VARCHAR(64) NOT NULL,
    event_type VARCHAR(64) NOT NULL,
    payload_json TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_customers_pesel ON customers(pesel);
CREATE INDEX IF NOT EXISTS idx_customers_vat_id ON customers(vat_id);
CREATE INDEX IF NOT EXISTS idx_profiles_customer_id ON customer_financial_profiles(customer_id);
CREATE INDEX IF NOT EXISTS idx_repayment_customer_id ON repayment_history(customer_id);
CREATE INDEX IF NOT EXISTS idx_chat_sessions_employee_id ON chat_sessions(employee_id, created_at);
CREATE INDEX IF NOT EXISTS idx_loan_applications_chat_session ON loan_applications(chat_session_id, created_at);
CREATE INDEX IF NOT EXISTS idx_audit_events_case_timeline ON audit_events(chat_session_id, created_at);
