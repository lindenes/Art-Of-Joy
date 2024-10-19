CREATE SEQUENCE IF NOT EXISTS person_id_seq INCREMENT BY 1 START WITH 1;

CREATE TABLE IF NOT EXISTS person (
    id int default nextval('person_id_seq') NOT NULL,
    surname varchar(50) NULL,
    firstname varchar(50) NULL,
    middlename varchar(50) NULL,
    phone varchar(20) NULL,
    email varchar(50) NOT NULL,
    password_hash varchar(80) NULL,
    "role" int NOT NULL,
    created_at timestamp without time zone DEFAULT current_timestamp NOT NULL,
    is_confirm_email boolean,
    is_confirm_phone boolean,
    CONSTRAINT user_pk PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS category_id_seq INCREMENT BY 1 START WITH 1;

CREATE TABLE IF NOT EXISTS category (
	id int default nextval('category_id_seq') NOT NULL,
	"name" varchar(50) NOT NULL,
	CONSTRAINT category_pk PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS subcategory_id_seq INCREMENT BY 1 START WITH 1;

CREATE TABLE IF NOT EXISTS subcategory (
    id int default nextval('subcategory_id_seq') NOT NULL,
	"name" varchar(50) NOT NULL,
	category_id int NOT NULL,
	CONSTRAINT subcategory_pk PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS brand_id_seq INCREMENT BY 1 START WITH 1;

CREATE TABLE IF NOT EXISTS brand (
    id int default nextval('brand_id_seq') NOT NULL,
	"name" varchar(50) NOT NULL,
	CONSTRAINT brand_pk PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS product_id_seq INCREMENT BY 1 START WITH 1;

CREATE TABLE IF NOT EXISTS product (
    id int default nextval('product_id_seq') NOT NULL,
	article varchar(40) NOT NULL,
	"name" varchar(200) NOT NULL,
	"description" varchar(2500) NULL,
	price decimal(10, 2) NOT NULL,
	subcategory_id int NULL,
	brand_id int NULL,
    created_at timestamp without time zone DEFAULT current_timestamp NOT NULL,
    article_wb varchar(40),
    barcode varchar(40) NOT NULL,
    material varchar(50),
    fragility boolean,
    product_country varchar(50),
    color varchar(40),
    height double precision,
    width double precision,
    "size" varchar(20),
    ru_size varchar(20),
	product_group_id int4 NULL,
	CONSTRAINT product_pk PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS order_id_seq INCREMENT BY 1 START WITH 1;

CREATE TABLE IF NOT EXISTS "order" (
    id int default nextval('order_id_seq') NOT NULL,
	person_id int NOT NULL,
	address_id int NOT NULL,
    comment varchar(200) NULL,
    created_at timestamp without time zone DEFAULT current_timestamp NOT NULL,
	CONSTRAINT order_pk PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS address_id_seq INCREMENT BY 1 START WITH 1;

CREATE TABLE IF NOT EXISTS address (
    id int default nextval('address_id_seq') NOT NULL,
	city varchar(50) NOT NULL,
	street varchar(100) NOT NULL,
	apartment_number varchar(20) NOT NULL,
	created_at timestamp without time zone DEFAULT current_timestamp NOT NULL,
	person_id int,
	CONSTRAINT address_pk PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS product_image_id_seq INCREMENT BY 1 START WITH 1;

create table IF NOT EXISTS product_image(
    id int default nextval('product_image_id_seq') NOT NULL,
    binary_data bytea not null,
    product_id integer not null,
    CONSTRAINT product_image_pk PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS product_group_id_seq INCREMENT BY 1 START WITH 1;

CREATE TABLE IF NOT EXISTS product_group(
    id int default nextval('product_group_id_seq') NOT NULL,
    "name" varchar(50),
    CONSTRAINT product_group_pk PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS product_order_id_seq INCREMENT BY 1 START WITH 1;

CREATE TABLE IF NOT EXISTS product_order (
    id int default nextval('product_order_id_seq') NOT NULL,
	order_id int4 NOT NULL,
	product_id int4 NOT NULL,
	CONSTRAINT product_order_pk PRIMARY KEY (id)
);

create sequence if not exists section_id_seq INCREMENT BY 1 START WITH 1;

CREATE TABLE IF NOT EXISTS "section" (
    id int default nextval('section_id_seq') not null,
    "name" varchar(50) not null,
    CONSTRAINT section_pk primary key (id)
);

create sequence if not exists post_id_seq INCREMENT BY 1 START WITH 1;

CREATE TABLE IF NOT EXISTS post (
    id int default nextval('post_id_seq') not null,
    "name" varchar(60) not null,
    content varchar(1000) not null,
    CONSTRAINT post_pk primary key (id)
);

create sequence if not exists product_post_id_seq INCREMENT BY 1 START WITH 1;

create table if not exists product_post (
    id int default nextval('product_post_id_seq') not null,
    post_id int not null,
    product_id int not null,
    CONSTRAINT product_post_pk primary key (id)
);

create sequence if not exists cart_id_seq increment by 1 start with 1;

CREATE TABLE if not exists cart(
    id int default nextval('cart_id_seq') not null,
    product_id int not null,
    person_id int not null,
    count int not null default 1,
    CONSTRAINT cart_pk primary key (id)
);

create sequence if not exists comment_photo_id_seq increment by 1 start with 1;

create table if not exists comment_photo(
    id int default nextval('comment_photo_id_seq') not null,
    binary_data bytea not null,
    CONSTRAINT comment_photo_pk primary key (id)
);

create sequence if not exists comment_id_seq increment by 1 start with 1;

create table if not exists comment(
    id int default nextval('comment_id_seq') not null,
    product_id int not null,
    content varchar(600) null,
    parent_comment_id int null,
    CONSTRAINT comment_pk primary key (id)
);
