class Symbol:
    def __init__(self, row: tuple):
        self.id: int = row[0]
        self.name: str = row[1]
        self.ticker: str = row[2]
        self.currency: str = row[3]
        self.exchange: str = row[4]
        self.secType: str = ""
        self.yahooTicker: str = self.ticker.replace(" ", "-")
        if (
            row[5] and self.ticker != "^OMX"
        ):  # Check if there is a yahoo postfix, TODO: handle symbol on same marketplace better!
            self.yahooTicker += row[5]

    def __str__(self) -> str:
        return (
            f"Symbol ID: {self.id}, Name: {self.name}, "
            f"Ticker: {self.ticker}, Currency: {self.currency}, "
            f"Exchange: {self.exchange}, Security Type: {self.secType}"
            f"Yahoo ticker: {self.yahooTicker}"
        )

    def __repr__(self) -> str:
        return self.name


class Stock(Symbol):
    def __init__(self, row: tuple):
        super().__init__(row)
        self.yahooTicker = self.yahooTicker + row[5]
        self.secType = "STK"

    def __str__(self) -> str:
        return (
            f"Stock ID: {self.id}, Name: {self.name}, "
            f"Ticker: {self.ticker}, Currency: {self.currency}, "
            f"Exchange: {self.exchange}, Security Type: {self.secType}"
            f"Yahoo ticker: {self.yahooTicker}"
        )


class Tick:
    def __init__(self, **data) -> None:
        self.time_of_day: str = data["time_of_day"]
        self.tick_daily_id: int = data["tick_daily_id"]
        self.close: float = data["close"]
        self.volume: int = data["volume"]
        self.bid: float = data["bid"]
        self.time: float = data["ask"]
