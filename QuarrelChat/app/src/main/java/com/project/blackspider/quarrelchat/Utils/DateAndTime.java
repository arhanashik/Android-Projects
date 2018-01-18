package com.project.blackspider.quarrelchat.Utils;

/**
 * Created by blackSpider on 06/07/2016.
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateAndTime {

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm",
            Locale.getDefault());
    private SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM",
            Locale.getDefault());
    private SimpleDateFormat sdf3 = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss",
            Locale.getDefault());

    private SimpleDateFormat sdf4 = new SimpleDateFormat("dd MMM, hh:mm a",
            Locale.getDefault());

    private SimpleDateFormat sdf5 = new SimpleDateFormat("dd MMM yyyy",
            Locale.getDefault());
    private Calendar calendar;

    private String currentDateStr;
    private String currentDateAndTimeStr;

    private long currentTimeInMillis=0;

    public DateAndTime() {
    }

    public String currentDate(){
        calendar = Calendar.getInstance();
        currentDateStr = sdf.format(calendar.getTime());

        return currentDateStr;
    }

    public long currentTimeInMillis(){
        calendar = Calendar.getInstance();
        currentTimeInMillis = calendar.getTimeInMillis();

        return currentTimeInMillis;
    }

    public String currentDateAndTime(){
        calendar = Calendar.getInstance();
        currentDateAndTimeStr = sdf3.format(calendar.getTime());

        return currentDateAndTimeStr;
    }

    public String currentDateAndTimeFormate4(){
        calendar = Calendar.getInstance();
        currentDateAndTimeStr = sdf4.format(calendar.getTime());

        return currentDateAndTimeStr;
    }

    public String dateForList(String givenDate){
        Date date = new Date();
        String outputDate = "";
        try {
            date = sdf2.parse(givenDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        outputDate = sdf2.format(date);
        return outputDate;
    }

    public long timeInMillis(String givenDate){
        Date date = new Date();
        calendar = Calendar.getInstance();
        long output = calendar.getTimeInMillis();
        String year = " 2017";
        if (givenDate.contains("Dec")) year = " 2016";
        givenDate=givenDate.substring(0, givenDate.indexOf(","))+year;
        try {
            date = sdf5.parse(givenDate);
            output = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return output;
    }

}
