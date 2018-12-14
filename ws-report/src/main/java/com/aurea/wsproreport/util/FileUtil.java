package com.aurea.wsproreport.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import com.aurea.wsproreport.logger.WsLogger;

public class FileUtil {

    private FileUtil() {
        throw new UnsupportedOperationException("This class can not be instantiated!");
    }

    public static void saveToFile(String reportName, String content, Calendar reportDate) {
        try (OutputStream os = new FileOutputStream(new File(reportName +"-"+ DateUtil.formatDate(reportDate) + ".html"))) {
            os.write(content.getBytes());
            os.close();
        } catch (IOException e) {
            WsLogger.error(FileUtil.class, e.getMessage());
        }
    }
}
