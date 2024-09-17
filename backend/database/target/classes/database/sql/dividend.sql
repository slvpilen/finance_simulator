-- DROP TABLE if EXISTS financial.dividend;

CREATE TABLE if NOT EXISTS financial.dividend (
    id SERIAL PRIMARY KEY NOT NULL,
    -- symbol_id INTEGER REFERENCES financial.symbol(id) ON DELETE CASCADE ON UPDATE CASCADE,
    symbol_id INTEGER, 
	date date NOT NULL,
    value double precision,
    CONSTRAINT max_one_dividend_pr_day UNIQUE (date, symbol_id)
);
