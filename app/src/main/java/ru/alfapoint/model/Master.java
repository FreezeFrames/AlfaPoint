package ru.alfapoint.model;

import java.util.Map;

public class Master {
    String masteName;
    String masterPhoto;
    String saloonId;
    String masterID;

    public Master(String ids, String n){
        masteName = n;
        masterID = ids;
    }
    public Master(String ids){
        masterID = ids;
    }
    public Master(String ids, String n, String p, String id){
        masteName = n;
        masterPhoto = p;
        saloonId = id;
        masterID = ids;
    }

    public Master(String id, Map<String,Object> map){
        masteName = map.get("name").toString();
        masterPhoto = map.get("photo").toString();
        saloonId = map.get("saloonID").toString();
        masterID = id;
    }

    public String getMasteName() {
        return masteName;
    }

    public String getMasterID() {
        return masterID;
    }

    public void setMasteName(String masteName) {
        this.masteName = masteName;
    }

    public String getMasterPhoto() {
        return masterPhoto;
    }

    public void setMasterPhoto(String masterPhoto) {
        this.masterPhoto = masterPhoto;
    }
    public String getSaloonId() {
        return saloonId;
    }

    public void setSaloonId(String saloonId) {
        this.saloonId = saloonId;
    }
}
