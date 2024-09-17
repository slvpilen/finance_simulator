package database.fetch;

public class FetchDataToDB {

    /*
     * Remember to give access: chmod +x
     * /home/oskar/Dokumenter/simulator/downloadData/fetch_all_tick_daily.sh
     */
    public static void downloadDailyData() {
        // dev path
        String scriptPath = "/home/oskar/Dokumenter/simulator/downloadData/fetch_all_tick_daily.sh";
        ShellScriptExecutor.executeShellScript(scriptPath);
    }

    public static void main(String[] args) {
        downloadDailyData();
    }
}
