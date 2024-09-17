import { useState, useEffect } from "react";
import { request } from "./../scenes/login/auth/axios_helpers"; 

export const useFetchStrategyNames = (): string[] => {
  const [strategyNames, setStrategyNames] = useState<string[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await request('GET', `strategy/allstrategynames`);
        const resultNames: string[] = response.data;
        setStrategyNames(resultNames);
      } catch (error) {
        console.error("Failed to fetch data:", error);
      }
    };

    fetchData();
  }, []); 

  return strategyNames;
};

export default useFetchStrategyNames;
