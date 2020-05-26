package ru.alfapoint.model.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class SpinnerAdapter2 extends ArrayAdapter {

    public SpinnerAdapter2(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    @Override
    public boolean isEnabled(int position) {
        return super.isEnabled(position);

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);

    }
}
