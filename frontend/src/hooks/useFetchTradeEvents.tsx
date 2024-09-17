import { useState, useEffect } from "react";
import { request } from "./../scenes/login/auth/axios_helpers";

export interface TradeEvent {
  type: string;
  date: string;
}

export const useFetchTradeEvents = (searchTerm: string): TradeEvent[] => {
  const [tradeEvents, setTradeEvents] = useState<TradeEvent[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await request("GET", `${searchTerm}`);
        const rawHoldings: TradeEvent[] = response.data;

        const mappedData: TradeEvent[] = rawHoldings.map((tradeEvent) => {
          const dateString = tradeEvent.date.toString();
          const year = dateString.substring(0, 4);
          const month = dateString.substring(4, 6);
          const day = dateString.substring(6, 8);
          return {
            date: `${year}-${month}-${day}`,
            type: tradeEvent.type,
          };
        });

        setTradeEvents(mappedData);
      } catch (error) {
        console.error("Failed to fetch data:", error);
      }
    };

    if (searchTerm) {
      fetchData();
    }
  }, [searchTerm]);

  return tradeEvents;
};

export default useFetchTradeEvents;
