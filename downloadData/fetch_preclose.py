import yfinance as yf
from psycopg2 import sql
from databaseService import PostgresConnection
from models import Stock, Symbol
from tqdm import tqdm


def fetch_preclose_to_database(data, symbol, cur):
    if data.empty:
        raise Exception("Data is empty.")
    last_row = data.iloc[-1]
    Datetime = data.index[-1]  # Get the last date-time index

    date_str = Datetime.strftime("%Y-%m-%d")

    cur.execute(
        sql.SQL(
            """INSERT INTO financial.tick_daily (symbol_id, date, open, high, close, volume, low, adjclose, preclose) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
            ON CONFLICT (symbol_id, date)
            DO UPDATE SET
            open = EXCLUDED.open,
            high = EXCLUDED.high,
            low = EXCLUDED.low,
            close = EXCLUDED.close,
            adjclose = EXCLUDED.adjclose,
            preclose = EXCLUDED.preclose;
            """
        ),
        (
            symbol.id,
            date_str,
            float(last_row["Close"]),
            float(last_row["Close"]),
            float(last_row["Close"]),
            None,  # last_row["Volume"],
            float(last_row["Close"]),
            float(last_row["Close"]),
            float(last_row["Close"]),
        ),
    )


def __main__():

    db = PostgresConnection()

    symbols: list[Symbol] = db.get_symbols_in_database()

    for symbol in tqdm(symbols, desc="Downloading data"):
        data = yf.download(
            symbol.yahooTicker, period="1d", interval="1m", progress=False
        )
        if data.empty:
            print(f"No data was downloaded for: {symbol}")
            continue
        try:
            fetch_preclose_to_database(data, symbol, db.cur)
        except Exception as e:
            print(f"Error fetching preclose for {symbol}: {e}")

    db.commit()
    db.close()


if __name__ == "__main__":
    __main__()
