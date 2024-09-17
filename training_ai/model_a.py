import pandas as pd
import numpy as np
from sklearn.ensemble import RandomForestRegressor
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import OneHotEncoder


class ModelA:

    def __init__(self):
        self.model = None
        self.one_hot = None

    def fit(self, data=None):
        if data:
            # Convert dictionary to DataFrame if needed
            if isinstance(data, dict):
                data = pd.DataFrame(data)
        else:
            data_path = "./data/dow_dom_x_days_later_2.csv"
            data = pd.read_csv(data_path)

        # Feature columns: day_of_week, day_of_month, and the change columns
        feature_cols = ["day_of_week", "day_of_month"] + [
            f"{i}_day_change" for i in range(1, 6)
        ]

        # Target column (3_days_dater in this case)
        target_col = "10_days_dater"

        # Encode day_of_week as a categorical variable using one-hot encoding
        self.one_hot = OneHotEncoder(sparse_output=False)
        day_of_week_encoded = self.one_hot.fit_transform(data[["day_of_week"]])

        # Create a DataFrame with appropriate column names for one-hot encoded features
        day_of_week_encoded_df = pd.DataFrame(
            day_of_week_encoded,
            columns=self.one_hot.get_feature_names_out(["day_of_week"]),
        )

        # Apply cyclic transformation to day_of_month
        data["day_of_month_sin"] = np.sin(2 * np.pi * data["day_of_month"] / 31)
        data["day_of_month_cos"] = np.cos(2 * np.pi * data["day_of_month"] / 31)

        # Combine encoded and transformed features, including the new change columns
        X = pd.concat(
            [
                day_of_week_encoded_df,
                data[["day_of_month_sin", "day_of_month_cos"]],
                data[[f"{i}_day_change" for i in range(1, 6)]],
                data[["rsi_daily", "rsi_weekly", "rsi_monthly"]],
            ],
            axis=1,
        )

        # Extracting the specific target column
        y = data[target_col]

        # Check for consistent lengths of X and y
        if len(X) != len(y):
            raise ValueError(
                f"Inconsistent number of samples between features and target: {len(X)} vs {len(y)}"
            )

        # Split the data into training and testing sets
        X_train, X_test, y_train, y_test = train_test_split(
            X, y, test_size=0.2, random_state=42
        )

        # Initialize the model
        model = RandomForestRegressor(n_estimators=100, random_state=42)

        # Train the model
        model.fit(X, y)

        self.model = model
        print("Model A is trained")

    # 22497
    # 24253

    def transform_input(self, data):
        # Convert dictionary to DataFrame if needed
        if isinstance(data, dict):
            data = pd.DataFrame([data])  # Convert single dictionary to a DataFrame

        # One-hot encode day_of_week
        day_of_week_encoded = self.one_hot.transform(data[["day_of_week"]])
        day_of_week_encoded_df = pd.DataFrame(
            day_of_week_encoded,
            columns=self.one_hot.get_feature_names_out(["day_of_week"]),
        )

        # Apply cyclic transformation to day_of_month
        data["day_of_month_sin"] = np.sin(2 * np.pi * data["day_of_month"] / 31)
        data["day_of_month_cos"] = np.cos(2 * np.pi * data["day_of_month"] / 31)

        # Combine encoded and transformed features, including the new change columns
        X = pd.concat(
            [
                day_of_week_encoded_df,
                data[["day_of_month_sin", "day_of_month_cos"]],
                data[[f"{i}_day_change" for i in range(1, 6)]],
                data[["rsi_daily", "rsi_weekly", "rsi_monthly"]],
            ],
            axis=1,
        )

        return X

    """
    X_raw: A dictionary containing the following
    day_of_week: Integer values representing the day of the week. Typically, this might be encoded as 0 for Sunday, 1 for Monday, and so on up to 6 for Saturday.
    day_of_month: Integer values representing the day of the month, ranging from 1 to 31.
    1_day_change: Numerical values representing the change on the first day.
    2_day_change: Numerical values representing the change on the second day.
    3_day_change: Numerical values representing the change on the third day.
    4_day_change: Numerical values representing the change on the fourth day.
    5_day_change: Numerical values representing the change on the fifth day.
    """

    def predict(self, X_raw):
        X_transformed = self.transform_input(X_raw)

        # Make predictions
        return self.model.predict(X_transformed)


def __main__():
    model = ModelA()
    model.fit()
    prediction = model.predict(
        {
            "day_of_week": 1,
            "day_of_month": 1,
            "1_day_change": 0.1,
            "2_day_change": 0.2,
            "3_day_change": 0.3,
            "4_day_change": 0.4,
            "5_day_change": 0.5,
        }
    )
    print(prediction)


if __name__ == "__main__":
    __main__()
