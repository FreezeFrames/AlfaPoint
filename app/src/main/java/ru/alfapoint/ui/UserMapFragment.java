package ru.alfapoint.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.alfapoint.R;
import ru.alfapoint.EntryByClient;
import ru.alfapoint.UserActivity;
import ru.alfapoint.model.Client;
import ru.alfapoint.model.adapters.MapInfoWindowAdapter;
import ru.alfapoint.utils.Constants;
import ru.alfapoint.utils.FBHelper;
import ru.alfapoint.utils.Helper;

public class UserMapFragment extends Fragment {
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    Boolean isGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    Geocoder geocoder;
    List<MarkerOptions> markers = new ArrayList<>();
    List<MarkerOptions> markersTmp = new ArrayList<>();
    List<MarkerOptions> list = new ArrayList<>();
    Client client;
    UserActivity userActivity;

    public UserMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_usermap, container, false);
        userActivity = (UserActivity)getContext();
        client = userActivity.getClient();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        try {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    updateLocationUI();
                    loadmarkers();
                }
            });
        } catch (Exception ex) {
            Log.e("ex", ex.getMessage());
        }

        return v;
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
                //mLastKnownLocation = null;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constants.DefaultLocation,8));
            }
            for (MarkerOptions mkr: markers) {
                if (Helper.isVisibleArea(mkr, mMap)){
                    markersTmp.add(mkr);
                    mkr.visible(false);
                }
            }
        } catch (SecurityException e)  {
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
                                    new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), Constants.Default_ZOOM));
                            mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude()))
                                    .title("Я")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_im_here)).draggable(true));
                        }

                    }
                }
            });
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void loadmarkers(){
        FBHelper.GetUsers().whereEqualTo("type", "saloon").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
               for (DocumentSnapshot doc : task.getResult()){
                   Client client = new Client(doc.getData());
                   mMap.addMarker(new MarkerOptions()
                           .position(new LatLng(client.getLat(),client.getLng()))
                           .icon(BitmapDescriptorFactory.fromResource(R.drawable.salon_map))
                           .title(client.getName() + "," + client.getUid())
                           .snippet(client.getImage())
                   );
                   list.add(new MarkerOptions()
                           .position(new LatLng(client.getLat(),client.getLng()))
                           .title(client.getName())
                           .snippet(client.getImage())
                   );
               }
                // кастомизируем info-window - отображение маркеров
                mMap.setInfoWindowAdapter(new MapInfoWindowAdapter(getContext(), list));
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                   @Override
                   public void onInfoWindowClick(Marker marker) {
                       if(!marker.getTitle().equals("Я")){
                           //Log.e("here","here");
                           String[] tempArray = marker.getTitle().split(",");
                           Intent i = new Intent(getContext(), EntryByClient.class);
                           Bundle bb = new Bundle();
                           bb.putInt("from", 1); // пришли с главного экрана пользователя
                           bb.putParcelable("client", client);
                           bb.putString("saloonID", tempArray[1]);
                           bb.putBoolean("edited", false);
                           i.putExtras(bb);
                           startActivityForResult(i,1);
                       }

                   }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            //переход в записи клиента
            userActivity.GoToUserEntries();
        }
    }
}