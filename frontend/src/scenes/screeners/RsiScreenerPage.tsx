import React, { useEffect, useState } from "react";
import RSIScreener from "./components/RSIScreener";
import { useParams } from "react-router-dom";
import useFetchRSIScreener from "../../hooks/useFetchRSIScreener";

const RsiScreenerPage: React.FC = () => {
  const { symbolList } = useParams<{ symbolList: string }>() ?? {
    symbolList: "",
  };
  const [symbolListName, setSymbolListName] = useState(symbolList as string);

  useEffect(() => {
    let symbolListFormatted = symbolList ?? "";
    setSymbolListName(symbolListFormatted);
  }, [symbolList]);

  const columnsConfig = [
    {
      Header: "Ticker",
      accessor: "ticker",
    },
    {
      Header: "Country",
      accessor: "name",
    },
    {
      Header: "RSI-Daily",
      accessor: "rsiDaily",
    },
    {
      Header: "RSI-Weekly",
      accessor: "rsiWeekly",
    },
    {
      Header: "RSI-Monthly",
      accessor: "rsiMonthly",
    },
    {
      Header: "Updated",
      accessor: "updated",
    },
  ];

  return (
    <RSIScreener
      useFetchData={useFetchRSIScreener}
      columnsConfig={columnsConfig}
      symbolListName={symbolListName}
      subtitle="Chart"
    />
  );
};

export default RsiScreenerPage;
