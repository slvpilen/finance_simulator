package simulator.analyse;

public interface AnalyseListener {

    public void iterationDate(int date, double cash);

    public default void addOpenPosition(Position p) {
    }

    public default void removeOpenPosition(Position p) {
    }

}
