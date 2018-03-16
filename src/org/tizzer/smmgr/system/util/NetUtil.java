package org.tizzer.smmgr.system.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author tizzer
 * @version 1.0
 */
public class NetUtil {

    private static Logger log = LoggerFactory.getLogger(NetUtil.class);

    /**
     * 网络是否畅通
     *
     * @param ip
     * @return
     */
    public static boolean isNetworkUnimpeded(String ip) {
        Runtime runtime = Runtime.getRuntime();
        try {
            log.info("正在测试网络连通性 ip：" + ip);
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
                    log.info("网络正常，时间：" + TimeUtil.getCurrentTime());
                    return true;
                } else {
                    log.warn("网络断开，时间：" + TimeUtil.getCurrentTime());
                    return false;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("网络异常，时间：" + TimeUtil.getCurrentTime() + "，异常：" + e.getMessage());
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
            log.error("获取定位异常：" + e.getMessage());
        }
    }

}