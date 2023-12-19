package com.app.smartkeyboard.utils;

import android.content.Context;

import com.app.smartkeyboard.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.SimpleTimeZone;

public class TimeUtils {


    /**
     * 获取时间
     */
    public static String getTimeByNow(Context context){
        Calendar calendar =Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if(hour>=5 && hour<12){
            return context.getResources().getString(R.string.string_morning_good);
        }

        if(hour>=12 && hour<=18){
            return context.getResources().getString(R.string.string_afternoon_good);
        }

        return context.getResources().getString(R.string.string_evening_good);
    }


    public static long formatDateToLong(String timeStr,String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.CHINA);
        try {
            return simpleDateFormat.parse(timeStr).getTime()/1000;
        } catch (ParseException e) {
           return System.currentTimeMillis();
        }
    }
}
