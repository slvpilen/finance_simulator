package portfoliomixer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import database.apiModels.LineChartData;
import utils.HashArray;

public class PortfolioServiceTest {

    HashArray<Integer, Double> strategyResult1;
    HashArray<Integer, Double> strategyResult2;
    HashArray<Integer, Double> strategyResult3;
    HashArray<Integer, Double> strategyResult4;
    HashArray<Integer, Double> strategyResult5;
    HashArray<Integer, Double> strategyResult6;
    HashArray<Integer, Double> strategyResult7;
    List<HashArray<Integer, Double>> strategyResults;

    @BeforeEach
    void setup() {
        strategyResults = new ArrayList<>();

        strategyResult1 = new HashArray<>();
        strategyResult1.put(20240101, 100.0);
        strategyResult1.put(20240102, 101.0);
        strategyResult1.put(20240103, 102.0);
        strategyResult1.put(20240104, 200.0);
        strategyResult1.put(20240105, 100.0);

        strategyResult2 = new HashArray<>();
        strategyResult2.put(20240101, 100.0);
        strategyResult2.put(20240102, 101.0);
        strategyResult2.put(20240103, 102.0);
        strategyResult2.put(20240104, 200.0);
        strategyResult2.put(20240105, 100.0);

        strategyResult3 = new HashArray<>();
        strategyResult3.put(20231201, 200.0);
        strategyResult3.put(20231202, 202.0);
        strategyResult3.put(20231203, 204.0);

        strategyResult4 = new HashArray<>();
        strategyResult4.put(20240101, 200.0);
        strategyResult4.put(20240103, 202.0);
        strategyResult4.put(20240105, 204.0);

        strategyResult5 = new HashArray<>();
        strategyResult5.put(20240101, 100.0);
        strategyResult5.put(20240102, 122.2);
        strategyResult5.put(20240103, 112.0);
        strategyResult5.put(20240104, 120.4);
        strategyResult5.put(20240105, 121.1);

        strategyResult6 = new HashArray<>();
        strategyResult6.put(20240101, 100.0);
        strategyResult6.put(20240102, 50.0);
        strategyResult6.put(20240103, 25.0);

        strategyResult7 = new HashArray<>();
        strategyResult7.put(20240101, 100.0);
        strategyResult7.put(20240102, 100.0);
        strategyResult7.put(20240103, 100.0);
        strategyResult7.put(20240104, 100.0);
        strategyResult7.put(20240105, 100.0);
    }

    @Test
    @DisplayName("Should be the same as strategyResult1 and strategyResult2.")
    void test1GearingAndNoRebalancing() {
        strategyResults.add(strategyResult1);
        strategyResults.add(strategyResult2);

        PortfolioService portfolioService = new PortfolioServiceTestClass(
                strategyResults, 999, 1);
        LineChartData portfolioMixed = portfolioService
                .getPortfolioMixedResult();

        assertEquals(100,
                portfolioMixed.dataPoints.get(0).value);
        assertEquals(101,
                portfolioMixed.dataPoints.get(1).value);
        assertEquals(102,
                portfolioMixed.dataPoints.get(2).value);
        assertEquals(200,
                portfolioMixed.dataPoints.get(3).value);
        assertEquals(100,
                portfolioMixed.dataPoints.get(4).value);

        assertEquals(20240101,
                portfolioMixed.dataPoints.get(0).date);
        assertEquals(20240102,
                portfolioMixed.dataPoints.get(1).date);
        assertEquals(20240103,
                portfolioMixed.dataPoints.get(2).date);
        assertEquals(20240104,
                portfolioMixed.dataPoints.get(3).date);
        assertEquals(20240105,
                portfolioMixed.dataPoints.get(4).date);
    }

    @Test
    @DisplayName("Testing to different strategy results, with same dates but different values.")
    void test1GearingAndNoRebalancing2() {
        strategyResults.add(strategyResult1);
        strategyResults.add(strategyResult5);

        PortfolioService portfolioService = new PortfolioServiceTestClass(
                strategyResults, 999, 1);
        LineChartData portfolioMixed = portfolioService
                .getPortfolioMixedResult();

        assertEquals(100,
                portfolioMixed.dataPoints.get(0).value);
        assertEquals(111.6,
                portfolioMixed.dataPoints.get(1).value);
        assertEquals(107.0,
                portfolioMixed.dataPoints.get(2).value);
        assertEquals(160.2,
                portfolioMixed.dataPoints.get(3).value);
        assertEquals(110.55,
                portfolioMixed.dataPoints.get(4).value);
    }

    @Test
    @DisplayName("Testing 2x gearing and no rebalancing")
    void test2GearingAndNoRebalancing() {
        strategyResults.add(strategyResult1);
        strategyResults.add(strategyResult2);

        PortfolioService portfolioService = new PortfolioServiceTestClass(
                strategyResults, 999, 2);
        LineChartData portfolioMixed = portfolioService
                .getPortfolioMixedResult();

        assertEquals(100,
                portfolioMixed.dataPoints.get(0).value);
        assertEquals(102,
                portfolioMixed.dataPoints.get(1).value);
        assertEquals(104.02,
                portfolioMixed.dataPoints.get(2).value);
        assertEquals(303.90,
                portfolioMixed.dataPoints.get(3).value);
        assertEquals(0, portfolioMixed.dataPoints.get(4).value,
                "2x gearing and 50% decrease gives 0");
    }

    @Test
    @DisplayName("Testing normalazing with different dates, (and 1x gearing and no rebalancing).")
    void testNormalazing() {
        strategyResults.add(strategyResult1);
        strategyResults.add(strategyResult3);

        PortfolioService portfolioService = new PortfolioServiceTestClass(
                strategyResults, 999, 1);
        LineChartData portfolioMixed = portfolioService
                .getPortfolioMixedResult();

        assertEquals(8, portfolioMixed.dataPoints.size(),
                "Should have 8 data points (5+3)");

        assertEquals(20231201,
                portfolioMixed.dataPoints.get(0).date);
        assertEquals(20231202,
                portfolioMixed.dataPoints.get(1).date);
        assertEquals(20231203,
                portfolioMixed.dataPoints.get(2).date);
        assertEquals(20240101,
                portfolioMixed.dataPoints.get(3).date);
        assertEquals(20240102,
                portfolioMixed.dataPoints.get(4).date);
        assertEquals(20240103,
                portfolioMixed.dataPoints.get(5).date);
        assertEquals(20240104,
                portfolioMixed.dataPoints.get(6).date);
        assertEquals(20240105,
                portfolioMixed.dataPoints.get(7).date);

        assertEquals(100,
                portfolioMixed.dataPoints.get(0).value);
        assertEquals(100.5,
                portfolioMixed.dataPoints.get(1).value);
        assertEquals(101,
                portfolioMixed.dataPoints.get(2).value);
        assertEquals(101,
                portfolioMixed.dataPoints.get(3).value);
        assertEquals(101.5,
                portfolioMixed.dataPoints.get(4).value);
        assertEquals(102,
                portfolioMixed.dataPoints.get(5).value);
        assertEquals(151,
                portfolioMixed.dataPoints.get(6).value);
        assertEquals(101,
                portfolioMixed.dataPoints.get(7).value);
    }

    @Test
    @DisplayName("Testing normalazing when not all dates matches, (with 1x gearing and no rebalancing).")
    void testNormalazing2() {
        strategyResults.add(strategyResult1);
        strategyResults.add(strategyResult4);

        PortfolioService portfolioService = new PortfolioServiceTestClass(
                strategyResults, 999, 1);
        LineChartData portfolioMixed = portfolioService
                .getPortfolioMixedResult();

        assertEquals(5, portfolioMixed.dataPoints.size(),
                "Should have 5 data points");

        assertEquals(20240101,
                portfolioMixed.dataPoints.get(0).date);
        assertEquals(20240102,
                portfolioMixed.dataPoints.get(1).date);
        assertEquals(20240103,
                portfolioMixed.dataPoints.get(2).date);
        assertEquals(20240104,
                portfolioMixed.dataPoints.get(3).date);
        assertEquals(20240105,
                portfolioMixed.dataPoints.get(4).date);

        assertEquals(100,
                portfolioMixed.dataPoints.get(0).value);
        assertEquals(100.5,
                portfolioMixed.dataPoints.get(1).value);
        assertEquals(101.50,
                portfolioMixed.dataPoints.get(2).value);
        assertEquals(150.5,
                portfolioMixed.dataPoints.get(3).value);
        assertEquals(101.0,
                portfolioMixed.dataPoints.get(4).value);
    }

    @Test
    @DisplayName("Testing 0,5x gearing (its not gearing, only 50% exposure), without normalazing and no rebalancing.")
    void testGearing() {
        strategyResults.add(strategyResult1);
        strategyResults.add(strategyResult5);

        PortfolioService portfolioService = new PortfolioServiceTestClass(
                strategyResults, 999, 0.5);
        LineChartData portfolioMixed = portfolioService
                .getPortfolioMixedResult();

        assertEquals(100,
                portfolioMixed.dataPoints.get(0).value);
        assertEquals(105.8,
                portfolioMixed.dataPoints.get(1).value);
        assertEquals(103.73,
                portfolioMixed.dataPoints.get(2).value);
        assertEquals(129.99,
                portfolioMixed.dataPoints.get(3).value);
        assertEquals(111.46,
                portfolioMixed.dataPoints.get(4).value);
    }

    @Test
    @DisplayName("After portfolio goes to zero it should stay at zero.")
    void testPortfolioZero() {
        // This assuming gearing is working as intended
        strategyResults.add(strategyResult6);

        PortfolioService portfolioService = new PortfolioServiceTestClass(
                strategyResults, 999, 2);
        LineChartData portfolioMixed = portfolioService
                .getPortfolioMixedResult();

        assertEquals(100,
                portfolioMixed.dataPoints.get(0).value);
        assertEquals(0, portfolioMixed.dataPoints.get(1).value);
        assertEquals(0, portfolioMixed.dataPoints.get(2).value);
    }

    @Test
    @DisplayName("After portfolio goes below 0, it should stay at below zero.")
    void testPortfolioBelowZero() {
        strategyResults.add(strategyResult6);

        PortfolioService portfolioService = new PortfolioServiceTestClass(
                strategyResults, 999, 3);
        LineChartData portfolioMixed = portfolioService
                .getPortfolioMixedResult();

        assertEquals(100,
                portfolioMixed.dataPoints.get(0).value);
        assertEquals(-50,
                portfolioMixed.dataPoints.get(1).value);
        assertEquals(-50,
                portfolioMixed.dataPoints.get(2).value);
    }

    @Test
    @DisplayName("Daily rebalancing without gearing.")
    void testRebalancing() {
        strategyResults.add(strategyResult1);
        strategyResults.add(strategyResult5);

        PortfolioService portfolioService = new PortfolioServiceTestClass(
                strategyResults, 1, 1);
        LineChartData portfolioMixed = portfolioService
                .getPortfolioMixedResult();

        assertEquals(100,
                portfolioMixed.dataPoints.get(0).value);
        assertEquals(111.6,
                portfolioMixed.dataPoints.get(1).value);
        assertEquals(107.49,
                portfolioMixed.dataPoints.get(2).value);
        assertEquals(163.17,
                portfolioMixed.dataPoints.get(3).value);
        assertEquals(122.85,
                portfolioMixed.dataPoints.get(4).value);
    }

    @Test
    @DisplayName("Rebalancing every second day with 2x gearing.")
    void testGearingWithRebalancing() {
        strategyResults.add(strategyResult1);
        strategyResults.add(strategyResult7);

        PortfolioService portfolioService = new PortfolioServiceTestClass(
                strategyResults, 2, 2);
        LineChartData portfolioMixed = portfolioService
                .getPortfolioMixedResult();

        assertEquals(100,
                portfolioMixed.dataPoints.get(0).value);
        assertEquals(101.0,
                portfolioMixed.dataPoints.get(1).value);
        assertEquals(102.01,
                portfolioMixed.dataPoints.get(2).value);
        assertEquals(200.02,
                portfolioMixed.dataPoints.get(3).value);
        assertEquals(51.0,
                portfolioMixed.dataPoints.get(4).value);
    }
}
