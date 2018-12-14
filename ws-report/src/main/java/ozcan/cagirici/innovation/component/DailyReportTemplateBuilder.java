package ozcan.cagirici.innovation.component;

import java.util.Calendar;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import ozcan.cagirici.innovation.model.DailyReportModel;
import ozcan.cagirici.innovation.util.DateUtil;

@Component
public class DailyReportTemplateBuilder {

    private final TemplateEngine templateEngine;

    private final String templateName = "DailyCompliance";

    @Autowired
    public DailyReportTemplateBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String buildTemplate(final Map<String, DailyReportModel> dailyReportModelMap, Calendar reportDate) {
        Context context = new Context();
        context.setVariable("values", dailyReportModelMap.values());
        context.setVariable("date", DateUtil.formatDate(reportDate));
        return templateEngine.process(templateName, context);
    }
}
