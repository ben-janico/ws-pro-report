package com.aurea.wsproreport;

import com.aurea.wsproreport.service.DailyReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.aurea.wsproreport.logger.WsLogger;
import com.aurea.wsproreport.service.WeeklyReportService;
import com.aurea.wsproreport.util.StringUtil;

    @SpringBootApplication
    @EnableConfigurationProperties
    public class App implements CommandLineRunner {

        @Autowired
        private DailyReportService dailyReportService;

    @Autowired
    private WeeklyReportService weeklyReportService;


    public static void main(String... args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String reportType = "";
        String manager = "";
        String date = "";
        boolean dryRun = false;

        if (args.length > 0) {
            for (String arg : args) {
                String[] params = arg.split("=");
                String key = params[0].trim();
                if (StringUtil.nvlOrEmpty(key, false)) {
                    String lowerKey = key.toLowerCase().trim();
                    switch (lowerKey) {
                        case "dailyreport":
                        case "weeklyreport":
                        case "cpureport":
                            reportType = lowerKey;
                            break;
                        case "manager":
                            manager = getParamValue(params).toLowerCase().trim();
                            break;
                        case "date":
                            date = getParamValue(params).trim();
                            break;
                        case "lastweek":
                        case "thisweek":
                            date = lowerKey;
                            break;
                        case "dryrun":
                            dryRun = true;
                            break;
                    }
                }
            }
        }
        if (StringUtil.nvlOrEmpty(reportType, true)) {
            System.out.println("Report Type must be valid");
            showUsage();
        }

        switch (reportType) {
            case "dailyreport":
                if (StringUtil.nvlOrEmpty(manager, true)) {
                    System.out.println("Manager Name must be valid");
                    showUsage();
                }
                dailyReportService.buildDailyReportAndSendMail(manager, date, dryRun);
                break;

            case "weeklyreport":
                date = StringUtil.nvlOrEmpty(date, "lastweek");
                weeklyReportService.buildWeeklyReportAndSendMail(manager, date, dryRun);
                break;
            default:
                WsLogger.error(getClass(), "Unknown Report Type!");
                showUsage();
        }
    }

    private static void showUsage() {
        System.out.println(" Usage:");
        System.out.println(" java -jar wscompliance-{version}.jar <dailyReport or weeklyReport or performancereport>");
        System.out.println(" optional parameters: ");
        System.out.println(" date={yyyy-mm-dd} : for dailyReport, given date generate report date.");
        System.out.println(" manager={managername} : for dailyReport, create report only given manager.");
        System.out.println(" for weekly report, lastWeek is default option. You can run thisWeek option.");
        System.out.println(" dryRun : no email, only creates html page to disk.");
        System.exit(1);
    }

    private static String getParamValue(String[] params) {
        if (params.length > 1) {
            String value = params[1];
            if (StringUtil.nvlOrEmpty(value, false)) {
                return value.trim();
            }
        }
        showUsage();
        return null;
    }
}
