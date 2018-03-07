package org.tizzer.smmgr.system.util;

/**
 * @author tizzer
 * @version 1.0
 */
public class HTMLUtil {
    /**
     * 生成html文本<br/>
     * 如果param的长度为1，则param作为文本<br/>
     * 如果param的长度为2，则param依次作为颜色、文本<br/>
     * 如果param的长度为3，则param依次作为颜色、字体、文本
     *
     * @param param
     * @return
     */
    public static String text2HTML(String... param) {
        switch (param.length) {
            case 3:
                return "<html><font color=" + param[0] + " face='" + param[1] + "'>" + param[2] + "</font></html>";
            case 2:
                return "<html><font color=" + param[0] + " face='Microsoft YaHei'>" + param[1] + "</font></html>";
            default:
                return "<html><font color=white face='Microsoft YaHei'>" + param[0] + "</font></html>";
        }
    }
}
