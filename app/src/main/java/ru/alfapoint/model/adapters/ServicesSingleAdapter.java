package ru.alfapoint.model.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.List;

import ru.alfapoint.R;
import ru.alfapoint.model.InfoClass;
import ru.alfapoint.model.interfaces.InfoClassCallback;

public class ServicesSingleAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private int lastCheckedPosition = -1;
    InfoClassCallback callback;
    private List<InfoClass> list;
    Context mCon;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public CheckBox name;
        public MyViewHolder(View v){
            super(v);
            name = v.findViewById(R.id.checkBox9);
            name.setTypeface(Typeface.createFromAsset(mCon.getAssets(), "fonts/mon-reg.ttf"));
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //callback.getUID(list.get(getAdapterPosition()));
                    callback.getInfoClass(list.get(getAdapterPosition()));
                    lastCheckedPosition = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
        }
    }

    public ServicesSingleAdapter(Context ctx, List<InfoClass> myList, InfoClassCallback ck){
        this.list = myList;
        mCon = ctx;
        callback = ck;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog3,parent,false);
        return new ServicesSingleAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ServicesSingleAdapter.MyViewHolder holder0 = (ServicesSingleAdapter.MyViewHolder)viewHolder;
        holder0.name.setText(list.get(position).getStr1());
        holder0.name.setChecked(position == lastCheckedPosition);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

}
