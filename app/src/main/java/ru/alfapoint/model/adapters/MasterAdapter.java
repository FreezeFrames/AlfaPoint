package ru.alfapoint.model.adapters;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import ru.alfapoint.R;
import ru.alfapoint.model.Master;
import ru.alfapoint.model.interfaces.MasterInterface;
import ru.alfapoint.model.interfaces.PositionCallback;

public class MasterAdapter extends RecyclerView.Adapter<MasterAdapter.MyViewHolder> {
    List<Master> list;
    Context ctx;
    MasterInterface callback;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView name;
        ConstraintLayout clMaster;

        public MyViewHolder(View v){
            super(v);
            name = v.findViewById(R.id.textView28);
            imageView = v.findViewById(R.id.imageView7);
            clMaster = v.findViewById(R.id.clMaster);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.getImagePos(getAdapterPosition());
                }
            });
            clMaster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.getMasterInfo(getAdapterPosition());
                }
            });
        }
    }

    public MasterAdapter(List<Master> myList, Context context, MasterInterface ck){
        this.list = myList;
        this.ctx = context;
        this.callback = ck;
    }

    @Override
    public MasterAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_master,parent,false);
        return new MasterAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MasterAdapter.MyViewHolder holder, int position) {
        holder.name.setText(list.get(position).getMasteName());
        if(list.get(position).getMasterPhoto().equals("")){
            Glide.with(ctx).load(R.drawable.ic_user).into(holder.imageView);
        }else{
            //Glide.with(ctx).load(list.get(position).getMasterPhoto()).into(holder.imageView);
            Glide.with(ctx).load(list.get(position).getMasterPhoto()).apply(RequestOptions.circleCropTransform()).into(holder.imageView);
        }
        //holder.cost.setText(list.get(position).getCost());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
