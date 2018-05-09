package org.tizzer.smmgr.system.utils;

import org.tizzer.smmgr.system.common.Logcat;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author tizzer
 * @version 1.0
 */
public class IOUtil {
    private static final Class clazz = IOUtil.class;

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
                    Logcat.type(clazz, "文件读取异常：" + e.getMessage(), Logcat.LogLevel.ERROR);
                    e.printStackTrace();
                }
            }
        }
    }

}
