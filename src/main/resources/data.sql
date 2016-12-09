TRUNCATE TABLE customers;
TRUNCATE TABLE products;

INSERT INTO customers (name, balance)
VALUES ('Shop', 15000),
  ('Vasya', 20000),
  ('Petya', 38000),
  ('Irina', 17620);

INSERT INTO products (name, count, price)
VALUES ('pen', 20, 20),
  ('pencil', 30, 40),
  ('knife', 40, 100),
  ('TV', 10, 2000),
  ('scissors', 15, 200);
