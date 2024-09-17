import yfinance as yf
import matplotlib.pyplot as plt

# Define the ticker symbol and the period for which you want the data
ticker_symbol = "^GSPC"
period = "50y"
# Download the data
data = yf.download(ticker_symbol, period=period)

# Plot the closing prices
data["Close"].plot(title=f"{ticker_symbol} - Last {period}")
plt.xlabel("Date")
plt.ylabel("Close Price")
plt.show()
