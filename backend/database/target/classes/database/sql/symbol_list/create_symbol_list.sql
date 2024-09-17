CREATE TABLE if NOT EXISTS financial.symbol_list (
    id SERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(255) UNIQUE NOT NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE if NOT EXISTS financial.symbol_list_relation (
    symbol_list_id INTEGER REFERENCES financial.symbol_list(id) ON DELETE CASCADE ON UPDATE CASCADE,
    symbol_id INTEGER REFERENCES financial.symbol(id) ON DELETE CASCADE ON UPDATE CASCADE,
    PRIMARY KEY (symbol_list_id, symbol_id)
);

CREATE VIEW financial.list_symbols_detailed AS
SELECT
    l.id AS list_id,
    l.name AS list_name,
    s.*
FROM
    financial.symbol_list l
    JOIN financial.symbol_list_relation slr ON l.id = slr.symbol_list_id
    JOIN financial.symbol_detailed s ON slr.symbol_id = s.symbol_id;