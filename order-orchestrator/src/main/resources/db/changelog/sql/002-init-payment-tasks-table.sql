CREATE TABLE IF NOT EXISTS payment_tasks
(
    id              UUID PRIMARY KEY DEFAULT uuidv7(),
    order_id        UUID             DEFAULT uuidv7(),
    status          TEXT,
    step            TEXT,
    attempts        INT,
    next_attempt_at TIMESTAMPTZ,
    created_at      TIMESTAMPTZ,
    updated_at      TIMESTAMPTZ,
    locked_until    TIMESTAMPTZ,

    CONSTRAINT fk_order_id FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
);

COMMENT ON TABLE payment_tasks IS 'Заказы';
COMMENT ON COLUMN payment_tasks.order_id IS 'Ссылка на заказ';
COMMENT ON COLUMN payment_tasks.status IS 'Статус задачи';
COMMENT ON COLUMN payment_tasks.step IS 'Шаг обработки задачи';
COMMENT ON COLUMN payment_tasks.attempts IS 'Счётчик попыток обработки задачи';
COMMENT ON COLUMN payment_tasks.next_attempt_at IS 'Время следующей попытки';
COMMENT ON COLUMN payment_tasks.created_at IS 'Время создания задачи';
COMMENT ON COLUMN payment_tasks.updated_at IS 'Время последнего обновления задачи';