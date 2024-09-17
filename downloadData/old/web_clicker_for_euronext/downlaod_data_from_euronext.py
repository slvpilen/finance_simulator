from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
import time
from clickers import HistoricalDataEuronexClicker
from import_tickers_from_csv import get_stock_names_from_csv

"""
Dette programmet går til nettsiden https://live.euronext.com/
Der laster den ned data ved å klikke på knapper og lenker.
"""

LINK_TO_OSEBEX_STOCKS = "https://live.euronext.com/en/markets/oslo/equities/list"
# TICKERS = ["2020 BULKERS", "5TH PLANET GAMES"]


def setup_webdriver():
    service = Service(ChromeDriverManager().install())
    return webdriver.Chrome(service=service)


def download_data(*, driver, ticker):
    print("\n")
    print("#" * 10 + ticker + "#" * 10)

    try:
        clicker = HistoricalDataEuronexClicker(driver=driver, ticker=ticker)

        clicker.click_on_tickers_letter_alphabet()

        clicker.click_on_ticker()

        clicker.click_on_accept_cookies()

        clicker.click_on_more_details()

        clicker.click_on_date_input_and_write_date()

        clicker.click_on_download_icon()

        clicker.click_on_comma_delimited()

        clicker.click_on_komma_separator()

        clicker.click_on_download_button()

        time.sleep(3)  # Vent litt

    except Exception as e:
        print("Det oppstod en feil:", e)
        return ticker


def __main__():
    driver = setup_webdriver()

    tickers = get_stock_names_from_csv()

    failed_tickers = []
    for ticker in tickers:
        driver.get(LINK_TO_OSEBEX_STOCKS)
        failed_ticer = download_data(driver=driver, ticker=ticker)
        if failed_ticer:
            failed_tickers += failed_ticer

    time.sleep(10)  # Vent litt, så nettleseren får lastet ned filene
    driver.quit()

    print("Failed tickers:")
    print(failed_tickers)


if __name__ == "__main__":
    __main__()
