package com.aurea.wsproreport.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aurea.wsproreport.config.GradebookSheetConfig;
import com.aurea.wsproreport.logger.WsLogger;
import com.aurea.wsproreport.model.PerformanceProductivityModel;
import com.aurea.wsproreport.util.DateUtil;
import com.aurea.wsproreport.util.StringUtil;

@Service
public class PerformanceReportService {

    @Autowired
    private SheetService sheetService;

    @Autowired
    private GradebookSheetConfig gradebookSheetConfig;

    private Map<String, PerformanceProductivityModel> performanceReportModelMap;

    public List<PerformanceProductivityModel> buildPerformanceReportAndSendMail() throws IOException {
        performanceReportModelMap = readPerformanceSheet();

        List<PerformanceProductivityModel> sortedList = sortByStartDateAndScoreDesc(performanceReportModelMap);

        return sortedList;
    }

    private Map<String, PerformanceProductivityModel> readPerformanceSheet() {
        try {
            ValueRange response = sheetService.getGoogleSheetService()
                    .spreadsheets().values()
                    .get(gradebookSheetConfig.getSource(),
                            gradebookSheetConfig.getDataRange())
                    .execute();

            List<List<Object>> rvalues = response.getValues();
            if (rvalues == null || rvalues.isEmpty()) {
                return new HashMap<>();
            } else {
                Map<String, PerformanceProductivityModel> map = new HashMap<>();
                rvalues.forEach(row -> {
                    String icName = nvlOrDefaultString(row, 0);
                    String week2Team = nvlOrDefaultString(row, 8);
                    if (StringUtil.nvlOrEmpty(week2Team, false)) {
                        if (StringUtil.nvlOrEmpty(icName, false)) {
                            PerformanceProductivityModel model = new PerformanceProductivityModel();
                            model.setIcName(icName);
                            model.setRole(nvlOrDefaultString(row, 1));
                            model.setTech(nvlOrDefaultString(row, 2));
                            model.setBootStart(nvlOrDefaultString(row, 3));
                            model.setCurrentTeam(nvlOrDefaultString(row, 4));
                            model.setSem(nvlOrDefaultString(row, 5));
                            model.setWeek1team(nvlOrDefaultString(row, 6));
                            model.setWeek1Score(nvlOrDefaultInteger(row, 7));
                            model.setWeek2team(nvlOrDefaultString(row, 8));
                            model.setWeek2Score(nvlOrDefaultInteger(row, 9));
                            model.setWeek3team(nvlOrDefaultString(row, 10));
                            model.setWeek3Score(nvlOrDefaultInteger(row, 11));
                            model.setWeek4team(nvlOrDefaultString(row, 12));
                            model.setWeek4Score(nvlOrDefaultInteger(row, 13));
                            int count = 0;
                            int sum = 0;
                            if (model.getWeek1Score() >= 0) {
                                sum += model.getWeek1Score();
                                count++;
                            }
                            if (model.getWeek2Score() >= 0) {
                                sum += model.getWeek2Score();
                                count++;
                            }
                            if (model.getWeek3Score() >= 0) {
                                sum += model.getWeek3Score();
                                count++;
                            }
                            if (model.getWeek4Score() >= 0) {
                                sum += model.getWeek4Score();
                                count++;
                            }
                            model.setAvgScore((new Double(sum) / count) / 5);
                            if (model.getWeek4team().isEmpty()) {
                                if (model.getWeek3team().isEmpty()) {
                                    if (model.getWeek2team().isEmpty()) {
                                        model.setWhichWeek("week1");
                                        model.setCompletedWeek("new");
                                    } else {
                                        model.setWhichWeek("week2");
                                        model.setCompletedWeek("1st Week");
                                    }
                                } else {
                                    model.setWhichWeek("week3");
                                    model.setCompletedWeek("2nd Week");
                                }
                            } else {
                                model.setWhichWeek("week4");
                                model.setCompletedWeek("3rd Week");
                            }
                            map.put(model.getIcName(), model);
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

    private List<PerformanceProductivityModel> sortByStartDateAndScoreDesc(Map<String, PerformanceProductivityModel> reportModelMap) {
        Collection<PerformanceProductivityModel> collection = reportModelMap.values();
        List<PerformanceProductivityModel> values = new LinkedList<>();
        values.addAll(collection);

        values.sort(new Comparator<PerformanceProductivityModel>() {
            @Override public int compare(PerformanceProductivityModel o1, PerformanceProductivityModel o2) {
                Calendar cal1 = DateUtil.getCalendar(o1.getBootStart(), "dd/MM/yy");
                Calendar cal2 = DateUtil.getCalendar(o2.getBootStart(), "dd/MM/yy");
                if (cal1.before(cal2)) {
                    return -1;
                }
                if (cal1.equals(cal2)) {
                    int sum1 = o1.getWeek1Score() + o1.getWeek2Score() + o1.getWeek3Score() + o1.getWeek4Score();
                    int sum2 = o2.getWeek1Score() + o2.getWeek2Score() + o2.getWeek3Score() + o2.getWeek4Score();
                    return sum2 - sum1;
                }
                return 0;
            }
        });
        return values;
    }

    private Integer nvlOrDefaultInteger(List<Object> row, int index) {
        if (row.size() > index) {
            return StringUtil.nvlOrEmpty(row.get(index), 0);
        }
        return -1;

    }

    private String nvlOrDefaultString(List<Object> row, int index) {
        if (row.size() > index) {
            return StringUtil.nvlOrEmpty(row.get(index), "");
        }
        return "";
    }
}
