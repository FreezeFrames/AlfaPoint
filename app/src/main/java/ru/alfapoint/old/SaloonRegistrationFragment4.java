package ru.alfapoint.old;


import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.alfapoint.AddSaloonActivity;
import ru.alfapoint.R;
import ru.alfapoint.model.Service;
import ru.alfapoint.model.adapters.AddServiceAdapter;
import ru.alfapoint.model.interfaces.AddServiceCallback;
import ru.alfapoint.utils.FBHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class SaloonRegistrationFragment4 extends Fragment {
    String saloonID,serviceName,serviceID;
    AddServiceAdapter adapter;
    List<Service> serviceList = new ArrayList<>();
    RecyclerView rvAddservice;
    AddSaloonActivity addSaloonActivity;
    Service service;
    //List<Master> masters;
    FloatingActionButton floatingActionButton;
    LayoutAnimationController controller;

    public SaloonRegistrationFragment4() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_registration4, container, false);
        saloonID = getArguments().getString("docID");
        serviceName = getArguments().getString("serviceName");
        //Log.e("reg4", getArguments().getString("serviceID"));
        floatingActionButton = v.findViewById(R.id.floatingActionButton);

        service = ViewModelProviders.of(this).get(Service.class);
        service.serviceData(saloonID, serviceName).observe(this, new Observer<List<Service>>() {
            @Override
            public void onChanged(@Nullable List<Service> services) {
                serviceList = services;
                Log.e("serviceID " , serviceID);
                serviceID = serviceList.get(0).getServiceID();
                adapter.reload(serviceList);
            }
        });
        rvAddservice = v.findViewById(R.id.rvAddservice);
        addSaloonActivity = (AddSaloonActivity)getContext();
        adapter = new AddServiceAdapter(serviceList, getContext(),  new AddServiceCallback(){

            @Override
            public void removeService(int servicePosition) {
                FBHelper.GetServices().document(serviceList.get(servicePosition).getServiceID()).delete();

                serviceList.remove(servicePosition);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void addMaster(int position) {
                addSaloonActivity.loadSaloonRegistration5(position);
            }

            @Override
            public void addCost(String cost, int pos) {
                serviceList.get(pos).setCost(Long.valueOf(cost));
                HashMap<String, Object> newMap = new HashMap<>();
                newMap.put("cost", Long.valueOf(cost));
                FBHelper.GetServices().document(serviceList.get(pos).getServiceID()).update(newMap);
                adapter.notifyDataSetChanged();
            }

            /*@Override
            public void checkRasp(int pos) {
                addSaloonActivity.loadSaloonregistration6(serviceList.get(pos).getMasterID(),
                        serviceList.get(pos).getMasterName(), serviceList.get(pos).getServiceID());
            }*/
        });
        rvAddservice.setAdapter(adapter);
        rvAddservice.setHasFixedSize(true);
        rvAddservice.setLayoutManager(new LinearLayoutManager(getActivity()));

        // анимация RV
        controller =
                AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation);
        rvAddservice.setLayoutAnimation(controller);

        //новый сервис
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final HashMap<String, Object> newMap = new HashMap<>();
                newMap.put("name", serviceName);
                newMap.put("saloonID", saloonID);
                //newMap.put("checked", true);
                newMap.put("lat", serviceList.get(0).getLat());
                newMap.put("lng", serviceList.get(0).getLng());
                newMap.put("masterName", "Мастер");
                newMap.put("cost", "0000");

                FBHelper.GetServices().add(newMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        serviceList.add(new Service(task.getResult().getId() ,newMap));
                        rvAddservice.scheduleLayoutAnimation();
                        adapter.notifyDataSetChanged();
                    }
                });
                //rvAddservice.getAdapter().notifyDataSetChanged();

            }
        });
        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode==Activity.RESULT_OK) {
            //Bundle bb = data.getExtras();
            updateMaster(data.getExtras());
        }
    }

    private void updateMaster(Bundle dd){
        int pos = dd.getInt("adapterPosition");
        String masterName = dd.getString("masterName");
        String masterID = dd.getString("masterID");
        serviceList.get(pos).setMasterName(masterName);
        adapter.notifyDataSetChanged();
        HashMap<String, Object> newMap = new HashMap<>();
        newMap.put("masterID", masterID);
        newMap.put("masterName", masterName);
        FBHelper.GetServices().document(serviceList.get(pos).getServiceID()).update(newMap);
    }
}
