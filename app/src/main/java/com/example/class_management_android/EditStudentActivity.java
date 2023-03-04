package com.example.class_management_android;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.class_management_android.database.DbStudentHelper;
import com.example.class_management_android.model.Classroom;
import com.example.class_management_android.model.Student;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class EditStudentActivity extends AppCompatActivity
{
    private TextView tvTitle;
    private EditText etID, etName, etBirthday, etPhoneNumber, etEmail, etgroup, etmidterm, etfinalterm;
    private RadioButton radMale, radFemale;
    private Date mBirthday = new Date();
    private String mId;
    private String classID;
    private static String TAG = "EditStudentActivity";
    private List<Student> mListStudents;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseUser acct;
    private DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            etBirthday.setText(day + "/" + (month + 1) + "/" + year);
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);
            mBirthday = cal.getTime();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);

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

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        acct = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference("student");


        mListStudents = new ArrayList<>();
        tvTitle = (TextView) findViewById(R.id.tvTitleStudent);
        etID = (EditText) findViewById(R.id.etIDStudent);
        etName = (EditText) findViewById(R.id.etNameStudent);
        etBirthday = (EditText) findViewById(R.id.etBirthdayStudent);
        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etgroup = (EditText) findViewById(R.id.etGroup);
        etmidterm = (EditText) findViewById(R.id.etMidterm);
        etfinalterm = (EditText) findViewById(R.id.etFinalterm);
        radMale = (RadioButton) findViewById(R.id.radMaleStudent);
        radFemale = (RadioButton) findViewById(R.id.radFemaleStudent);
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
                if (mId == null) {
                    // ADD MODE
                    setDefaultInfo();
                } else {
                    // EDIT MODE
                    Student student = getStudent(mListStudents, mId);
                    tvTitle.setText(R.string.update_student);
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
                    if (student.getGender() == 0)
                        radFemale.setChecked(true);
                    else
                        radMale.setChecked(true);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    // create a menu for adding and editing mode
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mId == null)
            return super.onCreateOptionsMenu(menu);
        else {
            getMenuInflater().inflate(R.menu.menu_edit, menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                this.finish();
                return true;
            }
            case R.id.action_delete:
            {
                deleteStudent(mId);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // show a message to delete a student in db with student's id
    private void deleteStudent(final String id)
    {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(R.string.delete).setMessage(getString(R.string.delete_student_message) + " - Student's code: " + id + " ?")
                .setIcon(android.R.drawable.ic_delete)
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDatabase.child(classID).child(mId).setValue(null);
                        showToastMessage("Deleted!");
                        EditStudentActivity.this.finish();
                    }
                });
        b.create().show();
    }

    public void showDatePickerDialog(View v) {
        String strDate = etBirthday.getText() + "";
        String strArrtmp[] = strDate.split("/");
        int day = Integer.parseInt(strArrtmp[0]);
        int month = Integer.parseInt(strArrtmp[1]) - 1;
        int year = Integer.parseInt(strArrtmp[2]);
        DatePickerDialog datePicker = new DatePickerDialog(this, callback, year, month, day);
        datePicker.setTitle(R.string.ngaysinh);
        datePicker.show();
    }

    public void clear(View v) {
        if (mId == null) {
            // ADD MODE
            etID.setText("");
            etName.setText("");
            etPhoneNumber.setText("");
            etEmail.setText("");
            etgroup.setText("");
            etmidterm.setText("");
            etfinalterm.setText("");
            setDefaultInfo();
        } else {
            // EDIT MODE
            etName.setText("");
            etPhoneNumber.setText("");
            etEmail.setText("");
            etgroup.setText("");
            etmidterm.setText("");
            etfinalterm.setText("");
            Calendar cal = Calendar.getInstance();
            etBirthday.setText(getDateFormat(cal.getTime()));
            radMale.setChecked(true);
            etName.requestFocus();
        }

    }

    public void save(View v) {
        if (mId == null) {
            // ADD MODE
            addStudent();
        } else {
            // EDIT MODE
            updateStudent();
        }
    }

    private void addStudent() {
        String mId = etID.getText().toString().trim();
        if (mId.length() == 0) {
            etID.setError("?");
            etID.requestFocus();
            return;
        }
        String name = etName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString();
        String email = etEmail.getText().toString();
        String group = etgroup.getText().toString();
        String midterm = etmidterm.getText().toString();
        String finalterm = etfinalterm.getText().toString();
        if (name.length() == 0) {
            etName.setError("?");
            etName.requestFocus();
            return;
        }
        Student student = new Student();
        student.setId(mId);
        student.setName(name);
        student.setBirthday(getDateFormat(mBirthday));
        if (radMale.isChecked())
            student.setGender(1);
        else
            student.setGender(0);
        student.setClassId(classID);
        student.setPhoneNumber(phoneNumber);
        student.setEmail(email);
        student.setGroup(group);
        student.setMidterm(midterm);
        student.setFinalterm(finalterm);
        if(checkIdExits(mListStudents, mId)) {
            etID.setError("!");
            etID.requestFocus();
            showToastMessage("This ID has already existed!");
            return;
        }
        else {
            mDatabase.child(classID).child(student.getId()).setValue(student);
            showToastMessage("Saved!");
            this.finish();
        }
    }

    private void updateStudent() {
        String mId = etID.getText().toString().trim();
        if (mId.length() == 0) {
            etID.setError("?");
            etID.requestFocus();
            return;
        }
        String name = etName.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString();
        String email = etEmail.getText().toString();
        String group = etgroup.getText().toString();
        String midterm = etmidterm.getText().toString();
        String finalterm = etfinalterm.getText().toString();
        if (name.length() == 0) {
            etName.setError("?");
            etName.requestFocus();
            return;
        }
        Student student = new Student();
        student.setId(mId);
        student.setName(name);
        student.setBirthday(getDateFormat(mBirthday));
        if (radMale.isChecked()) {
            student.setGender(1);
        }
        else {
            student.setGender(0);
        }
        student.setClassId(classID);
        student.setPhoneNumber(phoneNumber);
        student.setEmail(email);
        student.setGroup(group);
        student.setMidterm(midterm);
        student.setFinalterm(finalterm);
        mDatabase.child(classID).child(student.getId()).setValue(student);
        showToastMessage("Updated!");
        this.finish();
    }

    private Student getStudent( List<Student> listStudent ,String id){
        Student result = new Student();
        for(Student i : listStudent){
            if(i.getId().equals(id)){
                result = i;
                break;
            }
        }
        return result;
    }

    private Boolean checkIdExits(List<Student> listStudent ,String id){
        for(Student i : listStudent){
            if(i.getId().equals(id)){
                return true;
            }
        }
        return false;
    }

    private void showToastMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    private void setDefaultInfo() {
        Calendar cal = Calendar.getInstance();
        etBirthday.setText(getDateFormat(cal.getTime()));
        radMale.setChecked(true);
        etID.requestFocus();
    }

    private String getDateFormat(Date date) {
        SimpleDateFormat dft = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dft.format(date);
    }
}