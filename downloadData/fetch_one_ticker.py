# from databaseService import Stock, PostgresConnection
# from fetcher import tick_daily_download_and_fetch_all_days_to_database

# def __main__():
#     # Postgres database connection
#     db = PostgresConnection()

#     stock: Stock = Stock
#     stock.yf_ticker = "AAPL"

#     for stock in stocks:
#         try:
#             tick_daily_download_and_fetch_all_days_to_database(stock, db.cur)
#         except Exception as e:
#             print(f"Error fetching all days for {stock}: {e}")

#     db.commit()
#     db.close()


# if __name__ == "__main__":
#     __main__()
