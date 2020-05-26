package ru.alfapoint.ui;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import ru.alfapoint.UserActivity;
import ru.alfapoint.model.Client;
import ru.alfapoint.model.Entry;
import ru.alfapoint.model.adapters.EntryAdapter;
import ru.alfapoint.model.interfaces.EntryCallback;
import ru.alfapoint.model.interfaces.UIDCallback;
import ru.alfapoint.utils.DialogHelper;
import ru.alfapoint.utils.FBHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserAllEntriesFragment extends Fragment {
    EntryAdapter adapter;
    List<Entry> entries= new ArrayList<>();
    RecyclerView recyclerView;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ConstraintLayout clEntr1;
    TextView tv;
    Client client;

    public UserAllEntriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_allentries, container, false);
        recyclerView = v.findViewById(R.id.rvAllEntries);
        clEntr1 = v.findViewById(R.id.clEntr1);
        tv = v.findViewById(R.id.textView34);
        adapter = new EntryAdapter(entries, getContext(), new EntryCallback() {
            @Override
            public void onPhoneClick(int position) {
                //запрашиваем разрешения
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},1);
                onPhone(position);
            }
            @Override
            public void onMapClick(int position) {
                onMap(position);
            }
            @Override
            public void onDelete(final int position) {
                DialogHelper.DialogOkNo(getContext(), "Удаление!", "Вы уверены, что хотите удалить запись?", new UIDCallback() {
                    @Override
                    public void getUID(String string) {
                        if(string.equals("OK")){
                            onEntryDelete(position);
                            load();
                        }
                    }
                });
            }
            @Override
            public void onEdit(int position) {
                if(!entries.get(position).getStatus().equals("Заявка отклонена")){
                    Intent i = new Intent(getContext(), EntryByClient.class);
                    Bundle bb = new Bundle();
                    bb.putInt("from",2); // пришли с экрана поиска
                    bb.putString("service", entries.get(position).getService());
                    bb.putString("serviceID", entries.get(position).getServiceID());
                    bb.putString("saloonID", entries.get(position).getSaloonID());
                    bb.putString("entryID", entries.get(position).getEntryID());
                    bb.putBoolean("edited", true);
                    i.putExtras(bb);
                    startActivity(i);
                }
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        load();
        return v;
    }

    private void load(){
        entries.clear();
        if(mAuth.getCurrentUser() != null){
            UserActivity userActivity = (UserActivity)getActivity();
            client = userActivity.getClient();
            FBHelper.GetEntries().whereEqualTo("clientID", mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot doc : task.getResult()){
                        entries.add(new Entry(doc.getId(), doc.getData()));
                    }
                    if(entries.size()==0){
                        clEntr1.setVisibility(View.VISIBLE);
                        tv.setText("У вас нет записей!");
                    }else{
                        Collections.sort(entries, new Comparator<Entry>() {
                            @Override
                            public int compare(Entry o1, Entry o2) {
                                return o1.getDay().compareTo(o2.getDay());
                            }
                        });
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            clEntr1.setVisibility(View.GONE);
        }else{
            clEntr1.setVisibility(View.VISIBLE);
        }
    }

    private void onEntryDelete(int position) {
        FBHelper.GetEntries().document(entries.get(position).getEntryID()).delete();
    }

    private void onPhone(int pos){
        if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.CALL_PHONE)==-1){
            Toast.makeText(getContext(), "Разрешение на звонок не получено!", Toast.LENGTH_SHORT).show();
        }else{
            startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel: +7" + entries.get(pos).getSaloonPhone())));
        }
    }

    private void onMap(int pos){
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/maps?q=" +entries.get(pos).getSaloonAddress()));
        startActivity(i);
    }
}
