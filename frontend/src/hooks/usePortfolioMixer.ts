import { useState, useCallback } from 'react';
import { LineChartTick } from '../types/types';
import { request } from './../scenes/login/auth/axios_helpers';

export const usePortfolioMixer = () => {
  const [result, setResult] = useState<LineChartTick[]>([]);
  const [loading, setLoading] = useState<boolean>(false);

  const submitData = useCallback(async (
    strategyNameAndResult: string[][],
    rebalanceInterval: number,
    gearing: number
  ) => {
    setLoading(true);
    try {
      const response = await request('POST', 'portfoliomixer', {
        strategyNameAndResult,
        rebalanceInterval,
        gearing,
      });

      const data = response.data;

      const mappedData: LineChartTick[] = data.dataPoints.map(
        (tick: { date: string; value: number }) => {
          const dateString = tick.date.toString();
          const year = dateString.substring(0, 4);
          const month = dateString.substring(4, 6);
          const day = dateString.substring(6, 8);
          return {
            time: `${year}-${month}-${day}`,
            value: tick.value,
          };
        }
      );

      setResult(mappedData);
    } catch (error) {
      console.error('Error:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  return { result, submitData, loading };
};
