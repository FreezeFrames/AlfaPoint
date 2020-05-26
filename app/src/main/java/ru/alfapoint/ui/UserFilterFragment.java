package ru.alfapoint.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.alfapoint.R;
import ru.alfapoint.EntryByClient;
import ru.alfapoint.model.Client;
import ru.alfapoint.model.InfoClass;
import ru.alfapoint.model.SaloonData;
import ru.alfapoint.model.Service;
import ru.alfapoint.model.adapters.SearchSaloonAdapter;
import ru.alfapoint.model.interfaces.InfoClassCallback;
import ru.alfapoint.model.interfaces.PositionCallback;
import ru.alfapoint.model.interfaces.SaloonCallback;
import ru.alfapoint.utils.DialogHelper;
import ru.alfapoint.utils.FBHelper;

public class UserFilterFragment extends Fragment {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ConstraintLayout clEntr2;
    TextView textView37,textView38,textView39;
    Client client;
    Button button;
    String service,serviceID;
    long startCost = 0, stopCost = 200;
    List<Service> serviceList = new ArrayList<>();
    List<SaloonData> saloonList = new ArrayList<>();
    RecyclerView rvSearch;
    SearchSaloonAdapter adapter;
    Boolean lastService=false;

    public UserFilterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_userfilter, container, false);
        clEntr2 = v.findViewById(R.id.clEntr2);
        textView37 = v.findViewById(R.id.textView37);
        textView38 = v.findViewById(R.id.textView38);
        textView39 = v.findViewById(R.id.textView39);
        button = v.findViewById(R.id.button12);
        rvSearch = v.findViewById(R.id.rvSearch);

        //fonts
        textView37.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        textView38.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        textView39.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));

        adapter = new SearchSaloonAdapter(getContext(), saloonList, new PositionCallback() {
            @Override
            public void getPosition(int pos) {
                Intent i = new Intent(getContext(), EntryByClient.class);
                Bundle bb = new Bundle();
                bb.putInt("from", 2); // пришли с экрана поиска
                bb.putString("service", saloonList.get(pos).getServiceName());
                bb.putString("serviceID", saloonList.get(pos).getServiceID());
                bb.putString("saloonID", saloonList.get(pos).getUID());
                bb.putBoolean("edited", false);
                //bb.putParcelable("client", client);
                //bb.putParcelable("saloon", (Parcelable) saloonList.get(pos));
                i.putExtras(bb);
                startActivity(i);
            }
        });

        rvSearch.setAdapter(adapter);
        rvSearch.setHasFixedSize(true);
        rvSearch.setLayoutManager(new LinearLayoutManager(getActivity()));
        if(mAuth.getCurrentUser() != null){
            // пользователь существует
            clEntr2.setVisibility(View.VISIBLE);
            loadUserInfo();
            // выбор услуги
            textView38.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogHelper.GetDefaultServices(getContext(), new InfoClassCallback() {
                        @Override
                        public void getInfoClass(InfoClass pos) {
                            textView38.setText(pos.getStr1());
                            service = pos.getStr1();
                            serviceID = pos.getStr2();
                        }
                    });
                }
            });
            // выбор цены
            textView39.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogHelper.SelectionCostBy(getContext(), new InfoClassCallback() {
                        @Override
                        public void getInfoClass(InfoClass pos) {
                            startCost = Long.valueOf(pos.getStr1());
                            stopCost = Long.valueOf(pos.getStr2());
                        }
                    });
                }
            });
            // кнопка показать
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(service != null ){
                        showServices();
                        Log.e("service", service);
                    }

                }
            });
        }else {
            clEntr2.setVisibility(View.GONE);
        }

        return v;
    }

    private void showServices() {
        FBHelper.GetServices()
                .whereEqualTo("name", service)
                .whereGreaterThan("cost", startCost-1)
                .whereLessThan("cost", stopCost +1 ).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                serviceList.clear();
                saloonList.clear();
                for(final DocumentSnapshot doc : task.getResult()){
                    serviceList.add(new Service(doc.getId(),doc.getData()));
                    loadSaloonData(new Service(doc.getId(),doc.getData()), new SaloonCallback() {
                        @Override
                        public void getSaloon(SaloonData saloonData) {
                            saloonList.add(saloonData);
                            Collections.sort(saloonList, new Comparator<SaloonData>() {
                                @Override
                                public int compare(SaloonData o1, SaloonData o2) {
                                    return Double.compare(o1.getFullDistance(),o2.getFullDistance());
                                }
                            });
                            adapter.notifyDataSetChanged();
                            disableAll();
                        }
                    });
                }
                if(serviceList.size()==0){
                    Toast.makeText(getContext(),"Нет предложений!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void loadSaloonData(final Service service, final SaloonCallback saloonCallback){

        FBHelper.GetUsers().document(service.getSaloonID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().getData() != null) {
                    saloonCallback.getSaloon(new SaloonData(task.getResult().getData(),
                            new LatLng(client.getLat(),client.getLng()), service));
                }
            }
        });
    }
    private void disableAll(){
        clEntr2.setVisibility(View.GONE);
        rvSearch.setVisibility(View.VISIBLE);
    }

    private void loadUserInfo() {
        FBHelper.GetUsers().whereEqualTo("uid", mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot doc : task.getResult().getDocuments()){
                    client = new Client(doc.getData());
                }
                if(client.getAddress()!=null){
                    textView37.setText(client.getAddress());
                }
            }
        });
    }


}
