import {
  createChart,
  IChartApi,
  SeriesMarker,
  Time,
  LineSeriesPartialOptions,
} from "lightweight-charts";
import React, { useRef, useEffect, useState } from "react";
import { TradeEvent } from "../../hooks/useFetchTradeEvents";
import { LineChartTick } from "../../types/types";
import { min } from "simple-statistics";
import { RSI } from "technicalindicators";

export interface CandlestickData {
  time: string;
  open: number;
  high: number;
  low: number;
  close: number;
}

export interface CandlestickChartProps {
  data: CandlestickData[];
  tradeEvents?: TradeEvent[];
  lineTicks?: LineChartTick[];
  showRsi?: boolean; // Add showRsi parameter
}

const calculateRsi = (data: CandlestickData[], period: number) => {
  const closes = data.map((d) => d.close);
  const rsiValues = RSI.calculate({ values: closes, period });
  return rsiValues.map((value, index) => ({
    time: data[index + period - 1].time,
    value,
  }));
};

const CandlestickChart: React.FC<CandlestickChartProps> = ({
  data,
  tradeEvents = [],
  lineTicks = [], // Initialize lineTicks as an empty array if not provided
  showRsi = false, // Default value for showRsi
}) => {
  const chartContainerRef = useRef<HTMLDivElement>(null);
  const rsiContainerRef = useRef<HTMLDivElement>(null); // Ref for RSI chart

  const [windowWidth, setWindowWidth] = useState(window.innerWidth);
  const [windowHeight, setWindowHeight] = useState(window.innerHeight);

  useEffect(() => {
    const handleResize = () => {
      setWindowWidth(window.innerWidth);
      setWindowHeight(window.innerHeight);
    };
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  useEffect(() => {
    let chart: IChartApi | null = null;
    let rsiChart: IChartApi | null = null;

    //const isPhone = windowWidth < 600;
    const width = 0; //isPhone ? windowWidth - 12 : windowWidth - 70;

    if (chartContainerRef.current) {
      chart = createChart(chartContainerRef.current, {
        width: width,
        height: min([windowHeight - 80, windowWidth - 100]),
        layout: {
          background: {
            color: "#f0f0f0",
          },
          textColor: "#000000",
        },
      });
      const candlestickSeries = chart.addCandlestickSeries();
      candlestickSeries.setData(data);

      const markers = tradeEvents.map((event) => ({
        time: event.date as Time,
        position: event.type === "+" ? "aboveBar" : "belowBar",
        color: event.type === "+" ? "green" : "red",
        shape: event.type === "+" ? "arrowDown" : "arrowUp",
        id: `${event.type}-signal-${event.date}`,
        text: event.type === "+" ? "Buy" : "Sell",
      }));

      candlestickSeries.setMarkers(markers as SeriesMarker<Time>[]);

      if (lineTicks.length > 0) {
        const lineSeries = chart.addLineSeries({
          color: "rgba(60, 120, 216, 1)",
          lineWidth: 1,
        } as LineSeriesPartialOptions);

        lineSeries.setData(lineTicks);
      }

      chart.timeScale().fitContent();
    }

    if (showRsi && rsiContainerRef.current) {
      rsiChart = createChart(rsiContainerRef.current, {
        width: width,
        height: 150, // Height for the RSI chart
        layout: {
          background: {
            color: "#f0f0f0",
          },
          textColor: "#000000",
        },
      });

      const rsiSeries = rsiChart.addLineSeries({
        color: "rgba(255, 0, 0, 1)",
        lineWidth: 1,
      } as LineSeriesPartialOptions);

      const rsiData = calculateRsi(data, 14);
      rsiSeries.setData(rsiData);

      rsiChart.timeScale().fitContent();

      // Synchronize time scales
      const synchronize = (source: any, target: any) => {
        source.subscribeVisibleTimeRangeChange((newTimeRange: any) => {
          target.setVisibleRange(newTimeRange);
        });
      };

      if (chart) {
        synchronize(chart.timeScale(), rsiChart.timeScale());
        synchronize(rsiChart.timeScale(), chart.timeScale());
      }
    }

    return () => {
      chart?.remove();
      rsiChart?.remove();
    };
  }, [data, tradeEvents, lineTicks, showRsi, windowWidth, windowHeight]);

  useEffect(() => {
    if (chartContainerRef.current && rsiContainerRef.current) {
      const handleResize = () => {
        const isPhone = windowWidth < 600;
        const width = isPhone ? windowWidth - 7 : windowWidth - 70;
        if (chartContainerRef.current) {
          chartContainerRef.current.style.width = `${width}px`;
        }
        if (rsiContainerRef.current) {
          rsiContainerRef.current.style.width = `${width}px`;
        }
      };
      window.addEventListener("resize", handleResize);
      return () => window.removeEventListener("resize", handleResize);
    }
  }, [windowWidth, windowHeight]);

  return (
    <>
      <div
        ref={chartContainerRef}
        style={{ width: "100%", height: showRsi ? "70%" : "100%" }}
      />
      {showRsi && (
        <div ref={rsiContainerRef} style={{ width: "100%", height: "30%" }} />
      )}
    </>
  );
};

export default CandlestickChart;
