from databaseService import PostgresConnection
from models import Symbol
import yfinance as yf
from tqdm import tqdm

"""
This is sciprt take some time to run, because its downloading all the data from yahoo finance and inserting it into the database. 
This should only be runned once or when missing some data. It will not update the data, only insert new data, so its safe to run it multiple times.
Even if the data is already in the database, it will not be duplicated.
"""


def fetch_dividens(dividends, symbol, cur):
    if dividends.empty:
        raise Exception("Data is empty.")

    for date, value in dividends.items():
        cur.execute(
        """
        INSERT INTO financial.dividend (symbol_id, date, value)
        VALUES (%s, %s, %s)
        ON CONFLICT (symbol_id, date) 
        DO UPDATE SET
        value = EXCLUDED.value;
        """,
            (
                symbol.id,
                date,
                float(value)
            ),
        )


def __main__():
    db = PostgresConnection()

    symbols: list[Symbol] = db.get_symbols_in_database()

    for symbol in tqdm(symbols, desc="Downloading data"):
        dividends = yf.Ticker(symbol.yahooTicker).dividends
        try:
            fetch_dividens(dividends, symbol, db.cur)
        except Exception as e:
            print(f"Error fetching all days for symbol:  {symbol}: {e}")

    db.commit()
    db.close()
    print("All data fetched and inserted into database.")


if __name__ == "__main__":
    __main__()
