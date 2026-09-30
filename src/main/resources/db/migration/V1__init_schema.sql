CREATE TABLE users (
    id              UUID PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    email           VARCHAR(100) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    full_name       VARCHAR(100) NOT NULL,
    role            VARCHAR(20)  NOT NULL,
    enabled         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ  NOT NULL,
    updated_at      TIMESTAMPTZ  NOT NULL
);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_username ON users (username);

CREATE TABLE loans (
    id                      UUID PRIMARY KEY,
    user_id                 UUID           NOT NULL REFERENCES users (id),
    principal_amount        NUMERIC(19, 2) NOT NULL,
    annual_interest_rate    NUMERIC(5, 2)  NOT NULL,
    term_in_months          INTEGER        NOT NULL,
    start_date              DATE           NOT NULL,
    status                  VARCHAR(20)    NOT NULL,
    created_at              TIMESTAMPTZ    NOT NULL,
    updated_at              TIMESTAMPTZ    NOT NULL
);

CREATE INDEX idx_loans_user_id ON loans (user_id);
CREATE INDEX idx_loans_status ON loans (status);

CREATE TABLE installments (
    id                  UUID PRIMARY KEY,
    loan_id             UUID           NOT NULL REFERENCES loans (id) ON DELETE CASCADE,
    installment_number  INTEGER        NOT NULL,
    amount              NUMERIC(19, 2) NOT NULL,
    paid_amount         NUMERIC(19, 2) NOT NULL DEFAULT 0,
    due_date            DATE           NOT NULL,
    status              VARCHAR(20)    NOT NULL,
    created_at          TIMESTAMPTZ    NOT NULL,
    updated_at          TIMESTAMPTZ    NOT NULL,
    CONSTRAINT uq_loan_installment_number UNIQUE (loan_id, installment_number)
);

CREATE INDEX idx_installments_loan_id ON installments (loan_id);
CREATE INDEX idx_installments_due_date ON installments (due_date);
CREATE INDEX idx_installments_status ON installments (status);

CREATE TABLE payments (
    id              UUID PRIMARY KEY,
    user_id         UUID           NOT NULL REFERENCES users (id),
    installment_id  UUID           NOT NULL REFERENCES installments (id),
    amount          NUMERIC(19, 2) NOT NULL,
    status          VARCHAR(20)    NOT NULL,
    paid_at         TIMESTAMPTZ    NOT NULL,
    reference       VARCHAR(100),
    created_at      TIMESTAMPTZ    NOT NULL,
    updated_at      TIMESTAMPTZ    NOT NULL
);

CREATE INDEX idx_payments_user_id ON payments (user_id);
CREATE INDEX idx_payments_installment_id ON payments (installment_id);
CREATE INDEX idx_payments_status ON payments (status);

CREATE TABLE audit_logs (
    id              UUID PRIMARY KEY,
    entity_type     VARCHAR(50)  NOT NULL,
    entity_id       UUID         NOT NULL,
    action          VARCHAR(50)  NOT NULL,
    performed_by    VARCHAR(100),
    details         TEXT,
    created_at      TIMESTAMPTZ  NOT NULL
);

CREATE INDEX idx_audit_entity ON audit_logs (entity_type, entity_id);
CREATE INDEX idx_audit_created_at ON audit_logs (created_at);
