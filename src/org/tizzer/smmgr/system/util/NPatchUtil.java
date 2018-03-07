package org.tizzer.smmgr.system.util;

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

    private static Map<Integer, NinePatchIcon> ninePatchIconCache = new HashMap<>();
    private static Map<String, NinePatchStatePainter> ninePatchStatePainterCache = new HashMap<>();

    /**
     * 点9图
     *
     * @param index
     * @return
     */
    public static NinePatchIcon getNinePatchIcon(int index) {
        if (!ninePatchIconCache.containsKey(index)) {
            ninePatchIconCache.put(index, NinePatchUtils.loadNinePatchIcon(
                    NPatchUtil.class.getResource("/org/tizzer/smmgr/config/npicon/label" + index + ".xml")));
        }
        return ninePatchIconCache.get(index);
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
