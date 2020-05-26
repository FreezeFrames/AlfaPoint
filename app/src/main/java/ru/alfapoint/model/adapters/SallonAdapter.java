package ru.alfapoint.model.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import java.util.List;
import ru.alfapoint.R;
import ru.alfapoint.model.SaloonData;

public class SallonAdapter extends RecyclerView.Adapter<SallonAdapter.MyViewHolder>{

    private Context context;
    private List<SaloonData> saloonList;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView saloonName, saloonType, saloonAdr, dist, tvCountry;
        public ImageView imageView,imageView21;

        public MyViewHolder(View v){
            super(v);
            saloonName = (TextView) v.findViewById(R.id.textView);
            tvCountry = (TextView) v.findViewById(R.id.textView121);
            saloonType = (TextView) v.findViewById(R.id.textView2);
            saloonAdr = (TextView) v.findViewById(R.id.textView3);
            dist = (TextView) v.findViewById(R.id.textView4);
            imageView = v.findViewById(R.id.imageView);
            imageView21 = v.findViewById(R.id.imageView21);

        }
    }

    public SallonAdapter(Context context, List<SaloonData> myList){
        this.saloonList = myList;
        this.context = context;
    }


    @Override
    public SallonAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_sallon,parent,false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.saloonName.setText(saloonList.get(position).getSaloonName());
        holder.saloonType.setText(saloonList.get(position).getDescription());
        holder.saloonAdr.setText(saloonList.get(position).getAddress());
        holder.dist.setText(saloonList.get(position).getDistance());
        holder.tvCountry.setText(saloonList.get(position).getCountry() + ", " + saloonList.get(position).getCity());

        if(saloonList.get(position).getSaloonImage().contains("salon")){
            int imageId = context.getResources().getIdentifier(saloonList.get(position).getSaloonImage(), "drawable", context.getPackageName());
            Glide.with(context).load(imageId).into(holder.imageView);
        }else{
            Glide.with(context).load(saloonList.get(position).getSaloonImage()).apply(RequestOptions.circleCropTransform()).into(holder.imageView);
        }

        // premium card
        if(saloonList.get(position).isPremium()){
            holder.imageView21.setVisibility(View.VISIBLE);
        }else{
            holder.imageView21.setVisibility(View.GONE);
        }

        //fonts
        holder.saloonName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/mon-bold.ttf"));
        holder.dist.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/mon-bold.ttf"));
        holder.saloonType.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/mon-reg.ttf"));
        holder.saloonAdr.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/mon-reg.ttf"));
        holder.tvCountry.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/mon-reg.ttf"));
    }

    @Override
    public int getItemCount() {
        return saloonList.size();
    }

    public void update(List<SaloonData> myList){
        this.saloonList = myList;
        notifyDataSetChanged();
    }

    public void calculateDistance(){
        double d = SphericalUtil.computeDistanceBetween(new LatLng(59.801141,30.522425), new LatLng(60.02611575280627,30.341502428054813))/1000;
    }
}
