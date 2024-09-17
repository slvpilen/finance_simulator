// StockList.tsx
import React, { useState } from "react";
import "./StockList.css";

export interface StockMeta {
  name: string;
  ticker: string;
  industry: string;
}

interface StockListProps {
  stocksMeta: StockMeta[];
  onTickerClick?: (ticker: string) => void; // Optional prop to handle click events on tickers
}

interface VisibleIndustries {
  [industry: string]: boolean;
}

const StockList: React.FC<StockListProps> = ({ stocksMeta, onTickerClick }) => {
  const [visibleIndustries, setVisibleIndustries] = useState<VisibleIndustries>(
    {}
  );

  const toggleIndustryVisibility = (industry: string) => {
    setVisibleIndustries((prev) => ({
      ...prev,
      [industry]: !prev[industry],
    }));
  };

  const handleTickerClick = (ticker: string) => {
    onTickerClick?.(ticker);
  };

  const groupedByIndustry = stocksMeta.reduce(
    (acc: Record<string, StockMeta[]>, stock) => {
      if (!acc[stock.industry]) {
        acc[stock.industry] = [];
      }
      acc[stock.industry].push(stock);
      return acc;
    },
    {}
  );

  return (
    <div className="stock-list-container">
      <h2>Stocks</h2>
      {Object.entries(groupedByIndustry).map(([industry, stocks]) => (
        <div key={industry}>
          <h3
            onClick={() => toggleIndustryVisibility(industry)}
            className="industry-header"
          >
            {industry} ({visibleIndustries[industry] ? "-" : "+"})
          </h3>
          {visibleIndustries[industry] && (
            <div className="table-responsive">
              <table className="table table-hover">
                <tbody>
                  {stocks.map((stock) => (
                    <tr
                      key={stock.ticker}
                      className="stock-item"
                      onClick={() => handleTickerClick(stock.ticker)}
                    >
                      <td>{stock.name}</td>
                      <td>{stock.ticker}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      ))}
    </div>
  );
};

export default StockList;
