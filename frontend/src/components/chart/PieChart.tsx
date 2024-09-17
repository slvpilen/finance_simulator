import { ResponsivePie } from "@nivo/pie";

// export type Props = {};

// const data = [
//   { id: "Makro Trend", label: "Makro Trend", value: 600 },
//   { id: "Ex2", label: "Ex2", value: 200 },
//   { id: "Ex3", label: "Ex3", value: 200 },
// ];

export type PieChartData = {
  id: string;
  label: string;
  value: number;
}[];

export type Props = {
  data: PieChartData;
};

const PieChart: React.FC<Props> = ({ data }) => {
  return (
    <div style={{ height: 220, maxWidth: "100%" }}>
      <ResponsivePie
        data={data}
        margin={{ top: 40, right: 80, bottom: 80, left: 80 }}
        innerRadius={0.5}
        padAngle={0.7}
        cornerRadius={3}
        colors={{ scheme: "nivo" }}
        borderWidth={1}
        borderColor={{ from: "color", modifiers: [["darker", 0.2]] }}
        legends={[
          {
            anchor: "bottom",
            direction: "row",
            translateY: 56,
            itemWidth: 100,
            itemHeight: 18,
            itemTextColor: "#999",
            symbolSize: 18,
            symbolShape: "circle",
            effects: [
              {
                on: "hover",
                style: {
                  itemTextColor: "#000",
                },
              },
            ],
          },
        ]}
      />
    </div>
  );
};

export default PieChart;
