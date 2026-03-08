CREATE TABLE IF NOT EXISTS payment_tasks
(
    id              UUID PRIMARY KEY DEFAULT uuidv7(),
    order_id        UUID NOT NULL,
    status          VARCHAR(30),
    step            VARCHAR(20),
    attempts        INT,
    next_attempt_at TIMESTAMPTZ,
    created_at      TIMESTAMPTZ,
    updated_at      TIMESTAMPTZ,

    CONSTRAINT fk_order_id FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT ck_payment_tasks_status CHECK (status IN ('NEW', 'IN_PROGRESS', 'SUCCEEDED', 'FAILED_RETRYABLE',
                                                         'FAILED_NON_RETRYABLE')),
    CONSTRAINT ck_payment_tasks_step CHECK (step IN ('AUTH', 'REPRICE', 'CAPTURE'))
);

COMMENT ON TABLE payment_tasks IS 'Заказы';
COMMENT ON COLUMN payment_tasks.order_id IS 'Ссылка на заказ';
COMMENT ON COLUMN payment_tasks.status IS 'Статус задачи';
COMMENT ON COLUMN payment_tasks.step IS 'Шаг обработки задачи';
COMMENT ON COLUMN payment_tasks.attempts IS 'Счётчик попыток обработки задачи';
COMMENT ON COLUMN payment_tasks.next_attempt_at IS 'Время следующей попытки';
COMMENT ON COLUMN payment_tasks.created_at IS 'Время создания задачи';
COMMENT ON COLUMN payment_tasks.updated_at IS 'Время последнего обновления задачи';