import os
import psycopg2
from datetime import datetime
from dotenv import load_dotenv
from models import Stock, Symbol, Tick


# Service
class PostgresConnection:
    def __init__(self) -> None:
        # Load .env file
        load_dotenv()
        self.conn: psycopg2 = psycopg2.connect(
            dbname=os.getenv("POSTGRES_DB_NAME"),
            user=os.getenv("POSTGRES_USER"),
            password=os.getenv("POSTGRES_PASSWORD"),
            host=os.getenv("POSTGRES_HOST"),
            port=os.getenv("POSTGRES_PORT"),
        )
        # Open a cursor to perform database operations
        self.cur = self.conn.cursor()

    def get_stocks_in_database(self) -> list[Stock]:
        self.cur.execute(
            "SELECT stock_id, stock_name, ticker, currency, exchange, yahoo_postfix FROM financial.stock_detailed"
        )
        rows: tuple = self.cur.fetchall()
        stocks: list[Stock] = [Stock(row) for row in rows]

        return stocks

    def get_symbols_in_database(self) -> list[Symbol]:
        self.cur.execute(
            "SELECT symbol_id, symbol_name, ticker, currency, exchange, yahoo_postfix FROM financial.symbol_detailed"
        )
        rows: tuple = self.cur.fetchall()
        symbols: list[Symbol] = [Symbol(row) for row in rows]

        return symbols

    def get_commoditys_in_database(self) -> list[Symbol]:
        self.cur.execute(
            "SELECT commodity_id, commodity_name, ticker, currency, exchange, yahoo_postfix FROM financial.commodity_detailed"
        )
        rows: tuple = self.cur.fetchall()
        commoditys: list[Symbol] = [Symbol(row) for row in rows]

        return commoditys

    def insert_ticks(self) -> None:
        pass

    def insert_ticks(self, ticks: list[Tick]) -> None:
        pass

    def commit(self):
        self.conn.commit()

    def close(self) -> None:
        self.cur.close()
        self.conn.close()


def _testing():
    database_connection: PostgresConnection = PostgresConnection()
    stocksToUpdate: list[Stock] = database_connection.get_stocks_in_database()

    database_connection.close()


_testing()
