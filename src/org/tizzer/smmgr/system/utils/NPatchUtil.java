package org.tizzer.smmgr.system.utils;

import com.alee.extended.painter.NinePatchStatePainter;
import com.alee.utils.NinePatchUtils;
import com.alee.utils.ninepatch.NinePatchIcon;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tizzer
 * @version 1.0
 */
public class NPatchUtil {

    private static Map<String, NinePatchIcon> ninePatchIconCache = new HashMap<>();
    private static Map<String, NinePatchStatePainter> ninePatchStatePainterCache = new HashMap<>();

    /**
     * 点9图
     *
     * @param paintConfig
     * @return
     */
    public static NinePatchIcon getNinePatchIcon(String paintConfig) {
        if (!ninePatchIconCache.containsKey(paintConfig)) {
            ninePatchIconCache.put(paintConfig, NinePatchUtils.loadNinePatchIcon(
                    NPatchUtil.class.getResource("/org/tizzer/smmgr/config/npicon/" + paintConfig + ".xml")));
        }
        return ninePatchIconCache.get(paintConfig);
    }

    /**
     * 点9状态图
     *
     * @param name
     * @return
     */
    public static NinePatchStatePainter getNinePatchPainter(String name) {
        if (!ninePatchStatePainterCache.containsKey(name)) {
            ninePatchStatePainterCache.put(name, NinePatchUtils.loadNinePatchStatePainter(NPatchUtil.class.getResource("/org/tizzer/smmgr/config/nppainter/" + name)));
        }
        return ninePatchStatePainterCache.get(name);
    }

}
