UPDATE payment_tasks
SET status          = 'NEW',
    step            = 'AUTH',
    attempts        = 0,
    next_attempt_at = null,
    updated_at      = null
WHERE status != 'NEW'