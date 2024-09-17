package springboot.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

        @Autowired
        private ShellScriptExecutor shellScriptExecutor;

        @Scheduled(cron = "0 10 9 * * MON-FRI")
        @Scheduled(cron = "0 35 15 * * MON-FRI")
        @Scheduled(cron = "0 18 17 * * MON-FRI")
        @Scheduled(cron = "0 38 21 * * MON-FRI")
        @Scheduled(cron = "0 30 22 * * MON-FRI")
        @Scheduled(cron = "0 0 6 * * SAT")
        public void downloadDailyData() {
                System.out.println(
                                "Download daiy data :: Execution Time - "
                                                + System.currentTimeMillis());
                shellScriptExecutor
                                .executeDownloadDailyDataScript();
        }

        @Scheduled(cron = "0 0 12 * * MON-FRI")
        @Scheduled(cron = "0 1 16 * * MON-FRI")
        public void downloadPrecloseData() {
                System.out.println(
                                "Download preclose data :: Execution Time - "
                                                + System.currentTimeMillis());
                shellScriptExecutor
                                .executeDownloadPreclsoeDataScript();
        }

        @Scheduled(cron = "0 10 16 * * MON-FRI")
        public void runStrategiesScheduled1610() {
                strategies.GlobalSettings.storeHoldingsInDatabase = true;
                strategies.GlobalSettings.closeDatabaseConnection = false;

                strategies.makroTrendStrategy.StrategyRunnerLive
                                .main(new String[] {});

        }

        @Scheduled(cron = "0 45 21 * * MON-FRI")
        public void runStrategiesScheduled2145() {
                strategies.GlobalSettings.storeHoldingsInDatabase = true;
                strategies.GlobalSettings.closeDatabaseConnection = false;

                strategies.presidentElection.StrategyRunner
                                .main(new String[] {});

                strategies.mtShortEdge.StrategyRunner
                                .main(new String[] {});

                strategies.presidentElection.StrategyRunner
                                .main(new String[] {});

                strategies.medicineCycle.StrategyRunner
                                .main(new String[] {});

        }

        @Scheduled(cron = "0 15 22 * * MON-FRI")
        public void downloadDividends() {
                System.out.println(
                                "Download dividends data :: Execution Time - "
                                                + System.currentTimeMillis());
                shellScriptExecutor
                                .executeDownloadDividendsScript();
        }

}
