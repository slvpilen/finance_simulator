import { useState, useEffect } from "react";
import { request } from "../scenes/login/auth/axios_helpers";

interface RSIScreenerDTO {
  ticker: string;
  name: string;
  rsiDaily: number;
  rsiWeekly: number;
  rsiMonthly: number;
  updated: string;
}

const useFetchRSIScreener = (symbolList: string, date: string) => {
  const [data, setData] = useState<RSIScreenerDTO[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      setError(null);

      try {
        const response = await request(
          "GET",
          `screener/rsi/${symbolList}/${date}`
        );
        setData(response.data);
      } catch (error: any) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [symbolList, date]);

  return { data, loading, error };
};

export default useFetchRSIScreener;
