package ru.alfapoint.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SearchView;
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

import ru.alfapoint.R;
import ru.alfapoint.model.InfoClass;
import ru.alfapoint.model.adapters.InfoClassAdapter;
import ru.alfapoint.model.adapters.MasterAdapter2;
import ru.alfapoint.Additional.RecycleTouchListener;
import ru.alfapoint.model.adapters.ServicesAdapter;
import ru.alfapoint.model.adapters.ServicesSingleAdapter;
import ru.alfapoint.model.adapters.SpinnerAdapter2;
import ru.alfapoint.model.interfaces.BoolCallback;
import ru.alfapoint.model.interfaces.DateCallback;
import ru.alfapoint.model.interfaces.InfoClassCallback;
import ru.alfapoint.model.interfaces.ListCallback;
import ru.alfapoint.model.interfaces.UIDCallback;

public class DialogHelper {
    private static AlertDialog alertDialog;
    private static String timeStart, timeStop;
    private static InfoClass infoClass;
    private static Boolean isEdit =false;

    public static void AddNewService(Context ctx, final UIDCallback callback){
        LayoutInflater li = LayoutInflater.from(ctx);
        AlertDialog.Builder b = new AlertDialog.Builder(ctx);
        View promptsView4 = li.inflate(R.layout.dialog2, null);
        b.setView(promptsView4);
        TextView textView = promptsView4.findViewById(R.id.textView26);
        final EditText editText7 = promptsView4.findViewById(R.id.editText7);
        textView.setText("Введите название новой услуги");
        b.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        b.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (editText7.getText().length() != 0){
                    callback.getUID(editText7.getText().toString());
                }
            }
        });
        final AlertDialog a = b.create();
        a.show();
    }

    public static void GetDefaultServices(final Context ctx, final InfoClassCallback callback){
        final AlertDialog.Builder b = new AlertDialog.Builder(ctx);
        View promptsView4 = LayoutInflater.from(ctx).inflate(R.layout.dialog2service, null);
        b.setView(promptsView4);
        RecyclerView recyclerView = promptsView4.findViewById(R.id.recyclerView);
        final List<InfoClass> servicesList = new ArrayList<>();
        final List<InfoClass> tempList = new ArrayList<>();
        final ServicesSingleAdapter adapter = new ServicesSingleAdapter(ctx, servicesList, new InfoClassCallback() {
            @Override
            public void getInfoClass(InfoClass pos) {
                infoClass = pos;
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        b.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        b.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callback.getInfoClass(infoClass);
            }
        });
        FBHelper.GetReference().whereEqualTo("type",1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot doc : task.getResult().getDocuments()){
                    servicesList.add(new InfoClass(doc.get("name").toString(),doc.getId()));
                }
                adapter.notifyDataSetChanged();
                alertDialog = b.create();
                alertDialog.show();
            }
        });
    }

    public static void GetDefaultServicesBySaloon(final Context ctx, final ListCallback callback){
        final AlertDialog.Builder b = new AlertDialog.Builder(ctx);
        View promptsView4 = LayoutInflater.from(ctx).inflate(R.layout.dialog2service, null);
        b.setView(promptsView4);
        RecyclerView recyclerView = promptsView4.findViewById(R.id.recyclerView);
        final List<String> servicesList = new ArrayList<>();
        final List<String> tempList = new ArrayList<>();
        final ServicesAdapter adapter = new ServicesAdapter(ctx, servicesList, new BoolCallback() {
            @Override
            public void checkService(int pos, Boolean isCheck) {
                if(isCheck){
                    tempList.add(servicesList.get(pos));
                }else{
                    tempList.remove(servicesList.get(pos));
                }
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        b.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        b.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callback.getList(tempList);
            }
        });
        FBHelper.GetReference().whereEqualTo("type",1).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot doc : task.getResult().getDocuments()){
                    servicesList.add(doc.get("name").toString());
                }
                adapter.notifyDataSetChanged();
                alertDialog = b.create();
                alertDialog.show();
            }
        });
    }

    public static void SelectionServiceBy(final Context ctx, String saloonID, final InfoClassCallback callback){
        final AlertDialog.Builder b = new AlertDialog.Builder(ctx);
        View promptsView4 = LayoutInflater.from(ctx).inflate(R.layout.dialog2service, null);
        b.setView(promptsView4);
        ImageView ivAdd = promptsView4.findViewById(R.id.imageView8);
        ivAdd.setVisibility(View.GONE);
        RecyclerView recyclerView = promptsView4.findViewById(R.id.recyclerView);
        final List<InfoClass> servicesList = new ArrayList<>();
        final ServicesSingleAdapter adapter = new ServicesSingleAdapter(ctx, servicesList, new InfoClassCallback() {
            @Override
            public void getInfoClass(InfoClass info) {
                infoClass = info;
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        b.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        b.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callback.getInfoClass(infoClass);
            }
        });
        FBHelper.GetServices().whereEqualTo("saloonID", saloonID).whereEqualTo("fullCompleted",true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                final HashMap<String,Object> checkMap = new HashMap<>();
                for(DocumentSnapshot doc : task.getResult().getDocuments()){
                    // удаление дубликатов
                    if(checkMap.get(doc.get("name")) == null){
                        checkMap.put(doc.get("name").toString(),doc.get("name").toString());
                        servicesList.add(new InfoClass(doc.get("name").toString(), doc.getId()));
                    }
                }
                adapter.notifyDataSetChanged();
                if(servicesList.size()==0){
                    Toast.makeText(ctx,"Услуги временно отсутствуют",Toast.LENGTH_SHORT).show();
                }else {
                    alertDialog = b.create();
                    alertDialog.show();
                }
            }
        });
    }

    public static void SelectionMasterBy(final Context ctx, String saloonID, String serviceName, final InfoClassCallback callback){
        final AlertDialog.Builder b = new AlertDialog.Builder(ctx);
        View promptsView4 = LayoutInflater.from(ctx).inflate(R.layout.dialog2service, null);
        b.setView(promptsView4);
        promptsView4.findViewById(R.id.imageView8).setVisibility(View.GONE);
        RecyclerView recyclerView = promptsView4.findViewById(R.id.recyclerView);
        TextView tv = promptsView4.findViewById(R.id.textView29);
        tv.setText("Выбор мастера");
        tv.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-bold.ttf"));
        final List<InfoClass> mastersList = new ArrayList<>();
        final List<InfoClass> tempList = new ArrayList<>();
        final MasterAdapter2 adapter = new MasterAdapter2(mastersList, ctx);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        recyclerView.addOnItemTouchListener(new RecycleTouchListener(ctx, recyclerView, new RecycleTouchListener.RecycleListener() {
            @Override
            public void onClick(View view, int position) {
                switch (position) {
                    default:
                        tempList.clear();
                        tempList.add(mastersList.get(position));
                        break;
                }
            }
            @Override
            public void onLongClick(View view, final int position) {
            }
        }));
        b.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        b.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(tempList.size()!=0)
                    callback.getInfoClass(tempList.get(0));
            }
        });
        FBHelper.GetServices().whereEqualTo("saloonID",saloonID).whereEqualTo("name", serviceName).whereEqualTo("fullCompleted",true)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot doc : task.getResult().getDocuments()){
                    if(doc.get("masterName")!=null && doc.get("cost") != null)
                        mastersList.add(new InfoClass(doc.get("masterName").toString(),
                                doc.get("cost").toString(), doc.get("masterID").toString())); //
                }
                if(mastersList.size()==0){
                    Toast.makeText(ctx, "Данная услуга временно отсутствует!", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }else{
                    adapter.notifyDataSetChanged();
                    alertDialog = b.create();
                    alertDialog.show();
                }

            }
        });
    }

    // выбор даты
    public static void SelectionDateBy(final Context ctx, String saloonID, String masterID, final InfoClassCallback callback){
        final AlertDialog.Builder b = new AlertDialog.Builder(ctx);
        final List<Date> datesLoad = new ArrayList<>();
        final List<String> datesLoadRasp = new ArrayList<>();
        final List<InfoClass> choisenDateList = new ArrayList<>();
        final List<MonthCellDescriptor> selectedCells = new ArrayList<>();
        View promptsView4 = LayoutInflater.from(ctx).inflate(R.layout.fragment_registration6, null);
        b.setView(promptsView4);
        promptsView4.findViewById(R.id.checkBox15).setVisibility(View.INVISIBLE);
        promptsView4.findViewById(R.id.button9).setVisibility(View.GONE);
        final CalendarPickerView calendar_view = promptsView4.findViewById(R.id.calendar_view);
        Calendar calendar = Calendar.getInstance();
        // сбрасываем время в полночь
        final Date today = CalendarPickerView.setMidnightTime(calendar).getTime();
        calendar.add(Calendar.MONTH, 2);
        calendar_view.init(today, calendar.getTime()).inMode(CalendarPickerView.SelectionMode.SINGLE);
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
                            datesLoadRasp.add(doc.get("workTime").toString());
                        }
                    }
                    calendar_view.setTitleTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-bold.ttf"));
                    calendar_view.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-reg.ttf"));
                    calendar_view.highlightDates(datesLoad);
                    alertDialog = b.create();
                    alertDialog.show();
                }else{
                    Toast.makeText(ctx,"Расписание не определено!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        calendar_view.setCellClickInterceptor(new CalendarPickerView.CellClickInterceptor() {
            @Override
            public boolean onCellClicked(Date date, MonthCellDescriptor cellState) {
                selectedCells.add(cellState);
                choisenDateList.clear();
                if(cellState.isHighlighted()){
                    calendar_view.doSelect(cellState, selectedCells);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    for(int i=0; i< datesLoad.size();i++){
                        if(sdf.format(datesLoad.get(i)).equals(sdf.format(date))){
                            choisenDateList.add(new InfoClass(sdf.format(datesLoad.get(i)), datesLoadRasp.get(i), datesLoad.get(i)));
                            break;
                        }
                    }
                }else{
                    Toast.makeText(ctx,"Запись на данную дату невозможна!", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        calendar_view.setOnInvalidDateSelectedListener(new CalendarPickerView.OnInvalidDateSelectedListener() {
            @Override
            public void onInvalidDateSelected(Date date) {
                Toast.makeText(ctx,"Запись на данную дату невозможна!", Toast.LENGTH_SHORT).show();
            }
        });
        b.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        b.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(choisenDateList.size()!=0)
                    callback.getInfoClass(choisenDateList.get(0));
            }
        });
    }

    public static void SelectionCustomDate(final Context ctx, final ListCallback callback){
        final AlertDialog.Builder b = new AlertDialog.Builder(ctx);
        View promptsView4 = LayoutInflater.from(ctx).inflate(R.layout.fragment_registration6, null);
        final HashMap<String,Object> mapa = new HashMap<>();
        final List<MonthCellDescriptor> selectedCells = new ArrayList<>();
        b.setView(promptsView4);
        promptsView4.findViewById(R.id.checkBox15).setVisibility(View.INVISIBLE);
        promptsView4.findViewById(R.id.button9).setVisibility(View.GONE);
        final CalendarPickerView calendar_view = promptsView4.findViewById(R.id.calendar_view);
        final Calendar calendar = Calendar.getInstance();
        final Calendar calendarOld = Calendar.getInstance();
        //final Date today = CalendarPickerView.setMidnightTime(calendar).getTime();  // сбрасываем время в полночь
        calendar.add(Calendar.MONTH, 2);
        calendarOld.add(Calendar.MONTH,-2);
        calendar_view.init(calendarOld.getTime(), calendar.getTime()).inMode(CalendarPickerView.SelectionMode.SINGLE);
        calendar_view.setTitleTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-bold.ttf"));
        calendar_view.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-reg.ttf"));
        calendar_view.setCellClickInterceptor(new CalendarPickerView.CellClickInterceptor() {
            @Override
            public boolean onCellClicked(Date date, MonthCellDescriptor cellState) {
                if(cellState.isSelected()){
                    // do Unselect & remove from FB
                    calendar_view.doUnselect(cellState);
                    mapa.remove(date.toString());
                }else{
                    // select date
                    mapa.put(date.toString(), date);
                    calendar_view.doSelect(cellState);
                }
                return false;
            }
        });
        calendar_view.setOnInvalidDateSelectedListener(new CalendarPickerView.OnInvalidDateSelectedListener() {
            @Override
            public void onInvalidDateSelected(Date date) {
                Toast.makeText(ctx,"Выбрать дату невозможно!", Toast.LENGTH_SHORT).show();
            }
        });
        b.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        b.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(mapa.size() > 0){
                    List<Date> myList = new ArrayList<>();
                    for (Object value : mapa.values()) {
                        myList.add((Date)value);
                    }
                    callback.getList(myList);
                }
            }
        });
        alertDialog = b.create();
        alertDialog.show();
    }
    public static void SelectionCustomRange(final Context ctx, final ListCallback callback){
        final AlertDialog.Builder b = new AlertDialog.Builder(ctx);
        View promptsView4 = LayoutInflater.from(ctx).inflate(R.layout.fragment_registration6, null);
        final List<MonthCellDescriptor> selectedCells = new ArrayList<>();
        b.setView(promptsView4);
        promptsView4.findViewById(R.id.checkBox15).setVisibility(View.INVISIBLE);
        promptsView4.findViewById(R.id.button9).setVisibility(View.GONE);
        final CalendarPickerView calendar_view = promptsView4.findViewById(R.id.calendar_view);
        final Calendar calendar = Calendar.getInstance();
        final Calendar calendarOld = Calendar.getInstance();
        //final Date today = CalendarPickerView.setMidnightTime(calendar).getTime();  // сбрасываем время в полночь
        calendar.add(Calendar.MONTH, 2);
        calendarOld.add(Calendar.MONTH,-2);
        calendar_view.init(calendarOld.getTime(), calendar.getTime()).inMode(CalendarPickerView.SelectionMode.RANGE);
        calendar_view.setTitleTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-bold.ttf"));
        calendar_view.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-reg.ttf"));
        calendar_view.setCellClickInterceptor(new CalendarPickerView.CellClickInterceptor() {
            @Override
            public boolean onCellClicked(Date date, MonthCellDescriptor cellState) {
                calendar_view.doSelectDate(date, cellState);
                if(selectedCells.size()>2){
                    selectedCells.clear();
                    selectedCells.add(cellState);
                }else
                    selectedCells.add(cellState);
                return false;
            }
        });
        calendar_view.setOnInvalidDateSelectedListener(new CalendarPickerView.OnInvalidDateSelectedListener() {
            @Override
            public void onInvalidDateSelected(Date date) {
                Toast.makeText(ctx,"Выбрать дату невозможно!", Toast.LENGTH_SHORT).show();
            }
        });
        b.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        b.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(selectedCells.size() > 1){
                    List<Date> myList = new ArrayList<>();
                    for (MonthCellDescriptor value : selectedCells) {
                        myList.add(value.getDate());
                    }
                    callback.getList(myList);
                }
            }
        });
        alertDialog = b.create();
        alertDialog.show();
    }

    // simple date picker
    public static void SelectionDate(final Context ctx, final DateCallback callback) {
        AlertDialog.Builder b = new AlertDialog.Builder(ctx);
        final DatePicker picker = new DatePicker(ctx);
        picker.setCalendarViewShown(false);
        picker.setBackgroundColor(ctx.getResources().getColor(R.color.colorLightGrey2));
        b.setTitle("Выберите дату");
        b.setView(picker);

        b.setCancelable(false);
        b.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        b.setPositiveButton("Готово", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Date today = new Date();
                //Date tomorrow = null;
                //Calendar calendar1 = Calendar.getInstance();
                //calendar1.add(Calendar.DATE, 2);
                //String stringTomorrow = newFormat.format(calendar1.getTime());
                //try {
                    //tomorrow = newFormat.parse(stringTomorrow);
                //} catch (ParseException e) {
                    //e.printStackTrace();
                //}
                timeStart = Helper.addZero(picker.getDayOfMonth()) + "." + Helper.addZero((picker.getMonth() + 1)) + "." + String.valueOf(picker.getYear());
                //callback.getDate(timeStart);
            }
        });
        AlertDialog a = b.create();
        a.show();
        Button bq = a.getButton(DialogInterface.BUTTON_POSITIVE);
        Button bq1 = a.getButton(DialogInterface.BUTTON_NEGATIVE);
        bq.setTextColor(Helper.getColor(ctx, R.color.colorBlack));
        bq1.setTextColor(Helper.getColor(ctx, R.color.colorBlack));
    }

    public static void SelectionCostBy(final Context ctx, final InfoClassCallback callback) {
        timeStart = "100";
        timeStop="200";
        final AlertDialog.Builder b = new AlertDialog.Builder(ctx);
        View promptsView4 = LayoutInflater.from(ctx).inflate(R.layout.dialog2cost, null);
        b.setView(promptsView4);
        TextView tv = promptsView4.findViewById(R.id.textView74);
        //fonts
        tv.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-bold.ttf"));
        RadioGroup radioGroup1 = promptsView4.findViewById(R.id.radiogroup1);
        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.radioButton3:
                        timeStart = "100";
                        break;
                    case R.id.radioButton5:
                        timeStart = "200";
                        break;
                    case R.id.radioButton7:
                        timeStart = "500";
                        break;
                    case R.id.radioButton9:
                        timeStart = "1000";
                        break;
                    case R.id.radioButton11:
                        timeStart = "1500";
                        break;
                    case R.id.radioButton13:
                        timeStart = "2000";
                        break;
                    case R.id.radioButton15:
                        timeStart = "5000";
                        break;
                }
            }
        });
        RadioGroup radioGroup2 = promptsView4.findViewById(R.id.radiogroup2);
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.radioButton4:
                        timeStop = "200";
                        break;
                    case R.id.radioButton6:
                        timeStop = "500";
                        break;
                    case R.id.radioButton8:
                        timeStop = "1000";
                        break;
                    case R.id.radioButton10:
                        timeStop = "1500";
                        break;
                    case R.id.radioButton12:
                        timeStop = "2000";
                        break;
                    case R.id.radioButton14:
                        timeStop = "5000";
                        break;
                    case R.id.radioButton16:
                        timeStop = "10000";
                        break;
                }
            }
        });
        b.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        b.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callback.getInfoClass(new InfoClass(timeStart, timeStop));
            }
        });
        AlertDialog a = b.create();
        a.show();
    }

    public static void Dialog2(Context ctx, String title, String hint, final UIDCallback callback){
        LayoutInflater li = LayoutInflater.from(ctx);
        AlertDialog.Builder b = new AlertDialog.Builder(ctx);
        View promptsView4 = li.inflate(R.layout.dialog2, null);
        TextView textView26 = promptsView4.findViewById(R.id.textView26);
        final EditText editText7 = promptsView4.findViewById(R.id.editText7);
        editText7.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editText7.setHint(hint);
        textView26.setText(title);
        b.setView(promptsView4);
        b.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        b.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callback.getUID(editText7.getText().toString());
            }
        });
        AlertDialog a = b.create();
        a.show();
    }

    public static void DialogOkNo(Context ctx, String title, String hint, final UIDCallback callback){
        LayoutInflater li = LayoutInflater.from(ctx);
        AlertDialog.Builder b = new AlertDialog.Builder(ctx);
        View promptsView4 = li.inflate(R.layout.dialog2, null);
        TextView textView26 = promptsView4.findViewById(R.id.textView26);
        promptsView4.findViewById(R.id.editText7).setVisibility(View.GONE);
        Spannable wordtoSpan2 = new SpannableString(hint);
        wordtoSpan2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, wordtoSpan2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView26.setText(title + System.getProperty("line.separator"));
        textView26.append(System.getProperty("line.separator") + wordtoSpan2);
        b.setView(promptsView4);
        b.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callback.getUID("NOT OK");
                dialog.cancel();
            }
        });
        b.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callback.getUID("OK");
            }
        });
        AlertDialog a = b.create();
        a.show();
    }
    public static void DialogOk(Context ctx, String title, String hint, final UIDCallback callback){
        LayoutInflater li = LayoutInflater.from(ctx);
        AlertDialog.Builder b = new AlertDialog.Builder(ctx);
        View promptsView4 = li.inflate(R.layout.dialog2, null);
        TextView textView26 = promptsView4.findViewById(R.id.textView26);
        promptsView4.findViewById(R.id.editText7).setVisibility(View.GONE);
        Spannable wordtoSpan2 = new SpannableString(hint);
        wordtoSpan2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, wordtoSpan2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView26.setText(title + System.getProperty("line.separator"));
        textView26.append(System.getProperty("line.separator") + wordtoSpan2);
        b.setView(promptsView4);
        b.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callback.getUID("OK");
            }
        });
        AlertDialog a = b.create();
        a.show();
    }
    public static void GetWorkTime(Context context, final UIDCallback callback){
        final List<String> workTime = GetWorkTime();
        LayoutInflater li = LayoutInflater.from(context);
        final View promptsView = li.inflate(R.layout.dialog2time, null);
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        Spinner spinnerStart = promptsView.findViewById(R.id.textView17);
        Spinner spinnerStop = promptsView.findViewById(R.id.textView18);
        TextView button5 = promptsView.findViewById(R.id.button5);
        TextView button7 = promptsView.findViewById(R.id.button7);
        SpinnerAdapter2 adapter2 = new SpinnerAdapter2(context, R.layout.spinner_item, workTime);
        spinnerStart.setAdapter(adapter2);
        spinnerStop.setAdapter(adapter2);
        spinnerStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                timeStart = workTime.get(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinnerStop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                timeStop = workTime.get(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        b.setView(promptsView);
        final AlertDialog a = b.create();
        a.show();
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a.dismiss();
            }
        });
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.getUID(timeStart + " - " + timeStop);
                a.dismiss();
            }
        });
    }

    public static void GetWorkType(Context context, final UIDCallback callback){
        LayoutInflater li = LayoutInflater.from(context);
        final View promptsView = li.inflate(R.layout.dialog2day, null);
        AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setView(promptsView);
        final CheckBox checkBox = promptsView.findViewById(R.id.checkBox);
        final CheckBox checkBox2 = promptsView.findViewById(R.id.checkBox2);
        final CheckBox checkBox3 = promptsView.findViewById(R.id.checkBox3);
        final CheckBox checkBox4 = promptsView.findViewById(R.id.checkBox4);
        final CheckBox checkBox5 = promptsView.findViewById(R.id.checkBox5);
        final CheckBox checkBox6 = promptsView.findViewById(R.id.checkBox6);
        final CheckBox checkBox7 = promptsView.findViewById(R.id.checkBox7);
        b.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        b.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String workDays = "";
                if(checkBox.isChecked()&&checkBox2.isChecked()&&checkBox3.isChecked()&&checkBox4.isChecked()&&checkBox5.isChecked()&&checkBox6.isChecked()&&checkBox7.isChecked()){
                    workDays = "Ежедневно";
                }else{
                    if(checkBox.isChecked()){
                        workDays="Пн, ";
                    }
                    if(checkBox2.isChecked()){
                        workDays= workDays + "Вт, ";
                    }
                    if(checkBox3.isChecked()){
                        workDays= workDays + "Ср, ";
                    }
                    if(checkBox4.isChecked()){
                        workDays= workDays + "Чт, ";
                    }
                    if(checkBox5.isChecked()){
                        workDays= workDays + "Пт, ";
                    }
                    if(checkBox6.isChecked()){
                        workDays= workDays + "Сб, ";
                    }
                    if(checkBox7.isChecked()){
                        workDays= workDays + "Вс ";
                    }
                }
                callback.getUID(workDays);
                dialog.dismiss();
            }
        });
        AlertDialog a = b.create();
        a.show();
    }

    public static void GetLocationParams(Context context, int s, String hint, final UIDCallback callback){
        isEdit =false;
        final View promptsView = LayoutInflater.from(context).inflate(R.layout.dialog2search, null);
        final AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setView(promptsView);
        final SearchView searchView = promptsView.findViewById(R.id.search3);
        TextView textView97 = promptsView.findViewById(R.id.textView97);
        RecyclerView recyclerView = promptsView.findViewById(R.id.recView);
        textView97.setText(hint);
        final List<InfoClass> list = new ArrayList<>();
        final List<InfoClass> tempList = new ArrayList<>();
        final InfoClassAdapter adapter = new InfoClassAdapter(list);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addOnItemTouchListener(new RecycleTouchListener(context, recyclerView, new RecycleTouchListener.RecycleListener() {
            @Override
            public void onClick(View view, int position) {
                switch (position) {
                    default:
                        if(isEdit){
                            callback.getUID(tempList.get(position).getStr1());
                        }else{
                            callback.getUID(list.get(position).getStr1());
                        }
                        alertDialog.dismiss();
                        break;
                }
            }
            @Override
            public void onLongClick(View view, final int position) {
            }
        }));
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                isEdit = true;
                tempList.clear();
                for(InfoClass d : list){
                    if(d.getStr1().toLowerCase().contains(newText)){
                        tempList.add(d);
                    }
                }
                adapter.update(tempList);
                return false;
            }
        });
        b.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.cancel();
            }
        });

        switch(s){
            case 2: // Страна
                FBHelper.GetReference().whereEqualTo("type",2).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot doc : task.getResult()){
                            if(doc.getData().get("image")!=null){
                                list.add(new InfoClass(doc.getData().get("name").toString(), doc.getData().get("image").toString() ));
                            }else{
                                list.add(new InfoClass(doc.getData().get("name").toString()));
                            }

                        }
                        alertDialog = b.create();
                        alertDialog.show();
                        adapter.notifyDataSetChanged();
                    }
                });
                break;
            case 3: // город
                FBHelper.GetReference().whereEqualTo("type",3).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot doc : task.getResult()){
                            if(doc.getData().get("image")!=null){
                                list.add(new InfoClass(doc.getData().get("name").toString(), doc.getData().get("image").toString() ));
                            }else{
                                list.add(new InfoClass(doc.getData().get("name").toString()));
                            }
                        }
                        alertDialog = b.create();
                        alertDialog.show();
                        adapter.notifyDataSetChanged();
                    }
                });
                break;
        }
    }


    public static List<String> GetWorkTime(){
        List<String> workTime = new ArrayList<>();
        workTime.add("08:00");
        workTime.add("09:00");
        workTime.add("10:00");
        workTime.add("11:00");
        workTime.add("12:00");
        workTime.add("13:00");
        workTime.add("14:00");
        workTime.add("15:00");
        workTime.add("16:00");
        workTime.add("17:00");
        workTime.add("18:00");
        workTime.add("19:00");
        workTime.add("20:00");
        workTime.add("21:00");
        workTime.add("22:00");
        return workTime;
    }

}
