
package springboot.scheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
 * Used to execute shell script that fetch database with data from yahoo finance.
 */
@Component
public class ShellScriptExecutor {

    private final DownloadDataProperties downloadDataProperties;

    @Autowired
    public ShellScriptExecutor(
            DownloadDataProperties downloadDataProperties) {
        this.downloadDataProperties = downloadDataProperties;
    }

    protected void executeDownloadDailyDataScript() {
        String scriptPath = downloadDataProperties
                .getDailyDataScriptPath();
        String folderPath = downloadDataProperties
                .getFolderPath();

        executeShellScript(scriptPath, folderPath);
    }

    protected void executeDownloadPreclsoeDataScript() {
        String scriptPath = downloadDataProperties
                .getDailyDataScriptPath();
        String folderPath = downloadDataProperties
                .getFolderPath();

        executeShellScript(scriptPath, folderPath);
    }

    protected void executeDownloadDividendsScript() {
        String scriptPath = downloadDataProperties
                .getDividendsScriptPath();
        String folderPath = downloadDataProperties
                .getFolderPath();

        executeShellScript(scriptPath, folderPath);
    }

    private void executeShellScript(String scriptPath,
            String folderPath) {
        try {
            String[] command = { scriptPath };

            ProcessBuilder processBuilder = new ProcessBuilder(
                    command);
            processBuilder
                    .directory(new java.io.File(folderPath));

            Process process = processBuilder.start();

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
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("Exited with code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
