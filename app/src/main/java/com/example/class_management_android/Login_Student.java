package com.example.class_management_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.class_management_android.model.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Login_Student extends AppCompatActivity {
    private static final String ID_USER = "remember";
    private boolean isValid = false;

    private EditText edtID_student, edtPassword_Student;
    private Button btnLogin_Student;
    private String id_student, password_student, studentID;
    private CheckBox checkBox;
    private ArrayList<Student> students = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_student);

        ActivityCompat.requestPermissions(this, new String []{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        edtID_student = (EditText) findViewById(R.id.edtID_student);
        edtPassword_Student = (EditText) findViewById(R.id.edtPassword_Student);
        btnLogin_Student = (Button) findViewById(R.id.btnLogin_Student);
        checkBox = (CheckBox)findViewById(R.id.show_password_student);

        // Save email and password
        CheckBox checkRemember = (CheckBox) findViewById(R.id.remember_me_student);
        SharedPreferences sharedPreferences = getSharedPreferences(ID_USER, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String id_student = sharedPreferences.getString("IdStudent", "");
        String password_student = sharedPreferences.getString("PassStudent", "");
        if (sharedPreferences.contains("checked") && sharedPreferences.getBoolean("checked", false) == true){
            checkRemember.setChecked(true);
        }
        else{
            checkRemember.setChecked(false);
        }

        edtID_student.setText(id_student);
        edtPassword_Student.setText(password_student);

        btnLogin_Student.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String id_student = edtID_student.getText().toString();
                String password_student = edtPassword_Student.getText().toString();

                if (isConnected() == false)
                {
                    Toast.makeText(Login_Student.this, "Internet not connected sucessful.", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(Login_Student.this, "Internet avaliable.", Toast.LENGTH_SHORT).show();
                    readData();
                    if (checkRemember.isChecked()) {
                        editor.putBoolean("checked", true);
                        editor.apply();
                        StoreDataUsingSharedPref(id_student, password_student);
                    } else {
                        getSharedPreferences(ID_USER, MODE_PRIVATE).edit().clear().commit();
                    }

                    isValid = checkLogin(edtID_student.getText().toString(), edtPassword_Student.getText().toString());

                    if (isValid) {
                        Intent i = new Intent(Login_Student.this, MainStudentActivity.class);
                        i.putExtra("StudentID", studentID);
                        startActivity(i);
                    } else {
                        Toast.makeText(Login_Student.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        // Che hoặc hiện thị mật khẩu lên cho người dùng xem
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    // Show password (hiện thị mật khẩu)
                    edtPassword_Student.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else
                {
                    // Hide password (che mật khẩu
                    edtPassword_Student.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });


    }

    // Hàm check login đăng nhập của app điểm danh
    private boolean checkLogin(String id_student, String password_student)
    {
        // So sánh với kết quả đã có trong firebase hay chưa
        for (Student student: students)
        {
            if (student.getId().equals(id_student) && student.getEmail().equals(password_student)) {
                studentID = student.getId();
                return true;
            }
        }
        return false;

    }


    // Sử dụng firebase để tiến hành đăng nhập
    private void readData()
    {
        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = mFirebaseInstance.getReference();

        databaseReference.child("student").child("1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                students.clear();
                for (DataSnapshot snap:snapshot.getChildren()){
                    Student student = snap.getValue(Student.class);
                    students.add(student);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void StoreDataUsingSharedPref(String id_student, String password_student) {
        SharedPreferences.Editor editor =  getSharedPreferences(ID_USER, MODE_PRIVATE).edit();
        editor.putString("IdStudent", id_student);
        editor.putString("PassStudent", password_student);
        editor.apply();
    }

    private boolean isConnected(){
        // Kiểm tra kết nối internet
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);

        return connected;
    }

}