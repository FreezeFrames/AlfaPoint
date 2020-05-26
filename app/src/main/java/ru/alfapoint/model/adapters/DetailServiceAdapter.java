package ru.alfapoint.model.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import java.util.List;
import ru.alfapoint.R;
import ru.alfapoint.model.InfoClass;

public class DetailServiceAdapter extends RecyclerView.Adapter<DetailServiceAdapter.MyViewHolder> {
    List<InfoClass> list;
    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView name;
        EditText editText;

        public MyViewHolder(View v){
            super(v);
            name =  v.findViewById(R.id.textView33);
            editText =  v.findViewById(R.id.editText10);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }
                @Override
                public void afterTextChanged(Editable editable) {
                    list.get(getAdapterPosition()).setStr2(editable.toString());
                }
            });
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        hideKeyboard(v);
                    }
                }
            });
        }
    }

    public DetailServiceAdapter(Context ctx, List<InfoClass> myList){
        this.list = myList;
        mContext = ctx;
    }

    @Override
    public DetailServiceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_service2,parent,false);
        return new DetailServiceAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DetailServiceAdapter.MyViewHolder holder, int position) {
        holder.name.setText(list.get(position).getStr1());
        holder.editText.setText(list.get(position).getStr2());
        if(list.get(position).getCheckInfo()){
            holder.editText.setEnabled(true);
        }else{
            holder.editText.setEnabled(false);
        }
        switch (position){
            case 3:
                holder.editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                holder.editText.setImeOptions(EditorInfo.IME_FLAG_FORCE_ASCII);
                break;
            case 4:
                holder.editText.setEnabled(false);
                break;
            case 5:
                holder.editText.setEnabled(false);
            break;
        }

        //fonts
        holder.name.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/mon-reg.ttf"));
        holder.editText.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/mon-reg.ttf"));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void enableEdit(){
        for(int i=0; i<list.size(); i++){
            list.get(i).enableCheckInfo();
        }
        notifyDataSetChanged();
    }

    public void disableEdit(){
        for(int i=0; i<list.size(); i++){
            list.get(i).disableCheckInfo();
        }
        notifyDataSetChanged();
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)mContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
