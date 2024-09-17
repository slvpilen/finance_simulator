package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import database.apiModels.CandleStickChartData;
import database.apiModels.Holding;
import database.makroTrend.Settings;
import utils.HashArray;

// TODO: move all strategyrelated to DBServiceStrategy (and rename this to financial? so matching schema)
public class DatabaseService {

        private static Connection connection = DatabaseConnection
                        .getConnection();

        public static Stock getStock(String ticker) {
                String queryForStock = "SELECT * FROM financial.stock_with_full_info('"
                                + ticker + "')";

                List<Stock> stocks = getStocksFromQuery(
                                queryForStock);

                if (stocks.size() == 0) {
                        throw new IllegalArgumentException(
                                        "Ticker not found in database: "
                                                        + ticker);
                }

                if (stocks.size() != 1) {
                        throw new IllegalArgumentException(
                                        "Found more than one stock with ticker: "
                                                        + ticker);
                }

                return stocks.get(0);
        }

        /*
         * @return Symbol with all ticks in database
         */
        public static List<Symbol> getSymbols(
                        List<String> tickers) {
                List<Symbol> symbols = new ArrayList<>();
                tickers.forEach(ticker -> symbols
                                .add(getSymbol(ticker)));
                return symbols;
        }

        public static List<Symbol> getSymbols(
                        List<String> tickers, int startDate,
                        int endDate) {
                List<Symbol> symbols = new ArrayList<>();
                tickers.forEach(ticker -> symbols.add(getSymbol(
                                ticker, startDate, endDate)));
                return symbols;
        }

        public static List<Symbol> getSymbols(
                        String symbolListName, int startDate,
                        int endDate) {
                String query = "SELECT symbol_name, ticker, listed_name, symbol_id, market_place_id FROM financial.list_symbols_detailed WHERE list_name = '"
                                + symbolListName + "'";

                Map<Integer, MarketPlace> idMarketPlacesMap = getIdMarketPlacesMap();
                List<Symbol> symbols = new ArrayList<>();
                try (Statement statement = connection
                                .createStatement();
                                ResultSet resultSet = statement
                                                .executeQuery(query)) {

                        while (resultSet.next()) {
                                String name = resultSet
                                                .getString("symbol_name");
                                String ticker = resultSet
                                                .getString("ticker");
                                String listed = resultSet
                                                .getString("listed_name"); // listed
                                int symbolId = resultSet.getInt(
                                                "symbol_id");
                                int marketPlaceId = resultSet
                                                .getInt("market_place_id");
                                MarketPlace marketPlace = idMarketPlacesMap
                                                .get(marketPlaceId);

                                List<TickData> tickDatas = getTickDatas(
                                                symbolId,
                                                startDate,
                                                endDate);

                                List<Dividend> dividends = getDividends(
                                                symbolId);

                                Symbol symbol = new Symbol.SymbolBuilder()
                                                .id(symbolId)
                                                .listed(listed)
                                                .name(name)
                                                .ticker(ticker)
                                                .tickDataList(tickDatas)
                                                .marketPlace(marketPlace)
                                                .dividendsList(dividends)
                                                .build();

                                symbols.add(symbol);
                        }
                } catch (SQLException e) {
                        throw new RuntimeException(
                                        "Failed to retrieve data",
                                        e);
                }
                return symbols;
        }

        private static Symbol getSymbol(String ticker,
                        int startDate, int endDate) {
                String queryForSymbol = "SELECT * FROM financial.symbol_with_full_info('"
                                + ticker + "')";

                List<Symbol> symbols = getSymbolsFromQuery(
                                queryForSymbol, startDate,
                                endDate);

                if (symbols.size() == 0) {
                        throw new IllegalArgumentException(
                                        "Ticker not found in database: "
                                                        + ticker);
                }

                if (symbols.size() != 1) {
                        throw new IllegalArgumentException(
                                        "Found more than one stock with ticker: "
                                                        + ticker
                                                        + ". Symbol is unique ticker and listed");
                }

                return symbols.get(0);
        }

        private static List<Symbol> getSymbolsFromQuery(
                        String queryForSymbol, int startDate,
                        int endDate) {
                Map<Integer, MarketPlace> idMarketPlacesMap = getIdMarketPlacesMap();

                List<Symbol> symbols = new ArrayList<>();

                try (Statement statement = connection
                                .createStatement();
                                ResultSet resultSet = statement
                                                .executeQuery(queryForSymbol)) {

                        while (resultSet.next()) {
                                String name = resultSet
                                                .getString("name");
                                String ticker = resultSet
                                                .getString("ticker");
                                String listed = resultSet
                                                .getString("listed_name"); // listed
                                int symbolId = resultSet
                                                .getInt("id");
                                int marketPlaceId = resultSet
                                                .getInt("market_place_id");
                                MarketPlace marketPlace = idMarketPlacesMap
                                                .get(marketPlaceId);

                                List<TickData> tickDatas = getTickDatas(
                                                symbolId,
                                                startDate,
                                                endDate);

                                List<Dividend> dividends = getDividends(
                                                symbolId);

                                Symbol symbol = new Symbol.SymbolBuilder()
                                                .id(symbolId)
                                                .listed(listed)
                                                .name(name)
                                                .ticker(ticker)
                                                .tickDataList(tickDatas)
                                                .marketPlace(marketPlace)
                                                .dividendsList(dividends)
                                                .build();

                                symbols.add(symbol);
                        }
                } catch (SQLException e) {
                        throw new RuntimeException(
                                        "Failed to retrieve data",
                                        e);
                }
                return symbols;
        }

        public static List<Symbol> getSymbolsListedOn(
                        String listedName) {
                String query = "SELECT * FROM financial.symbol_detailed WHERE listed_name = '"
                                + listedName + "'";
                Map<Integer, MarketPlace> idMarketPlacesMap = getIdMarketPlacesMap();

                List<Symbol> symbols = new ArrayList<>();

                try (Statement statement = connection
                                .createStatement();
                                ResultSet resultSet = statement
                                                .executeQuery(query)) {

                        while (resultSet.next()) {
                                String name = resultSet
                                                .getString("symbol_name");
                                String ticker = resultSet
                                                .getString("ticker");
                                String listed = resultSet
                                                .getString("listed_name"); // listed
                                int symbolId = resultSet.getInt(
                                                "symbol_id");
                                int marketPlaceId = resultSet
                                                .getInt("market_place_id");
                                MarketPlace marketPlace = idMarketPlacesMap
                                                .get(marketPlaceId);

                                List<TickData> tickDatas = getMaxTickDatas(
                                                symbolId);

                                List<Dividend> dividends = getDividends(
                                                symbolId);

                                Symbol symbol = new Symbol.SymbolBuilder()
                                                .id(symbolId)
                                                .listed(listed)
                                                .name(name)
                                                .ticker(ticker)
                                                .tickDataList(tickDatas)
                                                .marketPlace(marketPlace)
                                                .dividendsList(dividends)
                                                .build();

                                symbols.add(symbol);
                        }
                } catch (SQLException e) {
                        throw new RuntimeException(
                                        "Failed to retrieve data",
                                        e);
                }
                return symbols;
        }

        /*
         * @return Symbol with all ticks in database
         */
        public static Symbol getSymbol(String ticker) {
                String queryForSymbol = "SELECT * FROM financial.symbol_with_full_info('"
                                + ticker + "')";

                List<Symbol> symbols = getSymbolsFromQuery(
                                queryForSymbol);

                if (symbols.size() == 0) {
                        throw new IllegalArgumentException(
                                        "Ticker not found in database: "
                                                        + ticker);
                }

                if (symbols.size() != 1) {
                        throw new IllegalArgumentException(
                                        "Found more than one stock with ticker: "
                                                        + ticker);
                }

                return symbols.get(0);
        }

        public static CandleStickChartData getSymbolCandleStickChartData(
                        String ticker) {
                String query = "SELECT * FROM financial.symbol_with_full_info('"
                                + ticker + "')";

                try (Statement statement = connection
                                .createStatement();
                                ResultSet resultSet = statement
                                                .executeQuery(query)) {

                        while (resultSet.next()) {
                                CandleStickChartData symbolOnlyTicksAndSimpleMeta = new CandleStickChartData();

                                symbolOnlyTicksAndSimpleMeta.ticker = resultSet
                                                .getString("ticker");
                                symbolOnlyTicksAndSimpleMeta.name = resultSet
                                                .getString("name");
                                symbolOnlyTicksAndSimpleMeta.listed = resultSet
                                                .getString("listed_name");

                                int symbolId = resultSet
                                                .getInt("id");
                                List<TickData> ticks = getMaxTickDatas(
                                                symbolId);
                                ticks.sort((t1, t2) -> Integer
                                                .compare(t1.getDate(),
                                                                t2.getDate()));
                                symbolOnlyTicksAndSimpleMeta.ticksSorted = ticks;

                                return symbolOnlyTicksAndSimpleMeta;
                        }
                        return null;
                } catch (SQLException e) {
                        throw new IllegalArgumentException(
                                        "Failed to retrieve data",
                                        e);
                }

        }

        public static Commodity getCommodity(String ticker) {
                String queryForCommodity = "SELECT * FROM financial.commodity_with_full_info('"
                                + ticker + "')";

                List<Commodity> commodities = getCommoditiesFromQuery(
                                queryForCommodity);

                if (commodities.size() == 0) {
                        throw new IllegalArgumentException(
                                        "Ticker not found in database: "
                                                        + ticker);
                }

                if (commodities.size() != 1) {
                        throw new IllegalArgumentException(
                                        "Found more than one commodity with ticker: "
                                                        + ticker);
                }

                return commodities.get(0);
        }

        public static Makro getMakro(String type) {
                String queryForMakro = "SELECT id, frequence FROM financial.makro WHERE type = '"
                                + type + "'";

                Makro makro = null;

                try (Statement statement = connection
                                .createStatement();
                                ResultSet resultSet = statement
                                                .executeQuery(queryForMakro)) {

                        while (resultSet.next()) {
                                String frequence = resultSet
                                                .getString("frequence");
                                String makroId = resultSet
                                                .getString("id");
                                HashArray<Integer, Double> ticks = getMakroTicks(
                                                makroId);

                                makro = new Makro.MakroBuilder()
                                                .type(type)
                                                .frequence(frequence)
                                                .ticks(ticks)
                                                .build();
                        }
                } catch (SQLException e) {
                        throw new RuntimeException(
                                        "Failed to retrieve data",
                                        e);
                }
                return makro;
        }

        private static HashArray<Integer, Double> getMakroTicks(
                        String makroId) {
                String query = "SELECT * FROM financial.makro_tick WHERE makro_id = "
                                + makroId;
                HashArray<Integer, Double> makroTicks = getMakroTicksFromQuery(
                                query);
                makroTicks.sortBasedKey();

                return makroTicks;
        }

        private static HashArray<Integer, Double> getMakroTicksFromQuery(
                        String query) {
                HashArray<Integer, Double> makroTicks = new HashArray<>();

                try (Statement statement = connection
                                .createStatement();
                                ResultSet resultSet = statement
                                                .executeQuery(query)) {

                        while (resultSet.next()) {
                                double value = resultSet
                                                .getDouble("value");
                                int date = convertDateToInteger(
                                                resultSet.getDate(
                                                                "publish"));
                                makroTicks.put(date, value);
                        }
                } catch (SQLException e) {
                        throw new RuntimeException(
                                        "Failed to retrieve tick data",
                                        e);
                }
                return makroTicks;
        }

        public static Map<String, String> getStockNamesAndInsustries(
                        String strategyName) {
                String queryStockNameAndIndustry = "SELECT * FROM strategy.stock_name_and_industry('"
                                + strategyName + "')";
                Map<String, String> stockNamesAndIndustris = getStockNamesAndInsustriesFromQuery(
                                queryStockNameAndIndustry);

                return stockNamesAndIndustris;
        }

        public static database.makroTrend.Settings getMakroTrendSettings(
                        int createdYear) {
                String query = "SELECT * FROM strategy.makro_trend_settings WHERE EXTRACT(YEAR FROM created) = "
                                + createdYear;

                database.makroTrend.Settings settings = new Settings();
                int settingsCreatedInYearCount = 0;
                try (Statement statement = connection
                                .createStatement();
                                ResultSet resultSet = statement
                                                .executeQuery(query)) {
                        while (resultSet.next()) {
                                settings.stockRankedWithin = resultSet
                                                .getInt("stockrankedwithin");
                                settings.industriScoreWeightingRoc22 = resultSet
                                                .getInt("industriscoreweightingroc22");
                                settings.industriScoreWeightingRoc43 = resultSet
                                                .getInt("industriscoreweightingroc43");
                                settings.industriScoreWeightingRoc82 = resultSet
                                                .getInt("industriscoreweightingroc82");
                                settings.industriScoreWeightingPredictiveAverage = resultSet
                                                .getInt("industriscoreweightingpredictiveaverage");
                                settingsCreatedInYearCount++;
                        }
                } catch (SQLException e) {
                        throw new RuntimeException(
                                        "Failed to retrieve data",
                                        e);
                }
                boolean hasSettingsInDB = settingsCreatedInYearCount > 0;
                if (!hasSettingsInDB) {
                        throw new IllegalArgumentException(
                                        "Could found settings for crated year="
                                                        + createdYear);
                }

                boolean hasMoreThenOneSettingsForYear = settingsCreatedInYearCount > 1;
                if (hasMoreThenOneSettingsForYear) {
                        throw new IllegalArgumentException(
                                        "Found more then 1 settings with created year="
                                                        + createdYear);
                }
                return settings;

        }

        public static List<MarketPlace> getMarketPlacesForStrategy(
                        String strategyName) {
                Map<Integer, MarketPlace> idMarketPlacesMap = getIdMarketPlacesMap();
                List<MarketPlace> marketPlacesRelatedToStrategy = new ArrayList<>();

                String query = "SELECT * FROM strategy.get_market_place_ids_by_strategy_name('"
                                + strategyName + "')";
                try (Statement statement = connection
                                .createStatement();
                                ResultSet resultSet = statement
                                                .executeQuery(query)) {

                        while (resultSet.next()) {
                                int market_place_id = resultSet
                                                .getInt("market_place_id");
                                marketPlacesRelatedToStrategy
                                                .add(idMarketPlacesMap
                                                                .get(market_place_id));
                        }
                } catch (SQLException e) {
                        throw new RuntimeException(
                                        "Failed to retrieve data",
                                        e);
                }

                return marketPlacesRelatedToStrategy;
        }

        public static List<Holding> getStrategyHoldings(
                        String strategyName, String resultName) {
                List<Holding> holdings = new ArrayList<>();
                String query = "SELECT sym_ticker, sym_name, holding_date FROM strategy.holding_detailed WHERE strategy_name = '"
                                + strategyName
                                + "' AND result_name = '"
                                + resultName + "'";
                try (Statement statement = connection
                                .createStatement();
                                ResultSet resultSet = statement
                                                .executeQuery(query)) {

                        while (resultSet.next()) {
                                Holding holding = new Holding();
                                holding.symbolName = resultSet
                                                .getString("sym_name");
                                holding.symbolTicker = resultSet
                                                .getString("sym_ticker");
                                holding.date = convertDateToInteger(
                                                resultSet.getDate(
                                                                "holding_date"));
                                holdings.add(holding);
                        }
                } catch (SQLException e) {
                        throw new RuntimeException(
                                        "Failed to retrieve holdings symbol name",
                                        e);
                }
                return holdings;
        }

        // public static void storeHoldings(String strategyName, List<Symbol> holdings,
        // int date) {
        // String query = "INSERT INTO strategy_holding (symbol_id, strategy_id,
        // holding_date) VALUES ";

        // for (Symbol symbol : holdings) {
        // String fullQuery = query + "(" + symbol.getId() + ", strategy.id, " + date
        // + ") FROM strategy WHERE name = '"
        // + strategyName + "'";
        // }
        // }

        public static void storeHoldings(String strategyName,
                        List<Symbol> holdings, int date,
                        String resultName) {
                String query = "INSERT INTO strategy.strategy_holding (symbol_id, strategy_id, holding_date, result_id) "
                                + "SELECT ?, (SELECT id FROM strategy.strategy WHERE name = ?), ?, (SELECT id FROM strategy.strategy_result WHERE name = ? AND strategy_id = (SELECT id FROM strategy.strategy WHERE name = ?))"
                                + "ON CONFLICT (symbol_id, strategy_id, holding_date, result_id) DO NOTHING";

                try (PreparedStatement pstmt = connection
                                .prepareStatement(query)) {

                        for (Symbol symbol : holdings) {
                                pstmt.setInt(1, symbol.getId());
                                pstmt.setString(2, strategyName);

                                pstmt.setDate(3, convertDate(
                                                date));
                                pstmt.setString(4, resultName);
                                pstmt.setString(5, strategyName);

                                pstmt.addBatch(); // Prepare this set of parameters for batch execution
                        }

                        pstmt.executeBatch(); // Execute all the prepared INSERT statements as a batch
                } catch (SQLException e) {
                        e.printStackTrace();
                }
        }

        public static void storeNewMakroTrendSettings(
                        Settings settings, int date,
                        String strategyName) {
                String query = "INSERT INTO strategy.makro_trend_settings (created, stockrankedwithin, industriscoreweightingroc22, "
                                + "industriscoreweightingroc43, industriscoreweightingroc82, industriscoreweightingpredictiveaverage, strategy_id) "
                                + "VALUES (?, ?, ?, ?, ?, ?, (SELECT id FROM strategy.strategy WHERE name = ?))";

                try (PreparedStatement pstmt = connection
                                .prepareStatement(query)) {

                        pstmt.setDate(1, convertDate(date));
                        pstmt.setInt(2, settings
                                        .getStockRankedWithin());
                        pstmt.setInt(3, settings
                                        .getIndustriScoreWeightingRoc22());
                        pstmt.setInt(4, settings
                                        .getIndustriScoreWeightingRoc43());
                        pstmt.setInt(5, settings
                                        .getIndustriScoreWeightingRoc82());
                        pstmt.setInt(6, settings
                                        .getIndustriScoreWeightingPredictiveAverage());
                        pstmt.setString(7, strategyName);

                        pstmt.addBatch(); // Prepare this set of parameters for batch execution

                        pstmt.executeBatch(); // Execute all the prepared INSERT statements as a batch
                } catch (SQLException e) {
                        e.printStackTrace();
                }

        }

        protected static java.sql.Date convertDate(int date) {
                SimpleDateFormat sdf = new SimpleDateFormat(
                                "yyyyMMdd");
                String dateString = Integer.toString(date);
                try {
                        java.util.Date parsedDate = sdf
                                        .parse(dateString);
                        java.sql.Date sqlDate = new java.sql.Date(
                                        parsedDate.getTime());
                        return sqlDate;
                } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(
                                        "Failed to convert date",
                                        e);
                }

        }

        // TODO: De to neste metodene likner, skriv om
        private static Map<String, String> getStockNamesAndInsustriesFromQuery(
                        String query) {
                Map<String, String> stockNamesAndIndustris = new HashMap<>();
                try (Statement statement = connection
                                .createStatement();
                                ResultSet resultSet = statement
                                                .executeQuery(query)) {

                        while (resultSet.next()) {
                                String name = resultSet
                                                .getString("stock_name")
                                                .intern();
                                String industry = resultSet
                                                .getString("industry_name")
                                                .intern();

                                stockNamesAndIndustris.put(name,
                                                industry);
                        }
                } catch (SQLException e) {
                        throw new RuntimeException(
                                        "Failed to retrieve stock names and industries from Query",
                                        e);
                }
                return stockNamesAndIndustris;
        }

        static List<Stock> getStocksFromQuery(String query) {
                Map<Integer, MarketPlace> idMarketPlacesMap = getIdMarketPlacesMap();

                List<Stock> stocks = new ArrayList<>();

                try (Statement statement = connection
                                .createStatement();
                                ResultSet resultSet = statement
                                                .executeQuery(query)) {

                        while (resultSet.next()) {
                                String name = resultSet
                                                .getString("name");
                                String ticker = resultSet
                                                .getString("ticker");
                                String industry = resultSet
                                                .getString("industry_name");
                                String listed = resultSet
                                                .getString("listed_name"); // listed
                                int symbolId = resultSet
                                                .getInt("id");
                                int marketPlaceId = resultSet
                                                .getInt("market_place_id");
                                MarketPlace marketPlace = idMarketPlacesMap
                                                .get(marketPlaceId);

                                List<TickData> tickDatas = getMaxTickDatas(
                                                symbolId);
                                List<Dividend> dividends = getDividends(
                                                symbolId);

                                Stock stock = new Stock.StockBuilder()
                                                .id(symbolId)
                                                .industry(industry)
                                                .listed(listed)
                                                .name(name)
                                                .ticker(ticker)
                                                .tickDataList(tickDatas)
                                                .marketPlace(marketPlace)
                                                .dividends(dividends)
                                                .build();

                                stocks.add(stock);
                        }
                } catch (SQLException e) {
                        throw new RuntimeException(
                                        "Failed to retrieve stocks from query",
                                        e);
                }
                return stocks;
        }

        // TODO: this is similar to getStocksQuery, make some generic method
        protected static List<Symbol> getSymbolsFromQuery(
                        String query) {
                Map<Integer, MarketPlace> idMarketPlacesMap = getIdMarketPlacesMap();

                List<Symbol> symbols = new ArrayList<>();

                try (Statement statement = connection
                                .createStatement();
                                ResultSet resultSet = statement
                                                .executeQuery(query)) {

                        while (resultSet.next()) {
                                String name = resultSet
                                                .getString("name");
                                String ticker = resultSet
                                                .getString("ticker");
                                String listed = resultSet
                                                .getString("listed_name"); // listed
                                int symbolId = resultSet
                                                .getInt("id");
                                int marketPlaceId = resultSet
                                                .getInt("market_place_id");
                                MarketPlace marketPlace = idMarketPlacesMap
                                                .get(marketPlaceId);

                                List<TickData> tickDatas = getMaxTickDatas(
                                                symbolId);

                                List<Dividend> dividends = getDividends(
                                                symbolId);

                                Symbol symbol = new Symbol.SymbolBuilder()
                                                .id(symbolId)
                                                .listed(listed)
                                                .name(name)
                                                .ticker(ticker)
                                                .tickDataList(tickDatas)
                                                .marketPlace(marketPlace)
                                                .dividendsList(dividends)
                                                .build();

                                symbols.add(symbol);
                        }
                } catch (SQLException e) {
                        throw new RuntimeException(
                                        "Failed to retrieve data",
                                        e);
                }
                return symbols;
        }

        private static List<Commodity> getCommoditiesFromQuery(
                        String query) {
                Map<Integer, MarketPlace> idMarketPlacesMap = getIdMarketPlacesMap();

                List<Commodity> commodoties = new ArrayList<>();

                try (Statement statement = connection
                                .createStatement();
                                ResultSet resultSet = statement
                                                .executeQuery(query)) {

                        while (resultSet.next()) {
                                String name = resultSet
                                                .getString("name");
                                String ticker = resultSet
                                                .getString("ticker");
                                String listed = resultSet
                                                .getString("listed_name"); // listed
                                int symbolId = resultSet
                                                .getInt("id");
                                int marketPlaceId = resultSet
                                                .getInt("market_place_id");
                                MarketPlace marketPlace = idMarketPlacesMap
                                                .get(marketPlaceId);

                                List<TickData> tickDatas = getMaxTickDatas(
                                                symbolId);

                                List<Dividend> dividends = getDividends(
                                                symbolId);

                                Commodity commodity = new Commodity.CommodityBuilder()
                                                .id(symbolId)
                                                .listed(listed)
                                                .name(name)
                                                .ticker(ticker)
                                                .tickDataList(tickDatas)
                                                .marketPlace(marketPlace)
                                                .dividends(dividends)
                                                .build();

                                commodoties.add(commodity);
                        }
                } catch (SQLException e) {
                        throw new RuntimeException(
                                        "Failed to retrieve data",
                                        e);
                }
                return commodoties;
        }

        /**
         * 
         * @return Integer is id for the market place
         */
        private static Map<Integer, MarketPlace> idMarketPlaceMapCache; // Called multiple times, so store it

        private static Map<Integer, MarketPlace> getIdMarketPlacesMap() {
                if (idMarketPlaceMapCache != null) {
                        return idMarketPlaceMapCache;
                }

                String queryAllMarketPlace = "SELECT * FROM financial.market_place";

                Map<Integer, MarketPlace> idMarketPlacesMap = new HashMap<>();

                try (Statement statement = connection
                                .createStatement();
                                ResultSet resultSet = statement
                                                .executeQuery(queryAllMarketPlace)) {

                        while (resultSet.next()) {
                                int market_place_id = resultSet
                                                .getInt("id");
                                String name = resultSet
                                                .getString("name");
                                String country = resultSet
                                                .getString("country");
                                String city = resultSet
                                                .getString("city");
                                String currency = resultSet
                                                .getString("currency");
                                String timeZone = resultSet
                                                .getString("time_zone");
                                HashArray<Integer, TradingHours> dateTradingHours = getDateTradingHours(
                                                market_place_id); // Sorted

                                MarketPlace marketPlace = new MarketPlace.MarketPlaceBuilder()
                                                .name(name)
                                                .country(country)
                                                .city(city)
                                                .currency(currency)
                                                .timeZone(timeZone)
                                                .dateTradingHours(
                                                                dateTradingHours)
                                                .build();

                                idMarketPlacesMap.put(
                                                market_place_id,
                                                marketPlace);
                        }
                } catch (SQLException e) {
                        throw new RuntimeException(
                                        "Failed to retrieve market place",
                                        e);
                }

                DatabaseService.idMarketPlaceMapCache = idMarketPlacesMap;
                return idMarketPlacesMap;
        }

        // TODO: fix all function to work with new schema. Rewrite it, so its using view
        // instead of function

        private static HashArray<Integer, TradingHours> getDateTradingHours(
                        int market_place_id) { // Sorted
                String queryDateTradingHours = "SELECT * FROM financial.date_trading_hours("
                                + market_place_id + ")";

                HashArray<Integer, TradingHours> dateTradingHours = new HashArray<>();
                try (Statement statement = connection
                                .createStatement();
                                ResultSet resultSet = statement
                                                .executeQuery(queryDateTradingHours)) {
                        while (resultSet.next()) {
                                int date = convertDateToInteger(
                                                resultSet.getDate(
                                                                "trading_date"));
                                int openTime = convertTimeToInteger(
                                                resultSet.getTime(
                                                                "open_time"));
                                int closeTime = convertTimeToInteger(
                                                resultSet.getTime(
                                                                "close_time"));

                                TradingHours tradingHours = new TradingHours(
                                                openTime,
                                                closeTime);

                                dateTradingHours.put(date,
                                                tradingHours);
                        }
                } catch (SQLException e) {
                        throw new RuntimeException(
                                        "Failed to retrieve trading hours data",
                                        e);
                }
                dateTradingHours.sortBasedKey();
                return dateTradingHours;
        }

        private static int convertTimeToInteger(
                        java.sql.Time sqlTime) {
                SimpleDateFormat sdf = new SimpleDateFormat(
                                "HHmm");
                String formattedTime = sdf.format(sqlTime);
                int time = Integer.parseInt(formattedTime);
                return time;
        }

        private static List<TickData> getMaxTickDatas(
                        int symbolo_id) {
                String query = "SELECT * FROM financial.tick_daily WHERE symbol_id = "
                                + symbolo_id;
                List<TickData> tickDatas = getTickDatasFromQuery(
                                query);

                return tickDatas;
        }

        private static List<TickData> getTickDatas(int symbol_id,
                        int startDate, int endDate) {
                // convert date to sql date
                java.sql.Date sqlStartDate = convertDate(
                                startDate);
                java.sql.Date sqlEndDate = convertDate(endDate);

                String query = "SELECT * FROM financial.tick_daily WHERE symbol_id = "
                                + symbol_id + " AND date >= '"
                                + sqlStartDate
                                + "' AND date <= '" + sqlEndDate
                                + "'";
                List<TickData> tickDatas = getTickDatasFromQuery(
                                query);

                return tickDatas;
        }

        private static List<TickData> getTickDatasFromQuery(
                        String query) {
                List<TickData> tickDatas = new ArrayList<>();

                try (Statement statement = connection
                                .createStatement();
                                ResultSet resultSet = statement
                                                .executeQuery(query)) {

                        while (resultSet.next()) {
                                TickData tickData = createTickDataFromResultSet(
                                                resultSet);
                                tickDatas.add(tickData);
                        }
                } catch (SQLException e) {
                        throw new RuntimeException(
                                        "Failed to retrieve tick tick datas",
                                        e);
                }
                return tickDatas;
        }

        private static List<Dividend> getDividends(
                        int symbol_id) {
                String query = "SELECT * FROM financial.dividend WHERE symbol_id = "
                                + symbol_id;
                List<Dividend> dividends = getDividendsFromQuery(
                                query);

                return dividends;
        }

        private static List<Dividend> getDividendsFromQuery(
                        String query) {
                List<Dividend> dividends = new ArrayList<>();

                try (Statement statement = connection
                                .createStatement();
                                ResultSet resultSet = statement
                                                .executeQuery(query)) {

                        while (resultSet.next()) {
                                int date = convertDateToInteger(
                                                resultSet.getDate(
                                                                "date"));
                                Double value = resultSet
                                                .getDouble("value");
                                Dividend dividend = Dividend
                                                .dividendBuilder()
                                                .date(date)
                                                .value(value)
                                                .build();

                                dividends.add(dividend);
                        }
                } catch (SQLException e) {
                        throw new RuntimeException(
                                        "Failed to retrieve data",
                                        e);
                }
                return dividends;
        }

        // Todo: use: convertDateToInteger
        protected static TickData createTickDataFromResultSet(
                        ResultSet tickResultSet)
                        throws SQLException {
                int date = convertDateToInteger(
                                tickResultSet.getDate("date"));

                double open = tickResultSet.getDouble("open");
                double high = tickResultSet.getDouble("high");
                double low = tickResultSet.getDouble("low");
                double close = tickResultSet.getDouble("close");
                double adjClose = tickResultSet
                                .getDouble("adjClose");
                int volume = (int) tickResultSet
                                .getDouble("volume");

                TickData tickData = new TickData.TickDataBuilder()
                                .date(date).open(open).high(high)
                                .low(low).close(close)
                                .adjClose(adjClose)
                                .volume(volume).build();
                return tickData;

        }

        protected static int convertDateToInteger(
                        java.sql.Date sqlDate) {
                SimpleDateFormat sdf = new SimpleDateFormat(
                                "yyyyMMdd");
                String formattedDate = sdf.format(sqlDate);
                int date = Integer.parseInt(formattedDate);
                return date;
        }

        public static void close() {
                try {
                        if (connection != null && !connection
                                        .isClosed()) {
                                connection.close();
                        }
                } catch (SQLException e) {
                        throw new RuntimeException(
                                        "Failed to close connection",
                                        e);
                }
        }

        /**
         * Test of database
         */
        public static void main(String[] args) {

                // TODO: move this to junit test
                // String strategyName = "makro-trend";

                // System.out.println("Test for strategy " + strategyName);

                // List<Stock> stocksForStrategy = DatabaseService.getStocks(strategyName);

                // System.out.println("Number og stock in strategy: " +
                // stocksForStrategy.size());
                // // System.out.println(
                // // "Dates for market to first stock in list: " +
                // // stocksForStrategy.get(0).getMarketPlace().getDates());

                // // System.out.println("--------------------");

                // // System.out.println("Test of database industry and tickers");
                // // Map<String, String> namesAndTickers =
                // // getStockNamesAndInsustries(strategyName);
                // // System.out.println(namesAndTickers);

                // System.out.println("--------------------");

                // System.out.println("Test of database ticker eqnr: ");
                // Stock eqnr = getStock("EQNR");
                // System.out.println(eqnr);
                // System.out.println("Industry: " + eqnr.getIndustry() + " listed: " +
                // eqnr.getListed());
                // System.out.println("Close: " + eqnr.getClose(20230901));
                // System.out.println("Market: " + eqnr.getMarketPlace());

                // System.out.println(eqnr.getAref(20230901, 1000));
                // System.out.println(DatabaseService.getMakro("Styringsrenta (KPRA)"));
                // System.out.println("Settings in 2023");
                // System.out.println(DatabaseService.getMakroTrendSettings(2023));

                // System.out.println("\n");

                // System.out.println("MarketPlacec for: " + strategyName);
                // System.out.println(getMarketPlacesForStrategy(strategyName));

                // // List<Symbol> symbols = new ArrayList<>();
                // // Symbol equinor = DatabaseService.getSymbol("EQNR");

                // // System.out.println(equinor);

                // // symbols.add(equinor);
                // // DatabaseService.storeHoldings(strategyName, symbols, 20240131);
                // // Settings settings = new Settings();
                // // DatabaseService.storeNewMakroTrendSettings(settings, 20240131,
                // strategyName);

                // System.out.println(DatabaseService.getSymbol("GSPC"));
                List<Symbol> symbols = DatabaseService
                                .getSymbols("Country ETFs on US stock exchange",
                                                20230101,
                                                20240131);

                System.out.println(symbols.size());
                // List<Dividend> dividends = getDividends(58);
                // System.out.println(dividends);
                // Symbol symbol = getSymbol("EQNR");
                // System.out.println(symbol.getDateList());
                DatabaseService.close();

        }

}
