package ru.alfapoint.model.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import ru.alfapoint.R;
import ru.alfapoint.model.Entry;
import ru.alfapoint.model.interfaces.EntryCallbackByOperator;
import ru.alfapoint.utils.FBHelper;

public class SaloonAdapter3 extends RecyclerView.Adapter<SaloonAdapter3.MyViewHolder> {
    List<Entry> entryList;
    Context ctx;
    EntryCallbackByOperator callback;
    Boolean onBind;
    HashMap<String, Object> map = new HashMap<>();

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView tvTime, tvDate, tvMasterName, tvServiceName, tvClientName, tvClientPhone, tvStatus, tvCost, smsNotification, tvEmail;
        public AppCompatCheckBox checkBox;
        ImageView imageView22,imageView18, ivPhoneNotification;

        public MyViewHolder(View v){
            super(v);
            tvTime = v.findViewById(R.id.textView100);
            tvDate = v.findViewById(R.id.textView99);
            tvMasterName = v.findViewById(R.id.textView101);
            tvServiceName = v.findViewById(R.id.textView102);
            tvClientName = v.findViewById(R.id.textView103);
            tvClientPhone = v.findViewById(R.id.textView104);
            tvStatus = v.findViewById(R.id.textView105);
            tvCost = v.findViewById(R.id.textView106);
            checkBox = v.findViewById(R.id.checkBox16);
            smsNotification = v.findViewById(R.id.textView125);
            tvEmail = v.findViewById(R.id.textView126);
            imageView22 = v.findViewById(R.id.imageView22);
            imageView18 = v.findViewById(R.id.imageView18);
            ivPhoneNotification = v.findViewById(R.id.imageView30);
            // подтверждение
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!onBind) {
                        if(entryList.get(getAdapterPosition()).getStatus().equals("Заявка отклонена")){
                            Toast.makeText(ctx,"Редактирование запрещено!", Toast.LENGTH_SHORT).show();
                        }else{
                            map.clear();
                            String status;
                            if (isChecked) {
                                status = "Заявка одобрена";
                            } else {
                                status = "Заявка в обработке";
                            }
                            map.put("status", status);
                            FBHelper.GetEntries().document(entryList.get(getAdapterPosition()).getEntryID()).update(map);
                            entryList.get(getAdapterPosition()).setSatus(status);
                            callback.onAccept(entryList.get(getAdapterPosition()).getCost(), isChecked);
                            notifyDataSetChanged();
                        }
                    }
                }
            });
            //редкатирование
            imageView22.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!entryList.get(getAdapterPosition()).getStatus().equals("Заявка отклонена")){
                        callback.onEdit(getAdapterPosition());
                    }else {
                        Toast.makeText(ctx,"Редактирование запрещено!", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            // отказ
            imageView18.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if(!onBind) {
                    if(entryList.get(getAdapterPosition()).getStatus().equals("Заявка одобрена")){
                        callback.onDelete(entryList.get(getAdapterPosition()).getCost());
                    }else
                        callback.onDelete(0);
                    map.clear();
                    map.put("status", "Заявка отклонена");
                    FBHelper.GetEntries().document(entryList.get(getAdapterPosition()).getEntryID()).update(map);
                    entryList.get(getAdapterPosition()).setSatus("Заявка отклонена");
                    Toast.makeText(ctx,"Заявка отклонена!", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                }
                }
            });
            tvClientPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onPhoneCall(getAdapterPosition());
                }
            });
        }
    }

    public SaloonAdapter3(List<Entry> myList, Context context,EntryCallbackByOperator ck){
        this.ctx = context;
        this.callback = ck;
        this.entryList = myList;
    }

    @Override
    public SaloonAdapter3.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_saloon,parent,false);
        return new SaloonAdapter3.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SaloonAdapter3.MyViewHolder holder, int position) {
        onBind = true;
        holder.tvDate.setText(entryList.get(position).getParsDate());
        holder.tvTime.setText(entryList.get(position).getTime());
        holder.tvMasterName.setText(entryList.get(position).getMasterName());
        if(entryList.get(position).getStatus().equals("Заявка одобрена")){
            holder.checkBox.setChecked(true);
            holder.tvStatus.setTextColor(ctx.getResources().getColor(R.color.colorGreen));
        }else{
            holder.checkBox.setChecked(false);
            holder.tvStatus.setTextColor(ctx.getResources().getColor(R.color.colorRed));
        }
        if(entryList.get(position).getPhoneNotification()){
            holder.ivPhoneNotification.setVisibility(View.VISIBLE);
            holder.ivPhoneNotification.setColorFilter(ctx.getResources().getColor(R.color.colorRed));
        }else{
            holder.ivPhoneNotification.setVisibility(View.GONE);
        }
        holder.tvEmail.setText("Email: " + entryList.get(position).getClientEmail());

        if(entryList.get(position).getSMSNotification()){
            holder.smsNotification.setVisibility(View.VISIBLE);
        }else{
            holder.smsNotification.setVisibility(View.GONE);
        }
        holder.tvStatus.setText(entryList.get(position).getStatus());
        holder.tvServiceName.setText(entryList.get(position).getService());
        holder.tvClientName.setText(entryList.get(position).getClientName());
        holder.tvClientPhone.setText(entryList.get(position).getClientPhone());
        holder.tvCost.setText(String.valueOf(entryList.get(position).getCost()) + " p.");

        //fonts
        holder.tvDate.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-reg.ttf"));
        holder.tvTime.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-bold.ttf"));
        holder.tvMasterName.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-bold.ttf"));
        holder.tvServiceName.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-bold.ttf"));
        holder.tvCost.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-bold.ttf"));
        holder.tvClientName.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-reg.ttf"));
        holder.tvClientPhone.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-reg.ttf"));
        holder.tvStatus.setTypeface(Typeface.createFromAsset(ctx.getAssets(), "fonts/mon-reg.ttf"));
        onBind = false;
    }

    @Override
    public int getItemCount() {
        return entryList.size();
    }

}
