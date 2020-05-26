package ru.alfapoint.model.adapters;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.alfapoint.R;
import ru.alfapoint.model.InfoClass;

public class SaloonAdapter2 extends RecyclerView.Adapter<SaloonAdapter2.MyViewHolder> {
    List<InfoClass> listParent;
    List<InfoClass> listChild;
    Context ctx;
    ArrayList<Integer> counter = new ArrayList<Integer>();

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tvNumber, tvMasterName, tvCost;
        private RecyclerView rv;
        ConstraintLayout clSaloon3;

        public MyViewHolder(View v){
            super(v);
            tvNumber = (TextView) v.findViewById(R.id.textView87);
            tvMasterName = (TextView) v.findViewById(R.id.textView85);
            tvCost = (TextView) v.findViewById(R.id.textView86);
            rv = v.findViewById(R.id.rvMasterTime);
            clSaloon3 = v.findViewById(R.id.clSaloon3);
        }
    }

    public SaloonAdapter2(Context context, List<InfoClass> par, List<InfoClass> chil){
        this.listParent = par;
        this.listChild = chil;
        ctx = context;

    }

    @Override
    public SaloonAdapter2.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_saloon3, parent,false);
        return new SaloonAdapter2.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SaloonAdapter2.MyViewHolder holder, final int position) {
        final InnerAdapter innerAdapter = new InnerAdapter(listParent.get(position).getStr1(), listChild, ctx);
        holder.rv.setLayoutManager(new LinearLayoutManager(ctx));
        holder.rv.setHasFixedSize(true);

        //Log.e("counter",listParent.get(position).getStr3());
        holder.clSaloon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (counter.get(position) % 2 == 0) {
                    holder.rv.setVisibility(View.VISIBLE);
                } else {
                    holder.rv.setVisibility(View.GONE);
                }
                counter.set(position, counter.get(position) + 1);
            }
        });
        holder.rv.setAdapter(innerAdapter);

        holder.tvMasterName.setText(listParent.get(position).getStr1());
        holder.tvCost.setText(listParent.get(position).getStr2());
        holder.tvNumber.setText(listParent.get(position).getStr3());
        holder.tvMasterName.setTextColor(ctx.getResources().getColor(R.color.colorWhite));
        holder.tvNumber.setTextColor(ctx.getResources().getColor(R.color.colorWhite));
        holder.tvCost.setTextColor(ctx.getResources().getColor(R.color.colorWhite));

    }

    @Override
    public int getItemCount() {
        return listParent.size();
    }
    public void updateList(List<InfoClass> myList,List<InfoClass> myList1 ){
        this.listParent = myList;
        this.listChild = myList1;
        for (int i = 0; i < listParent.size(); i++) {
            counter.add(0);
        }
        //Log.e("size",String.valueOf(counter.size()));
        notifyDataSetChanged();
    }
}
