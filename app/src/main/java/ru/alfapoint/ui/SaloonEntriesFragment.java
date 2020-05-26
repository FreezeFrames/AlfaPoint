package ru.alfapoint.ui;


import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import ru.alfapoint.EntryBySaloon;
import ru.alfapoint.R;
import ru.alfapoint.SaloonActivity;
import ru.alfapoint.model.Client;
import ru.alfapoint.model.Entry;
import ru.alfapoint.model.InfoClass;
import ru.alfapoint.model.adapters.SaloonAdapter3;
import ru.alfapoint.model.adapters.SaloonCustomEntriesAdapter;
import ru.alfapoint.model.interfaces.EntryCallbackByOperator;
import ru.alfapoint.model.interfaces.ListCallback;
import ru.alfapoint.utils.DialogHelper;
import ru.alfapoint.utils.FBHelper;
import ru.alfapoint.utils.Helper;


public class SaloonEntriesFragment extends Fragment {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    TabLayout tab_layout;
    ConstraintLayout clSE1, cldate;
    TextView textView98,textView107, tvDay;
    String currentDate;
    Calendar calendar = Calendar.getInstance();
    final String dateDefaultStr = DateFormat.format("dd", calendar.getTime()) + "." + DateFormat.format("MM", calendar.getTime()) + "." +
            DateFormat.format("yyyy", calendar.getTime());
    ImageView imageView;
    SaloonAdapter3 newEntriesAdapter, currentEntriesAdapter;
    SaloonCustomEntriesAdapter customEntryAdapter;
    RecyclerView rvNewEntries, rvCurrent, rvCustom;
    List<Entry> newEntries = new ArrayList();
    List<Entry> currentEntries = new ArrayList();
    List<InfoClass> customEntries = new ArrayList();
    List<Entry> tempEntries = new ArrayList();
    long summ=0;
    ImageView imageAddNewEntry, imageViewAdd, imageViewMinus;
    Toolbar toolbar2;

    public SaloonEntriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_saloonentries, container, false);
        tab_layout = v.findViewById(R.id.tabsLayout);
        clSE1 = v.findViewById(R.id.clSE1);
        cldate = v.findViewById(R.id.cldate);
        textView98 = v.findViewById(R.id.textView98);
        textView107 = v.findViewById(R.id.textView107);
        imageView = v.findViewById(R.id.imageView17);
        rvNewEntries = v.findViewById(R.id.rvNewEntries);
        rvCurrent = v.findViewById(R.id.rvToday);
        tvDay = v.findViewById(R.id.textView83);
        rvCustom = v.findViewById(R.id.rvCustom);
        imageViewAdd = v.findViewById(R.id.imageView14);
        imageViewMinus = v.findViewById(R.id.imageView15);
        toolbar2 = getActivity().findViewById(R.id.toolbar2);
        imageAddNewEntry = toolbar2.findViewById(R.id.imageView25);
        imageAddNewEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), EntryBySaloon.class);
                Bundle bb = new Bundle();
                SaloonActivity saloonActivity = (SaloonActivity)getContext();
                Client client = saloonActivity.getClient();
                bb.putString("saloonID", client.getUid());
                bb.putString("clientID", "none");
                bb.putBoolean("isNew", true);
                i.putExtras(bb);
                startActivity(i);
            }
        });

        //dateDefaultStr =

        Helper.DisableViews(clSE1);
        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==0){
                    Helper.DisableViews(clSE1, rvCurrent,rvCustom, textView107);
                    Helper.EnableViews(rvNewEntries);
                    if(newEntries.size()==0){
                        Helper.EnableViews(textView107);
                        textView107.setText("Нет новых записей!");
                    }
                }else if(tab.getPosition()==1){
                    Helper.EnableViews(clSE1, rvCurrent,cldate);
                    Helper.DisableViews(rvNewEntries,rvCustom,textView107);
                    textView98.setText("Итого: 0 руб." );
                    Glide.with(getActivity()).load(R.drawable.ic_rouble_symbol).into(imageView);
                    textView98.setEnabled(false);
                    showDate(calendar.getTime());
                }else{
                    Helper.EnableViews(clSE1, rvCustom);
                    Helper.DisableViews(rvNewEntries, rvCurrent, textView107, cldate);
                    textView98.setText("Укажите дату:" );
                    textView98.setEnabled(true);
                    Glide.with(getActivity()).load(R.drawable.ic_calendar).into(imageView);
                    customEntries.clear();
                    customEntryAdapter.notifyDataSetChanged();
                    /*DialogHelper.SelectionCustomDate(getContext(), new ListCallback() {
                        @Override
                        public void getList(List<?> pos) {
                            loadCustomEntries((List<Date>)pos);
                        }
                    });*/
                    DialogHelper.SelectionCustomRange(getContext(), new ListCallback() {
                        @Override
                        public void getList(List<?> pos) {
                            loadCustomEntries((List<Date>) pos);
                        }
                    });
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // новые записи
        newEntriesAdapter = new SaloonAdapter3(newEntries, getContext(), new EntryCallbackByOperator() {
            @Override
            public void onEdit(int pos) {
                Intent i = new Intent(getContext(), EntryBySaloon.class);
                Bundle bb = new Bundle();
                bb.putString("saloonID", newEntries.get(pos).getSaloonID());
                bb.putString("entryID", newEntries.get(pos).getEntryID());
                bb.putString("clientID", newEntries.get(pos).getClientID());
                i.putExtras(bb);
                startActivity(i);
            }
            @Override
            public void onDelete(long pos) {
                Toast.makeText(getContext(),"Заявка отклонена!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onAccept(long pos, Boolean check) {
                Toast.makeText(getContext(),"Заявка подтверждена!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onPhoneCall(int pos) {
                //запрашиваем разрешения
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},1);
                onPhone(pos,newEntries);
            }
        });
        rvNewEntries.setAdapter(newEntriesAdapter);
        rvNewEntries.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNewEntries.setHasFixedSize(true);

        // записи на сегодня
        currentEntriesAdapter = new SaloonAdapter3(currentEntries, getContext(), new EntryCallbackByOperator() {
            @Override
            public void onEdit(int pos) {
                Intent i = new Intent(getContext(), EntryBySaloon.class);
                Bundle bb = new Bundle();
                bb.putString("saloonID", currentEntries.get(pos).getSaloonID());
                bb.putString("entryID", currentEntries.get(pos).getEntryID());
                bb.putString("clientID", currentEntries.get(pos).getClientID());
                bb.putBoolean("isNew",false);
                i.putExtras(bb);
                startActivity(i);
            }
            @Override
            public void onDelete(long pos) {
                summ = summ - pos;
                textView98.setText("Итого: " + String.valueOf(summ) + " p.");
            }
            @Override
            public void onAccept(long pos, Boolean check) {
                if(check){
                    summ = summ + pos;
                }else{
                    summ = summ - pos;
                }
                textView98.setText("Итого: " + String.valueOf(summ) + " p.");
            }

            @Override
            public void onPhoneCall(int pos) {
                //запрашиваем разрешения
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},1);
                onPhone(pos, currentEntries);
            }
        });

        rvCurrent.setAdapter(currentEntriesAdapter);
        rvCurrent.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCurrent.setHasFixedSize(true);

        // записи из журнала
        customEntryAdapter = new SaloonCustomEntriesAdapter(getContext(), customEntries);

        rvCustom.setAdapter(customEntryAdapter);
        rvCustom.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCustom.setHasFixedSize(true);


        // слушаем клики на журнале
        textView98.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.SelectionCustomRange(getContext(), new ListCallback() {
                    @Override
                    public void getList(List<?> pos) {
                        loadCustomEntries((List<Date>)pos);
                    }
                });
            }
        });
        // записи на новый день
        imageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DAY_OF_MONTH,1);
                showDate(calendar.getTime());
            }
        });
        imageViewMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DAY_OF_MONTH,-1);
                showDate(calendar.getTime());
            }
        });
        loadNewEntries();

        return v;
    }

    private void loadNewEntries() {
        FBHelper.GetEntries()
                .whereEqualTo("saloonID", mAuth.getCurrentUser().getUid())
                .whereEqualTo("status", "Заявка в обработке").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                newEntries.clear();
                if(queryDocumentSnapshots.isEmpty()){
                    Helper.EnableViews(textView107);
                    textView107.setText("Нет новых записей!");
                }else {
                    for(DocumentSnapshot doc : queryDocumentSnapshots){
                        newEntries.add(new Entry(doc.getId(), doc.getData()));
                    }
                    Collections.sort(newEntries, new Comparator<Entry>() {
                        @Override
                        public int compare(Entry o1, Entry o2) {
                            return o1.getDay().compareTo(o2.getDay());
                        }
                    });
                    newEntriesAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void loadCurrentEntries(String dateStr){
        currentEntries.clear();
        summ=0;
        FBHelper.GetEntries()
                .whereEqualTo("saloonID", mAuth.getCurrentUser().getUid())
                .whereEqualTo("parsDate", dateStr).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().isEmpty()){
                    Helper.EnableViews(textView107);
                    textView107.setText("Нет записей");
                }else {
                    for(DocumentSnapshot doc : task.getResult()){
                        currentEntries.add(new Entry(doc.getId(), doc.getData()));
                        if(doc.get("status").toString().equals("Заявка одобрена")){
                            summ = summ + (long)doc.get("cost");
                        }
                    }
                    textView98.setText("Итого: " + String.valueOf(summ) + " p.");
                    Collections.sort(currentEntries, new Comparator<Entry>() {
                        @Override
                        public int compare(Entry o1, Entry o2) {
                            return o1.getDay().compareTo(o2.getDay());
                        }
                    });
                    currentEntriesAdapter.notifyDataSetChanged();
                    Helper.DisableViews(textView107);
                }
            }
        });
    }

    private void showDate(Date picker){
        currentDate = new SimpleDateFormat("dd.MM.yyyy").format(picker);
        if(dateDefaultStr.equals(currentDate)){
            tvDay.setText("Сегодня");
        }else{
            tvDay.setText(DateFormat.format("d", picker.getTime()) + " " + DateFormat.format("MMMM", picker.getTime()));
        }
        loadCurrentEntries(currentDate);
    }


    private void loadCustomEntries(List<Date> list){
        customEntries.clear();
        tempEntries.clear();
        if(list.size() == 1){ // если выборка для 1 даты
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            FBHelper.GetEntries()
                    .whereEqualTo("saloonID", mAuth.getCurrentUser().getUid())
                    .whereEqualTo("parsDate", sdf.format(list.get(0))).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.getResult().isEmpty()){
                        Helper.EnableViews(textView107);
                        textView107.setText("Нет записей");
                    }else {
                        textView107.setVisibility(View.GONE);
                        for(DocumentSnapshot doc : task.getResult()){
                            tempEntries.add(new Entry(doc.getId(), doc.getData()));
                        }
                        checkerMap(tempEntries);
                    }
                }
            });
        }else{
            // если диапазон дат
            Calendar c = Calendar.getInstance();
            c.setTime(Collections.max(list));
            c.add(Calendar.DATE,1);
            FBHelper.GetEntries()
                    .whereEqualTo("saloonID", mAuth.getCurrentUser().getUid())
                    .whereGreaterThan("day", Collections.min(list))
                    .whereLessThan("day", c.getTime()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.getResult().isEmpty()){
                        Helper.EnableViews(textView107);
                        textView107.setText("Нет записей");
                    }else {
                        textView107.setVisibility(View.GONE);
                        for(DocumentSnapshot doc : task.getResult()){
                            tempEntries.add(new Entry(doc.getId(), doc.getData()));
                        }
                        checkerMap(tempEntries);
                    }
                }
            });
        }
    }

    private void checkerMap(List<Entry> entryList){
        HashMap<String, Object> checkMap = new HashMap<>();
        HashMap<String, Object> counterMap = new HashMap<>();

        for(int i=0;i<entryList.size();i++) {
            if (checkMap.get(entryList.get(i).getMasterName()) == null) {
                // проверка статуса заявки
                if(entryList.get(i).getStatus().equals("Заявка одобрена")){
                    checkMap.put(entryList.get(i).getMasterName(), entryList.get(i).getCost());
                    counterMap.put(entryList.get(i).getMasterName(), 1);
                }else{
                    checkMap.put(entryList.get(i).getMasterName(), Long.valueOf(0));
                    counterMap.put(entryList.get(i).getMasterName(), 0);
                }
            } else {
                if(entryList.get(i).getStatus().equals("Заявка одобрена")){
                    int counter = (Integer) counterMap.get(entryList.get(i).getMasterName()) + 1;
                    counterMap.put(entryList.get(i).getMasterName(), counter);
                    long cost = (Long) checkMap.get(entryList.get(i).getMasterName()) + Long.valueOf(entryList.get(i).getCost());
                    checkMap.put(entryList.get(i).getMasterName(), cost);
                }
            }
        }
        for(Map.Entry<String, Object> entry : checkMap.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();
            customEntries.add(new InfoClass(key, value.toString(), counterMap.get(key).toString()));
        }
        customEntryAdapter.notifyDataSetChanged();
    }

    private void onPhone(int pos, List<Entry> list){
        if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.CALL_PHONE)==-1){
            Toast.makeText(getContext(), "Разрешение на звонок не получено!", Toast.LENGTH_SHORT).show();
        }else{
            startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel: +7" + list.get(pos).getClientPhone())));
        }
    }
}


