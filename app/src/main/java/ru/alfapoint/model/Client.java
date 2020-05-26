package ru.alfapoint.model;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.alfapoint.utils.FBHelper;

public class Client  extends ViewModel implements Parcelable {
    String name;
    String phone;
    String email;
    double lat;
    double lng;
    String address;
    String uid;
    String type;
    String description;
    String workTime;
    String workType;
    String city;
    String country;
    String code;
    String token;
    String image;

    protected Client(Parcel in) {
        name = in.readString();
        phone = in.readString();
        email = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        address = in.readString();
        uid = in.readString();
        type = in.readString();
        description = in.readString();
        workTime = in.readString();
        workType = in.readString();
        city = in.readString();
        country = in.readString();
        code = in.readString();
        token = in.readString();
        image = in.readString();
    }

    public static final Creator<Client> CREATOR = new Creator<Client>() {
        @Override
        public Client createFromParcel(Parcel in) {
            return new Client(in);
        }

        @Override
        public Client[] newArray(int size) {
            return new Client[size];
        }
    };

    public String getDescription() {
        return description;
    }

    public Client(Map<String,Object> map){
        if(map.get("code") != null){
            code = map.get("code").toString();
        }else{
            code = "";
        }

        if(map.get("city")!=null){
            city = map.get("city").toString();
        }else{
            city = "";
        }

        if(map.get("country")!=null){
            country = map.get("country").toString();
        }else{
            country = "";
        }

        if(map.get("address")!=null){
            address = map.get("address").toString();
        }else{
            address = "";
        }

        if(map.get("workTime")!=null){
            workTime = map.get("workTime").toString();
        }else{
            workTime = "";
        }

        if(map.get("workType")!=null){
            workType = map.get("workType").toString();
        }else{
            workType = "";
        }

        if(map.get("description")!=null){
            description = map.get("description").toString();
        }else{
            description = "";
        }

        email = map.get("email").toString();
        type = map.get("type").toString();
        uid = map.get("uid").toString();
        name = map.get("name").toString();
        phone = map.get("phone").toString();
        lat = (Double)map.get("lat");
        lng = (Double)map.get("lng");

        if(map.get("token") != null){
            token = map.get("token").toString();
        }else{
            token = "";
        }
        if(map.get("image")!=null){
            image = map.get("image").toString();
        }else{
            image = "R.drawable.salon_map";
        }
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public LatLng getLatLng(){
        if (lat!=0 && lng!=0)
            return new LatLng(lat,lng);
        else
            return null;
    }

    public String getCode() {
        return code;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getAddress() {
        return address;
    }

    public String getUid() {
        return uid;
    }

    public String getType() {
        return type;
    }

    public String getWorkTime() {
        return workTime;
    }

    public String getWorkType() {
        return workType;
    }

    public String getToken() {
        return token;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(address);
        dest.writeString(uid);
        dest.writeString(type);
        dest.writeString(description);
        dest.writeString(workTime);
        dest.writeString(workType);
        dest.writeString(city);
        dest.writeString(country);
        dest.writeString(code);
        dest.writeString(token);
        dest.writeString(image);
    }
}
