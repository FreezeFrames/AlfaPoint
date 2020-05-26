package ru.alfapoint.model.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import ru.alfapoint.R;
import ru.alfapoint.model.InfoClass;
import ru.alfapoint.utils.FBHelper;

public class MasterAdapter2 extends RecyclerView.Adapter<MasterAdapter2.MyViewHolder> {
    List<InfoClass> list;
    private int lastCheckedPosition = -1;
    Context ctx;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tvCost;
        public RadioButton masterName;
        public ImageView imageView28;

        public MyViewHolder(View v){
            super(v);
            tvCost = v.findViewById(R.id.textView56);
            masterName = v.findViewById(R.id.radioButton2);
            imageView28 = v.findViewById(R.id.imageView28);
            masterName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastCheckedPosition = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
        }
    }

    public MasterAdapter2(List<InfoClass> myList,Context context){
        this.list = myList;
        ctx = context;
    }

    @Override
    public MasterAdapter2.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_master3,parent,false);
        return new MasterAdapter2.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MasterAdapter2.MyViewHolder holder, int position) {
        holder.tvCost.setText(list.get(position).getStr2() + " руб.");
        holder.masterName.setText(list.get(position).getStr1());
        holder.masterName.setChecked(position == lastCheckedPosition);
        FBHelper.GetMasters().document(list.get(position).getStr3()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        if(!document.get("photo").toString().equals("")){
                            Glide.with(ctx).load(document.get("photo")).apply(RequestOptions.circleCropTransform()).into(holder.imageView28);
                        }
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
