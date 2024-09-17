import yfinance as yf
import psycopg2
from psycopg2 import sql
from yfinanceToDatabase_oneTicker import download_and_fetch_to_database

############ NOTE! This is not tested after refactorization! Remove this if it works! ########


def __main__():
    # Database connection parameters - replace these with your details
    db_params = {
        "dbname": "FinanceDB",  # Extracted from the URL in your Java code
        "user": "postgres",  # Same as USER in your Java code
        "password": "replace-password",  # Replace with the actual password
        "host": "localhost",  # Extracted from the URL in your Java code
        "port": 5432,  # Default PostgreSQL port, extracted from the URL
    }

    # Connect to your database
    conn = psycopg2.connect(**db_params)
    cur = conn.cursor()

    # Fetch tickers from the symbol table
    cur.execute("SELECT ticker FROM symbol WHERE industry = 'Industrials'")
    symbols = cur.fetchall()
    SUFFIX = ".ST"

    for symbol_tuple in symbols:
        download_and_fetch_to_database(symbol_tuple[0], SUFFIX, cur)

    # Commit changes and close connection
    conn.commit()
    cur.close()
    conn.close()
