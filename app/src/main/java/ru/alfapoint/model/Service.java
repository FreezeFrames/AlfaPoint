package ru.alfapoint.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.alfapoint.utils.FBHelper;

public class Service extends ViewModel {
    String name;
    long cost;
    Boolean checked;
    Boolean raspChecked;
    Boolean enabled;
    String serviceID;
    String masterID;
    String saloonID;
    String masterName;
    int mastersCount;
    double lat;
    double lng;
    Boolean fullCompleted;

    public String getMasterName() {
        return masterName;
    }

    public String getSaloonID() {
        return saloonID;
    }

    private MutableLiveData<List<Service>> serviceData;

    public LiveData<List<Service>> serviceData(String serviceUID, String serviceName) {
        if (serviceData == null) {
            serviceData = new MutableLiveData<>();
            getServiceData(serviceUID, serviceName);
        }
        return serviceData;
    }
    private void getServiceData(String uid,String serviceName){
        FBHelper.GetServices().whereEqualTo("saloonID", uid).
            whereEqualTo("name",serviceName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<Service> tempList = new ArrayList<>();
                    for(DocumentSnapshot doc : task.getResult()){
                        tempList.add(new Service(doc.getId(),doc.getData()));
                        //Log.e("service",doc.get("masterName").toString());
                    }
                    serviceData.postValue(tempList);
                }
        });
    }
    public Service(){
    }
    public Service(String strName){
        name = strName;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public Service(String id, Map<String,Object> map){
        serviceID = id;

        if(map.get("checked")!=null){
            checked = (Boolean)map.get("checked");
        }else{
            checked = false;
        }

        if(map.get("raspChecked")!=null){
            raspChecked = (Boolean)map.get("raspChecked");
        }else{
            raspChecked = false;
        }

        if(map.get("enabled")!=null){
            enabled = (Boolean)map.get("enabled");
        }else{
            enabled = false;
        }

        if(map.get("cost") != null){
            cost = (long)map.get("cost");
        }else {
            cost = 0;
        }

        if(map.get("name")!=null){
            name = map.get("name").toString();
        }else
            name = "";

        if(map.get("masterID")!=null){
            masterID = map.get("masterID").toString();
        }else
            masterID = "";

        if(map.get("masterName")!=null){
            masterName = map.get("masterName").toString();
        }else
            masterName = "";

        if(map.get("saloonID")!=null){
            saloonID = map.get("saloonID").toString();
        }else
            saloonID = "";

        if(map.get("lat")!=null){
            lat = (double) map.get("lat");
        }else
            lat = 0;

        if(map.get("lng")!=null){
            lng = (double) map.get("lng");
        }else
            lng = 0;

        if(map.get("fullCompleted")!=null){
            fullCompleted = (Boolean) map.get("fullCompleted");
        }else{
            fullCompleted = false;
        }

    }

    public String getMasterID() {
        return masterID;
    }

    public String getServiceName() {
        return name;
    }

    public long getCost() {
        return cost;
    }

    public Boolean getChecked() {
        return checked;
    }

    public String getServiceID() {
        return serviceID;
    }

    public Boolean getRaspChecked() {
        return raspChecked;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public int getMastersCount() {
        return mastersCount;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public Boolean getFullCompleted() {
        return fullCompleted;
    }

    public void setMastersCount(int mastersCount) {
        this.mastersCount = mastersCount;
    }
}
