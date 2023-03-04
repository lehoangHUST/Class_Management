package com.example.class_management_android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.class_management_android.ChatFragment;
import com.example.class_management_android.R;
import com.example.class_management_android.model.Student;

import java.util.List;

public class Student_Chat_Adapter extends ArrayAdapter<Student>
{
    private Context mContext;
    private int mResourceId;
    private List<Student> mListStudents;

    public Student_Chat_Adapter(Context context, int resource, List<Student> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResourceId = resource;
        this.mListStudents = objects;
    }

    private class viewHolder
    {
        TextView tvNameStudent;
        ImageView profile_image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        viewHolder holder;
        if(convertView == null || convertView.getTag() == null){
            convertView = View.inflate(mContext, mResourceId,null);

            holder = new Student_Chat_Adapter.viewHolder();
            holder.tvNameStudent = (TextView) convertView.findViewById(R.id.username);
            holder.profile_image = (ImageView) convertView.findViewById(R.id.profile_image);
            convertView.setTag(holder);
        }else
            holder = (viewHolder) convertView.getTag();

        Student student = mListStudents.get(position);

        holder.tvNameStudent.setText(student.getName());
        holder.profile_image.setImageResource(R.drawable.image_user);

        return convertView;
    }

}
