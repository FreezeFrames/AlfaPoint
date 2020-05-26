package ru.alfapoint.model;

import java.util.Date;

public class InfoClass {
    String str1;
    String str2;
    String str3;
    String str4;
    Boolean checkInfo;
    Date date;
    int version;

    public InfoClass(String s1){
        str1 = s1;
    }
    public InfoClass(Date s1){
        date = s1;
    }
    public InfoClass(int vers, String s1, String s2){
        version = vers;
        str1 = s1;
        str2 = s2;
        checkInfo = false;
    }
    public InfoClass(Boolean bool, String s1, String s2, String s3, String s4){
        str1 = s1;
        str2 = s2;
        str3 = s3;
        str4 = s4;
        checkInfo = bool;
    }
    public InfoClass(String s1, int s2){
        str1 = s1;
        version = s2;
    }

    public void setStr3(String str3) {
        this.str3 = str3;
    }

    public void setStr4(String str4) {
        this.str4 = str4;
    }

    public InfoClass(String s1, String s2){
        str1 = s1;
        str2 = s2;
        checkInfo = false;
    }
    public InfoClass(String s1, String s2, Date dd){
        str1 = s1;
        str2 = s2;
        checkInfo = false;
        date = dd;
    }
    public InfoClass(String s1, String s2,String s3){
        str1 = s1;
        str2 = s2;
        str3 =s3;
        checkInfo = false;
    }

    public String getStr3() {
        return str3;
    }

    public String getStr1() {
        return str1;
    }

    public String getStr2() {
        return str2;
    }
    public String getStr4() {
        return str4;
    }
    public void enableCheckInfo(){
        checkInfo = true;
    }
    public void disableCheckInfo(){
        checkInfo = false;
    }

    public Boolean getCheckInfo() {
        return checkInfo;
    }

    public Date getDate() {
        return date;
    }

    public void setStr2(String str2) {
        this.str2 = str2;
    }
}
