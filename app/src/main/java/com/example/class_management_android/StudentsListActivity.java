package com.example.class_management_android;

import android.app.AlertDialog;
import androidx.lifecycle.LifecycleObserver;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.class_management_android.adapter.StudentAdapter;
//import com.example.class_management_android.database.DbClassroomHelper;
//import com.example.class_management_android.database.DbStudentHelper;
import com.example.class_management_android.model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StudentsListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, LifecycleObserver
{
    private ListView lvListStudents;
    private List<Student> mListStudents;
    private StudentAdapter mAdapter;
    private TextView tvStudentCount;
    private String classID;
    private List<Student> searchList;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser acct;

    private Boolean switchClik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_list);
        classID = getIntent().getStringExtra("idClassroomClick");

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        // Customize the back button
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back_action);
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        // edit title in action bar
        actionBar.setTitle("Classroom's Code: " + classID);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1E313E"))); // dark_blue

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        acct = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference("student");

        tvStudentCount =(TextView) findViewById(R.id.tvStudentCount);
        lvListStudents = (ListView) findViewById(R.id.lvListStudents);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mListStudents = new ArrayList<>();
        searchList = new ArrayList<>();
        switchClik = false;

        mAdapter = new StudentAdapter(this, R.layout.student_row, mListStudents);
        lvListStudents.setAdapter(mAdapter);
        addEventListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListStudents.clear();
                for(DataSnapshot dataSnapshot : snapshot.child(classID).getChildren()){
                    Student student = dataSnapshot.getValue(Student.class);
                    mListStudents.add(student);
                }
                mAdapter.notifyDataSetChanged();
                tvStudentCount.setText(" " + Integer.toString(mListStudents.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("Insert name");
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.finish();
                return true;
            }
            case R.id.random_action: {
                callRandomStudent();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void callRandomStudent(){
        if (mListStudents.size() == 0) {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle(R.string.diemdanh).setMessage("There 're no students in the student list!");
            b.create().show();
        }
        else {
            Random generator = new Random();
            int index = generator.nextInt(mListStudents.size());
            Student temp_std = mListStudents.get(index);
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle(R.string.diemdanh).setMessage("Name: " + temp_std.getName() + "\nCode: " + temp_std.getId());
            b.create().show();
        }
    }

    // Thêm sinh viên vào trong lớp học
    public void addStudent(View v)
    {
        Intent i = new Intent(this, EditStudentActivity.class);
        i.putExtra("idClassroom", classID);
        startActivity(i);
    }

    // Hiển thị ra số buổi đi học và số buổi nghỉ của sinh viên đó
    public void AttendenceStudent(View v){
        Intent i  = new Intent(this, Attendence_detail.class);
        // Truyền giá trị lớp học của học sinh đó
        i.putExtra("idClassroom", classID);
        startActivity(i);
    }

    private void addEventListener()
    {
        lvListStudents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                Student student;
                if(switchClik == false){
                    student = mListStudents.get(position);
                }else{
                    student = searchList.get(position);
                }
                Intent i = new Intent(StudentsListActivity.this, EditStudentActivity.class);
                i.putExtra("idStudent", student.getId());
                i.putExtra("idClassroom", classID);
                startActivity(i);
                return true;

            }
        });

        lvListStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Student student;
                if (switchClik == false){
                    student = mListStudents.get(position);
                }else{
                    student = searchList.get(position);
                }

                // Bắt đầu hiển thị sang các activity chi tiết về thông tin của học sinh đó
                Intent i = new Intent(StudentsListActivity.this, View_Information_Student.class);
                i.putExtra("idStudent", student.getId());
                i.putExtra("idClassroom", classID);
                startActivity(i);
            }
        });
    }


    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        searchList.clear();
        if(newText.length() != 0){
            switchClik = true;
            for(Student i : mListStudents){
                if(i.getName().toLowerCase().contains(newText.toLowerCase())){
                    searchList.add(i);
                }
            }
            lvListStudents.setAdapter(new StudentAdapter(this, R.layout.student_row, searchList));
        }else {
            switchClik = false;
            lvListStudents.setAdapter(new StudentAdapter(this, R.layout.student_row, mListStudents));
        }
        return true;
    }

}