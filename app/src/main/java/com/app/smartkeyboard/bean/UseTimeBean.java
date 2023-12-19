package com.app.smartkeyboard.bean;

/**
 * 键盘使用时间的bean
 */
public class UseTimeBean {

    /**时间，小时**/
    private int hour;

    /**使用时长 分钟**/
    private int useTime;

    /**是否选中**/
    private boolean isChecked;

    public UseTimeBean() {
    }

    public UseTimeBean(int hour, int useTime) {
        this.hour = hour;
        this.useTime = useTime;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getUseTime() {
        return useTime;
    }

    public void setUseTime(int useTime) {
        this.useTime = useTime;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
