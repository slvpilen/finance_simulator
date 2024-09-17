package database;

import java.util.List;

import lombok.Builder;

public class Commodity extends Symbol {

    @Builder(builderMethodName = "commodityBuilder")
    public Commodity(int id, String name, String ticker,
            String listed, List<TickData> tickDataList,
            MarketPlace marketPlace, List<Dividend> dividends) {
        super(id, name, ticker, listed, tickDataList,
                marketPlace, dividends);
    }

}
