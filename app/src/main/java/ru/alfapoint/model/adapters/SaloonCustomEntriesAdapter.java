package ru.alfapoint.model.adapters;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.alfapoint.R;
import ru.alfapoint.model.Entry;
import ru.alfapoint.model.InfoClass;

public class SaloonCustomEntriesAdapter extends RecyclerView.Adapter<SaloonCustomEntriesAdapter.MyViewHolder> {
    List<InfoClass> listParent;
    Context ctx;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tvMasterName, tvCost, tvNumber;

        public MyViewHolder(View v){
            super(v);
            tvNumber = (TextView) v.findViewById(R.id.textView87);
            tvMasterName = (TextView) v.findViewById(R.id.textView85);
            tvCost = (TextView) v.findViewById(R.id.textView86);
        }
    }
    public SaloonCustomEntriesAdapter(Context context, List<InfoClass> par){
        this.listParent = par;
        ctx = context;

    }

    @Override
    public SaloonCustomEntriesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_saloon3, parent,false);
        return new SaloonCustomEntriesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SaloonCustomEntriesAdapter.MyViewHolder holder, final int position) {

        holder.tvMasterName.setText(listParent.get(position).getStr1());
        holder.tvCost.setText(listParent.get(position).getStr2() + " p.");
        holder.tvNumber.setText(listParent.get(position).getStr3());

    }

    @Override
    public int getItemCount() {
        return listParent.size();
    }
}
