package ru.alfapoint.old;

public class OldDialog {

    /*
        FBHelper.GetDaySheet()
                .whereEqualTo("masterID", masterID)
                .whereEqualTo("saloonID",saloonID)
                //.whereEqualTo("serviceID", serviceID)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(!task.getResult().isEmpty()){
                        for(DocumentSnapshot doc: task.getResult()){
                            Date day = (Date)doc.get("day");
                            if(day.after(today)){
                                datesLoad.add((Date)doc.get("day"));
                                datesLoadRasp.add(doc.get("workTime").toString());
                            }
                        }
                        calendar_view.highlightDates(datesLoad);
                        //calendar_view.setSel
                        alertDialog = b.create();
                        alertDialog.show();
                    }else{
                        Toast.makeText(ctx,"Расписание не определено!", Toast.LENGTH_SHORT).show();
                    }
            }
        });
        calendar_view.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                // проверять день, что он рабочий
                choisenDateList.clear();
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                for(int i=0; i< datesLoad.size();i++){
                    if(sdf.format(datesLoad.get(i)).equals(sdf.format(date))){
                        choisenDateList.add(new InfoClass(sdf.format(datesLoad.get(i)), datesLoadRasp.get(i), datesLoad.get(i)));
                        break;
                    }
                }
                if(choisenDateList.size()==0){
                    Toast.makeText(ctx,"Запись на данную дату невозможна!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onDateUnselected(Date date) {
            }
        });

    public static void GetMasterWorkDays(final Context ctx, String saloonID, String masterID){
        final AlertDialog.Builder b = new AlertDialog.Builder(ctx);
        final List<Date> datesLoad = new ArrayList<>();
        View promptsView4 = LayoutInflater.from(ctx).inflate(R.layout.fragment_registration6, null);
        b.setView(promptsView4);
        CheckBox checkBox = promptsView4.findViewById(R.id.checkBox15);
        final ConstraintLayout clFR6 = promptsView4.findViewById(R.id.clFR6);
        checkBox.setText("Особое расписание");
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    clFR6.setVisibility(View.VISIBLE);
                }else{
                    clFR6.setVisibility(View.GONE);
                }
            }
        });

        final List<String> workTimeList = GetWorkTime();
        Spinner spinnerStart = promptsView4.findViewById(R.id.textView116);
        Spinner spinnerStop = promptsView4.findViewById(R.id.textView117);
        SpinnerAdapter2 adapter2 = new SpinnerAdapter2(ctx, R.layout.spinner_item, workTimeList);
        spinnerStart.setAdapter(adapter2);
        spinnerStop.setAdapter(adapter2);
        spinnerStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                timeStart = workTimeList.get(i);
                //Log.e("start", workTimeList.get(i));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinnerStop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                timeStop = workTimeList.get(i);
                //Log.e("stop", workTimeList.get(i));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        promptsView4.findViewById(R.id.button9).setVisibility(View.GONE);
        final CalendarPickerView calendar_view = promptsView4.findViewById(R.id.calendar_view);
        Calendar calendar = Calendar.getInstance();
        //Calendar calendar1 = Calendar.getInstance();
        final Date today = calendar.getTime();
        //calendar1.set(Calendar.DAY_OF_MONTH, -1);
        //final Date yesterday = calendar1.getTime();
        calendar.add(Calendar.MONTH, 2);
        calendar_view.init(today, calendar.getTime()).inMode(CalendarPickerView.SelectionMode.SINGLE);
        FBHelper.GetDaySheet()
                .whereEqualTo("masterID", masterID)
                .whereEqualTo("saloonID",saloonID)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(!task.getResult().isEmpty()){
                    for(DocumentSnapshot doc: task.getResult()){
                        Date day = (Date)doc.get("day");
                        if(day.after(today)){
                            datesLoad.add(day);
                        }
                    }
                    calendar_view.highlightDates(datesLoad);
                    alertDialog = b.create();
                    alertDialog.show();
                }else{
                    Toast.makeText(ctx,"Расписание не определено!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        b.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                alertDialog.dismiss();
            }
        });

    }



        */

}
