package ru.alfapoint.model.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.alfapoint.R;
import ru.alfapoint.model.InfoClass;

public class InnerAdapter  extends RecyclerView.Adapter<InnerAdapter.ViewHolder>{
    Context ctx;
    public List<InfoClass> nameList = new ArrayList<>();

    public InnerAdapter(String masterName, List<InfoClass> list, Context context) {
        for(int i=0; i<list.size();i++){
            if(masterName.equals(list.get(i).getStr1())){
                nameList.add(list.get(i));
            }
        }
        ctx = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView time, service, cost;
        ImageView imageView13;

        public ViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.textView88);
            service = itemView.findViewById(R.id.textView89);
            cost = itemView.findViewById(R.id.textView90);
            imageView13 = itemView.findViewById(R.id.imageView13);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_mastertime, parent, false);

        InnerAdapter.ViewHolder vh = new InnerAdapter.ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(nameList.get(position).getCheckInfo()){
            holder.imageView13.setVisibility(View.VISIBLE);
        }
        holder.time.setText(nameList.get(position).getStr2());
        holder.service.setText(nameList.get(position).getStr3());
        holder.cost.setText(nameList.get(position).getStr4());
        holder.time.setTextColor(ctx.getResources().getColor(R.color.colorWhite));
        holder.service.setTextColor(ctx.getResources().getColor(R.color.colorWhite));
        holder.cost.setTextColor(ctx.getResources().getColor(R.color.colorWhite));
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

}
