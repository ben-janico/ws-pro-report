package com.aurea.wsproreport.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aurea.wsproreport.config.ComplianceSheetConfig;
import com.aurea.wsproreport.logger.WsLogger;

@Service
public class MailReciepentService {

    @Autowired
    private SheetService sheetService;

    @Autowired
    private ComplianceSheetConfig complianceSheetConfig;

    public Map<String, String> readActiveICsEmailReturnsMap() {
        try {
            ValueRange response = sheetService.getGoogleSheetService()
                    .spreadsheets().values()
                    .get(complianceSheetConfig.getSource(),
                            complianceSheetConfig.getMasterRange())
                    .execute();
            List<List<Object>> rvalues = response.getValues();
            Map<String, String> map = new HashMap<>(rvalues.size());
            rvalues.forEach(row -> {
                if (row != null && row.get(0) != null && !"".equals(row.get(0))) {
                    String email = row.size() > 1 ? row.get(1).toString() : "";
                    String status = row.size() > 2 ? row.get(2).toString() : "";
                    status = status.toLowerCase();
                    if (status != null && !"".equals(status) && !status.isEmpty()) {
                        map.put(row.get(0).toString(), email);
                    }
                }
            });
            return map;
        } catch (IOException | GeneralSecurityException e) {
            WsLogger.error(getClass(), e.getMessage());
            return new HashMap<>();
        }
    }
}
