package ru.alfapoint;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import ru.alfapoint.model.Client;
import ru.alfapoint.model.ClientObserver;
import ru.alfapoint.utils.Constants;
import ru.alfapoint.utils.FBHelper;
import ru.alfapoint.utils.Helper;

public class UserActivity extends AppCompatActivity {
    ImageView ivAddNewSaloon;
    int version=1;
    NavHostFragment navHostUserFragment;
    BottomNavigationView bottomNavigationView;
    Client client;
    ClientObserver clientObserver;
    Toolbar toolbar;
    ActionBar actionBar;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ivAddNewSaloon = findViewById(R.id.imageView2);
        toolbar = findViewById(R.id.toolbar);
        navHostUserFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.navHostUserFragment);
        bottomNavigationView = findViewById(R.id.navigationUser);
        Helper.SetStatusBarGrey(this);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("");

        Glide.with(UserActivity.this).load(R.drawable.ic_plus).into(ivAddNewSaloon);
        ivAddNewSaloon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (version){
                    case 1:
                        startActivity(new Intent("android.intent.action.AddSaloonActivity"));
                        setToolbarAsHomeEnabled(false);
                        break;
                    case 4:
                        if(mAuth.getCurrentUser()!=null){
                            navHostUserFragment.getNavController().navigate(R.id.navigation_entries);
                            setToolbarAsHomeEnabled(true);
                        }else{
                            Toast.makeText(UserActivity.this,"Требуется авторизация",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });

        NavigationUI.setupWithNavController(bottomNavigationView, navHostUserFragment.getNavController());
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                switch (item.getItemId()){
                    case R.id.navigation_home:
                        navHostUserFragment.setArguments(getIntent().getExtras());
                        navHostUserFragment.getNavController().navigate(R.id.navigation_home);
                        ivAddNewSaloon.setVisibility(View.VISIBLE);
                        Glide.with(UserActivity.this).load(R.drawable.ic_plus).into(ivAddNewSaloon);
                        version=1;
                        return true;
                    case R.id.navigation_map:
                        navHostUserFragment.getNavController().navigate(R.id.navigation_map);
                        ivAddNewSaloon.setVisibility(View.VISIBLE);
                        Glide.with(UserActivity.this).load(R.drawable.ic_filter).into(ivAddNewSaloon);
                        version=4;
                        return true;
                    case R.id.navigation_profile:
                        navHostUserFragment.getNavController().navigate(R.id.navigation_profile);
                        ivAddNewSaloon.setVisibility(View.INVISIBLE);
                        return true;
                    case R.id.userAllEntriesFragment:
                        navHostUserFragment.getNavController().navigate(R.id.userAllEntriesFragment);
                        ivAddNewSaloon.setVisibility(View.INVISIBLE);
                        return true;
                }
                return false;
            }
        });
        if(mAuth.getCurrentUser()!=null){
            //observer to client
            clientObserver = ViewModelProviders.of(this).get(ClientObserver.class);
            clientObserver.getClient(mAuth.getCurrentUser().getUid()).observe(this, new Observer<Client>() {
                @Override
                public void onChanged(@Nullable Client getclient) {
                    client = getclient;
                    FBHelper.UpdateToken(UserActivity.this, client.getUid());
                }
            });
        }

    }

    public Client getClient(){
        return client;
    }

    public void GoToUserEntries(){
        navHostUserFragment.getNavController().navigate(R.id.userAllEntriesFragment);
    }

    public void navigateUp(){
        navHostUserFragment.getNavController().navigateUp();
    }

    public void setToolbarAsHomeEnabled(Boolean switcher){
        if(switcher){
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (toolbar != null) {
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        navHostUserFragment.getNavController().navigateUp();
                        setToolbarAsHomeEnabled(false);
                    }
                });
            }
        }else{
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

}
