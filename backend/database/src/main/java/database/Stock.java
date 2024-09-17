package database;

import java.util.Arrays;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

public class Stock extends Symbol {

    @Getter
    private final StockIndustri industry;

    @Builder(builderMethodName = "stockBuilder")
    public Stock(int id, String name, String ticker,
            String listed, List<TickData> tickDataList,
            MarketPlace marketPlace, String industry,
            List<Dividend> dividends) {
        super(id, name, ticker, listed, tickDataList,
                marketPlace, dividends);
        this.industry = StockIndustri.fromIndustryName(industry); // .intern()
    }

    public static void main(String[] args) {
        Stock eqnr = new Stock.StockBuilder().name("equinor")
                .ticker("eqnr").listed("Oslo Børs")
                .tickDataList(Arrays.asList()).industry("OIL")
                .build();

        System.out.println(eqnr.getIndustry());
    }

}
