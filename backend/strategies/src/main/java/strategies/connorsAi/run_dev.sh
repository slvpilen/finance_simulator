#!/bin/bash
echo "Delete old strategy results"

# Database credentials (Should be loaded from .env file)
DB_HOST=localhost
DB_PORT=5432
DB_NAME=FinanceDB
DB_USER=postgres
DB_PASS=replace-password


# Input variables
STRATEGY_NAME="connors-ai"
RESULT_NAME="v_10_sp500"

SQL_INSERT_STRATEGY="
    INSERT INTO strategy.strategy (name)
        VALUES ('$STRATEGY_NAME')
        ON CONFLICT (name) DO NOTHING;"


SQL_DELETE_STRATEGY_RESULT="
DELETE FROM strategy.strategy_result
    WHERE strategy_id IN (
        SELECT strategy.id
        FROM strategy.strategy
        WHERE strategy.name = '$STRATEGY_NAME'
    )
    AND name = '$RESULT_NAME';"

# Connect to the PostgreSQL database and execute the insert strategy if it does not exist
PGPASSWORD=$DB_PASS psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -a -c "$SQL_INSERT_STRATEGY"
# Connect to the PostgreSQL database and execute the delete old result
PGPASSWORD=$DB_PASS psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -a -c "$SQL_DELETE_STRATEGY_RESULT"

cd ../../../../../..
echo "Clean install"
mvn clean install
cd strategies
echo "Run strategy-simulation"
mvn exec:java -Dexec.mainClass="strategies.connorsAi.StrategyRunner"
