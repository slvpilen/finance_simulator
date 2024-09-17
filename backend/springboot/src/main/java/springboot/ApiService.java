package springboot;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import database.DBServiceStrategy;
import database.DatabaseService;
import database.Makro;
import database.apiModels.CandleStickChartData;
import database.apiModels.Holding;
import database.apiModels.LineChartData;
import database.apiModels.StockMeta;
import database.apiModels.TradeEventJson;
import portfoliomixer.PortfolioService;
import screeners.rsi.RSIScreenerSymbolDTO;
import screeners.TickerList;
import screeners.dividend.DividendScreenerDTO;

/**
 * This class is used to get appropriate responses to HTTP requests. The
 * controller sends the requests to this class, which then returns the
 * appropriate response.
 */
@Service
public class ApiService {

  public CandleStickChartData getSymbol(String ticker) {
    CandleStickChartData candleStickChartData = DatabaseService
        .getSymbolCandleStickChartData(ticker);
    if (candleStickChartData == null) {
      throw new TickerNotFoundException(
          "Invalid ticker: " + ticker);
    } else {
      return candleStickChartData;
    }

  }

  public List<StockMeta> getStrategyStocksMeta(
      String strategyName) {
    return DBServiceStrategy.getStrategyStocksMeta(strategyName);
  }

  public LineChartData getMakro(String type) {
    Makro makro = DatabaseService.getMakro(type);

    LineChartData lineChartData = new LineChartData();
    lineChartData.type = type;
    lineChartData.frequence = makro.getFrequence();

    makro.getDateList().forEach(date -> {
      lineChartData.addDataPoint(date, makro.getClose(date));
    });

    return lineChartData;
  }

  public List<Holding> getLatestStrategyHoldings(
      String strategyName, String resultName) {
    List<Holding> allStrategyHoldings = DatabaseService
        .getStrategyHoldings(strategyName, resultName);

    int newestDate = allStrategyHoldings.stream()
        .mapToInt(Holding::getDate).max().orElse(0);

    List<Holding> latestStrategyHoldings = allStrategyHoldings
        .stream()
        .filter(holding -> holding.getDate() == newestDate)
        .collect(Collectors.toList());

    return latestStrategyHoldings;
  }

  public LineChartData getStrategyResult(String strategyName,
      String resultName) {
    return DBServiceStrategy.getStrategyResult(strategyName,
        resultName);
  }

  public List<TradeEventJson> getTradeEvents(String strategyName,
      String strategyResult, String ticker) {
    return DBServiceStrategy.getTradeEvents(strategyName,
        strategyResult, ticker);
  }

  public List<String> getResults(String strategyName) {
    return DBServiceStrategy.getResults(strategyName);
  }

  public LineChartData getPortfolioMixedResult(
      List<String[]> strategyNameAndResult,
      int rebalanceInterval, double gearing) {

    PortfolioService portfolioService = new PortfolioService(
        strategyNameAndResult, rebalanceInterval, gearing);
    return portfolioService.getPortfolioMixedResult();
  }

  public void addHighscore() {
    // HighscoreFileManager.writeToHighscore(userScore,
    // HighscoreFileManager.getFile());

  }

  public void clearAllHighscores() {
    // HighscoreFileManager.clearHighscore(HighscoreFileManager.getFile());
  }

  public List<RSIScreenerSymbolDTO> getRSIScreener(
      String symbolListName, String date) {

    List<RSIScreenerSymbolDTO> RSIScreener = screeners.rsi.RSIScreener
        .getRSIScreenerSymbols(symbolListName,
            Integer.parseInt(date));

    return RSIScreener;
  }

  public List<DividendScreenerDTO> getDividendScreener(
      String symbolListName, String date) {

    List<DividendScreenerDTO> dividendScreener = screeners.dividend.DividendScreener
        .getDividendScreenerDTOs(symbolListName,
            Integer.parseInt(date));

    return dividendScreener;
  }

}
