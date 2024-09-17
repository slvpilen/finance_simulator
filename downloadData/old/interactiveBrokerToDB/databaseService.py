import os
import psycopg2
from datetime import datetime
from dotenv import load_dotenv


class Stock:
    def __init__(self, row: tuple):
        self.id: int = row[0]
        self.name: str = row[1]
        self.ticker: str = row[2].replace(" ", ".")
        self.currency: str = row[3]
        self.exchange: str = row[4]
        self.secType: str = "STK"

    def __str__(self) -> str:
        return (
            f"Stock ID: {self.id}, Name: {self.name}, "
            f"Ticker: {self.ticker}, Currency: {self.currency}, "
            f"Exchange: {self.exchange}, Security Type: {self.secType}"
        )

    def __repr__(self) -> str:
        return self.name


class Tick:
    def __init__(self, **data) -> None:
        self.time_of_day: str = data["time_of_day"]
        self.tick_daily_id: int = data["tick_daily_id"]
        self.close: float = data["close"]
        self.volume: int = data["volume"]
        self.bid: float = data["bid"]
        self.time: float = data["ask"]


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
            "SELECT stock_id, stock_name, ticker, currency, exchange FROM financial.stock_detailed"
        )
        rows: tuple = self.cur.fetchall()
        stocks: list[Stock] = [Stock(row) for row in rows]

        return stocks

    def insert_ticks(self) -> None:
        pass

    def insert_tick_daily_without_data(self, stocks: list[Stock]) -> None:
        # todays_date = datetime.now().strftime("%Y-%m-%d")
        # for stock in stocks:
        #     self.cur.execute(
        #         "INSERT INTO financialltick_daily (symbol_id, date) VALUES (?, ?)",
        #         (stock.id, todays_date),
        #     )
        # self.conn.commit()
        pass

    def insert_ticks(self, ticks: list[Tick]) -> None:
        pass

    def close(self) -> None:
        self.cur.close()
        self.conn.close()


def _testing():
    database_connection: PostgresConnection = PostgresConnection()
    stocksToUpdate: list[Stock] = database_connection.get_stocks_in_database()

    stock0 = stocksToUpdate[130]
    print(stock0)

    database_connection.close()


_testing()
