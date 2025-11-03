drop table if exists Product;
create table if not exists Product (
    product_number varchar(255) primary key,
    name varchar(255),
    number_on_stock int
);
INSERT INTO Product (product_number, name, number_on_stock) VALUES ('P001', 'Product 1', 100);
INSERT INTO Product (product_number, name, number_on_stock) VALUES ('P002', 'Product 2', 200);
INSERT INTO Product (product_number, name, number_on_stock) VALUES ('P003', 'Product 3', 300);