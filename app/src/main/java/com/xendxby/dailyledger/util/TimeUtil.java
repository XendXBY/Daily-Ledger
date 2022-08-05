package com.xendxby.dailyledger.util;

import android.annotation.SuppressLint;
import android.util.Log;

import com.xendxby.dailyledger.entity.MyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.SimpleFormatter;

public class TimeUtil {

    private static final String FORMAT_STRING = "yyyy-MM-dd hh:mm:ss";

    public static String getCurrentTime() {
        Date date = new Date();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_STRING);
        return dateFormat.format(date);
    }

    public static MyTime stringToDate(String s) {
        return new MyTime(
                Integer.parseInt(s.substring(0, 4)),
                Integer.parseInt(s.substring(5, 7)),
                Integer.parseInt(s.substring(8, 10)),
                Integer.parseInt(s.substring(11, 13)),
                Integer.parseInt(s.substring(14, 16)),
                Integer.parseInt(s.substring(17, 19)));
    }

}
