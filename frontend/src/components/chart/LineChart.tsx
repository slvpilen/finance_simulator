import { createChart, IChartApi } from "lightweight-charts";
import React, { useRef, useEffect, useState } from "react";
import { ticksWithColor } from "../../scenes/strategies/MakroTrend";
import { LineChartTick } from "./../../types/types";
import { min } from "simple-statistics";

export interface LineChartProps {
  ticks?: LineChartTick[][]; // Now 'ticks' is an array of tick arrays
  chartName?: string;
  colors?: string[];
  ticksWithColor?: ticksWithColor[];
}

const LineChart: React.FC<LineChartProps> = ({
  ticks,
  chartName = "",
  colors = ["black", "red", "green", "blue"],
  ticksWithColor,
}) => {
  const chartContainerRef = useRef<HTMLDivElement>(null);
  const chartRef = useRef<IChartApi | null>(null);

  const [windowWidth, setWindowWidth] = useState(window.innerWidth);
  const [windowHeight, setWindowHeight] = useState(window.innerHeight);

  useEffect(() => {
    const handleResize = () => {
      setWindowWidth(window.innerWidth);
      setWindowHeight(window.innerHeight);
      if (chartRef.current && chartContainerRef.current) {
        chartRef.current.applyOptions({
          width: chartContainerRef.current.clientWidth,
          height: min([window.innerHeight - 80, window.innerWidth - 100]),
        });
      }
    };

    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  useEffect(() => {
    if (chartContainerRef.current) {
      const chart = createChart(chartContainerRef.current, {
        width: 0, //chartContainerRef.current.clientWidth,
        height: min([windowHeight - 80, windowWidth - 100]),
        layout: {
          background: { color: "#f0f0f0" },
          textColor: "#000000",
        },
        rightPriceScale: {
          scaleMargins: {
            top: 0.1,
            bottom: 0.1,
          },
          mode: 2,
          borderColor: "rgba(197, 203, 206, 0.4)",
        },
      });
      chartRef.current = chart;
      chart.timeScale().fitContent();

      ticks?.forEach((tickSet, index) => {
        const series = chart.addLineSeries({
          color: colors[index] || "black",
        });
        series.setData(tickSet);
      });

      ticksWithColor?.forEach((tickSet, index) => {
        const series = chart.addLineSeries({
          color: colors[index] || "black",
        });
        series.setData(tickSet.data);
      });

      return () => {
        chart.remove();
        chartRef.current = null;
      };
    }
  }, [ticks, windowHeight, windowWidth, colors, ticksWithColor]);

  return (
    <div className="lineChartContainer">
      <h3>{chartName}</h3>
      <div
        ref={chartContainerRef}
        className="chart"
        style={{ width: "100%", height: "100%" }}
      />
    </div>
  );
};

export default LineChart;
