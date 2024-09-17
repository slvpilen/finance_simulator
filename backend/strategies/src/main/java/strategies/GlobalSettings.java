package strategies;

public class GlobalSettings {
    /*
     * Set to true to close the database connection after the strategy has been run
     * True in development, when running the strategy directly in shell scripts, but
     * false in production, when database connectipn should be kept open.
     */
    public static boolean closeDatabaseConnection = true;
    public static boolean storeHoldingsInDatabase = false;

}
