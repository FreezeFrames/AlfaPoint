package ru.alfapoint.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Entry {
    Map<String, Object> entry;
    String entryID;

    public Entry(String id, Map<String,Object> obj){
        entry = obj;
        entryID = id;
    }

    public String getEntryID() {
        return entryID;
    }

    public String getStatus() {
        return (String)entry.get("status");
    }

    public String getClientName() {
        return (String)entry.get("clientName");
    }

    public String getClientID() {
        return (String)entry.get("clientID");
    }
    public String getClientEmail(){
        return (String)entry.get("clientEmail");
    }

    public String getClientPhone() {
        return (String)entry.get("clientPhone");
    }

    public String getSaloonName() {
        return (String)entry.get("saloonName");
    }

    public String getSaloonPhone() {
        return (String)entry.get("saloonPhone");
    }

    public String getSaloonAddress() {
        return (String)entry.get("saloonAddress");
    }

    public String getSaloonCity() {
        return (String)entry.get("city");
    }





    public long getCost() {
        return (Long)entry.get("cost");
    }

    public Date getDay() {
       return (Date)entry.get("day");
    }

    public String getMasterID() {
        return (String)entry.get("masterID");
    }

    public String getParsDate() {
        return (String)entry.get("parsDate");
    }

    public Boolean getAppNotification() {
        return (Boolean)entry.get("appNotification");
    }

    public Boolean getPhoneNotification() {
        return (Boolean)entry.get("phoneNotification");
    }

    public Boolean getEmailNotification() {
        return (Boolean)entry.get("emailNotification");
    }

    public Boolean getSMSNotification() {
        return (Boolean)entry.get("smsNotification");
    }

    public String getSaloonID() {
        return (String)entry.get("saloonID");
    }

    public String getTime() {
        return (String)entry.get("time");
    }

    public String getToken() {
        return (String)entry.get("token");
    }

    public String getMasterName() {
        return (String)entry.get("masterName");
    }

    public String getService() {
        return (String)entry.get("service");
    }
    public String getServiceID() {
        return (String)entry.get("serviceID");
    }

    public void setSatus(String newstatus){
        entry.put("status",newstatus);
    }
}

/*
Boolean appNotification;
    String clientID;
    String clientName;
    String clientPhone;
    String cost;
    //Date day;
    String masterID;
    String parsDate;
    Boolean phoneNotification;
    String saloonID;
    String saloonName;
    String saloonPhone;
    String saloonAddress;
    String time;
    String token;
    String masterName;
    String service;

    String status;

appNotification = (Boolean)obj.get("appNotification");
        phoneNotification = (Boolean)obj.get("phoneNotification");
        clientID = (String)obj.get("clientID");
        cost = (String)obj.get("cost");
        masterID = (String)obj.get("masterID");
        parsDate = (String)obj.get("parsDate");
        saloonID = (String)obj.get("saloonID");
        time = (String)obj.get("time");
        token = (String)obj.get("token");
        day = (Date)obj.get("day");
        if(obj.get("masterName")!=null){
            masterName = (String)obj.get("masterName");
        }else
            masterName ="";

        if(obj.get("service")!=null){
            service = (String)obj.get("service");
        }else
            service ="";
        if(obj.get("clientName")!=null){
            clientName = (String)obj.get("clientName");
        }else
            clientName ="";
        if(obj.get("clientPhone")!=null){
            clientPhone = (String)obj.get("clientPhone");
        }else
            clientPhone ="";
        if(obj.get("saloonName")!=null){
            saloonName = (String)obj.get("saloonName");
        }else
            saloonName ="";
        if(obj.get("saloonPhone")!=null){
            saloonPhone = (String)obj.get("saloonPhone");
        }else
            saloonPhone ="";
        if(obj.get("saloonAddress")!=null){
            saloonAddress = (String)obj.get("saloonAddress");
        }else
            saloonAddress ="";
        if(obj.get("status")!=null){
            status = (String)obj.get("status");
        }else
            status ="";
 */