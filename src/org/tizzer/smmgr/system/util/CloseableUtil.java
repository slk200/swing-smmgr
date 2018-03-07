package org.tizzer.smmgr.system.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author tizzer
 * @version 1.0
 */
public class CloseableUtil {

    /**
     * 关闭closeable接口
     *
     * @param closeable
     */
    public static void close(Closeable... closeable) {
        for (Closeable element : closeable) {
            if (element != null) {
                try {
                    element.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
