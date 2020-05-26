package ru.alfapoint.old;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.alfapoint.R;
import ru.alfapoint.model.Entry;
import ru.alfapoint.model.InfoClass;
import ru.alfapoint.model.adapters.SaloonAdapter2;
import ru.alfapoint.model.interfaces.DateCallback;
import ru.alfapoint.utils.DialogHelper;
import ru.alfapoint.utils.FBHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class SaloonHomeFragment extends Fragment {
    TextView textView40, tvDate, tvSumma;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    SaloonAdapter2 adapter2;
    List<Entry> saloonEntries = new ArrayList<>();
    List<InfoClass> parentList = new ArrayList<>();
    List<InfoClass> childList = new ArrayList<>();
    RecyclerView recyclerView;
    String currentDate, dateDefault;
    long summa =0;
    ImageView arrowright,arrowleft;
    Calendar calendar;
    ImageView imageView16;

    public SaloonHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_saloonhome, container, false);
        textView40 = v.findViewById(R.id.textView40);
        recyclerView = v.findViewById(R.id.rvSaloonEnties);
        tvDate = v.findViewById(R.id.textView83);
        tvSumma = v.findViewById(R.id.textView84);
        arrowright = v.findViewById(R.id.imageView14);
        arrowleft = v.findViewById(R.id.imageView15);
        imageView16 = v.findViewById(R.id.imageView16);
        calendar = Calendar.getInstance();
        dateDefault = DateFormat.format("dd", calendar.getTime()) + "." + DateFormat.format("MM", calendar.getTime()) + "." +
                DateFormat.format("yyyy", calendar.getTime());
        showDate();
        //tvDate.setText(DateFormat.format("d", calendar.getTime()) + " " + DateFormat.format("MMMM", calendar.getTime()));

        //Log.e("currentdate", currentDate);
        adapter2 = new SaloonAdapter2(getContext(),parentList,childList);
        recyclerView.setAdapter(adapter2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        arrowright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveForward(currentDate);
            }
        });
        arrowleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveBack();
            }
        });


        return v;
    }


    private void moveBack(){
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        showDate();
        loadData();
    }

    private void moveForward(String ddate){
        calendar.add(Calendar.DAY_OF_MONTH,1);
        showDate();
        loadData();
    }

    private void showDate(){
        currentDate = DateFormat.format("dd", calendar.getTime()) + "." + DateFormat.format("MM", calendar.getTime()) + "." +
                DateFormat.format("yyyy", calendar.getTime());
        if(dateDefault.equals(currentDate)){
            tvDate.setText("Сегодня");
        }else{
            tvDate.setText(DateFormat.format("d", calendar.getTime()) + " " + DateFormat.format("MMMM", calendar.getTime()));
        }

    }
    private void showDate(DatePicker picker){
        calendar.set(picker.getYear(), picker.getMonth(), picker.getDayOfMonth());
        currentDate = DateFormat.format("dd", calendar.getTime()) + "." + DateFormat.format("MM", calendar.getTime()) + "." +
                DateFormat.format("yyyy", calendar.getTime());
        if(dateDefault.equals(currentDate)){
            tvDate.setText("Сегодня");
        }else{
            tvDate.setText(DateFormat.format("d", calendar.getTime()) + " " + DateFormat.format("MMMM", calendar.getTime()));
        }
        loadData();
    }

    private void loadData(){
        parentList.clear();
        childList.clear();
        saloonEntries.clear();
        summa = 0;
        if(mAuth.getCurrentUser()!=null) {
            FBHelper.GetEntries().whereEqualTo("saloonID", mAuth.getCurrentUser().getUid())
                                 .whereEqualTo("parsDate", currentDate).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.getResult().isEmpty()) { // no entries
                        textView40.setVisibility(View.VISIBLE);
                        tvSumma.setText("ИТОГ: 0 p.");
                    } else {
                        //show entries
                        tvSumma.setText("");
                        for(DocumentSnapshot dpc : task.getResult()){
                            saloonEntries.add(new Entry(dpc.getId(), dpc.getData()));
                        }
                        ChildParentList(saloonEntries);
                        textView40.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }



    private void ChildParentList(List<Entry> saloonEntries){

        HashMap<String, Object> checkMap = new HashMap<>();
        HashMap<String, Object> clientMap = new HashMap<>();
        HashMap<String, Object> clientMap2 = new HashMap<>();

        for(int i=0;i<saloonEntries.size();i++) {
            // удаление дубликатов
            /*if (checkMap.get(saloonEntries.get(i).getMasterName()) == null) {
                checkMap.put(saloonEntries.get(i).getMasterName(), saloonEntries.get(i).getMasterName());
                // numberList, masterName, Cost
                //parentList.add(new InfoClass(saloonEntries.get(i).getMasterName(), saloonEntries.get(i).getCost()));
                clientMap.put(saloonEntries.get(i).getMasterName(), 1);
                clientMap2.put(saloonEntries.get(i).getMasterName(), Long.valueOf(saloonEntries.get(i).getCost().substring(0,saloonEntries.get(i).getCost().indexOf(" "))));
            }else{
                int counter = (Integer) clientMap.get(saloonEntries.get(i).getMasterName())+1;
                clientMap.put(saloonEntries.get(i).getMasterName(),counter );
                long cost = (Long)clientMap2.get(saloonEntries.get(i).getMasterName()) + Long.valueOf(saloonEntries.get(i).getCost().substring(0,saloonEntries.get(i).getCost().indexOf(" ")));
                clientMap2.put(saloonEntries.get(i).getMasterName(),cost);
            }
            // наполнение клиентской части
            childList.add(new InfoClass(saloonEntries.get(i).getPhoneNotification(), saloonEntries.get(i).getMasterName(), saloonEntries.get(i).getTime(),
                    saloonEntries.get(i).getService(), saloonEntries.get(i).getCost()));
            summa = summa + Long.valueOf(saloonEntries.get(i).getCost().substring(0,saloonEntries.get(i).getCost().indexOf(" ")));
            */
        }


        for(Map.Entry<String, Object> entry : clientMap.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();
            for(int j=0; j<parentList.size();j++){
                if(parentList.get(j).getStr1().equals(key)){
                    parentList.get(j).setStr3(value.toString());
                }
            }
        }
        for(Map.Entry<String, Object> entry : clientMap2.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();
            for(int j=0; j<parentList.size();j++){
                if(parentList.get(j).getStr1().equals(key)){
                    parentList.get(j).setStr2(value.toString());
                }
            }
        }

        tvSumma.setText("ИТОГ: " + String.valueOf(summa) +" p.");
        adapter2.updateList(parentList,childList);
    }
    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }


}


/*

private void showDate(Date picker){
        currentDate = new SimpleDateFormat("dd.MM.yyyy").format(picker);
        //loadCustomEntries(currentDate);
        if(dateDefault.equals(currentDate)){
            textView98.setText("Сегодня");
        }else{
            textView98.setText(DateFormat.format("d", picker.getTime()) + " " + DateFormat.format("MMMM", picker.getTime()));
        }
    }

    private void showDate(DatePicker picker){
        calendar.set(picker.getYear(), picker.getMonth(), picker.getDayOfMonth());
        currentDate = DateFormat.format("dd", calendar.getTime()) + "." + DateFormat.format("MM", calendar.getTime()) + "." +
                DateFormat.format("yyyy", calendar.getTime());
        //loadCustomEntries(currentDate);
        if(dateDefault.equals(currentDate)){
            //textView98.setText("Сегодня");
        }else{
            //textView98.setText(DateFormat.format("d", calendar.getTime()) + " " + DateFormat.format("MMMM", calendar.getTime()));
        }
    }



 */