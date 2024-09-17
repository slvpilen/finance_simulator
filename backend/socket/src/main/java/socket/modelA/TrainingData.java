package socket.modelA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

public class TrainingData {

        private Map<Integer, Integer> dates_index; //
        private Map<Integer, Integer> index_dates; //

        private List<Double> dayOfWeek;
        private List<Double> dayOfMonth;
        private List<Double> roc1;
        private List<Double> roc2;
        private List<Double> roc3;
        private List<Double> roc4;
        private List<Double> roc5;
        private List<Double> oneDaysLater;
        private List<Double> twoDaysLater;
        private List<Double> threeDaysLater;
        private List<Double> fourDaysLater;
        private List<Double> fiveDaysLater;
        private List<Double> sixDaysLater;
        private List<Double> sevenDaysLater;
        private List<Double> eightDaysLater;
        private List<Double> nineDaysLater;
        private List<Double> tenDaysLater;
        private List<Double> elevenDaysLater;
        private List<Double> twelveDaysLater;
        private List<Double> thirteenDaysLater;
        private List<Double> fourteenDaysLater;
        private List<Double> fifteenDaysLater;
        private List<Double> rsiDaily;
        private List<Double> rsiWeekly;
        private List<Double> rsiMonthly;

        public TrainingData() {
                dates_index = new HashMap<>();
                index_dates = new HashMap<>();
                dayOfWeek = new ArrayList<>();
                dayOfMonth = new ArrayList<>();
                roc1 = new ArrayList<>();
                roc2 = new ArrayList<>();
                roc3 = new ArrayList<>();
                roc4 = new ArrayList<>();
                roc5 = new ArrayList<>();
                oneDaysLater = new ArrayList<>();
                twoDaysLater = new ArrayList<>();
                threeDaysLater = new ArrayList<>();
                fourDaysLater = new ArrayList<>();
                fiveDaysLater = new ArrayList<>();
                sixDaysLater = new ArrayList<>();
                sevenDaysLater = new ArrayList<>();
                eightDaysLater = new ArrayList<>();
                nineDaysLater = new ArrayList<>();
                tenDaysLater = new ArrayList<>();
                elevenDaysLater = new ArrayList<>();
                twelveDaysLater = new ArrayList<>();
                thirteenDaysLater = new ArrayList<>();
                fourteenDaysLater = new ArrayList<>();
                fifteenDaysLater = new ArrayList<>();
                rsiDaily = new ArrayList<>();
                rsiWeekly = new ArrayList<>();
                rsiMonthly = new ArrayList<>();

        }

        public void add(int date, double dayOfWeek,
                        double dayOfMonth, double roc1,
                        double roc2, double roc3, double roc4,
                        double roc5, double oneDaysLater,
                        double twoDaysLater,
                        double threeDaysLater,
                        double fourDaysLater,
                        double fiveDaysLater,
                        double sixDaysLater,
                        double sevenDaysLater,
                        double eightDaysLater,
                        double nineDaysLater,
                        double tenDaysLater,
                        double elevenDaysLater,
                        double twelveDaysLater,
                        double thirteenDaysLater,
                        double fourteenDaysLater,
                        double fifteenDaysLater, double rsiDaily,
                        double rsiWeekly, double rsiMonthly) {

                // date, index
                this.dates_index.put(date, this.roc1.size());
                this.index_dates.put(this.roc1.size(), date);

                // if any of data is lower then -100 then it is not valid data
                if (roc1 < -100 || roc2 < -100 || roc3 < -100
                                || roc4 < -100 || roc5 < -100
                                || oneDaysLater < -100
                                || twoDaysLater < -100
                                || threeDaysLater < -100
                                || fourDaysLater < -100
                                || fiveDaysLater < -100
                                || sixDaysLater < -100
                                || sevenDaysLater < -100
                                || eightDaysLater < -100
                                || nineDaysLater < -100
                                || tenDaysLater < -100
                                || elevenDaysLater < -100
                                || twelveDaysLater < -100
                                || thirteenDaysLater < -100
                                || fourteenDaysLater < -100
                                || fifteenDaysLater < -100
                                || rsiDaily < 0
                                || rsiWeekly < 0) {
                        return;
                }

                this.dayOfWeek.add(dayOfWeek);
                this.dayOfMonth.add(dayOfMonth);
                this.roc1.add(roc1);
                this.roc2.add(roc2);
                this.roc3.add(roc3);
                this.roc4.add(roc4);
                this.roc5.add(roc5);
                this.oneDaysLater.add(oneDaysLater);
                this.twoDaysLater.add(twoDaysLater);
                this.threeDaysLater.add(threeDaysLater);
                this.fourDaysLater.add(fourDaysLater);
                this.fiveDaysLater.add(fiveDaysLater);
                this.sixDaysLater.add(sixDaysLater);
                this.sevenDaysLater.add(sevenDaysLater);
                this.eightDaysLater.add(eightDaysLater);
                this.nineDaysLater.add(nineDaysLater);
                this.tenDaysLater.add(tenDaysLater);
                this.elevenDaysLater.add(elevenDaysLater);
                this.twelveDaysLater.add(twelveDaysLater);
                this.thirteenDaysLater.add(thirteenDaysLater);
                this.fourteenDaysLater.add(fourteenDaysLater);
                this.fifteenDaysLater.add(fifteenDaysLater);
                this.rsiDaily.add(rsiDaily);
                this.rsiWeekly.add(rsiWeekly);
                this.rsiMonthly.add(rsiMonthly);

        }

        /*
         * This methode is removing all date after the given date and convert to json
         */
        public JSONObject toJson(int date) {
                JSONObject jsonObject = new JSONObject();
                Integer index = dates_index.get(date);
                boolean dateNotInDates = index == null;
                if (dateNotInDates) {
                        // find the closest lower date in dates
                        int closestLowerDate = dates_index
                                        .keySet().stream()
                                        .filter(d -> d < date)
                                        .max(Integer::compare)
                                        .orElseThrow(() -> new IllegalStateException(
                                                        "No date lower than: "
                                                                        + date));
                        index = dates_index
                                        .get(closestLowerDate);
                }
                int startIndex = 0;
                int endIndex = index - 15;
                // startIndex = endIndex - 2500; // 10 years of training data
                startIndex = Math.max(startIndex, 0);
                endIndex = endIndex < 0
                                ? Math.min(endIndex,
                                                dayOfWeek.size())
                                : endIndex;

                jsonObject.put("day_of_week", dayOfWeek
                                .subList(startIndex, endIndex));
                jsonObject.put("day_of_month", dayOfMonth
                                .subList(startIndex, endIndex));
                jsonObject.put("1_day_change", roc1
                                .subList(startIndex, endIndex));
                jsonObject.put("2_day_change", roc2
                                .subList(startIndex, endIndex));
                jsonObject.put("3_day_change", roc3
                                .subList(startIndex, endIndex));
                jsonObject.put("4_day_change", roc4
                                .subList(startIndex, endIndex));
                jsonObject.put("5_day_change", roc5
                                .subList(startIndex, endIndex));
                jsonObject.put("1_days_dater", oneDaysLater
                                .subList(startIndex, endIndex));
                jsonObject.put("2_days_dater", twoDaysLater
                                .subList(startIndex, endIndex));
                jsonObject.put("3_days_dater", threeDaysLater
                                .subList(startIndex, endIndex));
                jsonObject.put("4_days_dater", fourDaysLater
                                .subList(startIndex, endIndex));
                jsonObject.put("5_days_dater", fiveDaysLater
                                .subList(startIndex, endIndex));
                jsonObject.put("6_days_dater", sixDaysLater
                                .subList(startIndex, endIndex));
                jsonObject.put("7_days_dater", sevenDaysLater
                                .subList(startIndex, endIndex));
                jsonObject.put("8_days_dater", eightDaysLater
                                .subList(startIndex, endIndex));
                jsonObject.put("9_days_dater", nineDaysLater
                                .subList(startIndex, endIndex));
                jsonObject.put("10_days_dater", tenDaysLater
                                .subList(startIndex, endIndex));
                jsonObject.put("11_days_dater", elevenDaysLater
                                .subList(startIndex, endIndex));
                jsonObject.put("12_days_dater", twelveDaysLater
                                .subList(startIndex, endIndex));
                jsonObject.put("13_days_dater", thirteenDaysLater
                                .subList(startIndex, endIndex));
                jsonObject.put("14_days_dater", fourteenDaysLater
                                .subList(startIndex, endIndex));
                jsonObject.put("15_days_dater", fifteenDaysLater
                                .subList(startIndex, endIndex));
                jsonObject.put("rsi_daily", rsiDaily
                                .subList(startIndex, endIndex));
                jsonObject.put("rsi_weekly", rsiWeekly
                                .subList(startIndex, endIndex));
                jsonObject.put("rsi_monthly", rsiMonthly
                                .subList(startIndex, endIndex));

                return jsonObject;
        }

        /*
         * Printing all data in CSV format
         */
        public void printToCSV() {
                // Header
                System.out.println(
                                "date,day_of_week,day_of_month,roc1,roc2,roc3,roc4,roc5,oneDaysLater,twoDaysLater,threeDaysLater,fourDaysLater,fiveDaysLater,sixDaysLater,sevenDaysLater,eightDaysLater,nineDaysLater,tenDaysLater,elevenDaysLater,twelveDaysLater,thirteenDaysLater,fourteenDaysLater,fifteenDaysLater,rsi_daily,rsi_weekly,rsi_monthly");
                for (int i = 0; i < dayOfWeek.size(); i++) {
                        System.out.print(index_dates.get(i)
                                        + ",");
                        System.out.print(dayOfWeek.get(i) + ",");
                        System.out.print(
                                        dayOfMonth.get(i) + ",");
                        System.out.print(roc1.get(i) + ",");
                        System.out.print(roc2.get(i) + ",");
                        System.out.print(roc3.get(i) + ",");
                        System.out.print(roc4.get(i) + ",");
                        System.out.print(roc5.get(i) + ",");
                        System.out.print(oneDaysLater.get(i)
                                        + ",");
                        System.out.print(twoDaysLater.get(i)
                                        + ",");
                        System.out.print(threeDaysLater.get(i)
                                        + ",");
                        System.out.print(fourDaysLater.get(i)
                                        + ",");
                        System.out.print(fiveDaysLater.get(i)
                                        + ",");
                        System.out.print(sixDaysLater.get(i)
                                        + ",");
                        System.out.print(sevenDaysLater.get(i)
                                        + ",");
                        System.out.print(eightDaysLater.get(i)
                                        + ",");
                        System.out.print(nineDaysLater.get(i)
                                        + ",");
                        System.out.print(tenDaysLater.get(i)
                                        + ",");
                        System.out.print(elevenDaysLater.get(i)
                                        + ",");
                        System.out.print(twelveDaysLater.get(i)
                                        + ",");
                        System.out.print(thirteenDaysLater.get(i)
                                        + ",");
                        System.out.print(fourteenDaysLater.get(i)
                                        + ",");
                        System.out.print(fifteenDaysLater.get(i)
                                        + ",");
                        System.out.print(rsiDaily.get(i) + ",");
                        System.out.print(rsiWeekly.get(i) + ",");
                        System.out.print(rsiMonthly.get(i));

                        System.out.println();

                }
        }
}
