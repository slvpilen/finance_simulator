import pandas as pd

"""
example of a row:
time, Account Equity
2024-02-01T01:00:00+01:00,1000000
"""
# SHould keep the latest time of each day.
# Should then sort the data by time. (oldes first)
# Then set return a new dataframe with the time and strategy positive. With 1 if account equity is greater then previous day, 0 if it is the same, -1 if it is less.


def transform_intraday_strategy_to_daily_positive_or_negative(df):
    # Ensure time column is in datetime format
    df["time"] = pd.to_datetime(df["time"], utc=True, errors="coerce")

    # Drop rows with invalid times (if any)
    df = df.dropna(subset=["time"])
    # remove rows with missing values
    print(df)
    # Create a column for just the date
    df["date"] = df["time"].dt.date

    # Group by date and get the row with the latest time of each day
    daily_data = df.loc[df.groupby("date")["time"].idxmax()]

    # Sort by time in ascending order (oldest first)
    daily_data = daily_data.sort_values(by="time")

    # Calculate strategy positive or negative based on Account Equity changes
    daily_data["strategy_positive"] = (
        daily_data["Account Equity"]
        .diff()
        .apply(lambda x: 1 if x > 0 else (0 if x == 0 else -1))
    )

    # Return a new dataframe with only 'time' and 'strategy_positive' columns
    return daily_data[["time", "strategy_positive"]]
