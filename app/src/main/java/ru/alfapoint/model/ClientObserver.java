package ru.alfapoint.model;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.alfapoint.utils.FBHelper;

public class ClientObserver extends ViewModel {

    private MutableLiveData<Client> localClient;

    public MutableLiveData<Client> getClient(String userUID) {
        if (localClient == null) {
            localClient = new MutableLiveData<>();
        }
        loadClientData(userUID);
        return localClient;
    }

    private void loadClientData(String userUID){
        final List<Client> myList = new ArrayList<>();
        FBHelper.GetUsers().whereEqualTo("uid", userUID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(!task.getResult().isEmpty()){
                    for(DocumentSnapshot doc : task.getResult()){
                        myList.add(new Client(doc.getData()));
                    }
                   localClient.postValue(myList.get(0));
                }
            }
        });
    }
}
