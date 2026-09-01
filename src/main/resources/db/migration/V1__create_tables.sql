CREATE TABLE orders (
    id UUID PRIMARY KEY,
    custumer_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    total NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT ck_orders_status
        CHECK (status IN ('PENDING', 'CREATED', 'CONFIRMED', 'CANCELLED')),
    CONSTRAINT ck_orders_total
        CHECK (total >= 0)
);

CREATE TABLE orders_items (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    sku VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(19, 2) NOT NULL,

    CONSTRAINT fk_orders_items_order
        FOREIGN KEY (order_id)
        REFERENCES orders (id)
        ON DELETE CASCADE,
    CONSTRAINT ck_orders_items_quantity
        CHECK (quantity > 0),
    CONSTRAINT ck_orders_items_unit_price
        CHECK (unit_price >= 0)
);

CREATE INDEX idx_orders_custumer_id
    ON orders (custumer_id);

CREATE INDEX idx_orders_status
    ON orders (status);

CREATE INDEX idx_orders_items_order_id
    ON orders_items (order_id);
