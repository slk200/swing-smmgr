package org.tizzer.smmgr.system.util;

import java.util.List;

/**
 * @author tizzer
 * @version 1.0
 */
public class ParamUtil {
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
}
