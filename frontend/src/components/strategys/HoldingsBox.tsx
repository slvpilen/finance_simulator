import React from "react";
import { HoldingsMeta } from "../../hooks/useFetchStrategyHoldings";
import "./HoldingsBox.css";

interface HoldingsBoxProps {
  holdings: HoldingsMeta[];
  onTickerClick?: (ticker: string) => void;
}

// TODO: make date be a input parameter, insted of first, make sure its
const HoldingsBox: React.FC<HoldingsBoxProps> = ({
  holdings,
  onTickerClick,
}) => {
  const handleTickerClick = (ticker: string) => {
    onTickerClick?.(ticker);
  };
  return (
    <div className="container mt-5">
      <h2>Holdings</h2>
      <div className="table-responsive">
        <table className="table table-hover">
          <thead className="thead-light">
            <tr>
              <th scope="col">Symbol Name</th>
              <th scope="col">Ticker</th>
              <th scope="col">Date</th>
            </tr>
          </thead>
          <tbody>
            {holdings.map((holding) => (
              <tr
                key={holding.ticker}
                className="stock-item"
                onClick={() => handleTickerClick(holding.ticker)}
              >
                <td>{holding.name}</td>
                <td>{holding.ticker}</td>
                <td>{holding.date}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default HoldingsBox;
