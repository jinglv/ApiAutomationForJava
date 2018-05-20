package com.api.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 获取当前时间格式化输出
 */
public class DateUtils {
    public static String getCurrentTime(){
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd_HH_mm_ss" );  //文件名称中是不支持:
        String dateString = format.format( date );
        return dateString;
    }
}
