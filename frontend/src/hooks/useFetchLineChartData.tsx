import { useState, useEffect } from "react";
import { LineChartTick } from "../types/types";
import { request } from "./../scenes/login/auth/axios_helpers";

interface ApiResponse {
  type: string;
  dataPoints: ApiLineTick[];
}

interface ApiLineTick {
  date: string;
  value: number;
}

export const useFetchLineChartData = (searchTerm: string): LineChartTick[] => {
  const [chartData, setChartData] = useState<LineChartTick[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await request("GET", `${searchTerm}`);
        const data: ApiResponse = response.data;

        const mappedData: LineChartTick[] = data.dataPoints.map((tick) => {
          const dateString = tick.date.toString();
          const year = dateString.substring(0, 4);
          const month = dateString.substring(4, 6);
          const day = dateString.substring(6, 8);
          return {
            time: `${year}-${month}-${day}`,
            value: tick.value,
          };
        });

        setChartData(mappedData);
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

export default useFetchLineChartData;
