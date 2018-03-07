package org.tizzer.smmgr.system.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author tizzer
 * @version 1.0
 */
public class TimeUtil {
    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }
}
