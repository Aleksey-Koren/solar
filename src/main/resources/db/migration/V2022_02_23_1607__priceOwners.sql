CREATE TABLE prices_owners
(
    price_id BIGINT NOT NULL REFERENCES prices (id) ON DELETE CASCADE,
    user_id  INT    NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    UNIQUE (price_id, user_id)
)