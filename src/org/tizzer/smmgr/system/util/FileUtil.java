package org.tizzer.smmgr.system.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author tizzer
 * @version 1.0
 */
public class FileUtil {

    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

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
            log.error("文件读取异常：" + e.getMessage());
        }
        return result;
    }


}
