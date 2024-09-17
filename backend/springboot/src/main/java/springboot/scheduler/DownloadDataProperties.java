package springboot.scheduler;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "downloaddata")
@Getter
@Setter
public class DownloadDataProperties {
    private String dailyDataScriptPath;
    private String precloseDataScriptPath;
    private String dividendsScriptPath;
    private String folderPath;
}
