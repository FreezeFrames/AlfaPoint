package ru.alfapoint.ui;


import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ru.alfapoint.R;
import ru.alfapoint.SaloonActivity;
import ru.alfapoint.model.Client;
import ru.alfapoint.model.InfoClass;
import ru.alfapoint.model.adapters.DetailServiceAdapter;
import ru.alfapoint.Additional.RecycleTouchListener;
import ru.alfapoint.model.interfaces.UIDCallback;
import ru.alfapoint.utils.DialogHelper;
import ru.alfapoint.utils.FBHelper;
import ru.alfapoint.utils.Helper;

/**
 * A simple {@link Fragment} subclass.
 */
public class SaloonProfileFragment extends Fragment {
    TextView textView32, tvName, tvDesc,etworkType,etWortTime, tvAddress, tvPhone, tvEmail;
    EditText etPhone, etMail, etStreet;
    Button btn10,btn11;
    String workTime, workType, photo;
    Client client;
    ImageView profileImage, hiddenImage;
    private static final int IMAGE = 1;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Geocoder geocoder;
    double lat, lng;

    public SaloonProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_saloonprofile, container, false);
        textView32 = v.findViewById(R.id.textView32);
        tvAddress = v.findViewById(R.id.textView122);
        tvPhone = v.findViewById(R.id.textView110);
        tvEmail = v.findViewById(R.id.textView111);
        btn10 = v.findViewById(R.id.button10);
        btn11 = v.findViewById(R.id.button11);
        profileImage = v.findViewById(R.id.imageView24);
        hiddenImage = v.findViewById(R.id.imageView26);
        tvName = v.findViewById(R.id.textView108);
        tvDesc = v.findViewById(R.id.textView109);
        etPhone = v.findViewById(R.id.editText8);
        etMail = v.findViewById(R.id.editText11);
        etStreet = v.findViewById(R.id.editText12);
        etworkType = v.findViewById(R.id.editText15);
        etWortTime = v.findViewById(R.id.editText14);

        //fonts
        textView32.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        tvName.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        tvDesc.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        btn10.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        btn11.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        etPhone.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        etMail.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        etStreet.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        etworkType.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        etWortTime.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        tvAddress.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        tvPhone.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        tvEmail.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));

        etMail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etMail.setImeOptions(EditorInfo.IME_FLAG_FORCE_ASCII);
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        // edit button
        btn10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn11.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_button_green));
                btn10.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_button2));
                etPhone.setEnabled(true);
                etMail.setEnabled(true);
                etStreet.setEnabled(true);
                etworkType.setEnabled(true);
                etWortTime.setEnabled(true);
            }
        });
        // save button
        btn11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disableEdit();
                HashMap<String,Object> mapa = new HashMap<>();
                if(etPhone.getText().length()>0)
                    mapa.put("phone", etPhone.getText().toString());
                if(etMail.getText().length()>0)
                    mapa.put("email", etMail.getText().toString());
                if(workType != null){
                    mapa.put("workType", workType);
                }
                if(workTime != null){
                   mapa.put("workTime", workTime);
                }
                if(etStreet.getText().length() > 0){
                    try {
                        List<Address> listOfAddress = geocoder.getFromLocationName(client.getCountry()
                                + "," + client.getCity() + "," + etStreet.getText().toString(),1);
                        if(listOfAddress != null && !listOfAddress.isEmpty()){
                            Address addr = listOfAddress.get(0);
                            lat = addr.getLatitude();
                            lng = addr.getLongitude();
                            mapa.put("lat", lat);
                            mapa.put("lng", lng);
                            mapa.put("address", etStreet.getText().toString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                FBHelper.GetUsers().document(client.getUid()).update(mapa);
                SaloonActivity saloonActivity = (SaloonActivity)getActivity();
                saloonActivity.ReloadClientData();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //запрашиваем разрешения
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                changeImage();
            }
        });
        textView32.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                changeImage();
            }
        });

        etworkType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.GetWorkType(getContext(), new UIDCallback() {
                    @Override
                    public void getUID(String string) {
                        workType = string;
                        etworkType.setText(workType);
                    }
                });
            }
        });
        etWortTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.GetWorkTime(getContext(), new UIDCallback() {
                    @Override
                    public void getUID(String string) {
                        workTime = string;
                        etWortTime.setText(workTime);
                    }
                });
            }
        });
        // загружаем инфо
        FBHelper.GetUsers().whereEqualTo("uid", mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && !task.getResult().isEmpty()){
                    for(DocumentSnapshot doc : task.getResult()){
                        client = new Client(doc.getData());
                    }
                    loadInfo();
                }
            }
        });
        return v;
    }

    private void disableEdit() {
        etPhone.setEnabled(false);
        etMail.setEnabled(false);
        etStreet.setEnabled(false);
        etworkType.setEnabled(false);
        etWortTime.setEnabled(false);
        btn10.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_button_green));
        btn11.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_button2));
    }

    private void changeImage(){
        if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE)==-1){
            Toast.makeText(getContext(), "Разрешение на загрузку фотографий в профиль не получено!", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, 1);
        }
    }

    private void loadInfo(){
        tvName.setText(client.getName());
        tvDesc.setText(client.getDescription());
        etPhone.setText(client.getPhone());
        etMail.setText(client.getEmail());
        etStreet.setText(client.getAddress());
        etworkType.setText(client.getWorkType());
        etWortTime.setText(client.getWorkTime());
        if(client.getImage().contains("salon")){
            int imageId = getResources().getIdentifier(client.getImage(), "drawable",getContext().getPackageName());
            Glide.with(getContext()).load(imageId).into(profileImage);
        }else
            Glide.with(getContext()).load(client.getImage()).apply(RequestOptions.circleCropTransform()).into(profileImage);
        disableEdit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null){
            switch (requestCode) {
                case IMAGE:
                    photo = data.getData().toString();
                    Uri originalUri = data.getData();
                    final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    try {
                        getActivity().getContentResolver().takePersistableUriPermission(originalUri, takeFlags);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                    hiddenImage.setImageURI(originalUri);
                    uploadPhoto(photo);
                break;
            }
        }
    }

    private void uploadPhoto(final String photo) {
        Helper.ShowProgress(getContext(),"Сохраняю...");
        hiddenImage.setDrawingCacheEnabled(true);
        hiddenImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) hiddenImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = FBHelper.GetStorageImage(client.getUid()).putBytes(data);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return FBHelper.GetStorageImage(client.getUid()).getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    HashMap<String,Object> we = new HashMap<>();
                    we.put("image", downloadUri.toString());
                    Glide.with(getContext()).load(photo).apply(RequestOptions.circleCropTransform()).into(profileImage);
                    FBHelper.GetUsers().document(client.getUid()).update(we);
                    Helper.HideProgress();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
