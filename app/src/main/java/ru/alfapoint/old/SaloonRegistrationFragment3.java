package ru.alfapoint.old;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import ru.alfapoint.AddSaloonActivity;
import ru.alfapoint.R;
import ru.alfapoint.SaloonActivity;
import ru.alfapoint.model.Client;
import ru.alfapoint.model.Service;
import ru.alfapoint.Additional.RecycleTouchListener;
import ru.alfapoint.model.adapters.ServicesAdapter2;
import ru.alfapoint.model.interfaces.UIDCallback;
import ru.alfapoint.utils.DialogHelper;
import ru.alfapoint.utils.FBHelper;
import ru.alfapoint.utils.Helper;

/**
 * A simple {@link Fragment} subclass.
 */
public class SaloonRegistrationFragment3 extends Fragment {
    //Spinner spinner;
    int tempCounter;
    String saloonID;
    Double lat, lng;
    RecyclerView recyclerView;
    List<Service> mainServicesList = new ArrayList<>();
    ServicesAdapter2 adapter;
    Button button6;
    TextView tvSelection;
    ImageView imageView11;

    public SaloonRegistrationFragment3() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_registration3, container, false);
        saloonID = getArguments().getString("docID");
        lat = getArguments().getDouble("lat");
        lng = getArguments().getDouble("lng");
        button6 = v.findViewById(R.id.button6);
        imageView11 = v.findViewById(R.id.imageView11);
        recyclerView = v.findViewById(R.id.rvServices);
        tvSelection = v.findViewById(R.id.textView9);
        adapter = new ServicesAdapter2(getContext(),mainServicesList);
        tvSelection.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        tvSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            /*DialogHelper.GetDefaultServices(getContext(), new ListCallback() {
                @Override
                public void getList(List<String> pos) {
                    HashMap<String,Object> mapa = new HashMap<>();
                    mapa.put("saloonID", saloonID);
                    mapa.put("lat", lat);
                    mapa.put("lng", lng);
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
            });*/
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
                        mapa.put("saloonID", saloonID);
                        mapa.put("lat", lat);
                        mapa.put("lng", lng);
                        mapa.put("name", string);
                        mapa.put("cost", 0);
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
                        AddSaloonActivity addSaloonActivity = (AddSaloonActivity)getContext();
                        addSaloonActivity.loadSaloonRegistration4(mainServicesList.get(position));
                        break;
                }
            }
            @Override
            public void onLongClick(View view, final int position) {
            }
        }));


        loadServices();

        //end registration
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Helper.ShowProgress(getContext(), "Сохранение...");
            FBHelper.GetUsers().whereEqualTo("uid", saloonID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && !task.getResult().isEmpty()){
                    for(DocumentSnapshot doc : task.getResult()){
                        Intent i = new Intent(getContext(), SaloonActivity.class);
                        Bundle dd = new Bundle();
                        dd.putParcelable("saloon", new Client(doc.getData()));
                        i.putExtras(dd);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        Helper.HideProgress();
                        break;
                    }
                }
                }
            });
            }
        });
        return v;
    }

    private void loadServices(){
        mainServicesList.clear();
        final  HashMap<String, Object> checkMap = new HashMap<>();
        FBHelper.GetServices().whereEqualTo("saloonID", saloonID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                // кол-во однотипных услуг
                for(tempCounter=0; tempCounter<mainServicesList.size(); tempCounter++){
                    countDocs(tempCounter, mainServicesList.get(tempCounter).getServiceName());
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
    private void countDocs(final int pos, String name){
        FBHelper.GetServices().whereEqualTo("saloonID", saloonID)
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