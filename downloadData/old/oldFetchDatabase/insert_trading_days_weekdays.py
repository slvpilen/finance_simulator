import psycopg2
from datetime import datetime, timedelta


def generate_weekday_inserts(start_date, end_date, market_place_id, trading_hours_id):
    inserts = []
    current_date = start_date

    while current_date <= end_date:
        if current_date.weekday() < 5:  # Weekdays only
            sql = f"INSERT INTO public.trading_day (date, market_place_id, trading_hours_id) VALUES ('{current_date.strftime('%Y-%m-%d')}', {market_place_id}, {trading_hours_id});"
            inserts.append(sql)
        current_date += timedelta(days=1)

    return inserts


def execute_inserts(insert_statements):
    try:
        # Connect to your database
        conn = psycopg2.connect(
            dbname="FinanceDB",
            user="postgres",
            password="replace-password",
            host="localhost",
        )
        cur = conn.cursor()

        # Execute each insert statement
        for statement in insert_statements:
            cur.execute(statement)

        # Commit the transaction
        conn.commit()

        # Close the cursor and connection
        cur.close()
        conn.close()

        print("Inserts executed successfully.")
    except psycopg2.DatabaseError as error:
        print(f"Error: {error}")
    finally:
        if conn is not None:
            conn.close()


def __main__():
    # Define the date range
    start_date = datetime(2024, 1, 1)
    end_date = datetime(2024, 12, 31)
    market_place_id = 1  # 1 = OSE, 2 = NASDAQ Stockholm
    trading_hours_id = 1  # 1 = 09:00-16:20, 2 = 09:00-17:30

    # Generate the insert statements
    insert_statements = generate_weekday_inserts(
        start_date, end_date, market_place_id, trading_hours_id
    )

    # Execute the insert statements
    execute_inserts(insert_statements)


if __name__ == "__main__":
    __main__()
