package com.example.class_management_android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.class_management_android.R;
import com.example.class_management_android.model.Attendance;
import com.example.class_management_android.model.Attendance_Student;
import com.example.class_management_android.model.Student;

import java.util.List;

public class AttendanceStudentAdapter extends ArrayAdapter {
    private Context mContext;
    private int mResourceId;
    private List<Attendance_Student> mListStudents;

    public AttendanceStudentAdapter(Context context, int resource, List<Attendance_Student> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResourceId = resource;
        this.mListStudents = objects;
    }

    private class viewHolder
    {
        TextView tvIdStudent, tvNameStudent, tvMissedStudent, tvAttendStudent;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        AttendanceStudentAdapter.viewHolder holder;
        if(convertView == null || convertView.getTag() == null)
        {
            convertView = View.inflate(this.mContext,this.mResourceId,null);
            holder = new AttendanceStudentAdapter.viewHolder();
            holder.tvIdStudent = (TextView) convertView.findViewById(R.id.tvIdStudent);
            holder.tvNameStudent = (TextView) convertView.findViewById(R.id.tvNameStudent);
            holder.tvMissedStudent = (TextView) convertView.findViewById(R.id.tvMissedStudent);
            holder.tvAttendStudent = (TextView) convertView.findViewById(R.id.tvAttendStudent);

            convertView.setTag(holder);
        }
        else
            holder = (AttendanceStudentAdapter.viewHolder) convertView.getTag();
        Attendance_Student attendance_student = this.mListStudents.get(position);
        holder.tvIdStudent.setText(attendance_student.getId());
        holder.tvNameStudent.setText(attendance_student.getName());
        holder.tvMissedStudent.setText(Integer.toString(attendance_student.getMissed()));
        holder.tvAttendStudent.setText(Integer.toString(attendance_student.getAttend()));
        return convertView;
    }

}
