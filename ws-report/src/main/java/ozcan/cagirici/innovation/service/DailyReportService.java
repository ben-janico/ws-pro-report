package ozcan.cagirici.innovation.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.services.sheets.v4.model.ValueRange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ozcan.cagirici.innovation.component.DailyReportTemplateBuilder;
import ozcan.cagirici.innovation.config.ComplianceSheetConfig;
import ozcan.cagirici.innovation.model.DailyReportModel;
import ozcan.cagirici.innovation.logger.WsLogger;
import ozcan.cagirici.innovation.util.DateUtil;
import ozcan.cagirici.innovation.util.FileUtil;
import ozcan.cagirici.innovation.util.StringUtil;

@Service
public class DailyReportService {

    @Autowired
    private SheetService sheetService;

    @Autowired
    private DailyReportTemplateBuilder dailyReportTemplateBuilder;

    @Autowired
    private ComplianceSheetConfig complianceSheetConfig;

    @Autowired
    private MailReciepentService mailReciepentService;

    @Autowired
    private MailSenderService mailSenderService;

    private Map<String, DailyReportModel> dailyReportModelMap;

    public void buildDailyReportAndSendMail(final String manager, final String date, final boolean dryRun) throws IOException {
        Calendar reportDate;
        if (StringUtil.nvlOrEmpty(date, true)) {
            reportDate = DateUtil.getLastWorkDay();
        } else {
            reportDate = DateUtil.getCalendar(date);
        }
        dailyReportModelMap = readDailySheet(manager, reportDate);

        dailyReportModelMap = filterActiveICsMap(dailyReportModelMap);

        String messageBody = dailyReportTemplateBuilder.buildTemplate(dailyReportModelMap, reportDate);
        if (dryRun) {
            FileUtil.saveToFile("dailyReport", messageBody, reportDate);
        } else {
            sendEmail(messageBody, reportDate);
        }

    }

    private Map<String, DailyReportModel> readDailySheet(final String manager, final Calendar reportDate) {
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
                Map<String, DailyReportModel> values = new HashMap<>();
                rvalues.forEach(row -> {
                    if (row.size() > 3) {
                        String rowManager = StringUtil.nvlOrEmpty(row.get(2));
                        String rowDate = StringUtil.nvlOrEmpty(row.get(3));
                        String icName = StringUtil.nvlOrEmpty(row.get(1));
                        if (StringUtil.nvlOrEmpty(icName, false)) {
                            boolean checkManager;
                            //empty means all manager
                            if (StringUtil.nvlOrEmpty(manager, true)) {
                                checkManager = true;
                            } else {
                                checkManager = rowManager.toLowerCase().trim().equals(manager.toLowerCase().trim());
                            }

                            if (checkManager && DateUtil.equalsToString(reportDate, rowDate)) {
                                DailyReportModel model = new DailyReportModel();
                                model.setIcName(icName);
                                model.setSevenHrsPerDay(StringUtil.nvlOrEmpty(row.get(4)));
                                model.setDeepWorkBlocks(StringUtil.nvlOrEmpty(row.get(5)));
                                model.setDevTime(StringUtil.nvlOrEmpty(row.get(6)));
                                model.setDailyCic(StringUtil.nvlOrEmpty(row.get(7)));
                                model.setIntensityFocus(StringUtil.nvlOrEmpty(row.get(8)));
                                model.setFocusScore(StringUtil.nvlOrEmpty(row.get(11), 0));
                                model.setIntensityScore(StringUtil.nvlOrEmpty(row.get(12), 0));
                                model.setDevTimePercentage(StringUtil.nvlOrEmpty(row.get(14), 0));
                                values.put(model.getIcName(), model);
                            }
                        }
                    }
                });
                return values;
            }
        } catch (IOException | GeneralSecurityException e) {
            WsLogger.error(getClass(), e.getMessage());
            return new HashMap<>();
        }
    }

    private Map<String, DailyReportModel> filterActiveICsMap(Map<String, DailyReportModel> reportModelMap) {
        Map<String, String> activeICsEmailMap = mailReciepentService.readActiveICsEmailReturnsMap();

        Map<String, DailyReportModel> filteredMap = new HashMap<>();

        reportModelMap.values().forEach(dailyModel -> {
            if (activeICsEmailMap.containsKey(dailyModel.getIcName())) {
                String mail = activeICsEmailMap.get(dailyModel.getIcName());
                dailyModel.setMail(StringUtil.nvlOrEmpty(mail));
                filteredMap.put(dailyModel.getIcName(), dailyModel);
            }
        });

        return filteredMap;
    }

    private void sendEmail(String messageBody, Calendar reportDate) throws IOException {
        mailSenderService.setSubject(String.format("Ws Pro Compliance Report of %s", DateUtil.formatDate(reportDate)));
        mailSenderService.setBody(messageBody);

        List<String> toList = new ArrayList<>(dailyReportModelMap.size());
        dailyReportModelMap.values().forEach(row -> {
            if (StringUtil.nvlOrEmpty(row.getMail(), false))
                toList.add(row.getMail());
        });

        mailSenderService.setRecipientList(toList);
        mailSenderService.sendEmail();
    }
}
