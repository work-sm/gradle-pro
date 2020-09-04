package com.sam.demo.perform.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class EpochTimeUtils {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public static void main(String[] args) {
        String lineOne = "1 40908U 15049K   20210.22689602  .00000012  00000-0  42839-5 0  9994";
        Date parse = parse(lineOne);
        System.out.println(sdf.format(parse));

        System.out.println(getTime("1596315121233"));
    }

    public static long getTime(String time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(time));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime();
    }

    public static Date parse(String lineOne){
        Calendar calendar = Calendar.getInstance();
        calendar.set(2000, 0, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if(lineOne == null || lineOne.length()!= 69){
            throw new IllegalArgumentException("illegal lineOne "+ lineOne);
        }
        char[] chars = lineOne.toCharArray();
        char[] years = Arrays.copyOfRange(chars, 18, 20);
        char[] days = Arrays.copyOfRange(chars, 20, 23);
        char[] times = Arrays.copyOfRange(chars, 23, 32);
        calendar.add(Calendar.YEAR, parseInt(years));
        calendar.add(Calendar.DATE, parseInt(days));
        calendar.add(Calendar.DATE, -1);
        double dayTime = parseDouble(times);
        dayTime *= 24;
        calendar.set(Calendar.HOUR_OF_DAY, (int)dayTime);
        dayTime = (dayTime%1)* 60;
        calendar.set(Calendar.MINUTE, (int)dayTime);
        dayTime = (dayTime%1)* 60;
        calendar.set(Calendar.SECOND, (int)dayTime);
        dayTime = (dayTime%1)* 1000;
        System.out.println((int)dayTime);
        calendar.set(Calendar.MILLISECOND, (int)dayTime);

        return calendar.getTime();
    }

    private static int parseInt(char[] chars){
        if(chars == null || chars.length == 0){
            throw new IllegalArgumentException("illegal chars "+ Arrays.toString(chars));
        }
        String s = String.valueOf(chars);
        return Integer.parseInt(s);
    }

    private static double parseDouble(char[] chars){
        if(chars == null || chars.length == 0){
            throw new IllegalArgumentException("illegal chars "+ Arrays.toString(chars));
        }
        String s = String.valueOf(chars);
        return Double.parseDouble(s);
    }

}
