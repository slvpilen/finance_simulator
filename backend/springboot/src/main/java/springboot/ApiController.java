package springboot;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import database.DBServiceStrategy;
import database.apiModels.CandleStickChartData;
import database.apiModels.Holding;
import database.apiModels.LineChartData;
import database.apiModels.StockMeta;
import database.apiModels.TradeEventJson;
import lombok.Data;
import screeners.dividend.DividendScreenerDTO;
import screeners.rsi.RSIScreenerSymbolDTO;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * This class is used to handle HTTP requests. It is a REST controller.
 */

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class ApiController {

  private final ApiService apiService;

  @Autowired
  public ApiController(ApiService apiService) {
    this.apiService = apiService;
  }

  @GetMapping("/symbol/{ticker}")
  public CandleStickChartData getSymbol(
      @PathVariable("ticker") String ticker) {
    return apiService.getSymbol(ticker.toUpperCase());
  }

  @GetMapping("/makro/{type}")
  public LineChartData getMakro(
      @PathVariable("type") String type) {
    return apiService.getMakro(type);
  }

  @GetMapping("/strategy/holdings/{strategyName}/{resultName}")
  public List<Holding> getLatestStrategyHoldings(
      @PathVariable("strategyName") String strategyName,
      @PathVariable("resultName") String resultName) {
    return apiService.getLatestStrategyHoldings(strategyName,
        resultName);
  }

  @GetMapping("/strategy/result/{strategyName}/{resultName}")
  public LineChartData getStrategyResult(
      @PathVariable("strategyName") String strategyName,
      @PathVariable("resultName") String resultName) {
    return apiService.getStrategyResult(strategyName,
        resultName);
  }

  @GetMapping("/strategy/stocksmeta/{strategyName}")
  public List<StockMeta> getStrategyStocksMeta(
      @PathVariable("strategyName") String strategyName) {
    return apiService.getStrategyStocksMeta(strategyName);
  }

  @GetMapping("/strategy/tradeevent/{strategyname}/{strategy_result}/{ticker}")
  public List<TradeEventJson> getTradeEvents(
      @PathVariable("strategyname") String strategyName,
      @PathVariable("strategy_result") String strategy_result,
      @PathVariable("ticker") String ticker) {
    return apiService.getTradeEvents(strategyName,
        strategy_result, ticker);
  }

  @GetMapping("/strategy/results/{strategyname}")
  public List<String> getResults(
      @PathVariable("strategyname") String strategyName) {
    return apiService.getResults(strategyName);
  }

  @GetMapping("/strategy/allstrategynames")
  public List<String> getAllStrategyNames() {
    return DBServiceStrategy.getAllStrategyNames();
  }

  @GetMapping("/screener/rsi/{symbollist}/{date}")
  public List<RSIScreenerSymbolDTO> getRSIScreeners(
      @PathVariable("date") String date,
      @PathVariable("symbollist") String symbolList) {
    return apiService.getRSIScreener(symbolList, date);
  }

  @GetMapping("/screener/dividend/{symbollist}/{date}")
  public List<DividendScreenerDTO> getDividendScreener(
      @PathVariable("date") String date,
      @PathVariable("symbollist") String symbolList) {
    return apiService.getDividendScreener(symbolList, date);
  }

  @PostMapping("/portfoliomixer")
  public LineChartData getPortfolioMixedResult(
      @RequestBody PortfolioMixingSetting portfolioMixingSetting) {
    return apiService.getPortfolioMixedResult(
        portfolioMixingSetting.getStrategyNameAndResult(),
        portfolioMixingSetting.getRebalanceInterval(),
        portfolioMixingSetting.getGearing());
  }

  @Data
  public static class PortfolioMixingSetting {
    private List<String[]> strategyNameAndResult;
    private int rebalanceInterval;
    private double gearing;
  }

  // /**
  // * This method is used to handle DELETE requests to the /highscores endpoint.
  // It
  // * deletes all the highscores from the highscore file.
  // */
  // @DeleteMapping("/highscores")
  // public void clearAllHighscores() {
  // // highscoreService.clearAllHighscores();
  // }
}
