package com.rsandhir.expenselog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter implements RadioGroup.OnCheckedChangeListener {

    private Context mContext;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    private ArrayList<String> Id = new ArrayList<String>();
    private ArrayList<String> Description = new ArrayList<String>();
    private ArrayList<String> Amount = new ArrayList<String>();
    private ArrayList<String> Date = new ArrayList<String>();
    private ArrayList<Boolean> Arr_Checked;

    public CustomAdapter(Context context, ArrayList<String> Id, ArrayList<String> Description, ArrayList<String> Amount, ArrayList<String> Date, ArrayList<Boolean> Arr_Checked) {
        this.mContext = context;
        this.Id = Id;
        this.Description = Description;
        this.Amount = Amount;
        this.Date = Date;
        this.Arr_Checked = Arr_Checked;
    }

    @Override
    public int getCount() {
        return Id.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final viewHolder holder;
        databaseHelper = new DatabaseHelper(mContext);
        LayoutInflater layoutInflater;
        if (convertView == null) {
            layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.custom_layout, null);
            holder = new viewHolder();
            holder.date = convertView.findViewById(R.id.tv_date);
            holder.Description = convertView.findViewById(R.id.tv_dis);
            holder.Amount = convertView.findViewById(R.id.tv_amt);
            holder.Arr_Checked = convertView.findViewById(R.id.cb1);
            convertView.setTag(holder);
        } else {
            holder = (viewHolder) convertView.getTag();
        }
        holder.date.setText(Date.get(position));
        holder.Description.setText(Description.get(position));
        holder.Amount.setText(Amount.get(position));
        holder.Arr_Checked.setId(position);
        holder.Arr_Checked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (Arr_Checked.get(id)) {
                    Arr_Checked.set(id, false);
                } else {
                    Arr_Checked.set(id, true);
                }
            }
        });

        holder.Arr_Checked.setChecked(Arr_Checked.get(position));
        return convertView;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }



    public class viewHolder {
        CheckBox Arr_Checked;
        TextView date;
        TextView Description;
        TextView Amount;
    }
}