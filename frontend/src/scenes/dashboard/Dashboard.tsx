// import LineChart from "../../components/chart/LineChart";
// import PieChart from "../../components/chart/PieChart";
// import "./Dashboard.css"; // Make sure the path is correct
// import useFetchLineChartData from "../../hooks/useFetchLineChartData";

// export default function Dashboard() {
//   const lineChartDataPath =
//     "strategy/result/makro-trend/ALL_INDUSTRIES_COMBINED";
//   const makroTrend = useFetchLineChartData(lineChartDataPath);

//   // This is just a demo data, replace with real data
//   const demoPieChartData = [
//     { id: "Makro Trend", label: "Makro Trend", value: 600 },
//     { id: "Ex2", label: "Ex2", value: 200 },
//     { id: "Ex3", label: "Ex3", value: 200 },
//   ];

//   return (
//     <>
//       <div className="dashboard-container">
//         <div className="performence-chart">
//           <h2>Performance</h2>
//           <LineChart ticks={[makroTrend]} chartName="makro-trend" />
//         </div>

//         {/* Create a component of this */}
//         <div className="account-info">
//           <h1>Account</h1>
//           <p>Account value: 100'000,-</p>
//           <p>YTD: 14,5% Max dd: 8%</p>
//           <p>1Y: 20% Max dd: 14%</p>
//           <p>2Y: 35% Max dd: 12%</p>
//           <p>3Y: 45% Max dd: 13%</p>
//         </div>

//         <div className="divied-chart">
//           <h2>Dividend between strategies</h2>
//           <PieChart data={demoPieChartData} />
//         </div>
//       </div>
//     </>
//   );
// }
// src/components/Dashboard.tsx
import React from "react";
import LatestTradesList from "./components/LatestTradesList";
import StrategyPerformanceTable from "./components/StrategyPerformanceTable";
import "./Dashboard.css";

import { Trade, StrategyPerformance } from "./types";

const trades: Trade[] = [
  {
    ticker: "AAPL",
    name: "Apple Inc.",
    date: "2023-07-26",
    strategyName: "Strategy A",
    orderType: "Buy",
  },
  {
    ticker: "GOOGL",
    name: "Alphabet Inc.",
    date: "2023-07-25",
    strategyName: "Strategy B",
    orderType: "Sell",
  },
  {
    ticker: "AAPL",
    name: "Apple Inc.",
    date: "2023-07-26",
    strategyName: "Strategy A",
    orderType: "Buy",
  },
  {
    ticker: "GOOGL",
    name: "Alphabet Inc.",
    date: "2023-07-25",
    strategyName: "Strategy B",
    orderType: "Sell",
  },
  {
    ticker: "AAPL",
    name: "Apple Inc.",
    date: "2023-07-26",
    strategyName: "Strategy A",
    orderType: "Buy",
  },
  {
    ticker: "GOOGL",
    name: "Alphabet Inc.",
    date: "2023-07-25",
    strategyName: "Strategy B",
    orderType: "Sell",
  },
  {
    ticker: "AAPL",
    name: "Apple Inc.",
    date: "2023-07-26",
    strategyName: "Strategy A",
    orderType: "Buy",
  },
  {
    ticker: "GOOGL",
    name: "Alphabet Inc.",
    date: "2023-07-25",
    strategyName: "Strategy B",
    orderType: "Sell",
  },
];

const performanceData: StrategyPerformance[] = [
  {
    strategyName: "Strategy A",
    week1: 1.2,
    month1: 3.4,
    month3: 5.6,
    ytd: 7.8,
    year1: 9.0,
    year3: 12.3,
  },
  {
    strategyName: "Strategy B",
    week1: 0.5,
    month1: 1.2,
    month3: 2.3,
    ytd: 3.4,
    year1: 4.5,
    year3: 5.6,
  },
  {
    strategyName: "Strategy A",
    week1: 1.2,
    month1: 3.4,
    month3: 5.6,
    ytd: 7.8,
    year1: 9.0,
    year3: 12.3,
  },
  {
    strategyName: "Strategy B",
    week1: 0.5,
    month1: 1.2,
    month3: 2.3,
    ytd: 3.4,
    year1: 4.5,
    year3: 5.6,
  },
  {
    strategyName: "Strategy A",
    week1: 1.2,
    month1: 3.4,
    month3: 5.6,
    ytd: 7.8,
    year1: 9.0,
    year3: 12.3,
  },
  {
    strategyName: "Strategy B",
    week1: 0.5,
    month1: 1.2,
    month3: 2.3,
    ytd: 3.4,
    year1: 4.5,
    year3: 5.6,
  },
  // more performance data
];

const Dashboard: React.FC = () => {
  return (
    <div>
      <h1>Dashboard</h1>
      <div style={{ display: "flex", justifyContent: "space-around" }}>
        <LatestTradesList trades={trades} />
        {/* <ChartComponent /> Assuming you have a ChartComponent */}
        <StrategyPerformanceTable performanceData={performanceData} />
      </div>
    </div>
  );
};

export default Dashboard;
