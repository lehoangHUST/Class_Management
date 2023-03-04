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

public class ClassroomAdapter extends ArrayAdapter<Classroom>
{
    private Context mContext;
    private int mResourceId;
    private List<Classroom> mListClassrooms;

    // constructor
    public ClassroomAdapter(@NonNull Context context, int resource, @NonNull List<Classroom> objects)
    {
        super(context, resource, objects);
        this.mContext = context;
        this.mResourceId = resource;
        this.mListClassrooms = objects;
    }

    // create a holder to hold view objects in the list view
    private class viewHolder
    {
        TextView tvOrderClass, tvIdClass, tvNameClass, tvTimeClass, tvRoomClass;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        ClassroomAdapter.viewHolder holder;
        if(convertView == null || convertView.getTag() == null)
        {
            convertView = View.inflate(this.mContext,this.mResourceId,null);
            holder = new ClassroomAdapter.viewHolder();
            holder.tvOrderClass = (TextView) convertView.findViewById(R.id.tvOrderClass);
            holder.tvIdClass = (TextView) convertView.findViewById(R.id.tvIdClass);
            holder.tvNameClass = (TextView) convertView.findViewById(R.id.tvNameClass);
            holder.tvTimeClass = (TextView) convertView.findViewById(R.id.tvTimeClass);
            holder.tvRoomClass = (TextView) convertView.findViewById(R.id.tvRoomClass);
            convertView.setTag(holder);
        }
        else
            holder = (ClassroomAdapter.viewHolder) convertView.getTag();
        Classroom classroom = this.mListClassrooms.get(position);
        holder.tvOrderClass.setText(String.valueOf(position + 1));
        holder.tvIdClass.setText(classroom.getId());
        holder.tvNameClass.setText(classroom.getSubjectName());
        holder.tvTimeClass.setText(classroom.getStartTime()+ '-' + classroom.getEndTime());
        holder.tvRoomClass.setText(classroom.getClassroomName());
        return convertView;
    }
}
