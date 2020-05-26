package ru.alfapoint.ui;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import ru.alfapoint.AddSaloonActivity;
import ru.alfapoint.MainActivity;
import ru.alfapoint.R;
import ru.alfapoint.SaloonActivity;
import ru.alfapoint.UserActivity;
import ru.alfapoint.model.Client;
import ru.alfapoint.model.interfaces.UIDCallback;
import ru.alfapoint.utils.FBHelper;
import ru.alfapoint.utils.Helper;

/**
 * A simple {@link Fragment} subclass.
 */
public class SaloonRegistrationFragment1 extends Fragment {
    TextView button;
    EditText editText, etMail;
    TextView textView5;
    CheckBox checkBoxMail;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    //Client client;

    public SaloonRegistrationFragment1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_registration1, container, false);
        button = v.findViewById(R.id.button);
        editText = v.findViewById(R.id.editText);
        etMail = v.findViewById(R.id.textView115);
        checkBoxMail = v.findViewById(R.id.textView114);
        textView5 = v.findViewById(R.id.textView5);
        textView5.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        textView5.setText(R.string.stringActivation);
        button.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/mon-reg.ttf"));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().length()==0){
                    Toast.makeText(getContext(),"Укажите код!",Toast.LENGTH_SHORT).show();
                }else{
                    // если есть регистрация
                    if(checkBoxMail.isChecked()){
                        if(etMail.getText().length()>0){  //проверяем что емайл указан
                            Helper.ShowProgress(getContext(),"Проверяю регистрацию...");
                            FBHelper.LoginUser(etMail.getText().toString(), editText.getText().toString(), new UIDCallback() {
                                @Override
                                public void getUID(String string) {
                                    if(!string.equals("Notgood")){
                                        FBHelper.GetUsers().whereEqualTo("uid", string).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful() && !task.getResult().isEmpty()){
                                                    //for(DocumentSnapshot doc : task.getResult()){
                                                        //client = new Client(doc.getData());
                                                   // }
                                                    Toast.makeText(getContext(),"Проверка выполнена успешно",Toast.LENGTH_SHORT).show();
                                                    // Bundle bundle = new Bundle();
                                                    //bundle.putParcelable("saloon", client);
                                                    Intent i = new Intent(getActivity(), SaloonActivity.class);
                                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    //i.putExtras(bundle);
                                                    startActivity(i);
                                                    Helper.HideProgress();
                                                }
                                            }
                                        });
                                    }else{
                                        Toast.makeText(getContext(),"Ошибка авторизации!", Toast.LENGTH_SHORT).show();
                                        Helper.HideProgress();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(getContext(),"Укажите емайл адрес!",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Helper.ShowProgress(getContext(),"Проверяю код...");
                        FBHelper.GetCodes().whereEqualTo("code", editText.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    if(task.getResult().getDocuments().isEmpty()){
                                        Toast.makeText(getContext(),"Неверный код",Toast.LENGTH_SHORT).show();
                                    }else{
                                        for (DocumentSnapshot doc : task.getResult().getDocuments()){
                                            if(doc.get("saloonID") != null){  // проверка кода регистрации
                                                Toast.makeText(getContext(),"Данный код уже используется!",Toast.LENGTH_SHORT).show();
                                            }else {
                                                Toast.makeText(getContext(),"Проверка выполнена успешно",Toast.LENGTH_SHORT).show();
                                                AddSaloonActivity addSaloonActivity = (AddSaloonActivity)getContext();
                                                Bundle b = new Bundle();
                                                b.putString("code", editText.getText().toString());
                                                b.putString("codeID", doc.getId());
                                                addSaloonActivity.loadSaloonRegistration2(b);
                                            }
                                        }
                                    }
                                    Helper.HideProgress();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(),"Неверный код",Toast.LENGTH_SHORT).show();
                                Helper.HideProgress();
                            }
                        });
                    }
                }
            }
        });

        checkBoxMail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    etMail.setVisibility(View.VISIBLE);
                }else
                    etMail.setVisibility(View.GONE);
            }
        });

        etMail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etMail.setImeOptions(EditorInfo.IME_FLAG_FORCE_ASCII);

        return v;
    }

}
