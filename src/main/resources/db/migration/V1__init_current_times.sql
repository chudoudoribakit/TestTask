CREATE SEQUENCE IF NOT EXISTS current_time_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS current_times (
    id          BIGINT PRIMARY KEY DEFAULT nextval('current_time_seq'),
    recorded_at TIMESTAMPTZ NOT NULL
    );

CREATE UNIQUE INDEX IF NOT EXISTS uniq_current_times_recorded_at
    ON current_times USING BTREE (recorded_at);