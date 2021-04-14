package com.hhgs.shows.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil extends  Date{



    public static Date getDateTime (String date){
        try{
            SimpleDateFormat sdf = null;
            if(date.contains("/")) {
                sdf=new SimpleDateFormat("yyyy/M/d HH:mm");
            }else if(date.contains("-")) {
                sdf=new SimpleDateFormat("yyyy-M-d HH:mm");
            }
            Date date1=sdf.parse(date);
            return date1;
        }catch (Exception e){
            e.printStackTrace();
            return new Date();
        }


    }

    public static Date stringToDate (String date){
        try{
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1= sdf.parse(date);
            return date1;
        }catch (Exception e){
            e.printStackTrace();
            return new Date();
        }


    }


    public static Date getDateTimeFromInfluxdb(String date) {
        //String[] strings1 = date.split("T");
        //String[] strings2 = strings1[1].split("\\+");
        //String finalStringDate = strings1[0]+" "+strings2[0];
        try {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date1=sdf.parse(date);
            return date1;
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static long getTime (String date){
        try{
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1=sdf.parse(date);
            return date1.getTime();
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }


    }

    public static long getCurrentTime() {
        long currentTimeMillis = System.currentTimeMillis();
        return currentTimeMillis;
    }

    public static Date getCurrentDate() {
        return new Date();
    }


    public static String getSdfTime (long date){
        try{

            //SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(date);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }


    }

    public static String getFormatTime (String date){
        try{

            //SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(date);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getFormatTime (Date date){
        try{

            //SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = sdf.format(date);
            String result = format.split(" ")[0];
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }


    }

    /**
     * 获取前一天的日期
     * @return
     */
    public static String getBeforeOneDayTime() {
        Date dnow = new Date(); //当前时间
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(dnow);//把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -1);//设置为前一天
        Date dbefore = calendar.getTime();//得到前一天的时间
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
        String defaultBeforeDate = sdf.format(dbefore);//格式化前一天
        return defaultBeforeDate;
    }

    public static void main(String[] args) {
        String date="2019-07-13 17:10:00";
        String date1="2019-11-10T15:31:16.469+08:00";



        /*System.out.println(getTime(date));
        String date1="2019-07-13 17:15:00";
        System.out.println(getTime(date1));


        long start=1559628730265l;
        System.out.println(getSdfTime(start));

        long s=System.currentTimeMillis();
        long s2=1000*60*60*24*100;

        System.out.println(s+"  "+s2);
        System.out.println(s-s2);*/
        Date dateTime = getDateTimeFromInfluxdb(date1);
        long time = dateTime.getTime();
        System.out.println(dateTime);
        System.out.println(time);

    }


    public static Object getFormatTime(Object getTimestampPower) {

        return null;
    }
}
