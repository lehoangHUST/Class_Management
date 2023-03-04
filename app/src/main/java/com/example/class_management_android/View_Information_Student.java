package com.example.class_management_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.example.class_management_android.model.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class View_Information_Student extends AppCompatActivity {

    // Lấy ra các trường thông tin của học sinh cho giáo viên xem
    private TextView titleStudent;
    private EditText etID, etName, etBirthday, etPhoneNumber, etEmail, etsex, etgroup, etmidterm, etfinalterm;
    private String classID, mId;
    private DatabaseReference mDatabase;
    private List<Student> mListStudents = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_information_student);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        // Customize the back button
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back_action);
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        // edit title in action bar
        actionBar.setTitle("");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1E313E"))); // dark_blue

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference("student");


        titleStudent = (TextView) findViewById(R.id.TitleStudent);
        etID = (EditText) findViewById(R.id.IDStudent);
        etName = (EditText) findViewById(R.id.etName);
        etBirthday = (EditText) findViewById(R.id.etBirthday);
        etPhoneNumber = (EditText) findViewById(R.id.etPhone);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etgroup = (EditText) findViewById(R.id.etGroup);
        etmidterm = (EditText) findViewById(R.id.etMid);
        etfinalterm = (EditText) findViewById(R.id.etFinal);
        etsex = (EditText) findViewById(R.id.etSex);
        classID = getIntent().getStringExtra("idClassroom");
        mId = getIntent().getStringExtra("idStudent");


    }


    @Override
    protected void onResume(){
        super.onResume();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListStudents.clear();
                for(DataSnapshot dataSnapshot : snapshot.child(classID).getChildren()){
                    Student student = dataSnapshot.getValue(Student.class);
                    mListStudents.add(student);
                }

                // Lấy sinh viên đã được bấm nút chọn sinh viên đó
                Student student = getStudent(mListStudents, mId);
                etID.setText(student.getId());
                etID.setEnabled(false);
                etName.setText(student.getName());
                etName.requestFocus();
                etPhoneNumber.setText(student.getPhoneNumber());
                etEmail.setText(student.getEmail());
                etgroup.setText(student.getGroup());
                etmidterm.setText(student.getMidterm());
                etfinalterm.setText(student.getFinalterm());
                etBirthday.setText(student.getBirthday());
                etsex.setText(Integer.toString(student.getGender()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private Student getStudent(List<Student> listStudent , String id){
        Student result = new Student();
        for(Student i : listStudent){
            if(i.getId().equals(id)){
                result = i;
                break;
            }
        }
        return result;
    }

}