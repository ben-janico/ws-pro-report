package com.aurea.wsproreport.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Configuration
public class GradebookSheetConfig {

    @Getter
    @Setter
    @Value("${sheet.gradebook.source}")
    private String source;

    @Getter
    @Setter
    @Value("${sheet.gradebook.data.name}")
    private String dataName;

    @Getter
    @Setter
    @Value("${sheet.gradebook.data.startColumn}")
    private String dataStartColumn;

    @Getter
    @Setter
    @Value("${sheet.gradebook.data.endColumn}")
    private String dataEndColumn;

    public String getDataRange() {
        return dataName + "!" + dataStartColumn + ":" + dataEndColumn;
    }
}
