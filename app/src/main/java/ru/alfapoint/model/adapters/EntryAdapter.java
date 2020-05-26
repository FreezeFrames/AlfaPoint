package ru.alfapoint.model.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.alfapoint.R;
import ru.alfapoint.model.Entry;
import ru.alfapoint.model.interfaces.EntryCallback;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.MyViewHolder> {
    EntryCallback callback;
    List<Entry> list;
    Context ctx;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView tvDate, tvTime, tvService, tvMasterName, tvCost, tvAdr, tvPhone, tvStatus,tvSaloon, tvCity;
        public ImageView deleteImage, editImage;

        public MyViewHolder(View v){
            super(v);
            tvDate = v.findViewById(R.id.textView69);
            tvTime = v.findViewById(R.id.textView70);
            tvService = v.findViewById(R.id.textView71);
            tvMasterName = v.findViewById(R.id.textView72);
            tvCost = v.findViewById(R.id.textView73);
            tvAdr = v.findViewById(R.id.textView50);
            tvPhone = v.findViewById(R.id.textView91);
            tvStatus = v.findViewById(R.id.textView93);
            tvSaloon = v.findViewById(R.id.textView94);
            deleteImage = v.findViewById(R.id.imageView20);
            editImage = v.findViewById(R.id.imageView19);
            tvCity = v.findViewById(R.id.textView119);
            tvPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onPhoneClick(getAdapterPosition());
                }
            });
            tvAdr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onMapClick(getAdapterPosition());
                }
            });
            deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onDelete(getAdapterPosition());
                }
            });
            editImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onEdit(getAdapterPosition());
                }
            });
        }
    }

    public EntryAdapter(List<Entry> myList, Context context, EntryCallback ck){
        this.list = myList;
        this.ctx = context;
        this.callback = ck;
    }

    @Override
    public EntryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_entry, parent,false);
        return new EntryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EntryAdapter.MyViewHolder holder, int position) {
        holder.tvDate.setText(list.get(position).getParsDate());
        holder.tvTime.setText(list.get(position).getTime());
        holder.tvService.setText(list.get(position).getService());
        holder.tvMasterName.setText(list.get(position).getMasterName());
        holder.tvCost.setText(String.valueOf(list.get(position).getCost()) + " p.");
        holder.tvPhone.setText(list.get(position).getSaloonPhone());
        holder.tvAdr.setText(list.get(position).getSaloonAddress());
        holder.tvSaloon.setText(list.get(position).getSaloonName());
        holder.tvStatus.setText(list.get(position).getStatus());
        holder.tvCity.setText(list.get(position).getSaloonCity());
        if(list.get(position).getStatus().equals("Заявка одобрена")){
            holder.tvStatus.setTextColor(ctx.getResources().getColor(R.color.colorGreen));
        }else{
            holder.tvStatus.setTextColor(ctx.getResources().getColor(R.color.colorRed));
        }

        //fonts
        holder.tvDate.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-reg.ttf"));
        holder.tvTime.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-bold.ttf"));
        holder.tvService.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-bold.ttf"));
        holder.tvSaloon.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-bold.ttf"));
        holder.tvCost.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-bold.ttf"));
        holder.tvAdr.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-reg.ttf"));
        holder.tvPhone.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-reg.ttf"));
        holder.tvCity.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-reg.ttf"));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
