package ru.alfapoint.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.alfapoint.AddSaloonActivity;
import ru.alfapoint.R;
import ru.alfapoint.SaloonActivity;
import ru.alfapoint.model.Master;
import ru.alfapoint.model.adapters.MasterAdapter;
import ru.alfapoint.Additional.RecycleTouchListener;
import ru.alfapoint.utils.FBHelper;
import ru.alfapoint.utils.Helper;

/**
 * A simple {@link Fragment} subclass.
 */
public class SaloonRegistrationFragment5 extends Fragment  {
    RecyclerView recyclerView;
    MasterAdapter adapter;
    List<Master> masterList = new ArrayList<>();
    String saloonID;
    Button imageView6;
    EditText editText9;
    int vers =0;

    public SaloonRegistrationFragment5() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_registration5, container, false);
        recyclerView = v.findViewById(R.id.rvMasters);
        imageView6 = v.findViewById(R.id.imageView6);
        editText9 = v.findViewById(R.id.editText9);
        adapter = new MasterAdapter(masterList, getContext(),null);
        saloonID = getArguments().getString("docID");
        if(getArguments().getInt("version")!= 0){
            vers = getArguments().getInt("version");
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new RecycleTouchListener(getContext(), recyclerView, new RecycleTouchListener.RecycleListener() {
            @Override
            public void onClick(View view, int position) {
                switch (position) {
                    default:
                        //drawerHelper.startSubjectsActivity(getActivity(), subjectPosition,position + 1);
                        if(vers==2){
                            HashMap<String,Object> masterMap = new HashMap<>();
                            masterMap.put("masterName", masterList.get(position).getMasteName());
                            masterMap.put("masterID", masterList.get(position).getMasterID());
                            FBHelper.GetServices().document(getArguments().getString("serviceID")).update(masterMap);
                            SaloonActivity saloonActivity =(SaloonActivity)getContext();
                            saloonActivity.GetBack();
                        }else{
                            Bundle bb = new Bundle();
                            bb.putString("masterName", masterList.get(position).getMasteName());
                            bb.putString("masterID", masterList.get(position).getMasterID());
                            bb.putInt("adapterPosition", getArguments().getInt("adapterPosition"));
                            getTargetFragment().onActivityResult(
                                    getTargetRequestCode(),
                                    Activity.RESULT_OK,
                                    new Intent().putExtras(bb)
                            );
                            AddSaloonActivity addSaloonActivity = (AddSaloonActivity)getContext();
                            addSaloonActivity.fromSaloonRegistration5();
                        }
                        break;
                }
            }
            @Override
            public void onLongClick(View view, final int position) {
            }
        }));
        Helper.ShowProgress(getContext(),"Загружаю мастеров...");
        FBHelper.GetMasters().whereEqualTo("saloonID", saloonID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().isEmpty()){
                        for (DocumentSnapshot doc : task.getResult()){
                            masterList.add(new Master(doc.getId(), doc.getData()));
                        }
                        adapter.notifyDataSetChanged();
                    }
                    Helper.HideProgress();
                }
            }
        });
        // добавление нового мастера
        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText9.getText().length()==0){
                    Toast.makeText(getContext(),"Укажите имя мастера", Toast.LENGTH_SHORT).show();
                }else{
                    // save
                    HashMap<String,Object> masterMap = new HashMap<>();
                    masterMap.put("saloonID",saloonID);
                    masterMap.put("name", editText9.getText().toString());
                    masterMap.put("photo", "");
                    FBHelper.GetMasters().add(masterMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            masterList.add(new Master(task.getResult().getId(),editText9.getText().toString(),"", saloonID));
                            //editText9.setFocusable(false);
                            editText9.setText("");
                            adapter.notifyDataSetChanged();
                            Helper.HideKeyboard(getActivity());
                        }
                    });
                }
            }
        });
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Helper.HideKeyboard(getActivity());
    }
}
