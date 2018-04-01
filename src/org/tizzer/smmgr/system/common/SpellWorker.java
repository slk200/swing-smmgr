package org.tizzer.smmgr.system.common;

import java.io.UnsupportedEncodingException;

/**
 * @author tizzer
 * @version 1.0
 */
public class SpellWorker {

    private static final Class clazz = SpellWorker.class;

    private final static int[] zh_SecPosValue = {1601, 1637, 1833, 2078, 2274,
            2302, 2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858,
            4027, 4086, 4390, 4558, 4684, 4925, 5249, 5590};
    private final static String[] zh_FirstLetter = {"a", "b", "c", "d", "e",
            "f", "g", "h", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "w", "x", "y", "z"};

    /**
     * 取得给定汉字的首字母,即声母
     *
     * @param chinese
     * @return
     */
    private static String getFirstLetter(String chinese) {
        if (chinese == null || chinese.trim().length() == 0) {
            return "";
        }
        chinese = convertChinese(chinese, "GB2312", "ISO8859-1");
        // 判断是不是汉字
        if (chinese.length() > 1) {
            int zh_SectorCode = (int) chinese.charAt(0); // 汉字区码
            int zh_PositionCode = (int) chinese.charAt(1); // 汉字位码
            zh_SectorCode = zh_SectorCode - 160;
            zh_PositionCode = zh_PositionCode - 160;
            int li_SecPosCode = zh_SectorCode * 100 + zh_PositionCode; // 汉字区位码
            if (li_SecPosCode > 1600 && li_SecPosCode < 5590) {
                for (int i = 0; i < 23; i++) {
                    if (li_SecPosCode >= zh_SecPosValue[i]
                            && li_SecPosCode < zh_SecPosValue[i + 1]) {
                        chinese = zh_FirstLetter[i];
                        break;
                    }
                }
            } else {
                // 非汉字字符,如图形符号或ASCII码
                chinese = convertChinese(chinese, "ISO8859-1", "GB2312");
                chinese = chinese.substring(0, 1);
            }
        }
        return chinese.toLowerCase();
    }

    /**
     * 字符串编码转换
     *
     * @param chinese
     * @param oldCharset
     * @param newCharset
     * @return
     */
    private static String convertChinese(String chinese, String oldCharset, String newCharset) {
        try {
            chinese = new String(chinese.getBytes(oldCharset), newCharset);
        } catch (UnsupportedEncodingException ex) {
            Logcat.type(clazz, "字符串编码转换异常：" + ex.getMessage(), LogLevel.ERROR);
        }
        return chinese;
    }

    /**
     * 取得给定汉字串的首字母串
     *
     * @param chinese
     * @return
     */
    public static String getAllFirstLetter(String chinese) {
        if (chinese == null || chinese.trim().length() == 0) {
            return "";
        }
        String result = "";
        for (int i = 0; i < chinese.length(); i++) {
            result = result + getFirstLetter(chinese.substring(i, i + 1));
        }
        return result;
    }

}  