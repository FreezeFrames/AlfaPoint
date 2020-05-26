package ru.alfapoint.model.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;
import ru.alfapoint.R;
import ru.alfapoint.model.InfoClass;


public class InfoClassAdapter extends RecyclerView.Adapter<InfoClassAdapter.MyViewHolder>{
    List<InfoClass> list;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public ImageView imageView;

        public MyViewHolder(View v){
            super(v);
            name = v.findViewById(R.id.textView28);
            imageView = v.findViewById(R.id.imageView7);
        }
    }

    public InfoClassAdapter(List<InfoClass> myList){
        this.list = myList;
    }

    @Override
    public InfoClassAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_master,parent,false);
        return new InfoClassAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InfoClassAdapter.MyViewHolder holder, int position) {
        holder.name.setText(list.get(position).getStr1());
        if(list.get(position).getStr2()!=null){
            Picasso.get().load(list.get(position).getStr2()).into(holder.imageView);
       }else{
            holder.imageView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void update(List<InfoClass> ll){
        list = ll;
        notifyDataSetChanged();
    }
}
