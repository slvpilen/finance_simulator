import { useState, useEffect } from "react";
import { request } from "./../scenes/login/auth/axios_helpers";

interface ApiStockMeta {
  ticker: string;
  name: string;
  industryName: string;
}

export interface StockMeta {
  name: string;
  ticker: string;
  industry: string;
}

export const useFetchHoldings = (searchTerm: string): StockMeta[] => {
  const [stocksMeta, setStocksMeta] = useState<StockMeta[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await request("GET", `${searchTerm}`);
        const rawHoldings: ApiStockMeta[] = response.data;

        const mappedStocks: StockMeta[] = rawHoldings.map((stock) => {
          return {
            ticker: stock.ticker,
            name: stock.name,
            industry: stock.industryName,
          };
        });

        setStocksMeta(mappedStocks);
      } catch (error) {
        console.error("Failed to fetch data:", error);
      }
    };

    if (searchTerm) {
      fetchData();
    }
  }, [searchTerm]);

  return stocksMeta;
};

export default useFetchHoldings;
