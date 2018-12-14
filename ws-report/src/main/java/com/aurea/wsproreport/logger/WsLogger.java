package com.aurea.wsproreport.logger;

import org.slf4j.LoggerFactory;

public class WsLogger {

    public static void error(Class clazz, String message) {
        LoggerFactory.getLogger(clazz).error(message);
    }
}
