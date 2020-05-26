package ru.alfapoint.ui;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import ru.alfapoint.R;
import ru.alfapoint.SaloonActivity;
import ru.alfapoint.model.Client;
import ru.alfapoint.model.Service;
import ru.alfapoint.Additional.RecycleTouchListener;
import ru.alfapoint.model.adapters.ServicesAdapter2;
import ru.alfapoint.model.interfaces.ListCallback;
import ru.alfapoint.model.interfaces.UIDCallback;
import ru.alfapoint.utils.DialogHelper;
import ru.alfapoint.utils.FBHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class SaloonServicesFragment extends Fragment {
    TextView textView9;
    RecyclerView recyclerView;
    ImageView imageView11;
    Client client;
    List<Service> mainServicesList = new ArrayList<>();
    int tempCounter=0;
    ServicesAdapter2 adapter;

    public SaloonServicesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_registration3, container, false);
        v.findViewById(R.id.button6).setVisibility(View.GONE);
        textView9 = v.findViewById(R.id.textView9);
        recyclerView = v.findViewById(R.id.rvServices);
        imageView11 = v.findViewById(R.id.imageView11);
        textView9.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        adapter = new ServicesAdapter2(getContext(),mainServicesList);
        loadInfo();
        //choose service to add
        textView9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogHelper.GetDefaultServicesBySaloon(getContext(), new ListCallback() {
                    @Override
                    public void getList(List<?> pos) {
                        HashMap<String,Object> mapa = new HashMap<>();
                        mapa.put("saloonID", client.getUid());
                        mapa.put("lat", client.getLat());
                        mapa.put("lng", client.getLng());
                        mapa.put("masterName", "Мастер");
                        mapa.put("cost", 0);
                        for(int i=0; i< pos.size(); i++){
                            mapa.put("name", pos.get(i));
                            if(i == pos.size()-1){
                                FBHelper.GetServices().add(mapa).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        loadServices();
                                    }
                                });
                            }else{
                                FBHelper.GetServices().add(mapa);
                            }
                        }
                    }
                });
            }
        });
        // добавление новой услуги
        imageView11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogHelper.AddNewService(getContext(), new UIDCallback() {
                    @Override
                    public void getUID(String string) {
                        HashMap<String,Object> mapa = new HashMap<>();
                        mapa.put("saloonID", client.getUid());
                        mapa.put("lat", client.getLat());
                        mapa.put("lng", client.getLng());
                        mapa.put("name", string);
                        mapa.put("cost", 0);
                        mapa.put("masterName", "Мастер");
                        FBHelper.GetServices().add(mapa).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                loadServices();
                            }
                        });
                    }
                });

            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecycleTouchListener(getContext(), recyclerView, new RecycleTouchListener.RecycleListener() {
            @Override
            public void onClick(View view, final int position) {
                switch (position) {
                    default:
                        // редактирование услуги
                        SaloonActivity saloonActivity = (SaloonActivity)getContext();
                        saloonActivity.EditService(mainServicesList.get(position).getServiceName(),
                                mainServicesList.get(position).getSaloonID());
                        break;
                }
            }
            @Override
            public void onLongClick(View view, final int position) {
            }
        }));
        return v;
    }


    private void loadInfo(){
        SaloonActivity saloonActivity = (SaloonActivity)getContext();
        client = saloonActivity.getClient();
        loadServices();

    }
    private void loadServices(){
        mainServicesList.clear();

        final  HashMap<String, Object> checkMap = new HashMap<>();
        FBHelper.GetServices().whereEqualTo("saloonID", client.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot doc : task.getResult().getDocuments()){
                    // удаление дубликатов
                    if(checkMap.get(doc.get("name")) == null){
                        checkMap.put(doc.get("name").toString(),doc.get("name").toString());
                        mainServicesList.add(new Service(doc.getId(), doc.getData()));
                    }
                }
                // сортировка по имени
                Collections.sort(mainServicesList, new Comparator<Service>() {
                    @Override
                    public int compare(Service newsEntry, Service t1) {
                        return newsEntry.getServiceName().compareTo(t1.getServiceName());
                    }
                });
                // кол-во однотипных услуг, проверка на fullCompleted
                for(tempCounter=0; tempCounter < mainServicesList.size(); tempCounter++){
                    //if(mainServicesList.get(tempCounter).getFullCompleted()){
                        countDocs(tempCounter, mainServicesList.get(tempCounter).getServiceName());
                    //}else{
                        //mainServicesList.get(tempCounter).setMastersCount(0);
                    //}

                }
                adapter.notifyDataSetChanged();
            }
        });
    }
    private void countDocs(final int pos, String name){
        FBHelper.GetServices().whereEqualTo("saloonID", client.getUid())
            .whereEqualTo("name", name).get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful() && !task.getResult().isEmpty()){
                        mainServicesList.get(pos).setMastersCount(task.getResult().getDocuments().size());
                        adapter.notifyDataSetChanged();
                    }
                }
            });
    }

}
