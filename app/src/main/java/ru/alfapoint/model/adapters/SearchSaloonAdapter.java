package ru.alfapoint.model.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ru.alfapoint.R;
import ru.alfapoint.model.SaloonData;
import ru.alfapoint.model.interfaces.PositionCallback;

public class SearchSaloonAdapter extends RecyclerView.Adapter<SearchSaloonAdapter.MyViewHolder> {
    private Context context;
    private List<SaloonData> saloonList;
    PositionCallback call;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView saloonName, saloonType, saloonAdr, salonRasp, service, cost;
        public Button write;

        //public ImageView imageView;

        public MyViewHolder(View v){
            super(v);
            saloonName = (TextView) v.findViewById(R.id.textView76);
            saloonType = (TextView) v.findViewById(R.id.textView78);
            saloonAdr = (TextView) v.findViewById(R.id.textView79);
            salonRasp = (TextView) v.findViewById(R.id.textView80);
            service = (TextView) v.findViewById(R.id.textView82);
            cost = (TextView) v.findViewById(R.id.textView81);
            write = v.findViewById(R.id.button45);
            write.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    call.getPosition(getAdapterPosition());
                }
            });
        }
    }

    public SearchSaloonAdapter(Context context, List<SaloonData> myList, PositionCallback callback){
        this.saloonList = myList;
        this.context = context;
        this.call = callback;
    }


    @Override
    public SearchSaloonAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_saloon2,parent,false);
        return new SearchSaloonAdapter.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(SearchSaloonAdapter.MyViewHolder holder, int position) {
        holder.service.setText(saloonList.get(position).getServiceName());
        holder.cost.setText(saloonList.get(position).getServiceCost() + " р.");
        holder.saloonName.setText(saloonList.get(position).getSaloonName());
        holder.saloonType.setText(saloonList.get(position).getDescription());
        holder.saloonAdr.setText(saloonList.get(position).getAddress());
        holder.salonRasp.setText(saloonList.get(position).getWorkTime());

        //fonts
        holder.saloonName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/mon-bold.ttf"));
        holder.cost.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/mon-bold.ttf"));
        holder.salonRasp.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/mon-reg.ttf"));
        holder.saloonType.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/mon-reg.ttf"));
        holder.saloonAdr.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/mon-reg.ttf"));
        holder.service.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/mon-reg.ttf"));
        holder.write.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/mon-reg.ttf"));
    }

    @Override
    public int getItemCount() {
        return saloonList.size();
    }

    public void update(List<SaloonData> myList){
        this.saloonList = myList;
        notifyDataSetChanged();
    }
}
