package ru.alfapoint;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import ru.alfapoint.model.Client;
import ru.alfapoint.utils.FBHelper;
import ru.alfapoint.utils.Helper;
import ru.alfapoint.utils.UserPreferences;


public class MainActivity extends AppCompatActivity{

    //BottomNavigationView bottomNavigationUser, bottomNavigationSaloon;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Bundle bundle = new Bundle();
    Client client;
    ImageView imageViewMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
        imageViewMain = findViewById(R.id.imageViewMain);
        Helper.SetStatusBarGrey(this);
        Picasso.get().load(R.drawable.preloader).fit().into(imageViewMain);

        if(mAuth.getCurrentUser() != null){
            FBHelper.GetUsers().whereEqualTo("uid", mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && !task.getResult().isEmpty()){
                    for(DocumentSnapshot doc : task.getResult()){
                        client = new Client(doc.getData());
                    }
                    if(client.getType().equals("saloon")){
                        bundle.putParcelable("saloon", client);
                        Intent i = new Intent(MainActivity.this, SaloonActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.putExtras(bundle);
                        startActivity(i);
                    }else{
                        //bundle.putParcelable("client", client);
                        Intent i = new Intent(MainActivity.this, UserActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                }else{
                    loadUserNavigation();
                }
                }
            });

        }else{
            // показываем салоны для пользователя без атворизации version =1
            loadUserNavigation();
        }


        if(UserPreferences.getInstance(this).getFirstRun() == null){
            loadUserNavigation();
            UserPreferences.getInstance(this).setFistrRun("yes");
        }
        //Log.e("e","122333");
        //FirebaseAuth.getInstance().signOut();


       //clearAll();

    }



    private void loadUserNavigation(){
        Log.e("e","loadUserNavigation");
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent i = new Intent(MainActivity.this, UserActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        },1100);
    }

    private void clearAll(){
        FBHelper.GetEntries().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //List<String> delList = new ArrayList<>();
                for(int i=0; i<task.getResult().getDocuments().size()-1;i++){
                    FBHelper.GetEntries().document(task.getResult().getDocuments().get(i).getId()).delete();
                }
            }
        });

        FBHelper.GetMasters().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //List<String> delList = new ArrayList<>();
                for(int i=0; i<task.getResult().getDocuments().size()-1;i++){
                    FBHelper.GetMasters().document(task.getResult().getDocuments().get(i).getId()).delete();
                }
            }
        });
        FBHelper.GetDaySheet().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //List<String> delList = new ArrayList<>();
                for(int i=0; i<task.getResult().getDocuments().size()-1;i++){
                    FBHelper.GetDaySheet().document(task.getResult().getDocuments().get(i).getId()).delete();
                }
            }
        });
        FBHelper.GetServices().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //List<String> delList = new ArrayList<>();
                for(int i=0; i<task.getResult().getDocuments().size()-1;i++){
                    FBHelper.GetServices().document(task.getResult().getDocuments().get(i).getId()).delete();
                }
            }
        });
    }
}


    /* @Override
    public void onBackPressed() {
        super.onBackPressed();
        switch(navHostFragment.getNavController().getCurrentDestination().getLabel().toString()){
            case "fragment_userhome":
                tvMain.setText("");
                break;
            case "fragment_profile":
                tvMain.setText("Мой профиль");
                break;
            case "fragment_usermap":
                tvMain.setText("Карта");
                break;
            case "fragment_userfilter":
                tvMain.setText("Мои записи");
                break;
        }
    }

    // удаление документов в коллекции





    */