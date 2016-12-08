TRUNCATE TABLE customers;
TRUNCATE TABLE products;

INSERT INTO customers (name, balance)
VALUES ('Shop', 1500),
  ('Vasya', 2000),
  ('Petya', 3800),
  ('Irina', 1762);

INSERT INTO products (name, count, price)
VALUES ('pen', 55, 25),
  ('pencil', 67, 45),
  ('knife', 234, 123),
  ('scissors', 15, 226);
