package org.tizzer.smmgr.system.template;

/**
 * @author tizzer
 * @version 1.0
 */
public interface LogListener {

    /**
     * 消息
     *
     * @param tag
     * @param message
     */
    void info(String tag, String message);

    /**
     * 警告
     *
     * @param tag
     * @param message
     */
    void warn(String tag, String message);

    /**
     * 错误
     *
     * @param tag
     * @param message
     */
    void error(String tag, String message);

}
