CREATE TABLE test_null (
    col_boolean     boolean,
    col_date        date,
    col_decimal     decimal(8, 2),
    col_double      double,
    col_float       float,
    col_int         int,
    col_varchar     varchar(255),
    col_time        time,
    col_timestamp   timestamp
)
;;
CREATE TABLE test_notnull (
    col_boolean     boolean NOT NULL,
    col_date        date NOT NULL,
    col_decimal     decimal(8, 2) NOT NULL,
    col_double      double NOT NULL,
    col_float       float NOT NULL,
    col_int         int NOT NULL,
    col_varchar     varchar(255) NOT NULL,
    col_time        time NOT NULL,
    col_timestamp   timestamp NOT NULL
)
