package org.tizzer.smmgr.system.model;

/**
 * @author tizzer
 * @version 1.0
 */
public class Preference {

    private boolean autoLogin;
    private boolean pendingAlarm;
    private boolean birthAlarm;

    public Preference() {
        super();
    }

    public boolean isAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;
    }

    public boolean isPendingAlarm() {
        return pendingAlarm;
    }

    public void setPendingAlarm(boolean pendingAlarm) {
        this.pendingAlarm = pendingAlarm;
    }

    public boolean isBirthAlarm() {
        return birthAlarm;
    }

    public void setBirthAlarm(boolean birthAlarm) {
        this.birthAlarm = birthAlarm;
    }

    @Override
    public String toString() {
        return "Preference{" +
                "autoLogin=" + autoLogin +
                ", pendingAlarm=" + pendingAlarm +
                ", birthAlarm=" + birthAlarm +
                '}';
    }

}
