package com.example.class_management_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Student_Or_Admin extends AppCompatActivity {

    Button btn_admin, btn_student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_or_admin);

        btn_admin = findViewById(R.id.btn_admin);
        btn_student = findViewById(R.id.btn_student);

        // Nếu bấm Admin sẽ chuyển qua màn hình LoginActivity
        btn_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Student_Or_Admin.this, LoginActivity.class));
            }
        });

        // Nếu bấm Admin sẽ chuyển qua màn hình LoginActivity
        btn_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Student_Or_Admin.this, Login_Student.class));
            }
        });

    }


}