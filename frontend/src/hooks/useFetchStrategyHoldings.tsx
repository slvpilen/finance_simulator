import { useState, useEffect } from "react";
import { request } from "./../scenes/login/auth/axios_helpers";

interface ApiHoldingsMeta {
  symbolName: string;
  symbolTicker: string;
  date: number;
}

export interface HoldingsMeta {
  name: string;
  ticker: string;
  date: number;
}

export const useFetchHoldings = (
  strategyName: string,
  resultName: string
): HoldingsMeta[] => {
  const [stocksMeta, setStocksMeta] = useState<HoldingsMeta[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await request(
          "GET",
          `strategy/holdings/${strategyName}/${resultName}`
        );
        const rawHoldings: ApiHoldingsMeta[] = response.data;

        const mappedHoldings: HoldingsMeta[] = rawHoldings.map((holding) => {
          return {
            ticker: holding.symbolTicker,
            name: holding.symbolName,
            date: holding.date,
          };
        });

        setStocksMeta(mappedHoldings);
      } catch (error) {
        console.error("Failed to fetch data:", error);
      }
    };

    if (strategyName) {
      fetchData();
    }
  }, [strategyName]);

  return stocksMeta;
};

export default useFetchHoldings;
