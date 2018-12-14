package ozcan.cagirici.innovation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Configuration
public class ComplianceSheetConfig {

    @Getter
    @Setter
    @Value("${sheet.appName}")
    private String appName;

    @Getter
    @Setter
    @Value("${sheet.compliance.source}")
    private String source;

    @Getter
    @Setter
    @Value("${sheet.compliance.data.name}")
    private String dataName;

    @Getter
    @Setter
    @Value("${sheet.compliance.data.startColumn}")
    private String dataStartColumn;

    @Getter
    @Setter
    @Value("${sheet.compliance.data.endColumn}")
    private String dataEndColumn;

    @Getter
    @Setter
    @Value("${sheet.compliance.email.name}")
    private String emailName;

    @Getter
    @Setter
    @Value("${sheet.compliance.email.startColumn}")
    private String emailStartColumn;

    @Getter
    @Setter
    @Value("${sheet.compliance.email.endColumn}")
    private String emailEndColumn;

    public String getDataRange() {
        return dataName + "!" + dataStartColumn + ":" + dataEndColumn;
    }

    public String getEmailRange() {
        return emailName + "!" + emailStartColumn + ":" + emailEndColumn;
    }
}
