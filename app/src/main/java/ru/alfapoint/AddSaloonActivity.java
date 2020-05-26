package ru.alfapoint;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.alfapoint.model.Service;
import ru.alfapoint.ui.SaloonRegistrationFragment1;
import ru.alfapoint.ui.SaloonRegistrationFragment2;
import ru.alfapoint.old.SaloonRegistrationFragment3;
import ru.alfapoint.old.SaloonRegistrationFragment4;
import ru.alfapoint.ui.SaloonRegistrationFragment5;
import ru.alfapoint.ui.SaloonMastersTime;
import ru.alfapoint.utils.Helper;

public class AddSaloonActivity extends AppCompatActivity {
    TextView tvMain;
    SaloonRegistrationFragment1 saloonRegistrationFragment1;
    SaloonRegistrationFragment2 saloonRegistrationFragment;
    SaloonRegistrationFragment3 saloonServicesFragment;
    SaloonRegistrationFragment4 saloonRegistrationFragment4;
    SaloonRegistrationFragment5 saloonRegistrationFragment5;
    //SaloonMastersTime saloonRegistrationFragment6;
    Toolbar toolbar;
    ImageView imageView25;

    //private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String saloonID, usluga;
    Bundle actBundle = new Bundle();
    Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_saloon);
        Helper.SetStatusBarGrey(this);
        toolbar = findViewById(R.id.toolbar2);
        tvMain = findViewById(R.id.tv_toolbar);
        imageView25 = findViewById(R.id.imageView25);
        imageView25.setVisibility(View.GONE);
        //typeface = Typeface.createFromAsset(getAssets(), "fonts/mon-med.ttf");
        tvMain.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/mon-med.ttf"));

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (toolbar != null) {
            actionBar.setTitle("");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fmWorking();
                }
            });
        }
        loadRegistrationFragment1();
        //loadSaloonRegistration3("f9UQHtv0XbZ6r3fxZQ4NgFaSIc32", 60.02838560000001, 30.284399999999998);
    }

    private void loadRegistrationFragment1() {
        saloonRegistrationFragment1 = new SaloonRegistrationFragment1();
        replaceFragment("registration1",saloonRegistrationFragment1);
        tvMain.setText("Регистрация салона");
    }

    public void loadSaloonRegistration2(Bundle bundle){
        actBundle = bundle;
        saloonRegistrationFragment = new SaloonRegistrationFragment2();
        replaceFragment("registration2", saloonRegistrationFragment, bundle);
    }



    public void loadSaloonRegistration4(Service service){
        this.usluga = service.getServiceName();
        tvMain.setText("Редактирование");
        actBundle.putString("serviceName", service.getServiceName());
        actBundle.putString("serviceID", service.getServiceID());
        //actBundle.putString("usluga", service.getServiceName());
        saloonRegistrationFragment4 = new SaloonRegistrationFragment4();
        replaceFragment("registration4", saloonRegistrationFragment4, actBundle);
    }

    public void loadSaloonRegistration5(int adapterPosition){
        tvMain.setText(R.string.stringSetMaster);
        actBundle.putInt("adapterPosition",adapterPosition);
        //getFragmentManager().beginTransaction().replace(R.id.addsaloonFrame, saloonRegistrationFragment5,"registration5").commit();
        saloonRegistrationFragment5 = new SaloonRegistrationFragment5();
        saloonRegistrationFragment5.setTargetFragment(saloonRegistrationFragment4, 1);
        replaceFragment("registration5", saloonRegistrationFragment5, actBundle);
    }
    public void fromSaloonRegistration5(){
        tvMain.setText("Редактирование");
        //actBundle.putString("master",master);
        saloonRegistrationFragment4 = new SaloonRegistrationFragment4();
        replaceFragment("registration4", saloonRegistrationFragment4, actBundle);
    }



    public void replaceFragment(String tag, Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.addsaloonFrame, fragment, tag);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }
    public void replaceFragment(String tag, Fragment fragment, Bundle bb) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bb);
        ft.replace(R.id.addsaloonFrame, fragment, tag);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    private void fmWorking(){
        if (getSupportFragmentManager() != null) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (fragments != null) {
                for (int i = fragments.size() - 1; i >= 0; i--) {
                    Fragment fragment = fragments.get(i);
                    if (fragment != null) {
                        if (fragment instanceof SaloonRegistrationFragment4){
                            //Log.e("onBack","SaloonRegistrationFragment4");
                            //fromSaloonRegistration4();
                        }else if(fragment instanceof SaloonRegistrationFragment5 || fragment instanceof SaloonMastersTime){
                            fromSaloonRegistration5();
                        }else{
                            finish();
                        }
                        break;
                    }
                }
            }else{
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        fmWorking();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}


/*
public void loadSaloonRegistration3(String string, double lat, double lng){
        tvMain.setText("Регистрация услуг");
        actBundle.putString("docID", string);
        actBundle.putDouble("lat", lat);
        actBundle.putDouble("lng", lng);
        saloonServicesFragment = new SaloonRegistrationFragment3();
        replaceFragment("registration3", saloonServicesFragment, actBundle);
    }



    public void fromSaloonRegistration4(){
        tvMain.setText("Регистрация услуг");
        saloonServicesFragment = new SaloonRegistrationFragment3();
        replaceFragment("registration3", saloonServicesFragment, actBundle);
    }

public void loadSaloonregistration6(String masterID, String materName, String serviceID){
        tvMain.setText(materName);
        actBundle.putString("masterID",masterID);
        actBundle.putString("serviceID",serviceID);
        saloonRegistrationFragment6 = new SaloonMastersTime();
        replaceFragment("registration6", saloonRegistrationFragment6, actBundle);
    }

 */