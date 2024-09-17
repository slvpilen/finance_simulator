
export interface Trade {
    ticker: string;
    name: string;
    date: string;
    strategyName: string;
    orderType: string;
  }
  
  export interface StrategyPerformance {
    strategyName: string;
    week1: number;
    month1: number;
    month3: number;
    ytd: number;
    year1: number;
    year3: number;
  }
  