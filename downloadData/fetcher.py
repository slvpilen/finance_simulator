import yfinance as yf
import psycopg2
from psycopg2 import sql
import os
from databaseService import Stock, Tick, PostgresConnection


def minut_data_download_and_fetch_to_database(stock, cur):
    # data = yf.download(stock.yahooTicker, period="1d", interval="1m")
    # for Datetime, row in data.iterrows():

    #     date_str = Datetime.strftime("%Y-%m-%d")
    #     time_str = Datetime.strftime("%H:%M:%S")

    #     # Move to PostgrsConection
    #     cur.execute(
    #         sql.SQL(
    #             "INSERT INTO financial.tick_intraday (time_of_day, date, symbol_id, open, high, low, close, adj_close, volume) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)"
    #         ),
    #         (
    #             time_str,
    #             date_str,
    #             stock.id,
    #             row["Open"],
    #             row["High"],
    #             row["Low"],
    #             row["Close"],
    #             row.get(
    #                 "Adj Close", row["Close"]
    #             ),  # Use Close if Adj Close is not available
    #             row["Volume"],
    #         ),
    #     )
    pass


# def preclose_download_and_fetch_to_database(stock, cur):
#     data = yf.download(stock.yahooTicker, period="1d", interval="1m")

#     # Access the last row directly
#     last_row = data.iloc[-1]
#     Datetime = data.index[-1]  # Get the last date-time index

#     date_str = Datetime.strftime("%Y-%m-%d")
#     # time_str = Datetime.strftime("%H:%M:%S")

#     # Execute SQL command for only the last entry
#     cur.execute(
#         sql.SQL(
#             """INSERT INTO financial.tick_daily (symbol_id, date, open, high, close, volume, low, adjclose, preclose) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
#             ON CONFLICT (symbol_id, date)
#             DO UPDATE SET
#             open = EXCLUDED.open,
#             high = EXCLUDED.high,
#             low = EXCLUDED.low,
#             close = EXCLUDED.close,
#             adjclose = EXCLUDED.adjclose,
#             preclose = EXCLUDED.preclose;
#             """
#         ),
#         (
#             stock.id,
#             date_str,
#             last_row["Close"],
#             last_row["Close"],
#             last_row["Close"],
#             None,  # last_row["Volume"],
#             last_row["Close"],
#             last_row["Close"],
#             last_row["Close"],
#         ),
#     )


# TODO: Implement this function, insert to correct table, iterate over ticks and insert each tick
# def tick_intraday_download_and_fetch_to_database(stock, cur):
#     data = yf.download(stock.yahooTicker, period="1d", interval="1m")

#     # Access the last row directly
#     last_row = data.iloc[-1]
#     Datetime = data.index[-1]  # Get the last date-time index

#     date_str = Datetime.strftime("%Y-%m-%d")
#     # time_str = Datetime.strftime("%H:%M:%S")

#     # Execute SQL command for only the last entry
#     cur.execute(
#         """INSERT INTO financial.tick_daily (symbol_id, date, open, high, close, volume, low, adjclose, preclose)
#             VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
#             ON CONFLICT (symbol_id, date)
#             DO UPDATE SET
#             open = EXCLUDED.open,
#             high = EXCLUDED.high,
#             close = EXCLUDED.close,
#             volume = EXCLUDED.volume,
#             low = EXCLUDED.low,
#             adjclose = EXCLUDED.adjclose;
#         """,
#         (
#             stock.id,
#             date_str,
#             last_row["Open"],
#             last_row["High"],
#             last_row["Close"],
#             last_row["Volume"],
#             last_row["Low"],
#             last_row["Adj Close"],
#             None,
#         ),
#     )


def tick_daily_download_and_fetch_all_days_to_database(stock, cur):
    data = yf.download(stock.yahooTicker)
    print(data)

    for date, row in data.iterrows():
        # Execute SQL command for only the last entry
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
                stock.id,
                date,
                row["Open"],
                row["High"],
                row["Close"],
                row["Volume"],
                row["Low"],
                row["Adj Close"],
                None,
            ),
        )


# def __main__():
#     # Load .env file
#     load_dotenv()
#     ib_IP: str = os.getenv("INTERACTIVE_BROKER_IP")
#     ib_PORT: int = int(os.getenv("INTERACTIVE_BROKER_PORT"))

#     # Postgres database connection
#     db = PostgresConnection()

#     # Get stocks from database
#     stocks: list[Stock] = db.get_stocks_in_database()

#     for stock in stocks:
#         # minut_data_download_and_fetch_to_database(stock, db.cur)
#         daily_data_download_and_fetch_to_database(stock, db.cur)

#     db.commit()
#     db.close()


# if __name__ == "__main__":
#     __main__()
