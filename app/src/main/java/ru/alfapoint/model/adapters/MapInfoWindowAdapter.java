package ru.alfapoint.model.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import ru.alfapoint.R;
import ru.alfapoint.model.interfaces.UIDCallback;

public class MapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{
    private Context context;
    private View myContentsView;
    List<MarkerOptions> list;
    boolean not_first_time_showing_info_window;


    public MapInfoWindowAdapter(Context context, List<MarkerOptions> mylist){
        this.context = context;
        this.list = mylist;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myContentsView = inflater.inflate(R.layout.infowindow, null);
        TextView tv_InfoW1 = myContentsView.findViewById(R.id.textView95);
        final ImageView imageView = myContentsView.findViewById(R.id.imageView23);

        if(!marker.getTitle().equals("Я")){
            tv_InfoW1.setText(marker.getTitle().split(",")[0]);

            //Glide.with(context).load(marker.getSnippet()).into(imageView);
        }else{
            return null;
        }
        return myContentsView;
    }

    private class InfoWindowRefresher implements UIDCallback {
        private Marker markerToRefresh;
        private ImageView ivStr;

        private InfoWindowRefresher(Marker marker, ImageView iv) {
            this.markerToRefresh = marker;
            ivStr = iv;
        }

        @Override
        public void getUID(String string) {
            if(markerToRefresh.isInfoWindowShown()){
                markerToRefresh.hideInfoWindow();
            }
            markerToRefresh.showInfoWindow();
            Glide.with(context).load(markerToRefresh.getSnippet()).apply(new RequestOptions().circleCrop()).into(ivStr);
        }
    }
}
/*

if(!marker.getSnippet().contains("salon")) {
                //Glide.with(context).load(R.drawable.salon4).apply(new RequestOptions().circleCrop()).into(imageView);
                Glide.with(context).load(marker.getSnippet()).apply(new RequestOptions().circleCrop()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        imageView.setImageDrawable(resource);
                        return false;
                    }
                }).into(imageView);




if(!marker.getSnippet().contains("salon")) {
                if (not_first_time_showing_info_window) {
                    Glide.with(context).load(marker.getSnippet()).apply(new RequestOptions().circleCrop()).into(imageView);
                } else {
                    not_first_time_showing_info_window = true;
                    Picasso.get().load(marker.getSnippet()).into(imageView, new InfoWindowRefresher(marker, imageView));
                    Glide.with(context).load(marker.getSnippet()).apply(new RequestOptions().circleCrop()).into(imageView);
                }
            }

for (int i=0; i<list.size();i++){

                if(list.get(i).getTitle()!=null && marker.getTitle()!=null){
                    String[] tempArray = marker.getTitle().split(",");
                    if(tempArray[0].equals(list.get(i).getTitle())){
                        tv_InfoW1.setText(list.get(i).getTitle());
                        if (not_first_time_showing_info_window) {
                            if(!list.get(i).getSnippet().contains("salon")){
                                //Picasso.get().load(list.get(i).getSnippet()).into(imageView);
                            }
                        } else {
                            not_first_time_showing_info_window = true;
                            if(!list.get(i).getSnippet().contains("salon")){
                                //Picasso.get().load(list.get(i).getSnippet()).into(imageView, new InfoWindowRefresher(marker));
                            }
                        }
                    }
                }
            }
             for(MarkerOptions mkr : list){
                if(tempArray[0].equals(mkr.getTitle())){
                    tv_InfoW1.setText(mkr.getTitle());
                    // загружаем фото, если не по умолчанию
                    if(marker.isInfoWindowShown()){
                        marker.hideInfoWindow();
                    }
                    if(!mkr.getSnippet().contains("salon")) {
                        if (not_first_time_showing_info_window) {
                            Glide.with(context).load(mkr.getSnippet()).apply(new RequestOptions().circleCrop()).into(imageView);
                        } else {
                            not_first_time_showing_info_window = true;
                            Picasso.get().load(mkr.getSnippet()).into(imageView, new InfoWindowRefresher(marker, imageView));
                        }
                    }
                }
            }*/
