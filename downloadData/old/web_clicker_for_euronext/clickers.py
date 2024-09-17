from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait
import time
import pyautogui

"""
Denne file inneholder klassen HistoricalDataEuronexClicker.
Denne klassen inneholder metoder som brukes for å klikke på knapper og lenker på nettsiden https://live.euronext.com/
Dette gjøres for å laste ned historiske data for en gitt ticker.
"""


class HistoricalDataEuronexClicker:
    def __init__(self, *, driver, ticker):
        self.driver = driver
        self.ticker = ticker
        # self.waiting_time = 20

    def click_on_tickers_letter_alphabet(self):
        has_no_alfabetic_ticker = self.ticker[0].isdigit()
        if has_no_alfabetic_ticker:
            print("has no alfabetical ticker")
            return

        number_to_letter = ord(self.ticker[0].lower()) - ord("a")
        alfabeth_id = "nav-tab-alphabet-" + str(number_to_letter)
        link = WebDriverWait(self.driver, 20).until(
            EC.element_to_be_clickable((By.ID, alfabeth_id))
        )
        link.click()
        print("Klikket på lenken til bokstaven " + self.ticker[0])

    def click_on_ticker(self):
        # Vent til lenken med teksten "2020 BULKERS" er lastet og klikkbar (maks 20 sek)
        link = WebDriverWait(self.driver, 20).until(
            EC.element_to_be_clickable((By.LINK_TEXT, self.ticker))
        )
        link.click()
        print("Klikket på lenken til " + self.ticker)

    def click_on_accept_cookies(self):
        try:
            accept_button = WebDriverWait(self.driver, 10).until(
                EC.element_to_be_clickable((By.ID, "onetrust-accept-btn-handler"))
            )
            accept_button.click()
            print("Cookie samtykke akseptert.")
        except Exception as e:
            # Denne trenger kun å trykkes en gang pr. nettleserøkt
            print(f"Kunne ikke finne eller klikke på 'I Accept'-knappen")

    def click_on_more_details(self):
        # Vent til den nye siden lastes, og lenken "More Details" blir klikkbar
        more_details_link = WebDriverWait(self.driver, 20).until(
            EC.element_to_be_clickable(
                (By.CSS_SELECTOR, "a[data-target='#historical-price']")
            )
        )
        self.driver.execute_script("arguments[0].click();", more_details_link)
        print("Klikket på 'More Details'-lenken for: " + self.ticker)

    def click_on_date_input_and_write_date(self):
        # Datoer som er tidligere lovlig date endre automatisk til første mulige dato
        input_date = "2021-11-08"

        # Finn input-elementet for 'From' dato
        input_from_date = WebDriverWait(self.driver, 20).until(
            EC.visibility_of_element_located((By.ID, "datetimepickerFrom"))
        )

        # Rens av eventuell forhåndsutfylt tekst i inputfeltet
        input_from_date.click()  # Fokus på inputfeltet
        # Simuler trykking av Backspace-tasten flere ganger for å slette eksisterende tekst
        time.sleep(1)
        for _ in range(11):
            input_from_date.send_keys(Keys.BACK_SPACE)
            time.sleep(0.1)

        input_from_date.send_keys(input_date)

        input_from_date.send_keys(Keys.RETURN)

        print("Skrev inn dato " + input_date + " for ticker " + self.ticker)

    def click_on_download_icon(self):
        # Finn SVG-elementet som inneholder nedlastningsikonet
        download_button = WebDriverWait(self.driver, 20).until(
            EC.element_to_be_clickable(
                (By.CSS_SELECTOR, "a[data-target='#awl_historical_price_dl']")
            )
        )

        self.driver.execute_script("arguments[0].click();", download_button)

        print("Klikket på nedlastningsikonet for ticker " + self.ticker)

    def click_on_comma_delimited(self):
        # For å få filen på .csv-format
        radio_button = self.driver.find_element(
            By.ID, "edit-format-csv-awl_historical_price"
        )

        self.driver.execute_script("arguments[0].click();", radio_button)
        print("Klikket på Comme Delimited radio button")

    def click_on_komma_separator(self):
        # For å få komma separert fil
        radio_button = self.driver.find_element(
            By.ID, "edit-decimal-separator-,-wrapper-awl_historical_price"
        )
        self.driver.execute_script("arguments[0].click();", radio_button)
        print("Klikket på komma separator radio button")

    def click_on_download_button(self):
        # download_button = self.driver.find_element(By.ID, "submit_dl_form")
        # self.driver.execute_script("arguments[0].click();", download_button)

        for i in range(2, 0, -1):
            print(i)
            time.sleep(1)

        pyautogui.click()

        print("Klikket på download button")
