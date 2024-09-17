package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseConnection {
    public static Connection connection = createConnection();

    /*
     * Note: The passsword is stored in a .env file, which is not pushed to the git
     * repository for security reasons. The .env file is located in the root of the
     * project (/simulator/.env). (Checkout: Is it possible to acces the .env file
     * in a better way, then using absolute path?)
     */
    private static Connection createConnection() {
        // Dotenv dotenv;
        try {
            // dotenv = Dotenv.configure()// .directory(".\\..")
            // .load();
            String PASSWORD = "replace-password";// dotenv.get("POSTGRES_PASSWORD");
            String USER = "postgres";// dotenv.get("POSTGRES_USER");
            String URL = "jdbc:postgresql://localhost:5432/FinanceDB";// dotenv.get("POSTGRES_URL");

            return connection = DriverManager.getConnection(URL,
                    USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to connect to database", e);
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}