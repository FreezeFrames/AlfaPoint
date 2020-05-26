package ru.alfapoint.Additional;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ru.alfapoint.R;
import ru.alfapoint.model.TimeSheet;
import ru.alfapoint.model.interfaces.UIDCallback;
import ru.alfapoint.utils.FBHelper;

public class TimeChecker {
    static List<Button> buttonsNamesList;
    static List<TimeSheet> buttonsDateList;
    private static AlertDialog alertDialog;
    static List<String> busyList = new ArrayList<>();
    private static Date startDate, stopDate;
    private static Context mContext;
    private static String choisenTime, pDate,currentDate;
    private static List<Integer> nNumber = new ArrayList<>();
    private static Boolean isChecken = false, isSaloon;
    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    public static void SelectionTimeBy(Boolean saloon, Context ctx, String title, String rasp, String parsDate, String saloonID, String masterID, final UIDCallback callback){
        mContext = ctx;
        busyList.clear();
        pDate = parsDate;
        isSaloon = saloon;
        final AlertDialog.Builder b = new AlertDialog.Builder(ctx);
        buttonsNamesList = new ArrayList<>();
        buttonsDateList = getListButtons();

        String startTime = rasp.substring(0,rasp.indexOf(" "));
        String stopTime = rasp.substring(rasp.indexOf("-")+1).replace(" ","");
        View promptsView4 = LayoutInflater.from(ctx).inflate(R.layout.dialog2choosetime, null);
        TextView tv = promptsView4.findViewById(R.id.textView55);
        tv.setText(title);
        b.setView(promptsView4);
        Button button13 = promptsView4.findViewById(R.id.button13);
        Button button14 = promptsView4.findViewById(R.id.button14);
        Button button15 = promptsView4.findViewById(R.id.button15);
        Button button16 = promptsView4.findViewById(R.id.button16);
        Button button17 = promptsView4.findViewById(R.id.button17);
        Button button18 = promptsView4.findViewById(R.id.button18);
        Button button19 = promptsView4.findViewById(R.id.button19);
        Button button20 = promptsView4.findViewById(R.id.button20);
        Button button21 = promptsView4.findViewById(R.id.button21);
        Button button22 = promptsView4.findViewById(R.id.button22);
        Button button23 = promptsView4.findViewById(R.id.button23);
        Button button24 = promptsView4.findViewById(R.id.button24);
        Button button25 = promptsView4.findViewById(R.id.button25);
        Button button26 = promptsView4.findViewById(R.id.button26);
        Button button27 = promptsView4.findViewById(R.id.button27);
        Button button28 = promptsView4.findViewById(R.id.button28);
        Button button29 = promptsView4.findViewById(R.id.button29);
        Button button30 = promptsView4.findViewById(R.id.button30);
        Button button31 = promptsView4.findViewById(R.id.button31);
        Button button32 = promptsView4.findViewById(R.id.button32);
        Button button33 = promptsView4.findViewById(R.id.button33);
        Button button34 = promptsView4.findViewById(R.id.button34);
        Button button35 = promptsView4.findViewById(R.id.button35);
        Button button36 = promptsView4.findViewById(R.id.button36);
        Button button37 = promptsView4.findViewById(R.id.button37);
        Button button38 = promptsView4.findViewById(R.id.button38);
        Button button39 = promptsView4.findViewById(R.id.button39);
        Button button40 = promptsView4.findViewById(R.id.button40);
        Button button41 = promptsView4.findViewById(R.id.button41);
        Button button42 = promptsView4.findViewById(R.id.button42);
        buttonsNamesList.add(button13);
        buttonsNamesList.add(button14);
        buttonsNamesList.add(button15);
        buttonsNamesList.add(button16);
        buttonsNamesList.add(button17);
        buttonsNamesList.add(button18);
        buttonsNamesList.add(button19);
        buttonsNamesList.add(button20);
        buttonsNamesList.add(button21);
        buttonsNamesList.add(button22);
        buttonsNamesList.add(button23);
        buttonsNamesList.add(button24);
        buttonsNamesList.add(button25);
        buttonsNamesList.add(button26);
        buttonsNamesList.add(button27);
        buttonsNamesList.add(button28);
        buttonsNamesList.add(button29);
        buttonsNamesList.add(button30);
        buttonsNamesList.add(button31);
        buttonsNamesList.add(button32);
        buttonsNamesList.add(button33);
        buttonsNamesList.add(button34);
        buttonsNamesList.add(button35);
        buttonsNamesList.add(button36);
        buttonsNamesList.add(button37);
        buttonsNamesList.add(button38);
        buttonsNamesList.add(button39);
        buttonsNamesList.add(button40);
        buttonsNamesList.add(button41);
        buttonsNamesList.add(button42);
        try {
             startDate = sdf.parse(startTime);
             stopDate = sdf.parse(stopTime);
             clearButtons();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        b.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        b.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(choisenTime != null)
                    callback.getUID(choisenTime);
            }
        });

        FBHelper.GetEntries()
            .whereEqualTo("parsDate", parsDate)
            .whereEqualTo("saloonID", saloonID)
            .whereEqualTo("masterID", masterID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(DocumentSnapshot doc : task.getResult().getDocuments()){
                        if (!doc.get("status").toString().equals("Заявка отклонена"))
                            busyList.add(doc.get("time").toString());
                    }
                    if(busyList.size()>0)
                        clearRasp2();
                    alertDialog = b.create();
                    alertDialog.show();
                }
        });

        button13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(0);
            }
        });
        button14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(1);
            }
        });
        button15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(2);
            }
        });
        button16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(3);
            }
        });
        button17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(4);
            }
        });
        button18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(5);
            }
        });
        button19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(6);
            }
        });
        button20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(7);
            }
        });
        button21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(8);
            }
        });
        button22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(9);
            }
        });
        button23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(10);
            }
        });
        button24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(11);
            }
        });
        button25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(12);
            }
        });
        button26.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(13);
            }
        });
        button27.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(14);
            }
        });
        button28.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(15);
            }
        });
        button29.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(16);
            }
        });
        button30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(17);
            }
        });
        button31.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(18);
            }
        });
        button32.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(19);
            }
        });
        button33.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(20);
            }
        });
        button34.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(21);
            }
        });
        button35.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(22);
            }
        });
        button36.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(23);
            }
        });
        button37.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(24);
            }
        });
        button38.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(25);
            }
        });
        button39.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(26);
            }
        });
        button40.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(27);
            }
        });
        button41.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(28);
            }
        });
        button42.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTime(29);
            }
        });
    }

    private static void clearButtons(){
        if(!isSaloon) // если салон не проверяем время
            checkForToday();

        for(int i=0; i < buttonsDateList.size(); i++){
            if(startDate.before(buttonsDateList.get(i).getDay()) || startDate.equals(buttonsDateList.get(i).getDay())){
                //включаем кнопки
            }else{
                //отключаем кнопки
                buttonsNamesList.get(i).setEnabled(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    buttonsNamesList.get(i).setBackground(mContext.getDrawable(R.drawable.shape_button_grey));
                }
            }
            if(stopDate.after(buttonsDateList.get(i).getDay())){
                //влючаем кнопки
            }else{
                //отключаем кнопки
                buttonsNamesList.get(i).setEnabled(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    buttonsNamesList.get(i).setBackground(mContext.getDrawable(R.drawable.shape_button_grey));
                }
            }
        }
    }
    private static void checkForToday(){
        //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        currentDate = DateFormat.format("dd", calendar.getTime()) + "." + DateFormat.format("MM", calendar.getTime()) + "." +
                DateFormat.format("yyyy", calendar.getTime());
        if(pDate.equals(currentDate)){
            // today
            try {
                startDate = sdf.parse(String.valueOf(DateFormat.format("HH", calendar.getTime()).toString()
                                + ":" + DateFormat.format("mm", calendar.getTime())));
                //Log.e("newStartHour: ",  DateFormat.format("HH", calendar.getTime()).toString()
                  //      + DateFormat.format("mm", calendar.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
             //startDate = sdf.parse(startTime);
        }else{
            Log.e("newStartHour: ", "not here");
        }
    }

    private static void clearRasp2(){
        for(int i=0; i < buttonsNamesList.size(); i++){
            for(int j=0; j <busyList.size(); j++){
                if(busyList.get(j).equals(buttonsNamesList.get(i).getText().toString())){
                    buttonsNamesList.get(i).setEnabled(false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        buttonsNamesList.get(i).setBackground(mContext.getDrawable(R.drawable.shape_button_grey));
                    }
                }
            }
        }
    }
    private static void setTime(int number){
        switch (number){
            default:
                checkNumber(number);
                isChecken = true;
                choisenTime = buttonsNamesList.get(number).getText().toString();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    buttonsNamesList.get(number).setBackground(mContext.getDrawable(R.drawable.shape_button_green));
                }
                break;
        }
    }

    private static void checkNumber(int num){
        if(isChecken){
            // сделать проверку на включена ли кнопка
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if(buttonsNamesList.get(nNumber.get(0)).isEnabled()){
                    buttonsNamesList.get(nNumber.get(0)).setBackground(mContext.getDrawable(R.drawable.shape_button2));
                }
            }
            nNumber.remove(0);
            nNumber.add(0,num);
        }else{
            nNumber.add(0,num);
        }
    }

    private static List<TimeSheet> getListButtons(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        List<TimeSheet> buttonsDateList = new ArrayList<>();
        try {
            buttonsDateList.add(new TimeSheet(sdf.parse("08:00"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("08:30"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("09:00"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("09:30"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("10:00"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("10:30"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("11:00"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("11:30"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("12:00"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("12:30"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("13:00"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("13:30"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("14:00"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("14:30"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("15:00"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("15:30"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("16:00"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("16:30"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("17:00"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("17:30"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("18:00"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("18:30"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("19:00"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("19:30"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("20:00"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("20:30"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("21:00"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("21:30"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("22:00"),false));
            buttonsDateList.add(new TimeSheet(sdf.parse("22:30"),false));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return buttonsDateList;
    }


}
