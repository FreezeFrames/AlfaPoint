package ru.alfapoint;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import ru.alfapoint.Additional.TimeChecker;
import ru.alfapoint.model.InfoClass;
import ru.alfapoint.model.SaloonData;
import ru.alfapoint.model.interfaces.InfoClassCallback;
import ru.alfapoint.model.interfaces.UIDCallback;
import ru.alfapoint.utils.DialogHelper;
import ru.alfapoint.utils.FBHelper;
import ru.alfapoint.utils.Helper;

public class EntryBySaloon extends AppCompatActivity {
    Toolbar toolbar;
    String saloonID, entryID,service, serviceID, master,cost,masterID,dateStr,masterRasp,timeStr,clientID;
    Date choisenDateTime,choisenDate;
    SaloonData saloonData;
    ImageView imageView9;
    TextView tv42,tv43,tv44,tv45,tv46,tv47,tv48,tvService,tvMaster, tvDate, tvTime, tvCity;
    Button save;
    Calendar calendar = Calendar.getInstance();
    ConstraintLayout clASD;
    Boolean isNew = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saloon_details);
        Helper.SetStatusBarGrey(this);
        toolbar = findViewById(R.id.toolbar3);
        clASD = findViewById(R.id.clASD);
        imageView9 = findViewById(R.id.imageView10);
        tv42 = findViewById(R.id.textView42);
        tv43 = findViewById(R.id.textView43);
        tv44 = findViewById(R.id.textView44);
        tv45 = findViewById(R.id.textView45);
        tv46 = findViewById(R.id.textView46);
        tv47 = findViewById(R.id.textView47);
        tv48 = findViewById(R.id.textView48);
        tvCity = findViewById(R.id.textView120);
        tvService = findViewById(R.id.textView49);
        tvMaster = findViewById(R.id.textView51);
        tvDate = findViewById(R.id.textView53);
        tvTime = findViewById(R.id.textView54);
        save = findViewById(R.id.button43);

        //fonts
        tv42.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/mon-bold.ttf"));
        tv43.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/mon-reg.ttf"));
        tv44.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/mon-reg.ttf"));
        tv45.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/mon-reg.ttf"));
        tv46.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/mon-reg.ttf"));
        tv47.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/mon-reg.ttf"));
        tv48.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/mon-bold.ttf"));
        tvService.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/mon-reg.ttf"));
        tvMaster.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/mon-reg.ttf"));
        tvDate.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/mon-reg.ttf"));
        tvTime.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/mon-reg.ttf"));
        save.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/mon-reg.ttf"));
        tvCity.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/mon-reg.ttf"));

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (toolbar != null) {
            actionBar.setTitle("");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        saloonID = getIntent().getExtras().getString("saloonID");
        entryID = getIntent().getExtras().getString("entryID");
        clientID = getIntent().getExtras().getString("clientID");
        if( getIntent().getExtras().getBoolean("isNew")){
            // new entry
            save.setText("Добавить");
            isNew = true;
        }else{
            save.setText("Изменить");
        }

        loadSaloonInfo();
        tvService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceSet();
            }
        });
        tvMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(service!=null){
                    masterSet();
                }else{
                    Snackbar.make(clASD, "Выберите услугу", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(service != null){
                    dateSet();
                }else{
                    Snackbar.make(clASD, "Выберите услугу", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(choisenDate != null){
                    timeSet();
                }else{
                    Snackbar.make(clASD, "Выберите дату", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(service!= null && master!=null && dateStr!=null && timeStr!=null){
                    saveEntry();

                }else{
                    Snackbar.make(clASD, "Заполните все поля!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }


    private void saveEntry(){
        calendar.setTime(choisenDate);
        String[] separated = timeStr.split(":");
        calendar.add(Calendar.HOUR, Integer.valueOf(separated[0]));
        calendar.add(Calendar.MINUTE, Integer.valueOf(separated[1]));
        choisenDateTime = calendar.getTime();
        final HashMap<String, Object> entry = new HashMap<>();
        entry.put("saloonID", saloonData.getUID());
        entry.put("saloonName", saloonData.getSaloonName());
        entry.put("saloonAddress", saloonData.getAddress());
        entry.put("city", saloonData.getCity());
        entry.put("saloonPhone", saloonData.getPhone());
        entry.put("day", choisenDateTime);
        entry.put("masterID", masterID);
        entry.put("masterName", master);
        entry.put("parsDate", dateStr);
        entry.put("service", service);
        entry.put("serviceID", serviceID);
        entry.put("time", timeStr);
        entry.put("cost", Long.valueOf(cost));
        entry.put("status", "Заявка одобрена");

        if(isNew){
            Helper.ShowProgress(EntryBySaloon.this,"Отправка...");
            entry.put("appNotification", true);
            entry.put("clientID", "");
            entry.put("clientName", "Клиент");
            entry.put("clientEmail", "Не определен");
            entry.put("clientPhone", "");
            entry.put("phoneNotification", false);
            entry.put("emailNotification", false);
            entry.put("smsNotification", false);
            FBHelper.GetEntries().add(entry).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if(task.isSuccessful()){
                        Helper.HideProgress();
                        finish();
                    }
                }
            });
        }else{
            // проверка времени у клиента
            Helper.ShowProgress(EntryBySaloon.this,"Проверка доступного времени...");
            FBHelper.GetEntries()
                    .whereEqualTo("clientID", clientID)
                    .whereEqualTo("parsDate", dateStr)
                    .whereEqualTo("time", timeStr).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful() & task.getResult()!=null){
                        if (task.getResult().size() > 0) {
                            DialogHelper.DialogOk(EntryBySaloon.this, "Редактирование невозможно!", "Выберите другое время", new UIDCallback() {
                                @Override
                                public void getUID(String string) {
                                    Helper.HideProgress();
                                }
                            });
                        }else{
                            // нет записей у клиента
                            FBHelper.GetEntries().document(entryID).update(entry).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Helper.HideProgress();
                                        finish();
                                    }
                                }
                            });
                        }
                    }
                }
            });


        }
    }
    private void loadSaloonInfo(){
        FBHelper.GetUsers().document(saloonID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists() && task.isSuccessful()){
                    saloonData = new SaloonData(task.getResult().getData(), null);
                    tv42.setText(saloonData.getSaloonName());
                    tv43.setText(saloonData.getDescription());
                    tv44.setText(saloonData.getWorkType());
                    tv45.setText(saloonData.getWorkTime());
                    tv46.setText("Тел: " + saloonData.getPhone());
                    tv47.setText(saloonData.getAddress());
                    tv48.setText(saloonData.getDistance());
                    tvCity.setText(saloonData.getCountry() + ", " + saloonData.getCity());
                    if(saloonData.getSaloonImage().contains("salon")){
                        int imageId = getResources().getIdentifier(saloonData.getSaloonImage(), "drawable", getPackageName());
                        Glide.with(getApplicationContext()).load(imageId).into(imageView9);
                    }else
                        Glide.with(getApplicationContext()).load(saloonData.getSaloonImage()).apply(RequestOptions.circleCropTransform()).into(imageView9);
                }
            }
        });
    }

    private void serviceSet(){
        DialogHelper.SelectionServiceBy(EntryBySaloon.this, saloonID, new InfoClassCallback() {
            @Override
            public void getInfoClass(InfoClass ingo) {
                service = ingo.getStr1();
                serviceID = ingo.getStr2();
                tvService.setText(service);
                if(master != null){
                    clearParams(0);
                }
            }
        });
    }

    private void masterSet(){
        DialogHelper.SelectionMasterBy(EntryBySaloon.this, saloonData.getUID(), service, new InfoClassCallback() {
            @Override
            public void getInfoClass(InfoClass list) {
                master = list.getStr1();
                cost = list.getStr2();
                masterID = list.getStr3();
                clearParams(1);
                tvMaster.setText(master);

            }
        });
    }
    private void dateSet(){
        DialogHelper.SelectionDateBy(EntryBySaloon.this, saloonData.getUID(), masterID, new InfoClassCallback() {
            @Override
            public void getInfoClass(InfoClass pos) {
                dateStr = pos.getStr1();
                masterRasp = pos.getStr2();
                choisenDate = pos.getDate();
                tvDate.setText(dateStr);
                clearParams(2);
            }
        });
    }
    private void timeSet(){
        String title = DateFormat.format("d", choisenDate) + " " + DateFormat.format("MMMM", choisenDate);
        TimeChecker.SelectionTimeBy(true, EntryBySaloon.this, title, masterRasp, dateStr, saloonData.getUID(), masterID, new UIDCallback() {
            @Override
            public void getUID(String string) {
                timeStr = string;
                tvTime.setText(timeStr);
            }
        });
    }

    private void clearParams(int i) {
        switch (i){
            case 0:
                master = null;
                cost = null;
                masterID = null;
                masterRasp = null;
                choisenDate = null;
                dateStr = null;
                timeStr = null;
                tvMaster.setText("Выбор мастера");
                tvDate.setText("Выбор даты");
                tvTime.setText("Выбор времени");
                break;
            case 1:
                choisenDate = null;
                masterRasp = null;
                dateStr = null;
                timeStr = null;
                tvDate.setText("Выбор даты");
                tvTime.setText("Выбор времени");
                break;
            case 2:
                timeStr = null;
                tvTime.setText("Выбор времени");
                break;
        }
    }
}
