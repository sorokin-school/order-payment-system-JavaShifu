-- changeset init-orders-:001

CREATE TABLE IF NOT EXISTS orders
(
    id                UUID PRIMARY KEY DEFAULT uuidv7(),
    customer_id       UUID             DEFAULT uuidv7(),
    address           TEXT,
    client_estimate   NUMERIC(20, 2),
    final_amount      NUMERIC(20, 2),
    authorized_amount NUMERIC(20, 2),
    captured_amount   NUMERIC(20, 2),
    payment_status    TEXT,
    failure_reason    TEXT
);

COMMENT ON TABLE orders IS 'Заказы';
COMMENT ON COLUMN orders.customer_id IS 'id заказчика в таблице orders';
COMMENT ON COLUMN orders.address IS 'Адрес заказчика';
COMMENT ON COLUMN orders.client_estimate IS 'Оценочная сумма, которую передал фронтенд (сумма, на которую делаем hold)';
COMMENT ON COLUMN orders.final_amount IS 'Финальная сумма после пересчёта склада (может быть NULL, пока пересчёт не выполнен)';
COMMENT ON COLUMN orders.authorized_amount IS 'Фактически авторизованная сумма';
COMMENT ON COLUMN orders.captured_amount IS 'Фактически списанная сумма (NULL, если capture не происходил или неуспешен)';
COMMENT ON COLUMN orders.payment_status IS 'Текущий статус оплаты заказа';
COMMENT ON COLUMN orders.failure_reason IS 'Причина неуспеха (decline карты, изменение цены и т.п.) для логов';