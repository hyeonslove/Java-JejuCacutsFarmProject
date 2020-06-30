package com.example.newjejucacutsfarm;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class DateClass implements Serializable {
    private static final long serialVersionUID = -514235523541L;
    private ArrayList<CactusData> objectlist = new ArrayList<CactusData>();
    private int year,month,day,hour,min,sec;
    public DateClass() {
        this.year = (Calendar.getInstance().get(Calendar.YEAR)) % 100;
        this.month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        this.day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        this.hour = Calendar.getInstance().get(Calendar.HOUR);
        this.min = Calendar.getInstance().get(Calendar.MINUTE);
        this.sec = Calendar.getInstance().get(Calendar.SECOND);
    }
    public DateClass(int year,int month,int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = Calendar.getInstance().get(Calendar.HOUR);
        this.min = Calendar.getInstance().get(Calendar.MINUTE);
        this.sec = Calendar.getInstance().get(Calendar.SECOND);
    }
    public DateClass(int year,int month,int day,int hour,int min,int sec) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.min = min;
        this.sec = sec;
    }

    public ArrayList<CactusData> getObjectList() {
        return objectlist;
    }

    @Override
    public String toString() {
        return year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec;
    }

    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public int getMonth() {
        return month;
    }
    public void setMonth(int month) {
        this.month = month;
    }
    public int getDay() {
        return day;
    }
    public void setDay(int day) {
        this.day = day;
    }
    public int getHour() {
        return hour;
    }
    public void setHour(int hour) {
        this.hour = hour;
    }
    public int getMin() {
        return min;
    }
    public void setMin(int min) {
        this.min = min;
    }
    public int getSec() {
        return sec;
    }
    public void setSec(int sec) {
        this.sec = sec;
    }
    public int compareN(DateClass date) {
        if(getCode() > date.getCode())
            return 1;
        else if(getCode() < date.getCode())
            return -1;
        else
            return 0;
    }
    public int getNumber(){
        return (year * 10000) + (month* 100) + day;
    }
    public double getCode() {
        double sum = 0;
        double digits=1;
        sum+=(sec*digits);
        digits*=100;
        sum+=(min*digits);
        digits*=100;
        sum+=(hour*digits);
        digits*=100;
        sum+=(day*digits);
        digits*=100;
        sum+=(month*digits);
        digits*=100;
        sum+=(year*digits);
        digits*=100;
        return sum;
    }
}