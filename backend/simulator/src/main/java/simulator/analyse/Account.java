package simulator.analyse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import simulator.bases.StrategyBase;
import simulator.result.ResultService;

/*
 * 
 * This class is the main class for the analysis. It is responsible for running
 * the simulation. It handles the account and the strategies. It also handles
 * the communication between the account and the strategies. It is also
 * responsible for the communication between the account and the resultService.
 * It is also responsible for the position handling.
 *
*/
public class Account implements AnalyseListener {

    private double cash;
    @Getter
    private List<Position> openPositions = new ArrayList<>();
    public ResultService resultService = new ResultService();;
    private List<StrategyBase> strategies = new ArrayList<>();
    private List<AnalyseListener> analyseListners = new ArrayList<>();
    private int date;
    @Setter
    private boolean simulationStarted = false;

    public Account(double cash) {
        if (cash <= 0)
            throw new IllegalArgumentException(
                    "Cash in a account can't be negative or 0 in constructure!");

        this.cash = cash;
        addAnalyseListener(resultService);
        addAnalyseListener(this);
    }

    public void addStrategies(List<StrategyBase> strategies) {
        strategies.forEach(strategy -> addStrategy(strategy));
    }

    public void addStrategy(StrategyBase strategy) {
        if (simulationStarted)
            throw new IllegalStateException(
                    "Can't add strategy after simulation has started!");

        this.strategies.add(strategy);
        addAnalyseListener(strategy);

        // List<AnalyseListener> listnerInnStrategy = strategy.getAnalyseListners();
        // listnerInnStrategy.forEach(listner -> addAnalyseListener(listner));
    }

    public void noticeStrategysBeforeSimulation() {
        strategies.forEach(
                strategy -> strategy.beforeAllIteration());
        strategies
                .forEach(strategy -> strategy.beforeAllIteration(
                        new ArrayList<>(strategies)));

        strategies.get(0).beforeAllIterationOnlyOne();
    }

    /*
     * The order of the getSellSignal and getLongSignal is important and in some
     * cases it should be otherwise. For example if the strategy is short only
     * 
     * The hasEnoughhData if-statement is used to avoid problems when a stock
     * missing data for a periode, but have data after. This problem often occures
     * when a stock is newly listed.
     * 
     * The strategies.forEach( is runned twice because we want to sell in every
     * stock/strategy befor longing/buying
     * 
     * The cash input below is -1, because all use the same interface and the cash
     * is not used below. Account handle cash (not Analyser)
     */
    @Override
    public void iterationDate(int date, double cash) {
        this.date = date;
        updateListnersDateAndCash();

        strategies.get(0).beforeEachTickOnlyOne();

        strategies.forEach(strategy -> {
            strategy.beforeEachTick();
            checkSellSignal(strategy);
        });

        strategies.forEach(strategy -> {
            checkBuySignal(strategy);
        });

        strategies.forEach(strategy -> {
            checkSellSignal(strategy);
        });
        strategies.forEach(strategy -> {
            checkBuySignal(strategy);
            strategy.afterEachTick();
        });

        resultService.addDailyAccountValue();
    }

    private void checkSellSignal(StrategyBase strategy) {

        if (!strategy.hasEnoughhData()) {
            return;
        }

        if (strategy.isLongAllowed()
                && strategy.getSellSignal()) {
            addPosition(strategy, -strategy.getSellAmount(),
                    strategy.getSellPrice());
            updateListnersDateAndCash();
        }

        if (strategy.isShortAllowed()
                && strategy.getShortSignal()) {
            addPosition(strategy, -strategy.getSellAmount(),
                    strategy.getShortPrice());
            updateListnersDateAndCash();
        }
    }

    private void checkBuySignal(StrategyBase strategy) {

        if (!strategy.hasEnoughhData()) {
            return;
        }

        if (strategy.isLongAllowed()
                && strategy.getLongSignal()) {
            addPosition(strategy, strategy.getBuyAmount(),
                    strategy.getBuyPrice());
            updateListnersDateAndCash();
        }

        if (strategy.isShortAllowed()
                && strategy.getCoverSignal()) {
            addPosition(strategy, strategy.getBuyAmount(),
                    strategy.getCoverPrice());
            updateListnersDateAndCash();
        }
    }

    private void updateListnersDateAndCash() {
        analyseListners.forEach(listner -> {
            if (listner == this) {
                return; // skip to next iteration to avoid infinit loop
            }
            listner.iterationDate(date, cash);
        });
    }

    private void addAnalyseListener(AnalyseListener listner) {
        analyseListners.add(listner);
    }

    private void removeListner(AnalyseListener listner) {
        analyseListners.remove(listner);
    }

    public void addPosition(StrategyBase strategy, int amount,
            double price) {
        if (strategy.getInnehav() == 0) {
            handleNewPosition(strategy, amount, price);
        } else {
            handleExistingPosition(strategy, amount, price);
        }
    }

    /**
     * 
     * helping methode for addPosition
     * 
     * @param strategy
     * @param amount
     * @param price
     */
    private void handleExistingPosition(StrategyBase strategy,
            int amount, double price) {
        Position position = strategy.getOpenPosition();
        int holdings = position.getHoldings();

        boolean flippingFromLongToShort = holdings > 0
                && holdings + amount < 0;
        boolean flippingFromShortToLong = holdings < 0
                && holdings + amount > 0;

        if (flippingFromLongToShort || flippingFromShortToLong) {
            int rest = holdings + amount;
            position.addPosition(-holdings, price);

            resultService.addClosedTrade(position, amount); // this handles logikk (ex if amount+/-)
            position.addPosition(rest, price);
        } else {
            position.addPosition(amount, price);

            resultService.addClosedTrade(position, amount); // this handles logikk (ex if amount+/-)
        }

        this.cash -= amount * price;
        if (this.cash < 0)
            throw new IllegalStateException(
                    "negative amount of cash is not supperted!");

        if (position.getHoldings() == 0) {
            handlePositionClosure(position);
        }

    }

    /**
     * 
     * helping methode for handleExistingPosition
     * 
     * @param strategy
     * @param amount
     * @param price
     */
    private void handlePositionClosure(Position position) {
        removeListner(position);
        position.getStrategy().setOpenPosition(null);

        analyseListners.forEach(
                listner -> listner.removeOpenPosition(position));
    }

    /**
     * 
     * helping methode for addPosition
     * 
     * @param strategy
     * @param amount
     * @param price
     */
    private void handleNewPosition(StrategyBase strategy,
            int amount, double price) {
        Position newPosition;

        if (strategy.isLongAllowed()
                && !strategy.isShortAllowed()) {
            newPosition = new LongPosition(strategy.getSymbol(),
                    amount, price, strategy, date);
        } else if (!strategy.isLongAllowed()
                && strategy.isShortAllowed()) {
            newPosition = new ShortPosition(strategy.getSymbol(),
                    amount, price, strategy, date);
        } else {
            newPosition = new Position(strategy.getSymbol(),
                    amount, price, strategy, date);
        }

        strategy.setOpenPosition(newPosition);
        addAnalyseListener(newPosition);

        analyseListners.forEach(
                listner -> listner.addOpenPosition(newPosition));
        this.cash -= amount * price;
        if (this.cash < 0) {
            System.out.println("cash: " + this.cash + " price: "
                    + price + " amount " + amount
                    + " symbolName: " + strategy.getSymbolName()
                    + " data: " + this.date);
            throw new IllegalStateException(
                    "negative amount of cash is not supperted!");
        }
    }

    /**
     * Ithereat trhu all openposition multiply current price with amount and add
     * cash.
     * 
     * @return value of open position + cash
     */
    public double getAccountValue() {

        return openPositions.stream()
                .mapToDouble(
                        position -> position.getPositionValue())
                .sum() + cash;
    }

    public double getCash() { return this.cash; }

    // bad?
    public ResultService getResult() { return resultService; }

    public boolean hasStrategys() {
        return strategies.size() > 0;
    }

    @Override
    public void addOpenPosition(Position position) {
        openPositions.add(position);
    }

    @Override
    public void removeOpenPosition(Position position) {
        openPositions.remove(position);
    }

    public List<String> getAllSymbolNames() {
        List<String> symbolNames = new ArrayList<String>();
        for (StrategyBase strategy : strategies) {
            symbolNames.add(strategy.getSymbolName());
        }
        return symbolNames;
    }

    /**
     * Determines the strategy with the most dates.
     *
     * @param strategies the list of strategies to analyze
     * @return the list of dates of the strategy with the most dates
     * @throws IllegalArgumentException if no strategies are provided
     */
    public List<Integer> getStrategyWithMostDates() {
        StrategyBase strategyWithMostDates = strategies.stream()
                .max(Comparator
                        .comparingInt(p -> p.getDates().size()))
                .orElse(null);

        if (strategyWithMostDates == null)
            throw new IllegalArgumentException(
                    "Could not find any dates");

        return strategyWithMostDates.getDates();
    }

}
