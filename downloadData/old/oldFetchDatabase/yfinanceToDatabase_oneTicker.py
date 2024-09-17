import yfinance as yf
import psycopg2
from psycopg2 import sql


def download_and_fetch_to_database(ticker, suffix, cur):
    ticker_modified = ticker + suffix
    # Fetch the symbol id for the ticker
    cur.execute("SELECT id FROM financial.symbol WHERE ticker = %s", (ticker,))
    symbol_id = cur.fetchone()[0]

    ticker_modified = ticker.replace(" ", "-") + suffix

    # Fetch maximum available market data using yfinance
    data = yf.download(ticker_modified)

    # Iterate over each row in the downloaded data
    for date, row in data.iterrows():
        # Insert data into tick_daily table
        cur.execute(
            sql.SQL(
                "INSERT INTO financial.tick_daily (symbol_id, date, open, high, close, low, adjClose, volume) VALUES (%s, %s, %s, %s, %s, %s, %s, %s) ON CONFLICT DO NOTHING"
            ),
            (
                symbol_id,
                date,
                row["Open"],
                row["High"],
                row["Close"],
                row["Low"],
                row["Adj Close"],
                row["Volume"],
            ),
        )


def __main__():
    # Database connection parameters
    db_params = {
        "dbname": "FinanceDB",
        "user": "postgres",
        "password": "replace-password",
        "host": "localhost",
        "port": 5432,
    }

    # Connect to your database
    conn = psycopg2.connect(**db_params)
    cur = conn.cursor()

    # Specific tickers to process
    # A symbol with ticker has to exist in the database
    TICKER = "^GSPC"
    SUFFIX = ""  # ".ST" is for Stockholm Stock Exchange

    download_and_fetch_to_database(TICKER, SUFFIX, cur)

    # Commit changes and close connection
    conn.commit()
    cur.close()
    conn.close()


if __name__ == "__main__":
    __main__()
