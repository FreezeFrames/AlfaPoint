package ru.alfapoint;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import ru.alfapoint.Additional.TimeChecker;
import ru.alfapoint.model.Client;
import ru.alfapoint.model.InfoClass;
import ru.alfapoint.model.SaloonData;
import ru.alfapoint.model.interfaces.InfoClassCallback;
import ru.alfapoint.model.interfaces.UIDCallback;
import ru.alfapoint.utils.DialogHelper;
import ru.alfapoint.utils.FBHelper;
import ru.alfapoint.utils.Helper;

public class EntryByClient extends AppCompatActivity {
    Toolbar toolbar;
    ImageView imageView9;
    TextView tv42,tv43,tv44,tv45,tv46,tv47, tv48, tvService,tvMaster, tvDate,tvTime,tv_toolbarASD, tvCity;
    TextView tvSalonName, tvAdr, tvFulldate, tvServ, tvCost;
    String service, serviceID, master, cost, masterID, masterRasp, dateStr, timeStr, token, saloonID, entryID;
    CheckBox calendarCheckbox, phoneCheckbox, appNotificationCheckbox, emailCheckbox, smsCheckbox;
    ConstraintLayout clASD;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Date choisenDate,choisenDateTime;
    Button next, sendBtn;
    Client client;
    ConstraintLayout clConfirm;
    Boolean phoneNotification = false, appNotification = false, isEdited=false, smsNotification = false, emailNotification=false;
    Calendar calendar = Calendar.getInstance();
    SaloonData saloonData;
    Intent returnIntent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saloon_details);
        Helper.SetStatusBarGrey(this);
        toolbar = findViewById(R.id.toolbar3);
        imageView9 = findViewById(R.id.imageView10);
        tv42 = findViewById(R.id.textView42);
        tv43 = findViewById(R.id.textView43);
        tv44 = findViewById(R.id.textView44);
        tv45 = findViewById(R.id.textView45);
        tv46 = findViewById(R.id.textView46);
        tv47 = findViewById(R.id.textView47);
        tv48 = findViewById(R.id.textView48);
        tv_toolbarASD = findViewById(R.id.tv_toolbarASD);
        tvService = findViewById(R.id.textView49);
        tvServ = findViewById(R.id.textView66);
        tvMaster = findViewById(R.id.textView51);
        tvDate = findViewById(R.id.textView53);
        tvTime = findViewById(R.id.textView54);
        tvCity = findViewById(R.id.textView120);
        tvSalonName = findViewById(R.id.textView60);
        tvCost = findViewById(R.id.textView68);
        tvAdr = findViewById(R.id.textView62);
        tvFulldate = findViewById(R.id.textView64);
        clASD = findViewById(R.id.clASD);
        next = findViewById(R.id.button43);
        sendBtn = findViewById(R.id.button44);
        smsCheckbox = findViewById(R.id.checkBox10);
        calendarCheckbox = findViewById(R.id.checkBox12);
        emailCheckbox = findViewById(R.id.checkBox11);
        phoneCheckbox = findViewById(R.id.checkBox13);
        appNotificationCheckbox = findViewById(R.id.checkBox14);
        clConfirm = findViewById(R.id.clConfirm);
        clConfirm.setVisibility(View.GONE);
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
        next.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/mon-reg.ttf"));

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
        if(mAuth.getCurrentUser() != null) {
            FBHelper.GetUsers().document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() && task.isComplete()){
                        client = new Client(task.getResult().getData());
                    }
                }
            });
        }else{
            Snackbar.make(clASD, "Вы не зарегистрированы!", Snackbar.LENGTH_LONG).show();
        }
        saloonID = getIntent().getExtras().getString("saloonID");
        isEdited = getIntent().getExtras().getBoolean("edited");
        loadSaloonInfo();

        if(getIntent().getExtras().getInt("from") == 1){
            // пришли с главного экрана
            fromUserHome();
        }else{
            fromSearch();
        }


        tvMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(service!=null){
                    masterSet();
                }else{
                    Snackbar.make(clASD, "Выберите услугу", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(service != null){
                    dateSet();
                }else{
                    Snackbar.make(clASD, "Выберите услугу", Snackbar.LENGTH_LONG).show();
                }

            }
        });
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(choisenDate != null){
                    timeSet();
                }else{
                    Snackbar.make(clASD, "Выберите дату", Snackbar.LENGTH_LONG).show();
                }

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(client == null) {
                    Snackbar.make(clASD, "Вы не зарегистрированы!", Snackbar.LENGTH_LONG).show();
                }else{
                    if(service != null && master!= null && choisenDate!=null && timeStr!=null)
                        showDetails();
                    else {
                        Snackbar.make(clASD, "Заполните все поля!", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEntry();
            }
        });
        calendarCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    Helper.AddtoCalendar(EntryByClient.this, timeStr, saloonData.getSaloonName(), choisenDate,
                            saloonData.getCity() + ", " + saloonData.getAddress());
            }
        });
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token = instanceIdResult.getToken();
            }
        });
    }

    private void fromUserHome(){
        tvService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceSet();
            }
        });
    }

    private void fromSearch(){
        tvService.setText(getIntent().getExtras().getString("service"));
        service = getIntent().getExtras().getString("service");
        serviceID = getIntent().getExtras().getString("serviceID");
        entryID = getIntent().getExtras().getString("entryID");
    }

    // тображение выбранных параметров перед записью
    private void showDetails(){
        emailCheckbox.setChecked(true);
        appNotificationCheckbox.setChecked(true);
        Helper.DisableViews(imageView9,tv42,tv43,tv44,tv45,tv46,tv47,tv48,tvDate,tvMaster,tvTime,tvService, next, tvCity);
        Helper.EnableViews(clConfirm, tv_toolbarASD);
        tv_toolbarASD.setText("Мои записи");
        tvSalonName.setText(saloonData.getSaloonName());
        if(saloonData.getCity().equals("")){
            tvAdr.setText(saloonData.getAddress());
        }else
            tvAdr.setText(saloonData.getCity() + ", " + saloonData.getAddress());

        String fullDate = DateFormat.format("dd", choisenDate) + " " + DateFormat.format("MMMM", choisenDate)
                + " " + DateFormat.format("yyyy", choisenDate);
        tvServ.setText(service);
        tvCost.setText(cost + " р.");
        tvFulldate.setText(fullDate);
    }

    private void hideDetails(){
        Helper.EnableViews(imageView9,tv42,tv43,tv44,tv45,tv46,tv47,tv48,tvDate,tvMaster,tvTime,tvService, next, tvCity);
        Helper.DisableViews(clConfirm, tv_toolbarASD);
    }

    // отправить заявку
    private void sendEntry(){
        // проверяем если ли записи на это время у клиента
        FBHelper.GetEntries()
            .whereEqualTo("clientID", mAuth.getCurrentUser().getUid())
            .whereEqualTo("parsDate", dateStr)
            .whereEqualTo("time", timeStr).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
             if(task.isSuccessful()){
                 if(task.getResult()!=null){
                     if(task.getResult().size()>0){
                         DialogHelper.DialogOkNo(EntryByClient.this, "Невозможно записаться!", "Выберите другое время", new UIDCallback() {
                             @Override
                             public void getUID(String string) {
                             if(string.equals("OK")){
                                 hideDetails();
                             }else{
                                 setResult(Activity.RESULT_CANCELED, returnIntent);
                                 finish();
                             }
                             }
                         });
                     }else{
                         loadEntry();
                     }
                 }
             }
            }
        });



    }

    private void loadEntry(){
        if(phoneCheckbox.isChecked()){
            phoneNotification = true;
        }
        if(appNotificationCheckbox.isChecked()){
            appNotification = true;
        }
        if(smsCheckbox.isChecked()){
            smsNotification = true;
        }
        if(emailCheckbox.isChecked()){
            emailNotification = true;
        }
        // дата + время
        calendar.setTime(choisenDate);
        String[] separated = timeStr.split(":");
        calendar.add(Calendar.HOUR, Integer.valueOf(separated[0]));
        calendar.add(Calendar.MINUTE, Integer.valueOf(separated[1]));
        choisenDateTime = calendar.getTime();
        //сохранение заявки
        HashMap<String, Object> entry = new HashMap<>();
        entry.put("clientID", client.getUid());
        entry.put("clientName", client.getName());
        entry.put("clientPhone", client.getPhone());
        entry.put("clientEmail", client.getEmail());
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
        entry.put("status", "Заявка в обработке");
        if(token != null)
            entry.put("token", token);
        else
            entry.put("token", client.getToken());
        entry.put("phoneNotification", phoneNotification);
        entry.put("appNotification", appNotification);
        entry.put("smsNotification", smsNotification);
        entry.put("emailNotification", emailNotification);

        returnIntent.putExtra("result", "OK");
        Helper.ShowProgress(EntryByClient.this,"Отправка...");
        if(isEdited){
            FBHelper.GetEntries().document(entryID).update(entry).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Helper.HideProgress();
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                    }
                }
            });
        }else{
            FBHelper.GetEntries().add(entry).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if(task.isSuccessful()){
                        Helper.HideProgress();
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                    }
                }
            });
        }
    }

    private void serviceSet(){
        DialogHelper.SelectionServiceBy(EntryByClient.this, saloonData.getUID(), new InfoClassCallback() {
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
        DialogHelper.SelectionMasterBy(EntryByClient.this, saloonData.getUID(), service, new InfoClassCallback() {
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
        DialogHelper.SelectionDateBy(EntryByClient.this, saloonData.getUID(), masterID, new InfoClassCallback() {
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
        TimeChecker.SelectionTimeBy(false, EntryByClient.this, title, masterRasp, dateStr, saloonData.getUID(), masterID, new UIDCallback() {
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

    private void loadSaloonInfo(){
        Helper.ShowProgress(EntryByClient.this, "Информация загружается...");
        FBHelper.GetUsers().document(saloonID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists() && task.isSuccessful()){
                    if(client==null){
                        saloonData = new SaloonData(task.getResult().getData(), null);
                    }else
                        saloonData = new SaloonData(task.getResult().getData(), client.getLatLng());
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
                    Helper.HideProgress();
                }
            }
        });
    }
}
