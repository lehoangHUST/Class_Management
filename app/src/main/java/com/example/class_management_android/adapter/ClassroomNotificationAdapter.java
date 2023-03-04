package com.example.class_management_android.adapter;

import com.example.class_management_android.R;
import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.class_management_android.model.Classroom;

import java.util.List;

public class ClassroomNotificationAdapter extends ArrayAdapter<Classroom>
{
    private Context mContext;
    private int mResourceId;
    private List<Classroom> mListClassrooms;

    // constructor
    public ClassroomNotificationAdapter(@NonNull Context context, int resource, @NonNull List<Classroom> objects)
    {
        super(context, resource, objects);
        this.mContext = context;
        this.mResourceId = resource;
        this.mListClassrooms = objects;
    }

    // create a holder to hold view objects in the list view
    private class viewHolder
    {
        TextView tvInformationClass, tvIdAndNameClass, tvTimeClassStart, tvTimeClassEnd;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        ClassroomNotificationAdapter.viewHolder holder;
        if(convertView == null || convertView.getTag() == null)
        {
            convertView = View.inflate(this.mContext,this.mResourceId,null);
            holder = new ClassroomNotificationAdapter.viewHolder();
            holder.tvIdAndNameClass = (TextView) convertView.findViewById(R.id.tvIdAndNameClass);
            holder.tvTimeClassStart = (TextView) convertView.findViewById(R.id.tvTimeClassStart);
            holder.tvTimeClassEnd = (TextView) convertView.findViewById(R.id.tvTimeClassEnd);
            holder.tvInformationClass = (TextView) convertView.findViewById(R.id.tvInformationClass);
            convertView.setTag(holder);
        }
        else
            holder = (ClassroomNotificationAdapter.viewHolder) convertView.getTag();
        Classroom classroom = this.mListClassrooms.get(position);
        holder.tvIdAndNameClass.setText(classroom.getId() +" - "+ classroom.getSubjectName());
        holder.tvTimeClassStart.setText(classroom.getStartTime());
        holder.tvTimeClassEnd.setText(classroom.getEndTime());
        holder.tvInformationClass.setText(classroom.getWeekDay().toString() +", " + "Classroom: " + classroom.getClassroomName());
        return convertView;
    }
}
