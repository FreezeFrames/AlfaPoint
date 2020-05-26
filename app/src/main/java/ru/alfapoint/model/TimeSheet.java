package ru.alfapoint.model;

import android.widget.Button;

import java.util.Date;

public class TimeSheet {
    String workTime;
    Date day;
    Boolean check;
    //String saloonID;

    public TimeSheet(Date dday,String sal){
        day = dday;
        workTime = sal;
        //masterID = msr;
    }
    public TimeSheet(Date dday,Boolean sal){
        day = dday;
        check = sal;
        //masterID = msr;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public Date getDay() {
        return day;
    }

    public String getWorkTime() {
        return workTime;
    }
}
