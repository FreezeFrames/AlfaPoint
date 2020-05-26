package ru.alfapoint.model.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.alfapoint.R;
import ru.alfapoint.model.Service;

public class ServicesAdapter2 extends RecyclerView.Adapter<ServicesAdapter2.MyViewHolder>{
    List<Service> list;
    Context ctx;


    public ServicesAdapter2(Context mCtx, List<Service> myList){
        this.list = myList;
        this.ctx = mCtx;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name, count;


        public MyViewHolder(View v){
            super(v);
            name = v.findViewById(R.id.textView6);
            count = v.findViewById(R.id.textView51);
            name.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-reg.ttf"));
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView2 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_service,viewGroup,false);
        return new ServicesAdapter2.MyViewHolder(itemView2);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        holder.name.setText(list.get(i).getServiceName());
        holder.count.setText(String.valueOf(list.get(i).getMastersCount()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void update(List<Service> myList){
        this.list = myList;
        notifyDataSetChanged();
    }
}
