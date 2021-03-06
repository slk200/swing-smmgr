package org.tizzer.smmgr.system.utils;

import com.alee.extended.painter.NinePatchStatePainter;
import com.alee.utils.NinePatchUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tizzer
 * @version 1.0
 */
public class D9Util {

    private static Map<String, NinePatchStatePainter> ninePatchStatePainterCache = new HashMap<>();

    /**
     * 点9状态图
     *
     * @param name
     * @return
     */
    public static NinePatchStatePainter getNinePatchPainter(String name) {
        if (!ninePatchStatePainterCache.containsKey(name)) {
            ninePatchStatePainterCache.put(name, NinePatchUtils.loadNinePatchStatePainter(D9Util.class.getResource("/org/tizzer/smmgr/style/" + name)));
        }
        return ninePatchStatePainterCache.get(name);
    }

}
