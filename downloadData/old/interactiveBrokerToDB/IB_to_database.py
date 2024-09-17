import time
from ibapi.client import EClient
from ibapi.wrapper import EWrapper
from ibapi.contract import Contract
from ibapi.ticktype import TickTypeEnum
from dotenv import load_dotenv
import os
from databaseService import Stock, Tick, PostgresConnection


"""
This script is used to connect to the Interactive Brokers API and request market data for a list of stocks.
And insert the tick data into the postgres database. 
"""


class TWSConnection(EWrapper, EClient):
    def __init__(self):
        EClient.__init__(self, self)

    def error(self, reqId, errorCode, errorString):
        print("Error: ", reqId, " ", errorCode, " ", errorString)

    def tickPrice(self, reqId, tickType, price, attrib):
        print(
            f"Tick Price. Ticker Id: {reqId}, tickType: {TickTypeEnum.to_str(tickType)}, Price: {price}",
            end=" ",
        )
        # self.cancelMktData(reqId)
        # self.disconnect()
        # EClient.disconnect(self)

    # def tickSize(self, reqId, tickType, size):
    #     print(
    #         "Tick Size. Ticker Id:",
    #         reqId,
    #         "tickType:",
    #         TickTypeEnum.to_str(tickType),
    #         "Size:",
    #         size,
    #     )


def convert_to_contracts(stocks: list[Stock]) -> list[Contract]:
    contracts = []
    for stock in stocks:
        contract = Contract()
        contract.symbol = stock.ticker
        contract.secType = stock.secType
        contract.exchange = stock.exchange  # "SMART"
        contract.currency = stock.currency
        contracts.append([contract, stock])
    return contracts


def __main__():
    # Load .env file
    load_dotenv()
    ib_IP: str = os.getenv("INTERACTIVE_BROKER_IP")
    ib_PORT: int = int(os.getenv("INTERACTIVE_BROKER_PORT"))

    # Interactive Broker connection
    app = TWSConnection()
    app.connect(ib_IP, ib_PORT, 0)

    # Postgres database connection
    db = PostgresConnection()

    # Get stocks from database
    stocks: list[Stock] = db.get_stocks_in_database()

    # Convert stocks to contracts
    contracts: list[Contract] = convert_to_contracts(stocks)
    contracts_with_list = [
        contracts[i : i + 100] for i in range(0, len(contracts), 100)
    ]
    for contracts in contracts_with_list:

        # Request market data for each stock
        for i, contractAndStock in enumerate(contracts):
            try:
                databaseId: int = contractAndStock[1].id
                contract: Contract = contractAndStock[0]
                app.reqMktData(databaseId, contract, "", False, False, [])
            except Exception as e:
                print(
                    f"Failed to request market data for contract: {contract.symbol}, {e}"
                )
        app.run()


if __name__ == "__main__":
    __main__()
