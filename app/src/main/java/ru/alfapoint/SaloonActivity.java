package ru.alfapoint;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import ru.alfapoint.model.Client;
import ru.alfapoint.model.ClientObserver;
import ru.alfapoint.utils.FBHelper;
import ru.alfapoint.utils.Helper;

public class SaloonActivity extends AppCompatActivity {
    NavHostFragment navHostSaloonFragment;
    BottomNavigationView bottomNavigationView;
    Client client;
    TextView tv_toolbar;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Toolbar toolbar2;
    ActionBar actionBar;
    ImageView addNewentry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saloon);
        Helper.SetStatusBarGrey(this);
        //findViewById(R.id.imageView2).setVisibility(View.GONE);
        navHostSaloonFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostSaloonFragment);
        bottomNavigationView = findViewById(R.id.navigationSaloon);
        tv_toolbar = findViewById(R.id.tv_toolbar);
        toolbar2 = findViewById(R.id.toolbar2);
        addNewentry = findViewById(R.id.imageView25);
        setSupportActionBar(toolbar2);
        actionBar = getSupportActionBar();

        tv_toolbar.setText("График салона");
        NavigationUI.setupWithNavController(bottomNavigationView, navHostSaloonFragment.getNavController());
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                setToolbarAsHomeEnabled(false);
                switch (item.getItemId()) {
                    case R.id.menu_saloonhome:
                        navHostSaloonFragment.getNavController().navigate(R.id.navigation_saloonhome);
                        tv_toolbar.setText("График салона");
                        Glide.with(getApplicationContext()).load(R.drawable.ic_plus).into(addNewentry);
                        addNewentry.setVisibility(View.VISIBLE);
                        return true;
                    case R.id.menu_saloon2:
                        navHostSaloonFragment.getNavController().navigate(R.id.navigation_saloonmasters);
                        tv_toolbar.setText("Мастера");
                        addNewentry.setVisibility(View.GONE);
                        return true;
                    case R.id.menu_saloon3:
                        navHostSaloonFragment.getNavController().navigate(R.id.navigation_saloonprofile);
                        tv_toolbar.setText("Профиль");
                        addNewentry.setVisibility(View.GONE);
                        return true;
                    case R.id.menu_saloon4:
                        tv_toolbar.setText("Услуги");
                        addNewentry.setVisibility(View.GONE);
                        navHostSaloonFragment.getNavController().navigate(R.id.navigation_saloonservices);
                        return true;
                }
                return false;
            }
        });

    }

    public Client getClient(){
        return client;
    }

    public void EditService(String serviceName, String salonID){
        tv_toolbar.setText("Редактирование");
        Bundle b = new Bundle();
        b.putString("serviceName",serviceName);
        b.putString("saloonID",salonID);
        setToolbarAsHomeEnabled(true);
        navHostSaloonFragment.getNavController().navigate(R.id.SaloonServicesFragment2,b);
    }
    public void ChooseMaster(String salonID, String serviceId){
        tv_toolbar.setText("Редактирование");
        Bundle b = new Bundle();
        //b.putString("serviceName",serviceName);
        b.putString("docID",salonID);
        b.putString("serviceID", serviceId);
        b.putInt("version", 2);
        setToolbarAsHomeEnabled(true);
        navHostSaloonFragment.getNavController().navigate(R.id.saloonRegistrationFragment5,b);
    }

    public void ChooseRasp(String masterID, String materName, String salonID){
        Bundle b = new Bundle();
        tv_toolbar.setText("Календарь");
        b.putString("docID", salonID);
        b.putString("masterName", materName);
        b.putString("masterID", masterID);
        b.putString("workTime", client.getWorkTime());
        b.putString("workType", client.getWorkType());
        b.putInt("version", 2);
        setToolbarAsHomeEnabled(true);
        navHostSaloonFragment.getNavController().navigate(R.id.saloonRegistrationFragment6, b);
    }
    private void setToolbarAsHomeEnabled(Boolean switcher){
        if(switcher){

            actionBar.setDisplayHomeAsUpEnabled(true);
            if (toolbar2 != null) {
                toolbar2.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fmWorking();
                    }
                });
            }
        }else{
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    public void GetBack(){
        navHostSaloonFragment.getNavController().navigateUp();
    }
    public void GetBackFromCalendar(){
        navHostSaloonFragment.getNavController().navigateUp();
        tv_toolbar.setText("Мастера");
        setToolbarAsHomeEnabled(false);
    }
    private void fmWorking(){
        if(navHostSaloonFragment.getNavController().getCurrentDestination().getLabel().toString().equals("SaloonServicesFragment2")){
            setToolbarAsHomeEnabled(false);
            tv_toolbar.setText("Услуги");
        }else if(navHostSaloonFragment.getNavController().getCurrentDestination().getLabel().toString().equals("fragment_registration6")){
            tv_toolbar.setText("Мастера");
            setToolbarAsHomeEnabled(false);
        }
        else{
            tv_toolbar.setText("Редактирование");
            setToolbarAsHomeEnabled(false);
        }

        navHostSaloonFragment.getNavController().navigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ReloadClientData();

        /*if(mAuth.getCurrentUser()!= null){
            FBHelper.GetUsers().whereEqualTo("uid", mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && !task.getResult().isEmpty()){
                    for(DocumentSnapshot doc : task.getResult()){
                        client = new Client(doc.getData());
                    }
                    FBHelper.UpdateToken(SaloonActivity.this, mAuth.getCurrentUser().getUid());
                }
                }
            });
        }*/
    }

    public void ReloadClientData(){
        FBHelper.GetUsers().document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            if(task.isSuccessful()){
                client = new Client(task.getResult().getData());
            }
            FBHelper.UpdateToken(SaloonActivity.this, client.getUid());
            }
        });
    }
}
