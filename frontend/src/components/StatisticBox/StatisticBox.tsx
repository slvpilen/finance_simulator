import React, { useState } from "react";
import {
  calculatePearsonCorrelation,
  calculateBeta,
} from "../../utils/statisticCalculation";
import "./StatisticBox.css";

const StatisticBox: React.FC = () => {
  const [stockReturns] = useState<number[]>([0.05, 0.1, 0.15, 0.2, 0.25]);
  const [marketReturns] = useState<number[]>([0.02, 0.04, 0.06, 0.08, 0.1]);

  const pearsonR = calculatePearsonCorrelation(stockReturns, marketReturns);
  const beta = calculateBeta(stockReturns, marketReturns);

  return (
    <div className="statistic-box">
      <h2>Statistics</h2>
      <p>Pearson Correlation: {pearsonR}</p>
      <p>Beta: {beta}</p>
    </div>
  );
};

export default StatisticBox;
