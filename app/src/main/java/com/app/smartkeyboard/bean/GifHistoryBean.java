package com.app.smartkeyboard.bean;

import org.litepal.crud.LitePalSupport;

/**
 * Gif历史记录bean
 */
public class GifHistoryBean extends LitePalSupport {

    //id
    private int _id;

    //保存的时间戳 long
    private long saveTime;

    //yyyy-MM-dd HH:mm:ss格式
    private String saveTimeStr;

    //保存在SD卡中的地址,当图片的地址是“+”时，表示为+号
    private String fileUrl;

    //速度
    private int gifSpeed;

    //一代还是二代，一代是1代是2
    private int deviceType;

    //是否是GIF
    private boolean isGifType;

    //是否是默认动画
    private boolean isDefaultAni;


    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public long getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }

    public String getSaveTimeStr() {
        return saveTimeStr;
    }

    public void setSaveTimeStr(String saveTimeStr) {
        this.saveTimeStr = saveTimeStr;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public int getGifSpeed() {
        return gifSpeed;
    }

    public void setGifSpeed(int gifSpeed) {
        this.gifSpeed = gifSpeed;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public boolean isDefaultAni() {
        return isDefaultAni;
    }

    public void setDefaultAni(boolean defaultAni) {
        isDefaultAni = defaultAni;
    }

    public boolean isGifType() {
        return isGifType;
    }

    public void setGifType(boolean gifType) {
        isGifType = gifType;
    }
}
