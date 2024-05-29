CREATE TABLE IF NOT EXISTS public.person (
    id int GENERATED ALWAYS AS IDENTITY NOT NULL,
    surname varchar(50) NOT NULL,
    firstname varchar(50) NOT NULL,
    middlename varchar(50) NOT NULL,
    phone varchar(20) NULL,
    email varchar(50) NOT NULL,
    password_hash varchar(80) NULL,
    "role" int NOT NULL,
    created_at timestamp without time zone DEFAULT current_timestamp NOT NULL,
    is_confirm_email boolean,
    is_confirm_phone boolean,
    CONSTRAINT user_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.category (
	id int GENERATED ALWAYS AS IDENTITY NOT NULL,
	"name" varchar(50) NOT NULL,
	CONSTRAINT category_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.subcategory (
    id int GENERATED ALWAYS AS IDENTITY NOT NULL,
	"name" varchar(50) NOT NULL,
	category_id int NOT NULL,
	CONSTRAINT subcategory_pk PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS public.brand (
	id int GENERATED ALWAYS AS IDENTITY NOT NULL,
	"name" varchar(50) NOT NULL,
	CONSTRAINT brand_pk PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS public.product (
	id int GENERATED ALWAYS AS IDENTITY NOT NULL,
	article varchar(40) NULL,
	name varchar(200) NULL,
	"description" varchar(2500) NULL,
	price decimal(10, 2) NULL,
	subcategory_id int NULL,
	brand_id int NULL,
    created_at timestamp without time zone DEFAULT current_timestamp NOT NULL,
    article_wb varchar(40),
    barcode varchar(40),
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


CREATE TABLE IF NOT EXISTS public.order (
    id int GENERATED ALWAYS AS IDENTITY NOT NULL,
	person_id int NOT NULL,
	address_id int NOT NULL,
    comment varchar(200) NULL,
    created_at timestamp without time zone DEFAULT current_timestamp NOT NULL,
	CONSTRAINT order_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.address (
	id int GENERATED ALWAYS AS IDENTITY NOT NULL,
	city varchar(50) NOT NULL,
	street varchar(100) NOT NULL,
	apartment_number varchar(20) NOT NULL,
	created_at timestamp without time zone DEFAULT current_timestamp NOT NULL,
	person_id int,
	CONSTRAINT address_pk PRIMARY KEY (id)
);
create table IF NOT EXISTS product_image
(id integer primary key GENERATED ALWAYS AS IDENTITY not null,
 binary_data bytea,
 product_id integer not null
);

CREATE TABLE IF NOT EXISTS product_group(
id integer primary key GENERATED ALWAYS AS IDENTITY not NULL,
name varchar(50)
);

CREATE TABLE IF NOT EXISTS public.product_order (
	id int4 GENERATED ALWAYS AS IDENTITY NOT NULL,
	order_id int4 NOT NULL,
	product_id int4 NOT NULL,
	CONSTRAINT product_order_pk PRIMARY KEY (id)
);