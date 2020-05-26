package ru.alfapoint.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.alfapoint.R;
import ru.alfapoint.SaloonActivity;
import ru.alfapoint.model.Client;
import ru.alfapoint.model.Service;
import ru.alfapoint.model.adapters.AddServiceAdapter;
import ru.alfapoint.model.interfaces.AddServiceCallback;
import ru.alfapoint.utils.FBHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class SaloonServicesFragment2 extends Fragment {
    String saloonID, serviceName;
    Service service;
    List<Service> serviceList = new ArrayList<>();
    RecyclerView rvAddservice;
    FloatingActionButton floatingActionButton;
    AddServiceAdapter adapter;
    LayoutAnimationController controller;
    double lat,lng;
    Client client;
    SaloonActivity saloonActivity;

    public SaloonServicesFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_registration4, container, false);
        rvAddservice = v.findViewById(R.id.rvAddservice);
        floatingActionButton = v.findViewById(R.id.floatingActionButton);
        saloonID = getArguments().getString("saloonID");
        serviceName = getArguments().getString("serviceName");
        saloonActivity = (SaloonActivity)getContext();
        client = saloonActivity.getClient();
        lat = client.getLat();
        lng = client.getLng();

        service = ViewModelProviders.of(this).get(Service.class);
        service.serviceData(saloonID, serviceName).observe(this, new Observer<List<Service>>() {
            @Override
            public void onChanged(@Nullable List<Service> services) {
                serviceList = services;
                adapter.reload(serviceList);
            }
        });
        adapter = new AddServiceAdapter(serviceList, getContext(), new AddServiceCallback() {
            @Override
            public void removeService(int servicePosition) {
                // удаление услуги и расписания мастера
                FBHelper.GetServices().document(serviceList.get(servicePosition).getServiceID()).delete();
                String serviceID = serviceList.get(servicePosition).getServiceID();
                FBHelper.GetDaySheet().whereEqualTo("saloonID", saloonID).whereEqualTo("serviceID", serviceID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(int i=0; i<task.getResult().getDocuments().size();i++){
                            FBHelper.GetDaySheet().document(task.getResult().getDocuments().get(i).getId()).delete();
                        }
                    }
                });
                serviceList.remove(servicePosition);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void addMaster(int position) {
                saloonActivity.ChooseMaster(serviceList.get(position).getSaloonID(),
                        serviceList.get(position).getServiceID());
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
            public void checkRasp(int position) {
                saloonActivity.ChooseRasp(serviceList.get(position).getMasterID(),
                        serviceList.get(position).getMasterName(),serviceList.get(position).getServiceID(),
                        serviceList.get(position).getSaloonID());
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
                newMap.put("lat", lat);
                newMap.put("lng", lng);
                newMap.put("masterName", "Мастер");
                newMap.put("raspChecked", false);
                newMap.put("cost", Long.valueOf(0));
                FBHelper.GetServices().add(newMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        serviceList.add(new Service(task.getResult().getId() ,newMap));
                        rvAddservice.scheduleLayoutAnimation();
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
        return v;
    }

    private void loadService(){
        FBHelper.GetServices().whereEqualTo("saloonID", saloonID).
            whereEqualTo("name",serviceName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            serviceList.clear();
            for(DocumentSnapshot doc : task.getResult()){
                serviceList.add(new Service(doc.getId(),doc.getData()));
            }
            adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadService();
    }
}
