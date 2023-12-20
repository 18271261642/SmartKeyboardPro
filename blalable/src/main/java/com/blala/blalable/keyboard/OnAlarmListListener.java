package com.blala.blalable.keyboard;

import com.blala.blalable.blebean.AlarmBean;

import java.util.List;

/**
 * 所有闹钟回调
 */
public interface OnAlarmListListener {

    void backAlarmList(List<AlarmBean> list);
}
