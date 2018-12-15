package com.aurea.wsproreport.component;

import java.util.Calendar;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.aurea.wsproreport.config.TemplateConfig;
import com.aurea.wsproreport.model.DailyReportModel;
import com.aurea.wsproreport.util.DateUtil;

@Component
public class DailyReportTemplateBuilder {

    private final TemplateEngine templateEngine;

    @Autowired
    private TemplateConfig templateConfig;

    @Autowired
    public DailyReportTemplateBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String buildTemplate(final Map<String, DailyReportModel> dailyReportModelMap, Calendar reportDate) {
        Context context = new Context();
        context.setVariable("values", dailyReportModelMap.values());
        context.setVariable("date", DateUtil.formatDate(reportDate));
        return templateEngine.process(templateConfig.getDailyReportName(), context);
    }
}
