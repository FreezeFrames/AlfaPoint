package ru.alfapoint.ui;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import ru.alfapoint.UserActivity;
import ru.alfapoint.model.Client;
import ru.alfapoint.model.ClientObserver;
import ru.alfapoint.model.SaloonData;
import ru.alfapoint.Additional.RecycleTouchListener;
import ru.alfapoint.model.adapters.SallonAdapter;
import ru.alfapoint.utils.FBHelper;


public class UserHomeFragment extends Fragment {
    RecyclerView recyclerView;
    SallonAdapter adapter;
    List<SaloonData> sallonList = new ArrayList<>();
    List<SaloonData> tempList = new ArrayList<>();
    Client client;
    UserActivity userActivity;
    SearchView searchView;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ClientObserver clientObserver;

    public UserHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_userhome, container, false);
        recyclerView = v.findViewById(R.id.rvSalloons);
        userActivity = (UserActivity)getContext();
        adapter = new SallonAdapter(getContext(), sallonList);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecycleTouchListener(getContext(), recyclerView, new RecycleTouchListener.RecycleListener() {
            @Override
            public void onClick(View view, int position) {
                switch (position) {
                    default:
                        Intent i = new Intent(getContext(), EntryByClient.class);
                        Bundle bb = new Bundle();
                        bb.putInt("from", 1); // пришли с главного экрана пользователя
                        bb.putParcelable("client", client);
                        bb.putString("saloonID", sallonList.get(position).getUID());
                        bb.putBoolean("edited", false);
                        i.putExtras(bb);
                        //startActivity(i);
                        startActivityForResult(i,1);
                        break;
                }
            }
            @Override
            public void onLongClick(View view, final int position) {
            }
        }));
        searchView = v.findViewById(R.id.searchView);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filter(s);
                return false;
            }
        });

        if(mAuth.getCurrentUser() != null){
            clientObserver = ViewModelProviders.of(this).get(ClientObserver.class);
            clientObserver.getClient(mAuth.getCurrentUser().getUid()).observe(this, new Observer<Client>() {
                @Override
                public void onChanged(@Nullable Client getclient) {
                    client = getclient;
                    loadSaloons(1);
                }
            });
        }else{
            loadSaloons(2);
        }
        return v;
    }

    private void loadSaloons(int ver){
        Log.d("12345", "start");
        sallonList.clear();
        switch (ver){
            case 1:
                FBHelper.GetUsers().whereEqualTo("type","saloon").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot doc : task.getResult().getDocuments()){
                            Log.d("12345", doc.getId());
                            if(doc.getData().get("premium") != null && (Boolean) doc.getData().get("premium")){
                                sallonList.add(0, new SaloonData(doc.getData(), client.getLatLng()));
                            }else{
                                sallonList.add(new SaloonData(doc.getData(),client.getLatLng()));
                            }
                        }
                        //сортируем премиум вначале, далее по расстоянию
                        Collections.sort(sallonList, new Comparator<SaloonData>() {
                            @Override
                            public int compare(SaloonData o1, SaloonData o2) {
                                if(o1.isPremium() || o2.isPremium()){
                                    return 1;
                                }
                                return Double.compare(o1.getFullDistance(),o2.getFullDistance());
                            }
                        });
                        adapter.notifyDataSetChanged();
                    }
                });
                break;
            case 2:
                FBHelper.GetUsers().whereEqualTo("type","saloon").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot doc : task.getResult().getDocuments()){
                            Log.e("Salons ", doc.getId());
                            if(doc.getData().get("premium") != null && (Boolean) doc.getData().get("premium")){
                                sallonList.add(0, new SaloonData(doc.getData(), null));
                            }else{
                                sallonList.add(new SaloonData(doc.getData(),null));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
                break;
        }
    }

    //поиск
    private void filter(String text){
        tempList.clear();
        for(SaloonData d : sallonList){
            if(d.getSaloonName().toLowerCase().contains(text)){
                tempList.add(d);
            }
        }
        adapter.update(tempList);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            //переход в записи клиента
            userActivity.GoToUserEntries();
        }
    }
}

/*

FBHelper.GetUsers().whereEqualTo("type","saloon").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                            if(doc.getData().get("premium") != null && (Boolean) doc.getData().get("premium")){
                                sallonList.add(0, new SaloonData(doc.getData(), client.getLatLng()));
                            }else{
                                sallonList.add(new SaloonData(doc.getData(),client.getLatLng()));
                            }
                        }
                        //сортируем премиум вначале, далее по расстоянию
                        Collections.sort(sallonList, new Comparator<SaloonData>() {
                            @Override
                            public int compare(SaloonData o1, SaloonData o2) {
                                if(o1.isPremium() || o2.isPremium()){
                                    return 1;
                                }
                                return Double.compare(o1.getFullDistance(),o2.getFullDistance());
                            }
                        });
                        adapter.notifyDataSetChanged();
                    }
                });
                break;

 */