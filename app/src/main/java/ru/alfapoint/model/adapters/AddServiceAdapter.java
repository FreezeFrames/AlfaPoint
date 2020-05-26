package ru.alfapoint.model.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import ru.alfapoint.R;
import ru.alfapoint.model.Service;
import ru.alfapoint.model.interfaces.AddServiceCallback;
import ru.alfapoint.utils.DialogHelper;
import ru.alfapoint.model.interfaces.UIDCallback;
import ru.alfapoint.utils.FBHelper;

public class AddServiceAdapter extends RecyclerView.Adapter<AddServiceAdapter.MyViewHolder> {
    AddServiceCallback callback;
    List<Service> list;
    Context ctx;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView masterName, cost, serviceName;
        public ImageView remove;
        //public Button rasp;
        public View viewSRF4;

        public MyViewHolder(final View v){
            super(v);

            serviceName = v.findViewById(R.id.textView23);
            masterName = v.findViewById(R.id.textView25);
            cost = v.findViewById(R.id.textView24);
            //rasp = v.findViewById(R.id.button8);
            remove = v.findViewById(R.id.imageView4);
            viewSRF4 = v.findViewById(R.id.viewSRF4);

            //fonts
            serviceName.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-reg.ttf"));
            masterName.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-reg.ttf"));
            cost.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-reg.ttf"));
            //rasp.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-reg.ttf"));

            masterName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.addMaster(getAdapterPosition());
                }
            });
            cost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogHelper.Dialog2(ctx, "Укажите стоимость услуги","Стоимость", new UIDCallback() {
                        @Override
                        public void getUID(String string) {
                            callback.addCost(string, getAdapterPosition());
                        }
                    });
                }
            });
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //if(list.size()>1){
                        callback.removeService(getAdapterPosition());
                    //}
                }
            });

            /*rasp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.checkRasp(getAdapterPosition());
                }
            });*/
        }
    }

    public AddServiceAdapter(List<Service> myListEntriesAdapter, Context mContext, AddServiceCallback clickers){
        this.list = myListEntriesAdapter;
        this.callback = clickers;
        this.ctx = mContext;
    }

    @Override
    public AddServiceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_service,parent,false);
        return new AddServiceAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.serviceName.setText(list.get(position).getServiceName());
        //if(!list.get(position).getMasterName().equals("")){
            holder.masterName.setText(list.get(position).getMasterName());
        Log.e("master",list.get(position).getMasterName());
        //}
        //if(list.get(position).getCost()!=0){
            holder.cost.setText(String.valueOf(list.get(position).getCost())+" р.");
        //}
        //if(list.get(position).getRaspChecked()){
            //holder.rasp.setBackground(ctx.getResources().getDrawable(R.drawable.shape_button_green));
        //}else{
            //holder.rasp.setBackground(ctx.getResources().getDrawable(R.drawable.shape_button2));
        //}
        if(list.get(position).getCost()!=0 && !list.get(position).getMasterName().equals("")){ // && list.get(position).getRaspChecked()
            holder.viewSRF4.setBackground(ctx.getResources().getDrawable(R.drawable.shape_rect_green));
            // услуга полностью готова
            HashMap<String,Object> fullCompleted = new HashMap();
            fullCompleted.put("fullCompleted",true);
            FBHelper.GetServices().document(list.get(position).getServiceID()).update(fullCompleted);
        }else{
            holder.viewSRF4.setBackground(ctx.getResources().getDrawable(R.drawable.shape_rect_grey));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void reload(List<Service> list){
        this.list = list;
        notifyDataSetChanged();
    }
}
