package database.fetch;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RunPythonWithPipenv {
    public static void main(String[] args) {
        try {
            // Build the command to run the Python script through Pipenv
            String scriptPath = "/home/oskar/Dokumenter/simulator/downloadData/fetch_all_tick_daily.py";
            String[] command = { "pipenv", "run", "python",
                    scriptPath };

            // Create a process builder
            ProcessBuilder processBuilder = new ProcessBuilder(
                    command);

            // Start the process
            Process process = processBuilder.start();

            // Read the output of the command
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("OUTPUT: " + line);
            }

            // Read the error stream of the command
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(
                            process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.err.println("ERROR: " + line);
            }

            // Wait for the process to complete and check the exit value
            int exitCode = process.waitFor();
            System.out.println("Exit Code: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}