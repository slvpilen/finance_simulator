package database.apiModels;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class LineChartData {

    public String type;
    public String frequence;
    public List<LineChartTick> dataPoints = new ArrayList<>();

    @Data
    public class LineChartTick {
        public int date;
        public double value;

        public LineChartTick(int date, double value) {
            this.date = date;
            this.value = value;
        }
    }

    public void addDataPoint(int date, double value) { this.dataPoints.add(new LineChartTick(date, value)); }

}