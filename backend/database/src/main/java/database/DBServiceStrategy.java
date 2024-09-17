package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import database.apiModels.LineChartData;
import utils.HashArray;
import database.apiModels.StockMeta;
import database.apiModels.TradeEventJson;

public class DBServiceStrategy {

    private static Connection connection = DatabaseConnection
            .getConnection();

    public static List<TradeEventJson> getTradeEvents(
            String strategyName, String strategyResult,
            String ticker) {
        String query = "SELECT date, type FROM strategy.trade_event WHERE strategy_result_id = (SELECT id FROM strategy.strategy_result WHERE strategy_id = (SELECT id FROM strategy.strategy WHERE name = ?) AND name = ?) AND symbol_id = (SELECT id FROM financial.symbol WHERE ticker = ?) ORDER BY date";
        try (PreparedStatement pstmt = connection
                .prepareStatement(query)) {
            pstmt.setString(1, strategyName);
            pstmt.setString(2, strategyResult);
            pstmt.setString(3, ticker);
            try (ResultSet rs = pstmt.executeQuery()) {
                List<TradeEventJson> tradeEvents = new ArrayList<>();
                while (rs.next()) {
                    TradeEventJson tradeEvent = new TradeEventJson();
                    tradeEvent.setDate(
                            DatabaseService.convertDateToInteger(
                                    rs.getDate("date")));
                    tradeEvent.setType(rs.getString("type"));
                    tradeEvents.add(tradeEvent);
                }
                return tradeEvents;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void insertTradeEvents(String strategyName,
            String resultName, List<TradeEvent> tradeEvents,
            boolean isLive) {
        String query = "INSERT INTO strategy.trade_event (strategy_result_id, date, type, symbol_id) VALUES (?, ?, ?, ?) ON CONFLICT (strategy_result_id, date, type, symbol_id) DO NOTHING";
        try (PreparedStatement pstmt = connection
                .prepareStatement(query)) {
            Integer strategy_resultId = getStrategy_resultId(
                    strategyName, resultName, isLive);
            if (strategy_resultId == null) {
                System.out.println(
                        "WARNING: The strategy_result name does not exist in the database");
                return;
            }
            for (TradeEvent tradeEvent : tradeEvents) {
                pstmt.setInt(1, strategy_resultId);
                pstmt.setDate(2, DatabaseService
                        .convertDate(tradeEvent.getDate()));
                pstmt.setString(3, String.valueOf(
                        tradeEvent.getType().getType()));
                pstmt.setInt(4, tradeEvent.getSymbol().getId());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // TODO: Consider handle all Linechartdarta in one methode. Maybe refactory
    // Consider rename apiModels to JSONModels
    public static SimulationResult getSimulationAccountValues(
            String strategyName, String resultName) {
        String query = "SELECT date, account_value, account_exponation FROM strategy.tick_daily WHERE strategy_result_id = (SELECT id FROM strategy.strategy_result WHERE strategy_id = (SELECT id FROM strategy.strategy WHERE name = ?) AND name = ?) ORDER BY date";
        try (PreparedStatement pstmt = connection
                .prepareStatement(query)) {
            pstmt.setString(1, strategyName);
            pstmt.setString(2, resultName);
            try (ResultSet rs = pstmt.executeQuery()) {
                HashArray<Integer, Double> ticks = new HashArray<>();
                while (rs.next()) {
                    int date = DatabaseService
                            .convertDateToInteger(
                                    rs.getDate("date"));
                    double accountValue = rs
                            .getDouble("account_value");
                    // double accountExponation = rs.getDouble("account_exponation");

                    ticks.put(date, accountValue);
                }
                // TODO: use builder
                return new SimulationResult(strategyName,
                        resultName, ticks);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static List<Symbol> getSymbols(String strategyName) {
        String query = "SELECT * FROM strategy.symbols_in_strategy('"
                + strategyName + "')";
        List<Symbol> symbolsRelatedToStrategy = DatabaseService
                .getSymbolsFromQuery(query);

        return symbolsRelatedToStrategy;
    }

    public static List<Stock> getStocks(String strategyName) {
        String query = "SELECT * FROM strategy.stocks_in_strategy('"
                + strategyName + "')";

        List<Stock> stocksRelatedToStrategy = DatabaseService
                .getStocksFromQuery(query);

        return stocksRelatedToStrategy;
    }

    public static List<StockMeta> getStrategyStocksMeta(
            String strategyName) {
        String query = "SELECT ticker, stock_name, industry_name FROM strategy.stock_name_and_industry(?)";
        try (PreparedStatement pstmt = connection
                .prepareStatement(query)) {
            pstmt.setString(1, strategyName);
            try (ResultSet rs = pstmt.executeQuery()) {
                List<StockMeta> stockMetaList = new ArrayList<>();
                while (rs.next()) {
                    StockMeta stockMeta = new StockMeta();
                    stockMeta.ticker = rs.getString("ticker");
                    stockMeta.name = rs.getString("stock_name");
                    stockMeta.industryName = rs
                            .getString("industry_name");
                    stockMetaList.add(stockMeta);
                }
                return stockMetaList;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LineChartData getStrategyResult(
            String strategyName, String resultName) {
        String query = "SELECT date, account_value, account_exponation FROM strategy.tick_daily WHERE strategy_result_id = (SELECT id FROM strategy.strategy_result WHERE strategy_id = (SELECT id FROM strategy.strategy WHERE name = ?) AND name = ?) ORDER BY date";
        try (PreparedStatement pstmt = connection
                .prepareStatement(query)) {
            pstmt.setString(1, strategyName);
            pstmt.setString(2, resultName);
            try (ResultSet rs = pstmt.executeQuery()) {
                LineChartData lineChartData = new LineChartData();
                while (rs.next()) {
                    int date = DatabaseService
                            .convertDateToInteger(
                                    rs.getDate("date"));
                    double accountValue = rs
                            .getDouble("account_value");
                    // double accountExponation = rs.getDouble("account_exponation");

                    lineChartData.addDataPoint(date,
                            accountValue);
                }
                return lineChartData;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static HashArray<Integer, Double> getStrategyResultAsHashArray(
            String strategyName, String resultName) {
        String query = "SELECT date, account_value, account_exponation FROM strategy.tick_daily WHERE strategy_result_id = (SELECT id FROM strategy.strategy_result WHERE strategy_id = (SELECT id FROM strategy.strategy WHERE name = ?) AND name = ?) ORDER BY date";
        try (PreparedStatement pstmt = connection
                .prepareStatement(query)) {
            pstmt.setString(1, strategyName);
            pstmt.setString(2, resultName);
            try (ResultSet rs = pstmt.executeQuery()) {
                HashArray<Integer, Double> strategyResult = new HashArray<>();
                while (rs.next()) {
                    int date = DatabaseService
                            .convertDateToInteger(
                                    rs.getDate("date"));
                    double accountValue = rs
                            .getDouble("account_value");
                    // double accountExponation = rs.getDouble("account_exponation");

                    strategyResult.put(date, accountValue);
                }
                return strategyResult;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void insertStrategyResult(String strategyName,
            String resultName, boolean isLive,
            HashArray<Integer, Double> dailyAccountValue,
            HashArray<Integer, Double> dailyExponation) {

        boolean hasStrategyNameInDB = checkIfStrategyNameExists(
                strategyName);
        if (!hasStrategyNameInDB) {
            System.out.println(
                    "WARNING: The strategy name does not exist in the database: "
                            + strategyName);
            return;
        }

        String query = "INSERT INTO strategy.strategy_result (strategy_id, name, is_live) "
                + "SELECT id, ?, ? FROM strategy.strategy WHERE name = ? "
                + "ON CONFLICT (strategy_id, name, is_live) DO NOTHING";

        try (PreparedStatement pstmt = connection
                .prepareStatement(query)) {
            pstmt.setString(1, resultName);
            pstmt.setBoolean(2, isLive);
            pstmt.setString(3, strategyName);

            pstmt.executeUpdate();
            Integer strategy_resultId = getStrategy_resultId(
                    strategyName, resultName, isLive);
            if (strategy_resultId == null) {
                System.out.println(
                        "WARNING: The strategy_result name does not exist in the database: "
                                + resultName);
                return;
            }
            insertDailyTicks(pstmt, strategy_resultId,
                    dailyAccountValue, dailyExponation);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static List<String> getAllStrategyNames() {
        String query = "SELECT name FROM strategy.strategy";
        try (PreparedStatement pstmt = connection
                .prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                List<String> strategyNames = new ArrayList<>();
                while (rs.next()) {
                    strategyNames.add(rs.getString("name"));
                }
                return strategyNames;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static List<String> getResults(String strategyName) {
        String query = "SELECT name FROM strategy.strategy_result WHERE strategy_id = (SELECT id FROM strategy.strategy WHERE name = ?)";
        try (PreparedStatement pstmt = connection
                .prepareStatement(query)) {
            pstmt.setString(1, strategyName);
            try (ResultSet rs = pstmt.executeQuery()) {
                List<String> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(rs.getString("name"));
                }
                return results;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static Integer getStrategy_resultId(
            String strategyName, String resultName,
            boolean isLive) {
        String query = "SELECT sr.id FROM strategy.strategy_result sr "
                + "JOIN strategy.strategy s ON sr.strategy_id = s.id "
                + "WHERE s.name = ? AND sr.name = ? AND sr.is_live = ?";
        try (PreparedStatement pstmt = connection
                .prepareStatement(query)) {
            pstmt.setString(1, strategyName);
            pstmt.setString(2, resultName);
            pstmt.setBoolean(3, isLive);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean checkIfStrategyNameExists(
            String strategyName) {
        String checkQuery = "SELECT COUNT(*) FROM strategy.strategy WHERE name = ?";
        try (PreparedStatement checkStmt = connection
                .prepareStatement(checkQuery)) {
            checkStmt.setString(1, strategyName);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                return false;
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void insertDailyTicks(
            PreparedStatement pstmtStrategyResult,
            int strategyId,
            HashArray<Integer, Double> dailyAccountValue,
            HashArray<Integer, Double> dailyExponation)
            throws SQLException {

        String query = "INSERT INTO strategy.tick_daily (strategy_result_id , date, account_value, account_exponation) VALUES (?, ?, ?, ?) ON CONFLICT (strategy_result_id, date) DO UPDATE SET account_value = EXCLUDED.account_value, account_exponation = EXCLUDED.account_exponation";

        // double previuesAccountChange = 0;
        try (PreparedStatement pstmtTickDaily = connection
                .prepareStatement(query)) {
            for (Integer date : dailyAccountValue.getKeyList()) {
                pstmtTickDaily.setLong(1, strategyId);
                pstmtTickDaily.setDate(2,
                        DatabaseService.convertDate(date));

                pstmtTickDaily.setDouble(3,
                        dailyAccountValue.get(date));
                pstmtTickDaily.setDouble(4,
                        dailyExponation.get(date));

                pstmtTickDaily.addBatch();
            }
            pstmtTickDaily.executeBatch();
        }
    }

    // *************************
    // *************************

    public static void main(String[] args) {
        HashArray<Integer, Double> dailyAccountValue = new HashArray<>();
        dailyAccountValue.put(20220101, 1.0);
        dailyAccountValue.put(20220102, 2.0);
        dailyAccountValue.put(20220103, 3.0);
        dailyAccountValue.put(20220104, 4.0);
        dailyAccountValue.put(20220105, 5.0);

        HashArray<Integer, Double> dailyExponation = new HashArray<>();
        dailyExponation.put(20220101, 1.0);
        dailyExponation.put(20220102, 20.0);
        dailyExponation.put(20220103, 3.0);
        dailyExponation.put(20220104, 4.0);
        dailyExponation.put(20220105, 5.0);

        DBServiceStrategy.insertStrategyResult("makro-trend",
                "resultName", false, dailyAccountValue,
                dailyExponation);
    }
}