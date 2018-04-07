package org.tizzer.smmgr.system.utils;

import org.tizzer.smmgr.system.common.LogLevel;
import org.tizzer.smmgr.system.common.Logcat;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * @author tizzer
 * @version 1.0
 */
public class StreamUtil {
    private static final Class clazz = StreamUtil.class;

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
                    e.printStackTrace();
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
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/constant/entity/" + fileName));
            String temp;
            while ((temp = reader.readLine()) != null) {
                result.append(temp).append("\n");
            }
        } catch (IOException e) {
            Logcat.type(clazz, "文件读取异常：" + e.getMessage(), LogLevel.ERROR);
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * 列表转换参数
     *
     * @param list
     * @return
     */
    public static String list2String(List list) {
        StringBuilder param = new StringBuilder();
        if (list != null) {
            for (Object element : list) {
                param.append(element);
            }
        }
        return param.toString();
    }

}
