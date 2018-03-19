package org.tizzer.smmgr.system.common;

import org.apache.commons.lang.StringUtils;
import org.tizzer.smmgr.system.util.TextUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author tizzer
 * @version 1.0
 */
public class InternetWorker {

    private static final Class clazz = InternetWorker.class;

    /**
     * 网络是否畅通
     *
     * @param ip
     * @return
     */
    public static boolean isNetworkUnimpeded(String ip) {
        Runtime runtime = Runtime.getRuntime();
        try {
            Logcat.type(clazz, "正在测试网络连通性 ip：" + ip, LogLevel.INFO);
            Process process = runtime.exec("ping " + ip);
            InputStream iStream = process.getInputStream();
            InputStreamReader iSReader = new InputStreamReader(iStream, "UTF-8");
            BufferedReader bReader = new BufferedReader(iSReader);
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = bReader.readLine()) != null) {
                sb.append(line);
            }
            iStream.close();
            iSReader.close();
            bReader.close();
            String result = new String(sb.toString().getBytes("UTF-8"));
            if (!StringUtils.isBlank(result)) {
                if (result.indexOf("TTL") > 0 || result.indexOf("ttl") > 0) {
                    Logcat.type(clazz, "网络正常，时间：" + TextUtil.getCurrentTime(), LogLevel.INFO);
                    return true;
                } else {
                    Logcat.type(clazz, "网络断开，时间：" + TextUtil.getCurrentTime(), LogLevel.WARN);
                    return false;
                }
            }
            return false;
        } catch (Exception e) {
            Logcat.type(clazz, "网络异常，时间：" + TextUtil.getCurrentTime() + "，异常：" + e.getMessage(), LogLevel.ERROR);
            return false;
        }
    }

    /**
     * 获取定位
     */
    public static void getLocation() {
        String cmd = "cmd /c start iexplore http://localhost:8080/smmgr/location";
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            Logcat.type(clazz, "获取定位异常：" + e.getMessage(), LogLevel.ERROR);
        }
    }

}