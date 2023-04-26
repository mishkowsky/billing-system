
CREATE TABLE "customer" (
  "id" INT GENERATED BY DEFAULT AS IDENTITY UNIQUE PRIMARY KEY NOT NULL,
  "phone_number" decimal(11, 0),
  "id_tariff" int,
  "id_operator" int,
  "balance" int,
  "remain_minutes" int,
  "update_time" timestamp DEFAULT current_timestamp,
  "comment" varchar
);

CREATE TABLE "call" (
  "id" INT GENERATED BY DEFAULT AS IDENTITY UNIQUE PRIMARY KEY NOT NULL,
  "id_caller" int,
  "id_callee" int,
  "price" int,
  "start_date" timestamp,
  "end_date" timestamp,
  "update_time" timestamp DEFAULT current_timestamp,
  "comment" varchar
);

CREATE TABLE "tariff" (
  "id" INT GENERATED BY DEFAULT AS IDENTITY UNIQUE PRIMARY KEY NOT NULL,
  "tarrif_code" varchar,
  "tariff_name" varchar,
  "price" int,
  "minutes_limit" int,
  "update_time" timestamp DEFAULT current_timestamp,
  "comment" varchar
);

CREATE TABLE "price_per_minute" (
  "id" INT GENERATED BY DEFAULT AS IDENTITY UNIQUE PRIMARY KEY NOT NULL,
  "id_tariff" int,
  "id_operator" int,
  "call_type" varchar,
  "price_type" varchar,
  "price" int NOT NULL,
  "update_time" timestamp DEFAULT current_timestamp,
  "comment" varchar
);

CREATE TABLE "operator" (
  "id" INT GENERATED BY DEFAULT AS IDENTITY UNIQUE PRIMARY KEY NOT NULL,
  "operator_name" varchar,
  "update_time" timestamp DEFAULT current_timestamp,
  "comment" varchar
);

ALTER TABLE "customer" ADD FOREIGN KEY ("id_tariff") REFERENCES "tariff" ("id");

ALTER TABLE "price_per_minute" ADD FOREIGN KEY ("id_tariff") REFERENCES "tariff" ("id");

ALTER TABLE "price_per_minute" ADD FOREIGN KEY ("id_operator") REFERENCES "operator" ("id");

ALTER TABLE "customer" ADD FOREIGN KEY ("id_operator") REFERENCES "operator" ("id");

ALTER TABLE "call" ADD FOREIGN KEY ("id_caller") REFERENCES "customer" ("id");

ALTER TABLE "call" ADD FOREIGN KEY ("id_callee") REFERENCES "customer" ("id");