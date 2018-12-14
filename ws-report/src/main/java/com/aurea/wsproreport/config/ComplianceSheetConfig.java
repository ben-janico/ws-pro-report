package com.aurea.wsproreport.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import lombok.Getter;
import lombok.Setter;

@Configuration
@PropertySource("classpath:application.properties")
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
    private String masterName;

    @Getter
    @Setter
    @Value("${sheet.compliance.email.startColumn}")
    private String masterStartColumn;

    @Getter
    @Setter
    @Value("${sheet.compliance.email.endColumn}")
    private String masterEndColumn;

    public String getDataRange() {
        return dataName + "!" + dataStartColumn + ":" + dataEndColumn;
    }

    public String getMasterRange() {
        return masterName + "!" + masterStartColumn + ":" + masterEndColumn;
    }
}
