package strategies.connorsAi.aiModel;

import database.DatabaseService;
import database.DateValueRetriever;
import database.Symbol;
import lombok.Getter;
import simulator.bases.StrategyBase;
import simulator.bases.StrategyRunnerBase;
import socket.SocketClient;
import socket.modelA.TrainingData;
import strategies.connorsAi.StrategyRunner;

import java.util.List;
import java.util.Arrays;

public class ModelATraingDataFactory
                implements TrainingDataFactory {

        private DateValueRetriever symbol;
        @Getter
        private TrainingData trainingData;

        public ModelATraingDataFactory(
                        DateValueRetriever symbol) {
                this.symbol = symbol;
                createTraingData();
        }

        @Override
        public void createTraingData() {
                int START_DATE = 18140101;
                int END_DATE = 21500101;

                DataGenraterStrategy dataGenraterStrategy = new DataGenraterStrategy(
                                (Symbol) symbol, 100000);
                List<StrategyBase> strategies = Arrays
                                .asList(dataGenraterStrategy);

                StrategyRunnerBase strategyRunner = new StrategyRunner(
                                100000, START_DATE, END_DATE,
                                strategies, false);

                strategyRunner.runSimulation();
                trainingData = dataGenraterStrategy
                                .getTrainingData();
        }

        @Override
        public void fit(int date) {
                // Date is
                SocketClient.fitModelA(trainingData, date);

        }

        public static void main(String[] args) {
                // To manually create training data
                DateValueRetriever symbol = DatabaseService
                                .getSymbol("^GSPC");
                ModelATraingDataFactory modelATraingDataFactory = new ModelATraingDataFactory(
                                symbol);

                TrainingData trainingData = modelATraingDataFactory
                                .getTrainingData();

                trainingData.printToCSV();
        }

}
