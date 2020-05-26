package ru.alfapoint.ui;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ru.alfapoint.AddSaloonActivity;
import ru.alfapoint.R;
import ru.alfapoint.SaloonActivity;
import ru.alfapoint.model.Client;
import ru.alfapoint.utils.Constants;
import ru.alfapoint.utils.DialogHelper;
import ru.alfapoint.utils.FBHelper;
import ru.alfapoint.utils.Helper;
import ru.alfapoint.model.interfaces.UIDCallback;

/**
 * A simple {@link Fragment} subclass.
 */
public class SaloonRegistrationFragment2 extends Fragment {
    //final List<String> workType = new ArrayList<>();
    final List<String> workTime = new ArrayList<>();
    String country, city, time, name, address, phone, email, code, codeID, userID, workDays;
    double lat = 0, lng = 0;
    List<Address> listOfAddress;
    Geocoder geocoder;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastKnownLocation;
    EditText etName, etPhone, etEmail, etAddress;

    Button bn;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    TextView tvCode, tvCodeDesc, tvTime, tvDays, tvCountry, tvCity;


    public SaloonRegistrationFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_registration2, container, false);
        tvDays = v.findViewById(R.id.spinner);
        tvTime = v.findViewById(R.id.spinner2);
        etName = v.findViewById(R.id.editText2);
        etPhone = v.findViewById(R.id.editText4);
        etEmail = v.findViewById(R.id.editText5);
        etAddress = v.findViewById(R.id.editText3);
        tvCountry = v.findViewById(R.id.textView8);
        tvCity = v.findViewById(R.id.textView52);
        tvCode = v.findViewById(R.id.textView13);
        tvCodeDesc = v.findViewById(R.id.textView12);
        bn = v.findViewById(R.id.button2);

        //fonts
        tvDays.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        tvTime.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        etName.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        etPhone.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        etEmail.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        etAddress.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        tvCode.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        tvCodeDesc.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        tvCountry.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        tvCity.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));

        codeID = getArguments().getString("codeID");
        code = getArguments().getString("code");
        tvCode.setText(code);
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });

        tvDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDayListener();
            }
        });

        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTimeListener();
            }
        });
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        tvCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.GetLocationParams(getContext(), 2, "Укажите страну", new UIDCallback() {
                    @Override
                    public void getUID(String string) {
                        country = string;
                        tvCountry.setText(country);
                    }
                });
            }
        });
        tvCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.GetLocationParams(getContext(), 3, "Укажите город", new UIDCallback() {
                    @Override
                    public void getUID(String string) {
                        city = string;
                        tvCity.setText(city);
                    }
                });
            }
        });
        return v;
    }

    private void next(){
        if(tvCity.getText().length()==0 || tvCountry.getText().length() == 0){
            Toast.makeText(getContext(),"Укажите страну и город!", Toast.LENGTH_SHORT).show();
        }else{
            if(etName.getText().length()==0){
                Toast.makeText(getContext(),"Не указано название салона!", Toast.LENGTH_SHORT).show();
            }else{
                if(etAddress.getText().length()==0){
                    Toast.makeText(getContext(),"Не указан адрес!", Toast.LENGTH_SHORT).show();
                }else{
                    if(etEmail.getText().length()==0){
                        Toast.makeText(getContext(),"Не указан email!", Toast.LENGTH_SHORT).show();
                    }else{
                        if(etPhone.getText().length()==0){
                            Toast.makeText(getContext(),"Не указан телефон!", Toast.LENGTH_SHORT).show();
                        }else{
                            address = etAddress.getText().toString();
                            try{
                                listOfAddress  = geocoder.getFromLocationName(country + ","
                                        +city + "," + address,1);
                                if(listOfAddress.size()!=0){
                                    lat = listOfAddress.get(0).getLatitude();
                                    lng = listOfAddress.get(0).getLongitude();
                                }else{
                                    lat = Constants.DefaultLocation.latitude;
                                    lng = Constants.DefaultLocation.longitude;
                                }
                                save();
                            } catch(IOException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

    }

    // сохраняем салон
    private void save(){
        country = tvCountry.getText().toString();
        city = tvCity.getText().toString();
        name = etName.getText().toString();
        phone = etPhone.getText().toString();
        email = etEmail.getText().toString();
        HashMap<String,Object> mapa = new HashMap<>();
        mapa.put("email", email);
        mapa.put("premium", false);
        mapa.put("name", Helper.MakeUpperLetter(name));
        mapa.put("phone", phone);
        mapa.put("type", "saloon");
        mapa.put("address", Helper.MakeUpperLetter(address));
        mapa.put("lng", lng);
        mapa.put("lat", lat);
        mapa.put("workTime", time);
        if(time==null){
            time = "09:00 - 18:00";
        }
        if(workDays==null){
            workDays = "Ежедневно";
        }
        mapa.put("workType", workDays);
        mapa.put("description", "Салон красоты");
        mapa.put("city", Helper.MakeUpperLetter(city));
        mapa.put("country", Helper.MakeUpperLetter(country));
        final AddSaloonActivity addSaloonActivity = (AddSaloonActivity)getContext();
        Helper.ShowProgress(getContext(),"Регистрация...");
        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
            FBHelper.GetUsers().document(userID).update(mapa);
            HashMap<String, Object> codeMap = new HashMap<>();
            codeMap.put("saloonID", userID);
            FBHelper.GetCodes().document(codeID).update(codeMap);
            endRegistration();
        }else{
            mapa.put("code", code);
            mapa.put("codeID", codeID);
            mapa.put("pass", code);
            FBHelper.createUser(mapa, getContext(), new UIDCallback() {
                @Override
                public void getUID(String string) {
                    userID = string;
                    endRegistration();
                }
            });
        }
    }
    private void onTimeListener(){
        DialogHelper.GetWorkTime(getContext(), new UIDCallback() {
            @Override
            public void getUID(String string) {
                time = string;
                tvTime.setText(time);
            }
        });
    }

    private void onDayListener(){
        DialogHelper.GetWorkType(getContext(), new UIDCallback() {
            @Override
            public void getUID(String string) {
                workDays = string;
                tvDays.setText(workDays);
            }
        });
    }

    private void endRegistration(){
        Intent i = new Intent(getContext(), SaloonActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        Helper.HideProgress();

        /*
        FBHelper.GetUsers().whereEqualTo("uid", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && !task.getResult().isEmpty()){
                    for(DocumentSnapshot doc : task.getResult()){

                        //Bundle dd = new Bundle();
                        //dd.putParcelable("saloon", new Client(doc.getData()));
                        //i.putExtras(dd);

                        break;
                    }
                }
            }
        }); */
    }

    private void getDeviceLocation() {
        try {
            Task<Location> locationResult = mFusedLocationClient.getLastLocation();
            locationResult.addOnCompleteListener((Activity) getContext(), new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        mLastKnownLocation = task.getResult();
                        if(mLastKnownLocation!=null){
                            try {
                                listOfAddress = geocoder.getFromLocation(
                                        mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude(),1);
                                Address mAddress = listOfAddress.get(0);
                                tvCountry.setText(mAddress.getCountryName());
                                tvCity.setText(mAddress.getAdminArea());
                                if(mAddress.getThoroughfare()!=null && mAddress.getSubThoroughfare()!=null)
                                    etAddress.setText(mAddress.getThoroughfare() + ", "  + mAddress.getSubThoroughfare());
                                lat = mAddress.getLatitude();
                                lng = mAddress.getLongitude();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                getDeviceLocation();
                break;
        }
    }
}
