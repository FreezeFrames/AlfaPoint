package ru.alfapoint.model.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.List;

import ru.alfapoint.R;
import ru.alfapoint.model.interfaces.BoolCallback;

public class ServicesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> list;
    Context mCon;
    BoolCallback callback;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public CheckBox name;
        public MyViewHolder(View v){
            super(v);
            name = v.findViewById(R.id.checkBox9);
            name.setTypeface(Typeface.createFromAsset(mCon.getAssets(), "fonts/mon-reg.ttf"));
            name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    callback.checkService(getAdapterPosition(),isChecked);
                }
            });
        }
    }

    public ServicesAdapter(Context ctx, List<String> myList, BoolCallback ck){
        this.list = myList;
        this.mCon = ctx;
        this.callback = ck;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog3,parent,false);
        return new ServicesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ServicesAdapter.MyViewHolder holder0 = (ServicesAdapter.MyViewHolder)viewHolder;
        holder0.name.setText(list.get(i));

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void update(List<String> myList){
        this.list = myList;
        notifyDataSetChanged();
    }
}
