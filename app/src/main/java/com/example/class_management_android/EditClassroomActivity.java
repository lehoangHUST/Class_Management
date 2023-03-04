package com.example.class_management_android;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

//import com.example.class_management_android.database.DbClassroomHelper;
import com.example.class_management_android.model.Classroom;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditClassroomActivity extends AppCompatActivity
{
    private TextView tvTitle;
    private EditText etID, etName, etStart, etEnd, etRoom, etWeekDay;
    private String mId;
    private List<Classroom> mListClassroom;

    private DatabaseReference mDatabaseStudent;
    private DatabaseReference mDatabaseAttendance;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser acct;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_classroom);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        // Customize the back button
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back_action);
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        // edit title in action bar
        actionBar.setTitle("");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1E313E"))); // dark_blue

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        acct = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference("classroom");
        mDatabaseStudent = FirebaseDatabase.getInstance().getReference("student");
        mDatabaseAttendance = FirebaseDatabase.getInstance().getReference("attendance");

        mListClassroom = new ArrayList<>();
        tvTitle = (TextView) findViewById(R.id.tvTitleClass);
        etID = (EditText) findViewById(R.id.etIDClass);
        etName = (EditText) findViewById(R.id.etNameClass);
        etStart = (EditText) findViewById(R.id.etStart);
        etEnd = (EditText) findViewById(R.id.etEnd);
        etRoom = (EditText) findViewById(R.id.etClassRoom);
        etWeekDay = (EditText) findViewById(R.id.etWeekDay);
        mId = getIntent().getStringExtra("idClassroomClick");


        etWeekDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(EditClassroomActivity.this);
                builder.setTitle("Choose a weekday");
                // add a list
                String[] weekdays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
                builder.setItems(weekdays, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: etWeekDay.setText("Monday");
                            break;
                            case 1: etWeekDay.setText("Tuesday");
                            break;
                            case 2: etWeekDay.setText("Wednesday");
                            break;
                            case 3: etWeekDay.setText("Thursday");
                            break;
                            case 4: etWeekDay.setText("Friday");
                            break;
                            case 5: etWeekDay.setText("Saturday");
                            break;
                            case 6: etWeekDay.setText("Sunday");
                            break;
                        }
                    }
                });
                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        etStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(EditClassroomActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etStart.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        etEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(EditClassroomActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etEnd.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListClassroom.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Classroom classroom = dataSnapshot.getValue(Classroom.class);
                    mListClassroom.add(classroom);
                }
                if (mId == null) {
                    // ADD MODE
                    etID.requestFocus();
                } else {
                    // EDIT MODE

                    Classroom classroom = getClassroom(mListClassroom, mId);
                    tvTitle.setText(R.string.update_class);
                    etID.setText(classroom.getId());
                    etID.setEnabled(false); // không cho phép sửa ID trong lúc edit
                    etName.setText(classroom.getSubjectName());
                    etName.requestFocus();
                    etStart.setText(classroom.getStartTime());
                    etEnd.setText(classroom.getEndTime());
                    etRoom.setText(classroom.getClassroomName());
                    etWeekDay.setText(classroom.getWeekDay());
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
            // trong chế độ adding
            return super.onCreateOptionsMenu(menu);
        else
        {
            // trong chế độ editting
            getMenuInflater().inflate(R.menu.menu_edit, menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
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
                deleteClassroom(mId);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void clear(View v) {
        etName.setText("");
        if (mId == null) {
            // ADD MODE
            etID.setText("");
        } else {
            // EDIT MODE
            etName.requestFocus();
        }
        etID.requestFocus();
    }

    public void save(View v) {
        if (mId == null) {
            // ADD MODE
            addClassroom();
        } else {
            // EDIT MODE
            updateClassroom();
        }
    }

    private void addClassroom() {
        String mId = etID.getText().toString().trim();
        if (mId.length() == 0) {
            etID.setError("?");
            etID.requestFocus();
            return;
        }

        String name = etName.getText().toString().trim();
        if (name.length() == 0) {
            etName.setError("?");
            etName.requestFocus();
            return;
        }
        String timeStart = etStart.getText().toString().trim();
        if (timeStart.length() == 0) {
            etStart.setError("?");
            etStart.requestFocus();
            return;
        }
        String timeEnd = etEnd.getText().toString().trim();
        if (timeEnd.length() == 0) {
            etEnd.setError("?");
            etEnd.requestFocus();
            return;
        }
        String weekDay = etWeekDay.getText().toString().trim();
        if (timeEnd.length() == 0) {
            etWeekDay.setError("?");
            etWeekDay.requestFocus();
            return;
        }
        Classroom classroom = new Classroom();
        classroom.setId(mId);
        classroom.setSubjectName(name);
        classroom.setStartTime(timeStart);
        classroom.setEndTime(timeEnd);
        classroom.setClassroomName(etRoom.getText().toString());
        classroom.setWeekDay(weekDay);
        if(checkIdExits(mListClassroom, mId)){
            etID.setError("!");
            etID.requestFocus();
            showToastMessage("This ID has already existed!");
            return;
        }else{
            mDatabase.child(classroom.getId()).setValue(classroom);
            showToastMessage("Saved!");
            this.finish();
        }

    }

    private void updateClassroom()
    {
        String mId = etID.getText().toString().trim();
        if (mId.length() == 0) {
            etID.setError("?");
            etID.requestFocus();
            return;
        }

        String name = etName.getText().toString().trim();
        if (name.length() == 0) {
            etName.setError("?");
            etName.requestFocus();
            return;
        }
        String timeStart = etStart.getText().toString().trim();
        if (timeStart.length() == 0) {
            etStart.setError("?");
            etStart.requestFocus();
            return;
        }
        String timeEnd = etEnd.getText().toString().trim();
        if (timeEnd.length() == 0) {
            etEnd.setError("?");
            etEnd.requestFocus();
            return;
        }
        String weekDay = etWeekDay.getText().toString().trim();
        if (timeEnd.length() == 0) {
            etWeekDay.setError("?");
            etWeekDay.requestFocus();
            return;
        }
        Classroom classroom = new Classroom();
        classroom.setId(mId);
        classroom.setSubjectName(name);
        classroom.setStartTime(timeStart);
        classroom.setEndTime(timeEnd);
        classroom.setClassroomName(etRoom.getText().toString());
        classroom.setWeekDay(weekDay);
        mDatabase.child(acct.getUid()).child(classroom.getId()).setValue(classroom);
        showToastMessage("Updated!");
        this.finish();
    }

    // show a message to delete a classroom in db with classroom's id
    private void deleteClassroom(final String id)
    {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(R.string.delete).setMessage(getString(R.string.delete_classroom_message) + " - Classroom's code: " + id + " ?")
                .setIcon(android.R.drawable.ic_delete)
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDatabase.child(acct.getUid()).child(mId).setValue(null);
                        mDatabaseStudent.child(acct.getUid()).child(mId).setValue(null);
                        mDatabaseAttendance.child(acct.getUid()).child(mId).setValue(null);
                        showToastMessage("Deleted!");
                        EditClassroomActivity.this.finish();
                    }
                });
        b.create().show();
    }

    private Classroom getClassroom( List<Classroom> listClassroom ,String id){
        Classroom result = new Classroom();
        for(Classroom i : listClassroom){
            if(i.getId().equals(id)){
                result = i;
                break;
            }
        }
        return result;
    }

    private Boolean checkIdExits(List<Classroom> listClassroom ,String id){
        for(Classroom i : listClassroom){
            if(i.getId().equals(id)){
                return true;
            }
        }
        return false;
    }

    private void showToastMessage(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}