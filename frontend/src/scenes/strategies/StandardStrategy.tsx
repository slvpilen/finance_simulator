import LineChart from "../../components/chart/LineChart";
import CandlestickChart from "../../components/chart/CandlestickChart";
import useFetchLineChartData from "../../hooks/useFetchLineChartData";
import "./StandardStrategy.css";
import { useState } from "react";
import useFetchCandlestickChartData, {
  useFetchCandlestickChartDataAsLine,
} from "../../hooks/useFetchCandlestickChartData";
import useFetchTradeEvents from "../../hooks/useFetchTradeEvents";
import Checkbox from "../../components/CheckBox";
import { useParams } from "react-router-dom";
import useFetchResultsNames from "../../hooks/useFetchResultNames";
import { LineChartTick } from "../../types/types";
import LoadingAnimation from "../../components/LoadingAnimation";
import Dropdown from "../../components/DropDownList";

/*
 * This page is used as a standard/default page for stragys, that not require any special graphs etc.
 */

export interface ticksWithColor {
  name: string;
  color: string;
  data: LineChartTick[];
  active: boolean;
}

export default function StandardStrategy() {
  const { strategyName } = useParams<{ strategyName: string }>();
  const resultNames = useFetchResultsNames("strategy/results/" + strategyName);

  // selected strategyName useState

  //const TRADED_SYMBOL_NAME = "^GSPC";
  const TRADED_SYMBOL_NAME = "^GSPC"; // OBS only dev
  const BENCH_MARK = "^GSPC"; // OBS only dev

  const lineChartDataType = "strategy/result/" + strategyName + "/";

  const [selectedResultName, setSelectedStrategyName] = useState(
    resultNames[0]
  ); // Default first result

  const [simulationVer1Result] = [selectedResultName].map((resultName) =>
    useFetchLineChartData(lineChartDataType + resultName)
  );

  const tradedSymbol = useFetchCandlestickChartData(
    "symbol/" + TRADED_SYMBOL_NAME
  );

  const obx = useFetchCandlestickChartDataAsLine("symbol/" + BENCH_MARK);

  const tradeEventsData = useFetchTradeEvents(
    "strategy/tradeevent/" +
      strategyName +
      "/" +
      selectedResultName +
      "/" +
      TRADED_SYMBOL_NAME
  );

  const allResults = [
    [simulationVer1Result, "black"],
    [obx, "gold"],
    // [eqnr, "blue"],
    // [oil, "brown"],
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
    if (tradedSymbol.length > 0) {
      return (
        <CandlestickChart
          data={tradedSymbol[0].data}
          tradeEvents={tradeEventsData}
          //lineTicks={simulationVer1Result}
        />
      );
    } else {
      return <LoadingAnimation />;
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

  function handleSelect(selectedOption: string): void {
    setSelectedStrategyName(selectedOption);
  }

  return (
    <div className="strategy-container">
      <div className="header">
        <h1>{strategyName}</h1>
        <Dropdown options={resultNames} onSelect={handleSelect} />
      </div>
      <div className="simulations-multi-chart">
        <LineChart
          ticks={activeResults}
          chartName="Equity Curves"
          colors={activeColors}
        />
        <div className="checkboxes">
          {renderCheckbox(strategyName || "", 0, "black")}
          {renderCheckbox("OBX", 1, "gold")}
          {/* {renderCheckbox("EQNR", 2, "blue")}
          {renderCheckbox("Oil", 3, "brown")} */}
        </div>
      </div>
      <div className="linechart-container">
        <div className="barchart">{renderCandlestickChart()}</div>
      </div>
    </div>
  );
}
