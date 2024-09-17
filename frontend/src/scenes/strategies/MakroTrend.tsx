import LineChart from "../../components/chart/LineChart";
import CandlestickChart from "../../components/chart/CandlestickChart";
import useFetchLineChartData from "../../hooks/useFetchLineChartData";
import useFetchHoldings from "../../hooks/useFetchStrategyHoldings";
import HoldingsBox from "../../components/strategys/HoldingsBox";
import "./MakroTrend.css";
import useFetchStrategyStocksMeta from "../../hooks/useFetchStrategyStocksMeta";
import StockList from "../../components/StockList";
import { useState } from "react";
import useFetchCandlestickChartData, {
  useFetchCandlestickChartDataAsLine,
} from "../../hooks/useFetchCandlestickChartData";
import useFetchTradeEvents from "../../hooks/useFetchTradeEvents";
import Checkbox from "../../components/CheckBox";
import { LineChartTick } from "../../types/types";

export interface ticksWithColor {
  name: string;
  color: string;
  data: LineChartTick[];
  active: boolean;
}

export default function MakroTrend() {
  const STRATEGY_NAME = "makro-trend";
  const RESULT_NAME = "ALL_INDUSTRIES_COMBINED";

  const holdings = useFetchHoldings(STRATEGY_NAME, RESULT_NAME);

  const makroTrendStockmetaPathPostfix = "strategy/stocksmeta/" + STRATEGY_NAME;
  const stocksMeta = useFetchStrategyStocksMeta(makroTrendStockmetaPathPostfix);

  const lineChartDataType = "strategy/result/" + STRATEGY_NAME + "/";
  const [
    oilResult,
    shippingResult,
    salmonResult,
    consumerDiscretionaryResult,
    industrialsResult,
    industriesCombinedResult,
  ] = [
    "OIL",
    "SHIPPING",
    "SALMON",
    "CONSUMER_DISCRETIONARY",
    "INDUSTRIALS",
    "ALL_INDUSTRIES_COMBINED",
  ].map((ticker) => useFetchLineChartData(lineChartDataType + ticker));

  const defaultSearchTerm = "symbol/EQNR";

  const [searchTerm, setSelectedSearchTerm] =
    useState<string>(defaultSearchTerm);
  const [searchTermTradeEvents, setSelectedSearchTermTradeEvents] =
    useState<string>(
      "strategy/tradeevent/" + STRATEGY_NAME + "/ALL_INDUSTRIES_COMBINED/EQNR"
    );

  const chartData = useFetchCandlestickChartData(searchTerm);
  const tradeEventsData = useFetchTradeEvents(searchTermTradeEvents);

  const sp500 = useFetchCandlestickChartDataAsLine("symbol/^GSPC");

  const handleStockClick = (ticker: string) => {
    setSelectedSearchTerm("symbol/" + ticker);
    setSelectedSearchTermTradeEvents(
      "strategy/tradeevent/" +
        STRATEGY_NAME +
        "/ALL_INDUSTRIES_COMBINED/" +
        ticker
    );
  };

  const allResults = [
    [industriesCombinedResult, "black"],
    [oilResult, "red"],
    [shippingResult, "blue"],
    [salmonResult, "green"],
    [consumerDiscretionaryResult, "purple"],
    [industrialsResult, "orange"],
    [sp500, "gold"], // rename allResults, because sp500 is not a result
  ];

  const [activeSeries, setActiveSeries] = useState(
    new Array(allResults.length).fill(true) // Initially, all series are active
  );

  const handleCheckboxChange = (index: number) => {
    setActiveSeries((current) =>
      current.map((value, i) => (i === index ? !value : value))
    );
  };

  const activeResultsWithColor = allResults.filter(
    (_, index) => activeSeries[index]
  );

  const activeResults = activeResultsWithColor.map(
    ([result, _]) => result
  ) as LineChartTick[][];

  const activeColors = activeResultsWithColor.map(
    ([_, color]) => color
  ) as string[];

  const renderCandlestickChart = () => {
    if (chartData.length > 0) {
      return (
        <CandlestickChart
          data={chartData[0].data}
          tradeEvents={tradeEventsData}
        />
      );
    } else {
      return <p>Loading...</p>;
    }
  };

  const renderCheckbox = (label: string, index: number, color: string) => {
    return (
      <Checkbox
        label={label}
        onChange={() => handleCheckboxChange(index)}
        checked={activeSeries[index]}
        color={color}
      />
    );
  };

  return (
    <div className="MakroTrend-container">
      <h1>Makro Trend</h1>
      <div className="simulations-multi-chart">
        <LineChart
          ticks={activeResults}
          chartName="Equity Curves"
          colors={activeColors}
        />
        <div className="checkboxes">
          {renderCheckbox("Combined", 0, "black")}
          {renderCheckbox("Oil", 1, "red")}
          {renderCheckbox("Shipping", 2, "blue")}
          {renderCheckbox("Salmon", 3, "green")}
          {renderCheckbox("Consumer Discretionary", 4, "purple")}
          {renderCheckbox("Industrials", 5, "orange")}
          {renderCheckbox("S&P 500", 6, "gold")}
        </div>
      </div>
      <div className="linechart-container">
        <div className="barchart">
          <h2>{searchTerm.split("symbol/")[1]}</h2>
          {renderCandlestickChart()}
        </div>
        <StockList stocksMeta={stocksMeta} onTickerClick={handleStockClick} />
        <HoldingsBox holdings={holdings} onTickerClick={handleStockClick} />
      </div>
    </div>
  );
}
