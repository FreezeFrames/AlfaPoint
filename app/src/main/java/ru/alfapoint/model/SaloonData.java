package ru.alfapoint.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Random;

public class SaloonData {
    Map<String,Object> obj;
    LatLng userLatLng;
    Service service;

    public SaloonData(Map<String,Object> obj,LatLng user){
        this.obj = obj;
        userLatLng = user;
    }
    public SaloonData(Map<String,Object> obj,LatLng user, Service se){
        this.obj = obj;
        userLatLng = user;
        service = se;
    }

    public String getServiceID(){
        if(service != null){
            return service.getServiceID();
        }else{
            return "";
        }
    }
    public String getServiceName(){
        if(service != null){
            return service.getServiceName();
        }else{
            return "";
        }
    }
    public String getServiceCost(){
        if(service != null){
            return String.valueOf(service.getCost());
        }else{
            return "";
        }
    }


    public String getSaloonName(){
        if(obj.get("name") != null){
            return obj.get("name").toString();
        }else{
           return "";
        }
    }

    public String getDescription(){
        if(obj.get("description") != null){
            return obj.get("description").toString();
        }else{
            return "";
        }
    }

    public String getWorkType(){
        if(obj.get("workType") != null){
            return obj.get("workType").toString();
        }else{
            return "";
        }
    }

    public String getWorkTime(){
        if(obj.get("workTime") != null){
            return obj.get("workTime").toString();
        }else{
            return "";
        }
    }

    public String getCity(){
        if(obj.get("city") != null){
            return obj.get("city").toString();
        }else{
            return "";
        }
    }

    public String getCountry(){
        if(obj.get("country") != null){
            return obj.get("country").toString();
        }else{
            return "";
        }
    }
    public String getPhone(){
        if(obj.get("phone") != null){
            return obj.get("phone").toString();
        }else{
            return "";
        }
    }

    public String getAddress(){
        if(obj.get("address") != null){
            return obj.get("address").toString();
        }else{
            return "";
        }
    }

    public String getUID(){
        if(obj.get("uid") != null){
            return obj.get("uid").toString();
        }else{
            return "";
        }
    }

    public String getSaloonImage(){
        if(obj.get("image") != null){
            return obj.get("image").toString();
        }else{
            return "salon1";
        }
    }

    public Boolean isPremium(){
        if(obj.get("premium") != null){
            return (Boolean) obj.get("premium");
        }else{
            return false;
        }
    }

    public String getDistance(){
        if(userLatLng == null){
            return "";
        }else{
            double lat, lng;
            if(obj.get("lat") != null){
                lat = (double) obj.get("lat");
            }else{
                lat =0;
            }
            if(obj.get("lng") != null){
                lng = (double) obj.get("lng");
            }else{
                lng =0;
            }
            LatLng saloonLatLng = new LatLng(lat,lng);
            return new DecimalFormat("#.#").format(SphericalUtil.computeDistanceBetween(userLatLng, saloonLatLng)/1000) + " км.";
        }
    }

    public double getFullDistance(){
        if(userLatLng == null){
            return 0;
        }else {
            double lat, lng;
            if (obj.get("lat") != null) {
                lat = (double) obj.get("lat");
            } else {
                lat = 0;
            }
            if (obj.get("lng") != null) {
                lng = (double) obj.get("lng");
            } else {
                lng = 0;
            }
            LatLng saloonLatLng = new LatLng(lat, lng);
            return SphericalUtil.computeDistanceBetween(userLatLng, saloonLatLng) / 1000;
        }
    }

}
