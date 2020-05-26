package ru.alfapoint.ui;


import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.alfapoint.AddSaloonActivity;
import ru.alfapoint.Additional.RecycleTouchListener;
import ru.alfapoint.R;
import ru.alfapoint.SaloonActivity;
import ru.alfapoint.model.Client;
import ru.alfapoint.model.ClientObserver;
import ru.alfapoint.model.Master;
import ru.alfapoint.model.adapters.MasterAdapter;
import ru.alfapoint.model.interfaces.MasterInterface;
import ru.alfapoint.model.interfaces.PositionCallback;
import ru.alfapoint.utils.DialogHelper;
import ru.alfapoint.utils.FBHelper;
import ru.alfapoint.utils.Helper;

/**
 * A simple {@link Fragment} subclass.
 */
public class SaloonMastersFragment extends Fragment {
    RecyclerView rvSaloonMasters;
    Client client;
    List<Master> masterList = new ArrayList<>();
    MasterAdapter adapter;
    Boolean isInProgress = false;
    ImageView imageView5, hiddenImage;
    String masterID;
    SaloonActivity saloonActivity;

    public SaloonMastersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_registration5, container, false);
        rvSaloonMasters =v.findViewById(R.id.rvMasters);
        imageView5 =v.findViewById(R.id.imageView5);
        hiddenImage =v.findViewById(R.id.imageView27);
        v.findViewById(R.id.clM).setVisibility(View.GONE);
        saloonActivity = (SaloonActivity)getActivity();
        client = saloonActivity.getClient();
        adapter = new MasterAdapter(masterList, getContext(), new MasterInterface() {
            @Override
            public void getImagePos(int position) {
                masterID = masterList.get(position).getMasterID();
                //запрашиваем разрешения
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                changeImage();
            }

            @Override
            public void getMasterInfo(int position) {
                //DialogHelper.GetMasterWorkDays(getContext(),masterList.get(position).getSaloonId(), masterList.get(position).getMasterID());
                saloonActivity.ChooseRasp(masterList.get(position).getMasterID(),
                        masterList.get(position).getMasteName(), masterList.get(position).getSaloonId());
            }
        });
        rvSaloonMasters.setAdapter(adapter);
        rvSaloonMasters.setHasFixedSize(true);
        rvSaloonMasters.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadMasters();

        return v;
    }

    private void changeImage() {
        if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE)==-1){
            Toast.makeText(getContext(), "Разрешение на загрузку фотографий в профиль не получено!", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, 1);
        }
    }

    private void loadMasters(){
        if(!isInProgress) {
            Helper.ShowProgress(getContext(), "Загружаю мастеров...");
        }
        FBHelper.GetMasters().whereEqualTo("saloonID", client.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    masterList.clear();
                    if(!task.getResult().isEmpty()){
                        for (DocumentSnapshot doc : task.getResult()){
                            masterList.add(new Master(doc.getId(), doc.getData()));
                        }
                        adapter.notifyDataSetChanged();
                    }
                    Helper.HideProgress();
                }
            }
        });
    }

    private void uploadPhoto() {
        Helper.ShowProgress(getContext(),"Сохраняю...");
        isInProgress = true;
        hiddenImage.setDrawingCacheEnabled(true);
        hiddenImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) hiddenImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = FBHelper.GetMasterImage(masterID).putBytes(data);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return FBHelper.GetMasterImage(masterID).getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    HashMap<String,Object> we = new HashMap<>();
                    we.put("photo", downloadUri.toString());
                    FBHelper.GetMasters().document(masterID).update(we);
                    masterID = null;
                    loadMasters();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null){
            switch (requestCode) {
                case 1:
                    Uri originalUri = data.getData();
                    final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    try {
                        getActivity().getContentResolver().takePersistableUriPermission(originalUri, takeFlags);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                    hiddenImage.setImageURI(originalUri);
                    uploadPhoto();
                    break;
            }
        }
    }


}
