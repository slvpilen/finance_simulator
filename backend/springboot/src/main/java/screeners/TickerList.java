package screeners;

import java.util.Arrays;
import java.util.List;

public enum TickerList {
    COUNTRY(Arrays.asList("EWA", "EWO", "EWK", "EWZ", "EWC",
            "ECH", "GXC", "GXG", "EWQ", "EWG", "EWH", "PIN",
            "IDX", "EIRL", "EIS", "EWI", "EWM", "EWW", "EWN",
            "EPU", "EPOL", "EWS", "EZA", "EWY", "EWP", "EWD",
            "EWL", "EWT", "THD", "TUR", "EWU", "EUSA", "VNM",
            "ENZL", "NORW", "EPHE", "QAT", "UAE", "GREK", "EWJ",
            "KSA", "ARGT", "EDEN", "EFNL", "KWT")),
    SECTOR(Arrays.asList("XLRE", "XLP", "XLU", "XLB", "XLV",
            "XLY", "XLE", "XLI", "XLF", "XLK", "XLC")),
    NORDEA_STABIL_GLOBAL(
            Arrays.asList("Nordea Stabil Aksjer Global"));

    private final List<String> tickers;

    TickerList(List<String> tickers) { this.tickers = tickers; }

    public List<String> getTickers() { return tickers; }
}