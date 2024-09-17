import yfinance as yf
import pandas as pd

# Dictionary of company names and their respective tickers
companies_with_tickers = {
    "AcadeMedia": "ACAD",
    "Actic Group": "ATIC",
    "Autoliv SDB": "ALIV SDB",
    "Besqab": "BESQ",
    "Betsson B": "BETS B",
    "BHG Group": "BHG",
    "Bilia A": "BILI A",
    "Björn Borg": "BORG",
    "Bonava A": "BONAV A",
    "Bonava B": "BONAV B",
    "Boozt": "BOOZT",
    "Bulten": "BULTEN",
    "Byggmax Group": "BMAX",
    "Catena Media": "CTM",
    "Clas Ohlson B": "CLAS B",
    "Dometic Group": "DOM",
    "Dustin Group": "DUST",
    "Electrolux A": "ELUX A",
    "Electrolux B": "ELUX B",
    "Elon": "ELON",
    "Embracer Group B": "EMBRAC B",
    "Eniro Group": "ENRO",
    "Evolution": "EVO",
    "Fenix Outdoor International B": "FOI B",
    "G5 Entertainment": "G5EN",
    "Gaming Innovation Group": "GIGSEK",
    "Gränges": "GRNG",
    "Hennes & Mauritz B": "HM B",
    "Husqvarna A": "HUSQ A",
    "Husqvarna B": "HUSQ B",
    "KABE Group B": "KABE B",
    "Karnov Group": "KAR",
    "Kindred Group": "KIND SDB",
    "Lammhults Design Group B": "LAMM B",
    "MEKO": "MEKO",
    "Mips": "MIPS",
    "Modern Times Group A": "MTG A",
    "Modern Times Group B": "MTG B",
    "Moment Group": "MOMENT",
    "Nelly Group": "NELLY",
    "New Wave B": "NEWA B",
    "Nilörngruppen B": "NIL B",
    "Nobia": "NOBI",
    "Pierce Group": "PIERCE",
    "Rizzo Group B": "RIZZO B",
    "Rusta": "RUSTA",
    "RVRC Holding": "RVRC",
    "SAS": "SAS",
    "Scandic Hotels Group": "SHOT",
    "SkiStar B": "SKIS B",
    "Starbreeze A": "STAR A",
    "Starbreeze B": "STAR B",
    "Stillfront Group": "SF",
    "Strax": "STRAX",
    "Synsam": "SYNSAM",
    "Thule Group": "THULE",
    "VBG GROUP B": "VBG B",
    "Vestum": "VESTUM",
    "Volvo Car B": "VOLCAR B",
}


# Function to get today's closing price
def get_todays_close(ticker):
    stock = yf.Ticker(ticker)
    hist = stock.history(period="1d")
    return hist["Close"].iloc[0] if not hist.empty else None


# Getting today's closing price for each ticker
# for company, ticker in companies_with_tickers.items():
ticker = "NEWA-B"
if ticker:  # Check if ticker is not None
    ticker = ticker.replace(" ", "-")
    ticker_with_suffix = ticker + ".ST"
    closing_price = get_todays_close(ticker_with_suffix)
    print(f" ({ticker}): {round(closing_price,2)}")
else:
    print(f"has no ticker information.")
