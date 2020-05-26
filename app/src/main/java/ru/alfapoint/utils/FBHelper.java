package ru.alfapoint.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Random;

import ru.alfapoint.model.interfaces.UIDCallback;

public class FBHelper {
    static String userID = "";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static CollectionReference GetUsers(){
        return db.collection("Users");
    }

    /*public static CollectionReference GetTimeSheet(){
        return db.collection("TimeSheet");
    }*/
    public static CollectionReference GetDaySheet(){
        return db.collection("DaySheet");
    }

    public static CollectionReference GetMasters() {
        return db.collection("Masters");
    }

    public static CollectionReference GetEntries() {
        return db.collection("Entries");
    }
    public static CollectionReference GetCodes(){
        return db.collection("Codes");
    }
    // type 1 = услуги, 2 = Страна, 3 = Город
    public static CollectionReference GetReference(){
        return db.collection("Reference");
    }

    // услуги салона, поиск по салонАйди
    public static CollectionReference GetServices(){
        return db.collection("Services");
    }

    public static void createUser(final HashMap<String, Object> options, final Context ctx, final UIDCallback callback) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(options.get("email").toString(), options.get("pass").toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                userID = task.getResult().getUser().getUid();
                HashMap<String, Object> user = new HashMap<>();
                user.put("email", options.get("email"));
                user.put("name", options.get("name"));
                user.put("phone", options.get("phone"));
                user.put("address", options.get("address"));
                user.put("lng", options.get("lng"));
                user.put("lat", options.get("lat"));
                user.put("type", options.get("type"));
                user.put("city", options.get("city"));
                user.put("country", options.get("country"));
                user.put("uid", userID);

                if(options.get("type").toString().equals("client")){
                    GetUsers().document(userID).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ctx, "Спасибо за регистрацию!", Toast.LENGTH_SHORT).show();
                                callback.getUID("GoodAuth");
                            } else {
                                Log.e("FBHelper: ", task.getException().getMessage());
                            }
                        }
                    });
                }else{
                    user.put("workTime", options.get("workTime"));
                    user.put("workType", options.get("workType"));
                    user.put("description", options.get("description"));
                    user.put("code", options.get("code"));
                    user.put("image", getImage());

                    GetUsers().document(userID).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ctx, "Салон зарегистрирован!", Toast.LENGTH_SHORT).show();
                                HashMap<String, Object> codeMap = new HashMap<>();
                                codeMap.put("saloonID", userID);
                                callback.getUID(userID);
                                FBHelper.GetCodes().document(options.get("codeID").toString()).update(codeMap);
                            }else{
                                Log.e("FBHelper: ", task.getException().getMessage());
                            }
                        }
                    });
                }
            } else {
                if(task.getException().getMessage().contains("The given password is invalid")){
                    Toast.makeText(ctx, "Телефон должен состоять из 10 символов!", Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(ctx, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("authError", task.getException().getMessage());
                Helper.HideProgress();
            }
            }
        });

    }

    public static void LoginUser(String email, String pass, final UIDCallback callback){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    callback.getUID(task.getResult().getUser().getUid());
                }else
                    callback.getUID("Notgood");
            }
        });
    }

    // обновление токена
    public static void UpdateToken(Activity activity, final String user){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( activity,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                HashMap<String,Object> map = new HashMap<>();
                map.put("token", instanceIdResult.getToken());
                FBHelper.GetUsers().document(user).update(map);
            }
        });
    }

    private static String getImage(){
        String image;
        switch (new Random().nextInt(4)){
            default:
                image = "salon1";
                break;
            case 1:
                image = "salon2";
                break;
            case 2:
                image = "salon3";
                break;
            case 3:
                image = "salon4";
                break;
        }
        return image;
    }
    // сторадж
    public static StorageReference GetStorageImage(String imageName){
        return FirebaseStorage.getInstance().getReference().child("Images" + "/" + imageName + ".jpg");
    }
    public static StorageReference GetMasterImage(String masterID){
        return FirebaseStorage.getInstance().getReference().child("Masters" + "/" + masterID + ".jpg");
    }
}
