package database.fetch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class ShellScriptExecutor {

    public static void executeShellScript(String scriptPath) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", scriptPath);

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            System.out.println("Exit code: " + exitCode);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(
                            process.getErrorStream()));
            String errorLine;
            while ((errorLine = errorReader
                    .readLine()) != null) {
                System.err.println(errorLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
