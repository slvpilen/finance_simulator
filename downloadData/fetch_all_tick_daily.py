from databaseService import PostgresConnection
from models import Symbol
import yfinance as yf
from tqdm import tqdm


"""
This is sciprt take some time to run, because its downloading all the data from yahoo finance and inserting it into the database. 
This should only be runned once or when missing some data. It will not update the data, only insert new data, so its safe to run it multiple times.
Even if the data is already in the database, it will not be duplicated.
"""


def fetch_all_days_to_database(data, symbol, cur):
    if data.empty:
        raise Exception("Data is empty.")
    for date, row in data.iterrows():
        cur.execute(
                """INSERT INTO financial.tick_daily (symbol_id, date, open, high, close, volume, low, adjclose, preclose)
                VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
                ON CONFLICT (symbol_id, date) 
                DO UPDATE SET
                open = EXCLUDED.open,
                high = EXCLUDED.high,
                close = EXCLUDED.close,
                volume = EXCLUDED.volume,
                low = EXCLUDED.low,
                adjclose = EXCLUDED.adjclose;
                """,
            (
                symbol.id,
                date,
                float(row["Open"]),
                float(row["High"]),
                float(row["Close"]),
                float(row["Volume"]),
                float(row["Low"]),
                float(row["Adj Close"]),
                None,
            ),
        )


def __main__():
    db = PostgresConnection()

    symbols: list[Symbol] = db.get_symbols_in_database()

    for symbol in tqdm(symbols, desc="Downloading data"):
        # if symbol.yahooTicker != "ADVBOX":  # Remove this line to fetch all data
        #     continue
        # if symbol.id < 1000:
        #     continue
        data = yf.download(symbol.yahooTicker, progress=False, period="5")  # max, 10d, 5d
        if data.empty:
            print(f"No data was downloaded for: {symbol}")
            continue
        try:
            fetch_all_days_to_database(data, symbol, db.cur)
        except Exception as e:
            print(f"Error fetching all days for symbol:  {symbol}: {e}")

    db.commit()
    db.close()
    print("All data fetched and inserted into database.")


if __name__ == "__main__":
    __main__()
