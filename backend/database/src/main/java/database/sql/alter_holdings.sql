ALTER TABLE strategy.strategy_holding 
ADD COLUMN result_id INT,
ADD CONSTRAINT fk_result_id
FOREIGN KEY (result_id) REFERENCES strategy.strategy_result(id)
ON DELETE CASCADE 
ON UPDATE CASCADE;

-- ALL_INDUSTRIES_COMBINED
UPDATE strategy.strategy_holding 
SET result_id = 67;


ALTER TABLE strategy.strategy_holding 
ALTER COLUMN result_id SET NOT NULL;


ALTER TABLE strategy.strategy_holding
DROP CONSTRAINT unique_symbol_strategy_date;

ALTER TABLE strategy.strategy_holding
ADD CONSTRAINT unique_symbol_strategy_date_result
UNIQUE (symbol_id, strategy_id, holding_date, result_id);


CREATE VIEW strategy.holding_detailed AS
SELECT
    symbol.id AS sym_id,
    symbol.ticker AS sym_ticker,
    symbol.name AS sym_name,
    holding.holding_date AS holding_date,
    holding.result_id AS result_id,
    holding.strategy_id AS strategy_id,
    strategy.name AS strategy_name,
    result.name AS result_name
FROM
    strategy.strategy_holding holding
    INNER JOIN financial.symbol symbol ON holding.symbol_id = symbol.id
    INNER JOIN strategy.strategy strategy ON holding.strategy_id = strategy.id
    INNER JOIN strategy.strategy_result result ON holding.result_id = result.id;


DROP FUNCTION IF EXISTS strategy.get_strategy_holdings();
