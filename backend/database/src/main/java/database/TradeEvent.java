package database;

import lombok.Getter;

@Getter
public class TradeEvent {

    public enum TradeType {
        BUY('+'), SELL('-');

        @Getter
        private final char type;

        TradeType(char type) { this.type = type; }

    }

    private final TradeType type;
    private final int date;
    private final Symbol symbol;

    public TradeEvent(TradeType type, int date, Symbol symbol) {
        this.type = type;
        this.date = date;
        this.symbol = symbol;
    }

    public static void main(String[] args) {
        TradeEvent tradeEvent = new TradeEvent(TradeType.BUY, 20210101, null);
        System.out.println(tradeEvent.getType().getType());
        System.out.println(tradeEvent.getDate());
    }
}