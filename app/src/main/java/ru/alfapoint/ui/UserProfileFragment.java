package ru.alfapoint.ui;


import android.Manifest;
import android.app.Activity;

import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ru.alfapoint.R;
import ru.alfapoint.model.Client;
import ru.alfapoint.model.interfaces.UIDCallback;
import ru.alfapoint.utils.Constants;
import ru.alfapoint.utils.DialogHelper;
import ru.alfapoint.utils.FBHelper;
import ru.alfapoint.utils.Helper;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment {
    Boolean isGranted = false, isRegistered = true, isEdit=false;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Button button3,button4;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private final LatLng mDefaultLocation = Constants.DefaultLocation;
    private static final int DEFAULT_ZOOM = 15;
    EditText etName, etPhone, etEmail;
    Geocoder geocoder;
    TextView textView30, tvCountry, tvCity;
    CheckBox showMap;
    String address, city, country;
    double lat,lng;
    CheckBox checkBox17;
    ConstraintLayout clUser1;


    public UserProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_userprofile, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        button3 = v.findViewById(R.id.button3);
        button4 = v.findViewById(R.id.button4);
        etName = v.findViewById(R.id.etName);
        etPhone = v.findViewById(R.id.etPhone);
        etEmail = v.findViewById(R.id.etMail);
        textView30 = v.findViewById(R.id.textView30);
        checkBox17 = v.findViewById(R.id.checkBox17);
        tvCountry = v.findViewById(R.id.textView7);
        tvCity = v.findViewById(R.id.textView96);
        showMap = v.findViewById(R.id.textView92);
        clUser1 = v.findViewById(R.id.clUser1);
        //fonts
        etName.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        etPhone.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        etEmail.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        checkBox17.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        textView30.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        tvCountry.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        tvCity.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        textView30.setText(R.string.stringUserProfile);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapR);
        loadUserInfo();
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRegistered){
                    updateClientInfo();
                }else{
                    if(etName.getText().toString().equals("")){
                        Toast.makeText(getContext(), "Укажите имя", Toast.LENGTH_SHORT).show();
                    }else{
                        if(etPhone.getText().toString().equals("")){
                            Toast.makeText(getContext(), "Укажите телефон", Toast.LENGTH_SHORT).show();
                        }else{
                            if(etEmail.getText().toString().equals("")){
                                Toast.makeText(getContext(), "Укажите email", Toast.LENGTH_SHORT).show();
                            }else{
                                if(!checkBox17.isChecked()){
                                    Toast.makeText(getContext(), "Требуется Ваше согласие!", Toast.LENGTH_SHORT).show();
                                }else{
                                    if(!Helper.isEmailValid(etEmail.getText().toString())){
                                        Toast.makeText(getContext(), "Неверный формат email-адреса", Toast.LENGTH_SHORT).show();
                                    }else
                                        if(city==null | country==null){
                                            Toast.makeText(getContext(), "Укажите страну и город", Toast.LENGTH_SHORT).show();
                                        }else
                                            createUserEmail(etEmail.getText().toString(), etPhone.getText().toString());
                                }
                            }
                        }
                    }
                }


            }
        });

        // edit button
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etName.setEnabled(true);
                etPhone.setEnabled(true);
                etEmail.setEnabled(true);
                tvCity.setEnabled(true);
                tvCountry.setEnabled(true);
                button4.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_button_green));
                button3.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_button2));
            }
        });

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                updateLocationUI();
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng point) {
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(point));
                        try {
                            String[] adr = geocoder.getFromLocation(point.latitude,point.longitude,1).get(0).getAddressLine(0).split(",");
                            address = adr[0] + ", " + adr[1];
                            lat = point.latitude;
                            lng = point.longitude;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        tvCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.GetLocationParams(getContext(), 2, "Укажите страну", new UIDCallback() {
                    @Override
                    public void getUID(String string) {
                        country = string;
                        tvCountry.setText(country);
                        //Log.e("coun", country);
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
        showMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    clUser1.setVisibility(View.VISIBLE);
                }else{
                    clUser1.setVisibility(View.GONE);
                }
            }
        });
        return v;
    }


    private void loadUserInfo(){
        if(mAuth.getCurrentUser() != null){
            etName.setEnabled(false);
            etPhone.setEnabled(false);
            etEmail.setEnabled(false);
            checkBox17.setEnabled(false);
            checkBox17.setChecked(true);
            tvCity.setEnabled(false);
            tvCountry.setEnabled(false);
            FBHelper.GetUsers().whereEqualTo("uid", mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(DocumentSnapshot doc : task.getResult().getDocuments()){
                        Client client = new Client(doc.getData());
                        etName.setText(client.getName());
                        etPhone.setText(client.getPhone());
                        etEmail.setText(client.getEmail());
                        tvCity.setText(client.getCity());
                        tvCountry.setText(client.getCountry());
                    }

                }
            });
            button3.setEnabled(true);
        }else{
            button3.setEnabled(false);
            isRegistered = false;
            button4.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_button_green));
            button3.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_button2));
        }
    }
    private void createUserEmail(final String email, String password) {
        if(address==null){
            try {
                List<Address> listOfAddress = geocoder.getFromLocationName(country + "," +city,1);
                if(listOfAddress != null && !listOfAddress.isEmpty()){
                    Address addr = listOfAddress.get(0);
                    address = addr.getAdminArea();
                    lat = addr.getLatitude();
                    lng = addr.getLongitude();
                }else{
                    lat = 0;
                    lng = 0;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        HashMap<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("name", etName.getText().toString());
        user.put("phone", etPhone.getText().toString());
        user.put("address", address);
        user.put("city", city);
        user.put("country", country);
        user.put("lng", lng);
        user.put("lat", lat);
        user.put("type", "client");
        user.put("pass", password);
        FBHelper.createUser(user, getContext(), new UIDCallback() {
            @Override
            public void getUID(String string) {
                if(string.equals("GoodAuth")){
                    etName.setEnabled(false);
                    etPhone.setEnabled(false);
                    etEmail.setEnabled(false);
                    checkBox17.setEnabled(false);
                    tvCity.setEnabled(false);
                    tvCountry.setEnabled(false);
                    FBHelper.UpdateToken(getActivity(), mAuth.getCurrentUser().getUid());
                    button3.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_button_green));
                    button4.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_button2));
                }
            }
        });
    }

    private void updateClientInfo(){
        button3.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_button_green));
        button4.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_button2));
        if(address==null){
            try {
                List<Address> listOfAddress = geocoder.getFromLocationName(country + "," +city,1);
                if(listOfAddress != null && !listOfAddress.isEmpty()){
                    Address addr = listOfAddress.get(0);
                    address = addr.getAdminArea();
                    lat = addr.getLatitude();
                    lng = addr.getLongitude();
                }else{
                    lat = 0;
                    lng = 0;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        HashMap<String, Object> user = new HashMap<>();
        user.put("email", etEmail.getText().toString());
        user.put("name", etName.getText().toString());
        user.put("phone", etPhone.getText().toString());
        user.put("address", address);
        user.put("lat", lat);
        user.put("lng", lng);
        user.put("city", tvCity.getText());
        user.put("country", tvCountry.getText());
        FBHelper.GetUsers().document(mAuth.getCurrentUser().getUid()).update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(), "Данные обновлены!", Toast.LENGTH_SHORT).show();
                //button4.setEnabled(false);
                etName.setEnabled(false);
                etPhone.setEnabled(false);
                etEmail.setEnabled(false);
                checkBox17.setEnabled(false);
                tvCity.setEnabled(false);
                tvCountry.setEnabled(false);
            }
        });
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (isGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                getDeviceLocation();
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation,8));
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    private void getDeviceLocation() {
        try {
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener((Activity) getContext(), new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    // Set the map's camera position to the current location of the device.
                    mLastKnownLocation = task.getResult();
                    if(mLastKnownLocation!=null){
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(),
                                mLastKnownLocation.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_im_here)).draggable(true));
                        try {
                            String[] adr = geocoder.getFromLocation(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(),1).get(0).getAddressLine(0).split(",");
                            address = adr[0] + ", " + adr[1];
                            lat = mLastKnownLocation.getLatitude();
                            lng = mLastKnownLocation.getLongitude();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Log.e("location error ", "Exception: %s", task.getException());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    try {
                        String[] adr = geocoder.getFromLocation(mDefaultLocation.latitude, mDefaultLocation.longitude,1).get(0).getAddressLine(0).split(",");
                        address = adr[0] + ", " + adr[1];
                        lat = mDefaultLocation.latitude;
                        lng = mDefaultLocation.longitude;
                    } catch (IOException e) {
                        e.printStackTrace();
                    };
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
                isGranted = true;
                updateLocationUI();
                break;
        }
    }
}
