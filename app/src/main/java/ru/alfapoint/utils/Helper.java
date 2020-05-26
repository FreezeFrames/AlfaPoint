package ru.alfapoint.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.alfapoint.R;

public class Helper {
    private static SweetAlertDialog pDialog  = null;

    // Пользователь ожидает окончания загрузки данных
    public static void ShowProgress(Context context, String text) {
        if (pDialog == null) {
            try {
                pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText(text);
                pDialog.setCancelable(false);
                pDialog.show();
            } catch (Exception e) {
                Log.e("helperEx", e.getMessage());
            }
        }
    }

    public static void HideProgress() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    //установка цвета статус бара
    public static void SetStatusBarGrey(Activity activity){
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(activity.getResources().getColor(R.color.colorStatusbar));
        }
    }
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public static boolean GetLocationPermissions(Context ctx){
        if (ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    public static int getColor(Context mContext, int id) {
        int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(mContext, id);
        } else {
            return mContext.getResources().getColor(id);
        }
    }
    public static String MakeUpperLetter(String string){
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }

    public static void DisableViews(View... views) {
        for (View v : views) {
            v.setVisibility(View.GONE);
        }
    }
    public static void EnableViews(View... views) {
        for (View v : views) {
            v.setVisibility(View.VISIBLE);
        }
    }
    public static String getMonth(int i) {
        switch (i) {
            case 0:
                return "января";
            case 1:
                return "Февраля";
            case 2:
                return "марта";
            case 3:
                return "апреля";
            case 4:
                return "мая";
            case 5:
                return "июня";
            case 6:
                return "июля";
            case 7:
                return "августа";
            case 8:
                return "сентября";
            case 9:
                return "октября";
            case 10:
                return "ноября";
            case 11:
                return "декабря";
        }
        return "января";
    }

    public static void AddtoCalendar(Context context, String time, String title, Date date, String location) {
        Calendar beginTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        //String fullDate = (String)DateFormat.format("dd", date) + " " + (String)DateFormat.format("MMMM", date)
        //        + " " + DateFormat.format("yyyy", date);
        int year = Integer.valueOf(DateFormat.format("yyyy", date).toString());
        int month = Integer.valueOf(DateFormat.format("MM", date).toString());
        int day = Integer.valueOf(DateFormat.format("dd", date).toString());
        beginTime.set(year, month-1, day, getHour(time), getMinutes(time));
        endTime.set(year, month-1, day, getHour(time)+1, getMinutes(time));
        long startMillis = beginTime.getTimeInMillis();
        long endMillis = endTime.getTimeInMillis();
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.DESCRIPTION, "Запись в салон красоты")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        context.startActivity(intent);
    }

    public static int getHour(String str){
        return Integer.valueOf(str.substring(0,2));
    }
    public static int getMinutes(String str){
        //return Integer.valueOf(str.substring(3,5));
        return Integer.valueOf(str.substring(str.indexOf(":")+1));
    }
    public static boolean isVisibleArea(final MarkerOptions marker, GoogleMap googleMap){
        final LatLngBounds.Builder bld = new LatLngBounds.Builder();
        final VisibleRegion visibleRegion = googleMap.getProjection().getVisibleRegion();
        bld.include(visibleRegion.farLeft)
                .include(visibleRegion.farRight)
                .include(visibleRegion.nearLeft)
                .include(visibleRegion.nearRight);
        return bld.build().contains(marker.getPosition());
    }

    public static String addZero(int abc) {
        if (abc < 10) {
            return "0" + String.valueOf(abc);
        } else {
            return String.valueOf(abc);
        }
    }
    public static void ShowKeyBoardStatic(Context ctx) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void HideKeyboard(Activity context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (context.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void PlaySound(Context context){
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();
    }
}
