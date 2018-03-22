package org.tizzer.smmgr.system.util;

import org.tizzer.smmgr.system.common.LogLevel;
import org.tizzer.smmgr.system.common.Logcat;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TextUtil {
    private static final Class clazz = TextUtil.class;

    /**
     * 关闭closeable接口
     *
     * @param closeables
     */
    public static void close(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    Logcat.type(clazz, "文件读取异常：" + e.getMessage(), LogLevel.ERROR);
                }
            }
        }
    }

    /**
     * 获取文本文件内容
     *
     * @param fileName
     * @return
     */
    public static String getFileContent(String fileName) {
        String result = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/constant/entity/" + fileName));
            String temp;
            while ((temp = reader.readLine()) != null) {
                result += temp + "\n";
            }
        } catch (IOException e) {
            Logcat.type(clazz, "文件读取异常：" + e.getMessage(), LogLevel.ERROR);
        }
        return result;
    }

    /**
     * 生成html文本<br/>
     * 默认字体为微软雅黑，默认颜色为白色<br/>
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

    /**
     * 列表转换参数
     *
     * @param list
     * @return
     */
    public static String list2String(List list) {
        String param = "";
        if (list != null) {
            for (Object element : list) {
                param += element;
            }
        }
        return param;
    }

    public static Date startOfDay(String date) {
        return new SimpleDateFormat("yyyy-MM-dd").parse(date, new ParsePosition(0));
    }

    public static Date endOfDay(String date) {
        return new SimpleDateFormat("yyyy-MM-dd").parse(date, new ParsePosition(0));
    }

}
