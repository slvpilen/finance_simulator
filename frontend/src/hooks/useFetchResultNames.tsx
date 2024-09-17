import { useState, useEffect } from "react";
import { request } from "./../scenes/login/auth/axios_helpers";

export const useFetchResults = (searchTerm: string): string[] => {
  const [resultNames, setResultNames] = useState<string[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await request("GET", `${searchTerm}`);
        const resultNames: string[] = response.data;
        setResultNames(resultNames);
      } catch (error) {
        console.error("Failed to fetch data:", error);
      }
    };

    if (searchTerm) {
      fetchData();
    }
  }, [searchTerm]);

  return resultNames;
};

export default useFetchResults;
