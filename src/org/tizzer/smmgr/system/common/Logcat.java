package org.tizzer.smmgr.system.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tizzer
 * @version 1.0
 */
public class Logcat {
    /**
     * 日志对象缓存
     */
    private static Map<String, Logger> logCache = new HashMap<>();

    /**
     * 打印一个日志
     * 日志级别可以为：TRACE，DEBUG，INFO，WARN，ERROR
     *
     * @param clazz
     * @param message
     * @param logLevel
     * @see LogLevel
     */
    public static void type(Class clazz, String message, LogLevel logLevel) {
        if (!logCache.containsKey(clazz.getCanonicalName())) {
            Logger logger = LoggerFactory.getLogger(clazz);
            logCache.put(clazz.getCanonicalName(), logger);
        }
        switch (logLevel) {
            case TRACE:
                logCache.get(clazz.getCanonicalName()).trace(message);
                break;
            case DEBUG:
                logCache.get(clazz.getCanonicalName()).debug(message);
                break;
            case INFO:
                logCache.get(clazz.getCanonicalName()).info(message);
                break;
            case WARN:
                logCache.get(clazz.getCanonicalName()).warn(message);
                break;
            case ERROR:
                logCache.get(clazz.getCanonicalName()).error(message);
                break;
            default:
        }

    }

}
