"""
Creating trading_day for each market based on the symbol with most tick_daily
"""

import psycopg2
import pandas as pd


conn = psycopg2.connect(
    dbname="FinanceDB", user="postgres", password="replace-password", host="localhost"
)

###############

# query = """
# SELECT td.*, s.name as symbol_name, mp.name as market_name, mp.id as market_place_id
# FROM tick_daily td
# JOIN symbol s ON td.symbol_id = s.id
# JOIN listed l ON s.listed_id = l.id
# JOIN market_place mp ON l.market_place_id = mp.id
# """

# tick_daily_data = pd.read_sql_query(query, conn)


# most_active_symbols = (
#     tick_daily_data.groupby(["market_name", "symbol_name"])
#     .size()
#     .reset_index(name="counts")
# )
# most_active_symbols = most_active_symbols.sort_values(
#     ["market_name", "counts"], ascending=[True, False]
# )
# # print(most_active_symbols)
# most_active_symbols = most_active_symbols.drop_duplicates(subset=["market_name"])
# print(most_active_symbols)

# trading_days = {}

# for index, row in most_active_symbols.iterrows():
#     market = row["market_name"]
#     symbol = row["symbol_name"]
#     symbol_dates = tick_daily_data[
#         (tick_daily_data["market_name"] == market)
#         & (tick_daily_data["symbol_name"] == symbol)
#     ]["date"]
#     trading_days[market] = sorted(symbol_dates.unique())


# for market, dates in trading_days.items():
#     print(f"Market: {market}, Trading Days: {dates}")


################
cursor = conn.cursor()
cursor.execute(
    """
    SELECT td.date
    FROM financial.tick_daily td
    JOIN financial.symbol s ON td.symbol_id = s.id
    WHERE s.ticker = '^GSPC'
"""
)
tick_daily_entries = cursor.fetchall()

for entry in tick_daily_entries:
    date = entry[0]
    cursor.execute(
        """
        INSERT INTO financial.trading_day (date, market_place_id, trading_hours_id)
        VALUES (%s, 3, 4)
        RETURNING id
    """,
        (date,),
    )
    trading_day_id = cursor.fetchone()[0]
    print(f"Inserted trading day with ID {trading_day_id} for date {date}")

    conn.commit()

cursor.close()
conn.close()
