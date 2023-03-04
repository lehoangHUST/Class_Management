package com.example.class_management_android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.class_management_android.R;
import com.example.class_management_android.model.Student;

import java.util.List;

public class StudentAdapter extends ArrayAdapter<Student>
{
    private Context mContext;
    private int mResourceId;
    private List<Student> mListStudents;

    public StudentAdapter(Context context, int resource, List<Student> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResourceId = resource;
        this.mListStudents = objects;
    }

    private class viewHolder
    {
        TextView tvOrderStudent, tvIdStudent, tvNameStudent, tvBirthdayStudent;
        ImageView ivGenderStudent;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        viewHolder holder;
        if(convertView == null || convertView.getTag() == null){
            convertView = View.inflate(mContext, mResourceId,null);

            holder = new viewHolder();
            holder.tvOrderStudent = (TextView) convertView.findViewById(R.id.tvOrderStudent);
            holder.tvIdStudent = (TextView) convertView.findViewById(R.id.tvIdStudent);
            holder.tvNameStudent = (TextView) convertView.findViewById(R.id.tvNameStudent);
            holder.tvBirthdayStudent = (TextView) convertView.findViewById(R.id.tvBirthdayStudent);
            holder.ivGenderStudent = (ImageView) convertView.findViewById(R.id.ivGenderStudent);
            convertView.setTag(holder);
        }else
            holder = (viewHolder) convertView.getTag();

        Student student = mListStudents.get(position);

        holder.tvOrderStudent.setText(String.valueOf(position + 1));
        holder.tvIdStudent.setText(student.getId());
        holder.tvNameStudent.setText(student.getName());

        holder.tvBirthdayStudent.setText(student.getBirthday());
        if(student.getGender() == 0)
            holder.ivGenderStudent.setImageResource(R.mipmap.ic_female_student);
        else
            holder.ivGenderStudent.setImageResource(R.mipmap.ic_male_student);

        return convertView;
    }

}
