import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from sklearn.linear_model import LinearRegression
from sklearn.preprocessing import PolynomialFeatures

from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import mean_squared_error
from sklearn.preprocessing import OneHotEncoder

def plot_1_day_down():
    data = pd.read_csv('./data/training_1_day_down.csv')

    plt.figure(figsize=(6, 4))
    plt.scatter(data['One_day_change'], data['day_after_change'], c='blue', label='Data points')
    plt.grid(True)
    plt.xlabel('One_day_change', fontsize=14)
    plt.ylabel('day_after_change', fontsize=14)
    plt.title('One_day_change vs. day_after_change', fontsize=16)
    plt.legend()
    plt.show()

def plot_3_day_down():
    data = pd.read_csv('./data/training_3_day_down.csv')

    filtered_data = data[(data['Tree_day_change'] > 10) | (data['Tree_day_change'] < -10)]

    # Plot the filtered data
    plt.figure(figsize=(6, 4))
    plt.scatter(filtered_data['Tree_day_change'], filtered_data['day_after_change'], c='blue', label='Data points')
    plt.grid(True)
    plt.xlabel('Tree_day_change', fontsize=14)
    plt.ylabel('day_after_change', fontsize=14)
    plt.title('Tree_day_change vs. day_after_change', fontsize=16)
    plt.legend()
    # plota stroke on y = 0:
    plt.axhline(y=0, color='r', linestyle='-')
    plt.show()

def plot_drawdown_6_month_return():
    data = pd.read_csv('./data/draw_down_vs_next_6_months.csv')

    # filtered_data = data[(data['Tree_day_change'] > 10) | (data['Tree_day_change'] < -10)]

    # Plot the filtered data
    plt.figure(figsize=(6, 4))
    plt.scatter(data['Draw_down'], data['roc_next_6_months'], c='blue', label='Data points')
    plt.grid(True)
    plt.xlabel('Draw_down', fontsize=14)
    plt.ylabel('roc_next_6_months', fontsize=14)
    plt.title('Draw_down vs. roc_next_6_months', fontsize=16)
    plt.legend()
    # plota stroke on y = 0:
    plt.axhline(y=0, color='r', linestyle='-')
    plt.show()

def plot_drawdown_6_month_return_only_new_low():
    data = pd.read_csv('./data/draw_down_vs_next_6_months_only_new_low.csv')

    filtered_data = data[(data['Draw_down'] < 22) | (data['Draw_down'] < 22)]

    # Plot the filtered data
    plt.figure(figsize=(6, 4))
    plt.scatter(filtered_data['Draw_down'], filtered_data['roc_next_6_months'], c='blue', label='Data points')
    plt.grid(True)
    plt.xlabel('Draw_down', fontsize=14)
    plt.ylabel('roc_next_6_months', fontsize=14)
    plt.title('Draw_down vs. roc_next_6_months', fontsize=16)
    plt.legend()
    # plota stroke on y = 0:
    plt.axhline(y=0, color='r', linestyle='-')
    plt.show()


def plot_drawdown_10_next_year_return_only_new_low():
    data = pd.read_csv('./data/drawdown_ll_10_years_later.csv')

    filtered_data = data#data[(data['Draw_down'] < 22) | (data['Draw_down'] < 22)]

    # Plot the filtered data
    plt.figure(figsize=(6, 4))
    plt.scatter(filtered_data['Draw_down'], filtered_data['Next_10_years'], c='blue', label='Data points')
    plt.grid(True)
    plt.xlabel('Draw_down', fontsize=14)
    plt.ylabel('Next_10_years', fontsize=14)
    plt.title('Draw down (%) vs. Next 10 years (%)', fontsize=16)
    plt.legend()
    # plota stroke on y = 0:
    plt.axhline(y=0, color='r', linestyle='-')
    plt.show()

def plt_eqnr_oil():
    data = pd.read_csv('./data/eqnr_oil_price.csv')

    filtered_data = data#data[(data['oil'] < 22) | (data['oil'] < 22)]

    # Plot the filtered data
    plt.figure(figsize=(6, 4))
    plt.scatter(filtered_data['eqnr'], filtered_data['oil'], c='blue', label='Data points')
    plt.grid(True)
    plt.xlabel('eqnr', fontsize=14)
    plt.ylabel('oil', fontsize=14)
    plt.title('eqnr vs. oil', fontsize=16)
    plt.legend()
    # plota stroke on y = 0:
    plt.axhline(y=0, color='r', linestyle='-')
    plt.show()

def plt_10y_vs_next_10y():
    data = pd.read_csv('./data/10y_vs_next_10y.csv')

    # every quarter
    filtered_data = data.iloc[::13]
    # Plot the filtered data
    plt.figure(figsize=(6, 4))
    plt.scatter(filtered_data['10y'], filtered_data['next_10_years'], c='blue', label='Data points')
    plt.grid(True)
    plt.xlabel('% Return previus 10 years', fontsize=14)
    plt.ylabel('% Return next 10 years', fontsize=14)
    plt.title('Previus 10y (%) vs. next 10 years (%)', fontsize=16)
    plt.legend()
    # plota stroke on y = 0:
    plt.axhline(y=0, color='r', linestyle='-')
    plt.show()



def plt_dow_vs_next_2days():
    data = pd.read_csv('./data/dow_dom_next_2_days.csv')

    # Ensure the data is numeric
    data['day_of_month'] = pd.to_numeric(data['day_of_month'], errors='coerce')
    data['next_2_days'] = pd.to_numeric(data['next_2_days'], errors='coerce')

    # Drop any rows with missing values
    filtered_data = data.dropna()

    # Extracting the features and target variable
    X = filtered_data['day_of_month'].values.reshape(-1, 1)  # Feature
    y = filtered_data['next_2_days'].values  # Target

    # Create polynomial features
    polynomial_features = PolynomialFeatures(degree=30)
    X_poly = polynomial_features.fit_transform(X)

    # Create and fit the polynomial regression model
    model = LinearRegression()
    model.fit(X_poly, y)

    # Generate predictions for the full range of day_of_month
    X_range = np.linspace(X.min(), X.max(), 100).reshape(-1, 1)
    X_range_poly = polynomial_features.transform(X_range)
    y_pred = model.predict(X_range_poly)

    # Plot the filtered data
    plt.figure(figsize=(6, 4))
    plt.scatter(filtered_data['day_of_month'], filtered_data['next_2_days'], c='blue', label='Data points')
    plt.plot(X_range, y_pred, color='green', label='30th Degree Polynomial Regression Line')
    plt.grid(True)
    plt.xlabel('Day of Month', fontsize=14)
    plt.ylabel('Next Two Days (%)', fontsize=14)
    plt.title('Day of Month vs. Next Two Days (%)', fontsize=16)
    plt.legend()
    # Plot a line on y = 0:
    plt.axhline(y=0, color='r', linestyle='-')
    plt.show()




def plot_10_days_later_predictions():
    data_path = './data/dow_dom_x_days_later_2.csv'
    # Load the dataset
    data = pd.read_csv(data_path)

    # Feature columns: day_of_week, day_of_month, and the change columns
    feature_cols = ['day_of_week', 'day_of_month'] + [f'{i}_day_change' for i in range(1, 6)]

    # Target column (3_days_dater in this case)
    target_col = '3_days_dater'

    # Encode day_of_week as a categorical variable using one-hot encoding
    one_hot = OneHotEncoder(sparse_output=False)
    day_of_week_encoded = one_hot.fit_transform(data[['day_of_week']])
    
    # Create a DataFrame with appropriate column names for one-hot encoded features
    day_of_week_encoded_df = pd.DataFrame(day_of_week_encoded, columns=one_hot.get_feature_names_out(['day_of_week']))

    # Apply cyclic transformation to day_of_month
    data['day_of_month_sin'] = np.sin(2 * np.pi * data['day_of_month'] / 31)
    data['day_of_month_cos'] = np.cos(2 * np.pi * data['day_of_month'] / 31)

    # Combine encoded and transformed features, including the new change columns
    X = pd.concat([day_of_week_encoded_df, data[['day_of_month_sin', 'day_of_month_cos']], data[[f'{i}_day_change' for i in range(1, 6)]]], axis=1)
    
    # Extracting the specific target column
    y = data[target_col]

    # Check for consistent lengths of X and y
    if len(X) != len(y):
        raise ValueError(f"Inconsistent number of samples between features and target: {len(X)} vs {len(y)}")

    # Split the data into training and testing sets
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    # Initialize the model
    model = RandomForestRegressor(n_estimators=100, random_state=42)

    # Train the model
    model.fit(X_train, y_train)

    # Predict on the test set
    y_pred = model.predict(X_test)

    # Plotting actual vs predicted values for the target column
    plt.figure(figsize=(10, 6))
    plt.scatter(y_test, y_pred, alpha=0.5, label='Predicted vs Actual')
    plt.plot([min(y_test), max(y_test)], [min(y_test), max(y_test)], 'r--', label='Ideal Prediction')
    plt.xlabel('Actual ' + target_col)
    plt.ylabel('Predicted ' + target_col)
    plt.title('Actual vs Predicted Values for ' + target_col)
    plt.grid(True)
    plt.axhline(y=1, color='g', linestyle='-')
    plt.axvline(x=0, color='g', linestyle='-')
    plt.legend()
    plt.show()

def plot_day_of_month_predictions_btc():
    data_path = './data/btc_usd_model_a.csv'
    data = pd.read_csv(data_path)
    # Load the dataset

    # Ensure the data is numeric
    data['day_of_month'] = pd.to_numeric(data['day_of_month'], errors='coerce')
    data['tenDaysLater'] = pd.to_numeric(data['tenDaysLater'], errors='coerce')

    # Drop any rows with missing values
    filtered_data = data.dropna()

    # Extracting the features and target variable
    X = filtered_data['day_of_month'].values.reshape(-1, 1)  # Feature
    y = filtered_data['tenDaysLater'].values  # Target

    # Create polynomial features
    polynomial_features = PolynomialFeatures(degree=30)
    X_poly = polynomial_features.fit_transform(X)

    # Create and fit the polynomial regression model
    model = LinearRegression()
    model.fit(X_poly, y)

    # Generate predictions for the full range of day_of_month
    X_range = np.linspace(X.min(), X.max(), 100).reshape(-1, 1)
    X_range_poly = polynomial_features.transform(X_range)
    y_pred = model.predict(X_range_poly)

    # Plot the filtered data
    plt.figure(figsize=(6, 4))
    plt.scatter(filtered_data['day_of_month'], filtered_data['tenDaysLater'], c='blue', label='Data points')
    plt.plot(X_range, y_pred, color='green', label='30th Degree Polynomial Regression Line')
    plt.grid(True)
    plt.xlabel('Day of Month', fontsize=14)
    plt.ylabel('tenDaysLater (%)', fontsize=14)
    plt.title('Day of Month vs. tenDaysLater (%)', fontsize=16)
    plt.legend()
    # Plot a line on y = 0:
    plt.axhline(y=0, color='r', linestyle='-')
    plt.show()

def plot_5_days_roc_5_days_later():
    data_path = './data/btc_usd_model_a.csv'
    data = pd.read_csv(data_path)
    # Load the dataset
    filtered_data = data#data.iloc[::13]
    # Plot the filtered data
    plt.figure(figsize=(6, 4))
    plt.scatter(filtered_data['roc5'], filtered_data['tenDaysLater'], c='blue', label='Data points')
    plt.grid(True)
    plt.xlabel('% Return previus 5 days', fontsize=14)
    plt.ylabel('% Return next 5 days', fontsize=14)
    plt.title('Previus 5days (%) vs. next 5 days (%)', fontsize=16)
    plt.legend()
    # plota stroke on y = 0:
    plt.axhline(y=0, color='r', linestyle='-')
    plt.show()

def __main__():
    # plt_dow_vs_next_2days()
    plot_5_days_roc_5_days_later()

if __name__=="__main__":
    __main__()