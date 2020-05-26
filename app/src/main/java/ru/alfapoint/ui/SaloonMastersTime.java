package ru.alfapoint.ui;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.MonthCellDescriptor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.alfapoint.AddSaloonActivity;
import ru.alfapoint.R;
import ru.alfapoint.SaloonActivity;
import ru.alfapoint.model.TimeSheet;
import ru.alfapoint.model.adapters.SpinnerAdapter2;
import ru.alfapoint.model.interfaces.UIDCallback;
import ru.alfapoint.utils.DialogHelper;
import ru.alfapoint.utils.FBHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class SaloonMastersTime extends Fragment {
    CheckBox checkBox15;
    CalendarPickerView calendar_view ;
    List<TimeSheet> dates = new ArrayList<>();
    List<Date> datesLoad = new ArrayList<>();
    String masterID, saloonID, serviceID, workTime, saloonWorkType, timeStart, timeStop;
    Button button9;
    HashMap<String,Object> mapa = new HashMap<>();
    //String defaultRasp;
    ConstraintLayout clFR6;
    int vers =0;
    Calendar calendar = Calendar.getInstance();
    //CalendarPickerView.CellClickInterceptor cellClickInterceptor;

    public SaloonMastersTime() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_registration6, container, false);
        checkBox15 = v.findViewById(R.id.checkBox15);
        checkBox15.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        clFR6 = v.findViewById(R.id.clFR6);
        calendar_view = v.findViewById(R.id.calendar_view);
        button9 = v.findViewById(R.id.button9);
        saloonID = getArguments().getString("docID");
        masterID = getArguments().getString("masterID");
        workTime = getArguments().getString("workTime");
        saloonWorkType = getArguments().getString("workType").toLowerCase();

        final List<String> workTimeList = DialogHelper.GetWorkTime();
        Spinner spinnerStart = v.findViewById(R.id.textView116);
        Spinner spinnerStop = v.findViewById(R.id.textView117);
        SpinnerAdapter2 adapter2 = new SpinnerAdapter2(getContext(), R.layout.spinner_item, workTimeList);
        spinnerStart.setAdapter(adapter2);
        spinnerStop.setAdapter(adapter2);
        spinnerStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                timeStart = workTimeList.get(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinnerStop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                timeStop = workTimeList.get(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        if(getArguments().getInt("version")!=0){
            vers = getArguments().getInt("version");
        }
        final Date today = (CalendarPickerView.setMidnightTime(calendar).getTime());
        calendar.add(Calendar.MONTH, 2);
        Locale rusLocale = new Locale("ru","RU");
        calendar_view.init(today, calendar.getTime(), rusLocale).inMode(CalendarPickerView.SelectionMode.MULTIPLE);
        calendar_view.setTitleTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-bold.ttf"));
        calendar_view.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        FBHelper.GetDaySheet()
                .whereEqualTo("masterID", masterID)
                .whereEqualTo("saloonID", saloonID)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(!task.getResult().isEmpty()){
                    for(DocumentSnapshot doc: task.getResult()){
                        Date day = (Date)doc.get("day");
                        if(day.after(today) | day.equals(today)){
                            datesLoad.add((Date)doc.get("day"));
                            HashMap<String, Object> mMap = new HashMap<>();
                            mMap.put("date", (Date)doc.get("day"));
                            mMap.put("workTime", workTime);
                            mapa.put(doc.get("day").toString(), mMap);
                        }
                    }
                    calendar_view.selectDates(datesLoad);
                }
            }
        });
        calendar_view.setCellClickInterceptor(new CalendarPickerView.CellClickInterceptor() {
            @Override
            public boolean onCellClicked(Date date, MonthCellDescriptor cellState) {
                if(cellState.isSelected()){
                    // do Unselect & remove from FB
                    calendar_view.doUnselect(cellState);
                    mapa.remove(date.toString());
                    removeMastersRasp(date);
                }else{
                    // select date
                    if(checkWortType(date)){
                        HashMap<String, Object> mMap = new HashMap<>();
                        mMap.put("date", date);
                        mMap.put("workTime", workTime);
                        mapa.put(date.toString(), mMap);
                        calendar_view.doSelect(cellState);
                    }else {
                        Toast.makeText(getContext(),"Вне расписания салона!", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        calendar_view.setOnInvalidDateSelectedListener(new CalendarPickerView.OnInvalidDateSelectedListener() {
            @Override
            public void onInvalidDateSelected(Date date) {
                Toast.makeText(getContext(),"Неверная дата!", Toast.LENGTH_SHORT).show();
            }
        });

        checkBox15.setText("Особое расписание");
        checkBox15.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    clFR6.setVisibility(View.VISIBLE);
                }else{
                    clFR6.setVisibility(View.GONE);
                }
            }
        });

        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dates.clear();
                if(checkBox15.isChecked()){
                    if(timeStop.equals("08:00")){
                        Toast.makeText(getContext(),"Укажите окончание рабочего дня!", Toast.LENGTH_SHORT).show();
                    }else{
                        // особое расписание
                        for (Map.Entry<String, Object> entry : mapa.entrySet()) {
                            Map<String, Object> level2 = (Map<String, Object>) entry.getValue();
                            HashMap<String,Object> map = new HashMap<>();
                            map.put("day", level2.get("date"));
                            map.put("saloonID", saloonID);
                            map.put("masterID", masterID);
                            map.put("workTime", timeStart +" - " + timeStop);
                            FBHelper.GetDaySheet().add(map);
                        }
                    }
                }else{
                    // обычное расписание
                    for (Map.Entry<String, Object> entry : mapa.entrySet()) {
                        Map<String, Object> level2 = (Map<String, Object>) entry.getValue();
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("day", level2.get("date"));
                        map.put("saloonID", saloonID);
                        map.put("masterID", masterID);
                        map.put("workTime", level2.get("workTime").toString());
                        FBHelper.GetDaySheet().add(map);
                    }
                }
                if(vers==2){
                    SaloonActivity saloonActivity =(SaloonActivity)getContext();
                    saloonActivity.GetBackFromCalendar();
                }else{
                    AddSaloonActivity addSaloonActivity = (AddSaloonActivity)getContext();
                    addSaloonActivity.fromSaloonRegistration5();
                }
            }
        });

        return v;
    }

    // проверяем расписание с салоном
    private Boolean checkWortType(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("EE", new Locale("ru","RU"));
        //Log.e("saloWT", saloonWorkType);
        //Log.e("date", sdf.format(date).toLowerCase());
        if(saloonWorkType.equals("ежедневно") | saloonWorkType.contains(sdf.format(date).toLowerCase())){
            return true;
        }else{
            return false;
        }
    }

    private void removeMastersRasp(Date day){
            FBHelper.GetDaySheet()
                    .whereEqualTo("day", day)
                    .whereEqualTo("saloonID", saloonID)
                    .whereEqualTo("masterID", masterID)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(int i=0; i<task.getResult().getDocuments().size();i++){
                        FBHelper.GetDaySheet().document(task.getResult().getDocuments().get(i).getId()).delete();
                    }
                }
            });

    }

    /*private void laodInfo(){
        FBHelper.GetUsers().document(saloonID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().get("workTime")!=null){
                    saloonRasp = task.getResult().get("workTime").toString();
                    //checkBox15.setText("Установить время работы " + saloonRasp);
                }
                if(task.getResult().get("workType")!=null){
                    saloonWorkType = task.getResult().get("workType").toString().toLowerCase();
                }
            }
        });
    }

    else{
                        if (checkBox15.isChecked() && defaultRasp != null){
                            HashMap<String, Object> mMap = new HashMap<>();
                            mMap.put("date", date);
                            mMap.put("workTime", defaultRasp);
                            mapa.put(date.toString(), mMap);
                        }else{
                            DialogHelper.GetWorkTime(getContext(), new UIDCallback() {
                                @Override
                                public void getUID(String string) {
                                    defaultRasp = string;
                                    HashMap<String, Object> mMap = new HashMap<>();
                                    mMap.put("date", date);
                                    mMap.put("workTime", string);
                                    mapa.put(date.toString(), mMap);
                                }
                            });
                        }
                    }


    */


}
