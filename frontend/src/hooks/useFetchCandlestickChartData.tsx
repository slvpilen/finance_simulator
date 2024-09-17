import { useState, useEffect } from "react";
import { request } from "./../scenes/login/auth/axios_helpers";
import { LineChartTick } from "../types/types";

interface ApiResponse {
  ticker: string;
  ticksSorted: Tick[];
}

interface Tick {
  date: number;
  open: number;
  high: number;
  low: number;
  close: number;
}

export interface ChartData {
  id: string;
  data: {
    time: string;
    open: number;
    high: number;
    low: number;
    close: number;
  }[];
}

export const useFetchCandlestickChartData = (
  searchTerm: string
): ChartData[] => {
  const [chartData, setChartData] = useState<ChartData[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        if (searchTerm === "symbol/") {
          return [];
        }
        const response = await request("GET", `${searchTerm}`);
        const data: ApiResponse = response.data;

        const mappedData: ChartData["data"] = data.ticksSorted.map((tick) => {
          const dateString = tick.date.toString();
          const year = dateString.substring(0, 4);
          const month = dateString.substring(4, 6);
          const day = dateString.substring(6, 8);
          return {
            time: `${year}-${month}-${day}`,
            open: tick.open,
            high: tick.high,
            low: tick.low,
            close: tick.close,
          };
        });

        setChartData([{ id: data.ticker, data: mappedData }]);
      } catch (error) {
        console.error("Failed to fetch data:", error);
      }
    };

    if (searchTerm) {
      fetchData();
    }
  }, [searchTerm]);

  return chartData;
};

export default useFetchCandlestickChartData;

export const useFetchCandlestickChartDataAsLine = (
  searchTerm: string
): LineChartTick[] => {
  const [chartData, setChartData] = useState<LineChartTick[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await request("GET", `${searchTerm}`);
        const data: ApiResponse = response.data;

        const mappedData: LineChartTick[] = data.ticksSorted.map((tick) => {
          const dateString = tick.date.toString();
          const year = dateString.substring(0, 4);
          const month = dateString.substring(4, 6);
          const day = dateString.substring(6, 8);
          return {
            time: `${year}-${month}-${day}`,
            value: tick.close,
          };
        });

        setChartData(mappedData);
      } catch (error) {
        console.error("Failed to fetch data:", error);
        return [];
      }
    };

    if (searchTerm) {
      fetchData();
    }
  }, [searchTerm]);

  return chartData;
};
