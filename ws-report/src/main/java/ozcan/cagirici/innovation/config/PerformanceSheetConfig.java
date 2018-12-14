package ozcan.cagirici.innovation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
public class PerformanceSheetConfig {

    @Getter
    @Setter
    @Value("${sheet.performance.source}")
    private String source;

    @Getter
    @Setter
    @Value("${sheet.performance.data.name}")
    private String dataName;

    @Getter
    @Setter
    @Value("${sheet.performance.data.startColumn}")
    private String dataStartColumn;

    @Getter
    @Setter
    @Value("${sheet.performance.data.endColumn}")
    private String dataEndColumn;

    public String getDataRange() {
        return dataName + "!" + dataStartColumn + ":" + dataEndColumn;
    }
}
