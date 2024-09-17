package indicators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import database.Symbol;
import database.TickData;

public class RelativeStrengthIndexTest {

        @Test
        @DisplayName("Daily rsi 14")
        void rsi14Test() {
                List<Integer> closes = Arrays.asList(100, 99, 98,
                                97, 96, 95, 94, 93, 92, 91, 90,
                                89, 88, 87, 86);
                List<Integer> dates = Arrays.asList(20240101,
                                20240102, 20240103, 20240104,
                                20240105, 20240106, 20240107,
                                20240108, 20240109, 20240110,
                                20240111, 20240112, 20240113,
                                20240114, 20240115);

                List<TickData> ticks = generateTickDatas(closes,
                                dates);

                Symbol demoSymbol = Symbol.symbolBuilder()
                                .tickDataList(ticks)
                                .name("Demo Symbol").ticker("DS")
                                .build();

                RelativeStrengthIndex rsi = new RelativeStrengthIndex(
                                demoSymbol, 14);

                rsi.iterationDate(20240115, -1);

                double rsiValue = rsi.getValue();

                assertEquals(0, rsiValue);

        }

        private List<TickData> generateTickDatas(
                        List<Integer> closes,
                        List<Integer> dates) {
                if (closes.size() != dates.size()) {
                        throw new IllegalArgumentException(
                                        "Need same amount of dates and closes");
                }

                List<TickData> tickDatas = new ArrayList<>();
                for (int i = 0; i < closes.size(); i++) {
                        int date = dates.get(i);
                        double close = closes.get(i);

                        tickDatas.add(TickData.tickBuilder()
                                        .close(close).date(date)
                                        .build());
                }

                return tickDatas;
        }

}
