drop table if exists cart_products CASCADE;
drop table if exists members CASCADE;
drop table if exists product CASCADE;
drop table if exists orders CASCADE;

create table cart_products (
       cart_product_id bigint generated by default as identity,
        member_id bigint,
        product_id bigint,
        quantity integer,
        primary key (cart_product_id)
);

create table members (
       member_id bigint generated by default as identity,
        email varchar(255),
        password varchar(255),
        primary key (member_id)
);

create table product (
       product_id bigint generated by default as identity,
        image varchar(255),
        name varchar(255),
        price integer not null,
        primary key (product_id)
);

create table orders (
                        order_id bigint generated by default as identity,
                        member_id bigint,
                        price integer not null,
                        primary key (order_id)
);