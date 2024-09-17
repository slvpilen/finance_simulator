import React, { useState } from "react";
import CandlestickChart from "../components/chart/CandlestickChart";
import SearchField from "../components/SearchField";
import useFetchCandlestickChartData from "../hooks/useFetchCandlestickChartData";
import "./Analyse.css";
import StatisticBox from "../components/StatisticBox/StatisticBox";
import LoadingAnimation from "../components/LoadingAnimation";

const Analyse: React.FC = () => {
  const defaultTickerUpper = "EQNR";
  const defaultTickerLower = "DNO";

  const [searchTerm, setSearchTerm] = useState<string>(
    "symbol/" + defaultTickerUpper
  );

  const chartData = useFetchCandlestickChartData(searchTerm);

  const [searchTerm2, setSearchTerm2] = useState<string>(
    "symbol/" + defaultTickerLower
  );
  const chartData2 = useFetchCandlestickChartData(searchTerm2);
  return (
    <div className="analyse">
      <h1>Analyse</h1>

      <div className="chart-upper">
        <StatisticBox />
        <div>
          <SearchField
            title="Symbol ticker: "
            defaultSearchTerm={defaultTickerUpper}
            inputId="searchFieldU"
            onSearch={(value) => setSearchTerm("symbol/" + value)}
          />
        </div>
        <div className="candleStickChartU">
          {chartData.length > 0 ? (
            <CandlestickChart
              data={chartData[0].data}
              //tradeEvents={mappedData}
            />
          ) : (
            <LoadingAnimation />
          )}
        </div>
      </div>

      <div className="chart-midle">
        <div>
          <SearchField
            title="Symbol name: "
            defaultSearchTerm={defaultTickerLower}
            inputId="searchFieldL"
            onSearch={(value) => setSearchTerm2("symbol/" + value)}
          />
        </div>
        <div className="candleStickChartL">
          {chartData2.length > 0 ? (
            <CandlestickChart data={chartData2[0].data} />
          ) : (
            <LoadingAnimation />
          )}
        </div>
      </div>
    </div>
  );
};

export default Analyse;
