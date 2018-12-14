package com.aurea.wsproreport.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.aurea.wsproreport.component.WeeklyReportTemplateBuilder;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aurea.wsproreport.config.ComplianceSheetConfig;
import com.aurea.wsproreport.logger.WsLogger;
import com.aurea.wsproreport.model.PerformanceProductivityModel;
import com.aurea.wsproreport.model.WeeklyComplianceModel;
import com.aurea.wsproreport.util.DateUtil;
import com.aurea.wsproreport.util.FileUtil;
import com.aurea.wsproreport.util.StringUtil;

@Service
public class WeeklyReportService {

    @Autowired
    private SheetService sheetService;

    @Autowired
    private PerformanceReportService performanceReportService;

    @Autowired
    private WeeklyReportTemplateBuilder weeklyReportTemplateBuilder;

    @Autowired
    private ComplianceSheetConfig complianceSheetConfig;

    @Autowired
    private MailReciepentService mailReciepentService;

    @Autowired
    private MailSenderService mailSenderService;

    private Map<String, WeeklyComplianceModel> weeklyReportModelMap;

    private List<PerformanceProductivityModel> performanceProductivityModelList;

    public void buildWeeklyReportAndSendMail(final String manager, final String date, final boolean dryRun) throws IOException {
        Calendar reportStartDate;
        if (date.equals("lastweek")) {
            reportStartDate = DateUtil.getStartWorkDayOfLastWeek();
        } else if (date.equals("thisweek")) {
            reportStartDate = DateUtil.getStartWorkDayOfThisWeek();
        } else {
            reportStartDate = DateUtil.getCalendar(date);
        }

        weeklyReportModelMap = readWeeklySheet(manager, reportStartDate);

        weeklyReportModelMap = filterActiveICsMap(weeklyReportModelMap);

        performanceProductivityModelList = performanceReportService.buildPerformanceReportAndSendMail();

        String messageBody = weeklyReportTemplateBuilder.buildTemplate(weeklyReportModelMap, performanceProductivityModelList);
        if (dryRun) {
            FileUtil.saveToFile("weeklyReport", messageBody, reportStartDate);
        } else {
            sendEmail(messageBody, reportStartDate);
        }

    }

    private Map<String, WeeklyComplianceModel> readWeeklySheet(final String manager, final Calendar startDate) {
        try {
            ValueRange response = sheetService.getGoogleSheetService()
                    .spreadsheets().values()
                    .get(complianceSheetConfig.getSource(),
                            complianceSheetConfig.getDataRange())
                    .execute();

            List<List<Object>> rvalues = response.getValues();
            if (rvalues == null || rvalues.isEmpty()) {
                return new HashMap<>();
            } else {
                Map<String, WeeklyComplianceModel> map = new HashMap<>();
                Calendar endDate = (Calendar) startDate.clone();
                endDate.add(Calendar.DATE, 6);
                rvalues.forEach(row -> {
                    String rowManager = StringUtil.nvlOrEmpty(row.get(2));
                    String rowDate = StringUtil.nvlOrEmpty(row.get(3));
                    Calendar testDate = DateUtil.getCalendar(rowDate);
                    //empty means all manager
                    boolean checkManager;
                    if (StringUtil.nvlOrEmpty(manager, true)) {
                        checkManager = true;
                    } else {
                        checkManager = rowManager.equals(manager.toLowerCase().trim());
                    }
                    if (checkManager && DateUtil.betweenOrEqualAny(testDate, startDate, endDate)) {
                        String icName = StringUtil.nvlOrEmpty(nvlOrDefaultString(row, 1));
                        if (StringUtil.nvlOrEmpty(icName, false)) {
                            WeeklyComplianceModel model;
                            if (map.containsKey(icName)) {
                                model = map.get(icName);
                            } else {
                                model = new WeeklyComplianceModel();
                                model.setIcName(icName);
                            }
                            model.addSevenHrsPerDay(nvlOrDefaultInteger(row, 34));
                            model.addDeepWorkBlocks(nvlOrDefaultInteger(row, 35));
                            model.addDevTime(nvlOrDefaultInteger(row, 36));
                            model.addDailyCic(nvlOrDefaultInteger(row, 37));
                            model.addIntensityFocus(nvlOrDefaultInteger(row, 39));
                            map.put(icName, model);
                        }
                    }
                });
                return map;
            }
        } catch (IOException | GeneralSecurityException e) {
            WsLogger.error(getClass(), e.getMessage());
            return new HashMap<>();
        }
    }

    private Map<String, WeeklyComplianceModel> filterActiveICsMap(Map<String, WeeklyComplianceModel> reportModelMap) {
        Map<String, String> activeICsEmailMap = mailReciepentService.readActiveICsEmailReturnsMap();

        Map<String, WeeklyComplianceModel> filteredMap = new HashMap<>();

        reportModelMap.values().forEach(weekModel -> {
            if (activeICsEmailMap.containsKey(weekModel.getIcName())) {
                String mail = activeICsEmailMap.get(weekModel.getIcName());
                weekModel.setMail(StringUtil.nvlOrEmpty(mail));
                filteredMap.put(weekModel.getIcName(), weekModel);
            }
        });

        return filteredMap;
    }

    private void sendEmail(String messageBody, Calendar reportStartDate) throws IOException {
        mailSenderService.setSubject(String.format("Ws Pro Compliance Weekly Report of %s", DateUtil.formatDate(reportStartDate)));
        mailSenderService.setBody(messageBody);

        List<String> toList = new ArrayList<>(weeklyReportModelMap.size());
        weeklyReportModelMap.values().forEach(row -> {
            if (StringUtil.nvlOrEmpty(row.getMail(), false))
                toList.add(row.getMail());
        });

        mailSenderService.setRecipientList(toList);
        mailSenderService.sendEmail();
    }

    private Integer nvlOrDefaultInteger(List<Object> row, int index) {
        if (row.size() > index) {
            return StringUtil.nvlOrEmpty(row.get(index), -1);
        }
        return 0;

    }

    private String nvlOrDefaultString(List<Object> row, int index) {
        if (row.size() > index) {
            return StringUtil.nvlOrEmpty(row.get(index), "");
        }
        return "";
    }
}
