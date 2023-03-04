package com.example.class_management_android.adapter;


import com.example.class_management_android.R;
import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.class_management_android.model.Attendance;

import java.util.List;

public class AttendanceAdapter extends ArrayAdapter<Attendance>
{
    private Context mContext;
    private int mResourceid;
    private List<Attendance> mListAttendance;

    // constructor
    public AttendanceAdapter(@NonNull Context context, int resource, @NonNull List<Attendance> objects)
    {
        super(context, resource, objects);
        this.mContext = context;
        this.mResourceid = resource;
        this.mListAttendance = objects;
    }

    // create a holder to hold view objects in the list view
    private class viewHolder
    {
        TextView tvOrder, tvId, tvName;
        CheckBox checkBoxState;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        AttendanceAdapter.viewHolder holder;
        if(convertView == null || convertView.getTag() == null)
        {
            convertView = View.inflate(this.mContext,this.mResourceid,null);
            holder = new AttendanceAdapter.viewHolder();
            holder.tvOrder = (TextView) convertView.findViewById(R.id.tvOrderStudent);
            holder.tvId = (TextView) convertView.findViewById(R.id.tvIdStudent);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvNameStudent);
            holder.checkBoxState = (CheckBox) convertView.findViewById(R.id.checkBoxState);
            convertView.setTag(holder);
        }
        else
            holder = (AttendanceAdapter.viewHolder) convertView.getTag();
        Attendance attendance = this.mListAttendance.get(position);
        holder.tvOrder.setText(String.valueOf(position + 1));
        holder.tvId.setText(attendance.getIdStudent());
        holder.tvName.setText(attendance.getNameStudent());
        if(attendance.getState() == 1){
            holder.checkBoxState.setChecked(true);
        }else{
            holder.checkBoxState.setChecked(false);
        }
        return convertView;
    }
}

