package com.aurea.wsproreport.component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import com.aurea.wsproreport.model.PerformanceProductivityModel;
import com.aurea.wsproreport.model.WeeklyComplianceModel;
import com.aurea.wsproreport.util.StringUtil;

@Component
public class WeeklyReportTemplateBuilder {

    private final TemplateEngine templateEngine;

    private final String templateName = "WeeklyProductivityAndCompliance";

    @Autowired
    public WeeklyReportTemplateBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String buildTemplate(final Map<String, WeeklyComplianceModel> filteredRowMap,
            final List<PerformanceProductivityModel> performanceProductivityModels) {
        Context context = new Context();
        Integer numberOfSucDaysLastWeek = 0;
        Integer numberOfICsAverage = performanceProductivityModels.size();
        for (PerformanceProductivityModel pm : performanceProductivityModels) {
            if (StringUtil.nvlOrEmpty(pm.getWeek4team(), false)) {
                numberOfSucDaysLastWeek += pm.getWeek3Score();
            } else if (StringUtil.nvlOrEmpty(pm.getWeek3team(), false)) {
                numberOfSucDaysLastWeek += pm.getWeek2Score();
            } else if (StringUtil.nvlOrEmpty(pm.getWeek2team(), false)) {
                numberOfSucDaysLastWeek += pm.getWeek1Score();
            } else {
                numberOfICsAverage--;
            }
        }
        BigDecimal averageScore = new BigDecimal(numberOfSucDaysLastWeek).divide(new BigDecimal(numberOfICsAverage),2, RoundingMode.HALF_EVEN);
        context.setVariable("productivity", performanceProductivityModels);
        context.setVariable("compliance", filteredRowMap.values());
        context.setVariable("numberOfICs", performanceProductivityModels.size());
        context.setVariable("numberOfSucDays", numberOfSucDaysLastWeek.toString());
        context.setVariable("averageScore", String.format("%s / 5", averageScore.toString()));
        return templateEngine.process(templateName, context);
    }
}
