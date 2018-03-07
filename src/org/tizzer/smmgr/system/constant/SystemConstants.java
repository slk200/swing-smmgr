package org.tizzer.smmgr.system.constant;

import java.awt.*;
import java.text.SimpleDateFormat;

/**
 * @author tizzer
 * @version 1.0
 */
public interface SystemConstants {
    /**
     * 出厂日期
     */
    String _DATE_OF_MANUFACTURE = "2018-01-05";

    /**
     * 默认窗口大小
     */
    Dimension _DEFAULT_SIZE = new Dimension(1280, 800);

    /**
     * 起始时间格式
     */
    SimpleDateFormat _START_TIME_FORM = new SimpleDateFormat("yyyy-MM-dd 00:00:00");

    /**
     * 默认时间格式
     */
    SimpleDateFormat _DEFAULT_TIME_FORM = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 默认日期格式
     */
    SimpleDateFormat _DEFAULT_DATE_FORM = new SimpleDateFormat("yyyy-MM-dd");
}
