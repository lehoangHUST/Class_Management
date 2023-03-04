package com.example.class_management_android;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.telephony.SmsManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.class_management_android.adapter.AttendanceAdapter;
import com.example.class_management_android.adapter.ClassroomNotificationAdapter;
//import com.example.class_management_android.database.DbClassroomHelper;
import com.example.class_management_android.model.Attendance;
import com.example.class_management_android.model.Classroom;
import com.example.class_management_android.model.Student;
import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends android.app.Fragment {
    private CalendarView calendarView;
    private TextView textView;
    private Calendar calendar;

    private ListView lvListClassroom;
    private List<Classroom> mListClassroom; // mListClassroom - monitor of list classroom
    private List<Student> mListStudent;
    private List<Classroom> listClassroomDay;
    private List<Attendance> mListAttendance;
    private List<String> mListDataPush;
    private List<String> mListAttendanceUserId;
    private List<String> mListStudentSMS;
    private ClassroomNotificationAdapter mAdapter;

    private DatabaseReference mDatabaseClassroom;
    private DatabaseReference mDatabaseStudent;
    private DatabaseReference mDatabaseAttendance;
    private FirebaseAuth mAuth;
    private FirebaseUser acct;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.SEND_SMS},1);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notification, container, false);


        calendarView = (CalendarView) v.findViewById(R.id.calendar);
        textView = (TextView) v.findViewById(R.id.tvClassDay);
        lvListClassroom = (ListView) v.findViewById(R.id.lvListClassNotification);

        mListClassroom = new ArrayList<>();
        mListStudent = new ArrayList<>();
        mListAttendance = new ArrayList<>();
        mListAttendanceUserId = new ArrayList<>();
        mListDataPush = new ArrayList<>();
        mListStudentSMS = new ArrayList<>();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        acct = mAuth.getCurrentUser();

        mDatabaseClassroom = FirebaseDatabase.getInstance().getReference("classroom");
        mDatabaseStudent = FirebaseDatabase.getInstance().getReference("student");
        mDatabaseAttendance = FirebaseDatabase.getInstance().getReference("attendance");

        mDatabaseClassroom.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListClassroom.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Classroom classroom = dataSnapshot.getValue(Classroom.class);
                    mListClassroom.add(classroom);
                }
                //mAdapter.notifyDataSetChanged();
                calendar = Calendar.getInstance();
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                listClassroomDay = sortByTimeStart(classroomDay1(mListClassroom, dayOfWeek));
                updateClassroomDay();
                addEventListener();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override

            // In this Listener have one method
            // and in this method we will
            // get the value of DAYS, MONTH, YEARS
            public void onSelectedDayChange(
                    CalendarView view,
                    int year,
                    int month,
                    int dayOfMonth)
            {

                calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                // Store the value of date with
                // format in String type Variable
                // Add 1 in month because month
                // index is start with 0
                listClassroomDay.clear();
                listClassroomDay = sortByTimeStart(classroomDay1(mListClassroom, dayOfWeek));
                updateClassroomDay();

            }
        });

        return v;
    }
    @Override
    public void onResume() {
        super.onResume();

    }

    public void updateClassroomDay(){
        if(listClassroomDay.size() > 0){
            textView.setText(" ");
            mAdapter = new ClassroomNotificationAdapter(this.getActivity(), R.layout.classroom_row_notification, listClassroomDay);
            lvListClassroom.setAdapter(mAdapter);
        }else{
            textView.setText("No classes today");
            if(mAdapter == null){
                mAdapter = new ClassroomNotificationAdapter(this.getActivity(), R.layout.classroom_row_notification, listClassroomDay);
                lvListClassroom.setAdapter(mAdapter);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void addEventListener() {
        lvListClassroom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Classroom classroom = listClassroomDay.get(position);
                Intent i = new Intent(getActivity(), StudentsListActivity.class);
                i.putExtra("idClassroomClick", classroom.getId());
                startActivity(i);
            }
        });
        lvListClassroom.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position >= 0) {
                    Classroom classroom = listClassroomDay.get(position);
                    Date date = calendar.getTime();
                    String thisDate = new SimpleDateFormat("dd").format(date)
                            + "/" + new SimpleDateFormat("MM").format(date) + "/"
                            + new SimpleDateFormat("yyyy").format(date);
                    showDialog(getActivity(), classroom.getId(), thisDate);
                }
                return true;
            }
        });
    }

    private List<Classroom> classroomDay1(List<Classroom> classrooms, int weekday){
        List<Classroom> result = new ArrayList<>();
        switch(weekday){
            case 2: result = clasroomDay2(classrooms, "Monday");
            break;
            case 3: result = clasroomDay2(classrooms, "Tuesday");
            break;
            case 4: result = clasroomDay2(classrooms, "Wednesday");
            break;
            case 5: result = clasroomDay2(classrooms, "Thursday");
            break;
            case 6: result = clasroomDay2(classrooms, "Friday");
            break;
            case 7: result = clasroomDay2(classrooms, "Saturday");
            break;
            case 1: result = clasroomDay2(classrooms, "Sunday");
            break;

        }
        return  result;
    }

    private List<Classroom> clasroomDay2(List<Classroom> classrooms, String weekday){
        List<Classroom> result = new ArrayList<>();
        for(Classroom i : classrooms){
            if(i.getWeekDay().toString().equals(weekday)){
                result.add(i);
            }
        }
        return  result;
    }

    private  List<Classroom> sortByTimeStart(List<Classroom> classrooms){
        int length = classrooms.size();
        for(int i = 0; i < length - 1; i++){
            int min = i;
            for(int j = i + 1; j < length; j++){
                if(convertTimeFromStringToInt(classrooms.get(j).getStartTime())
                        < convertTimeFromStringToInt(classrooms.get(min).getStartTime())){
                    min = j;
                }
            }
            Classroom swap = classrooms.get(i);
            classrooms.set(i, classrooms.get(min));
            classrooms.set(min, swap);
        }
        return classrooms;
    }

    private int convertTimeFromStringToInt(String time){
        int result = 0;
        int length = time.length();
        int index = time.indexOf(":");
        if(index == 1){
            result = result + (Character.getNumericValue(time.charAt(0)))*60;
            if(length == 3){
                result = result + Character.getNumericValue(time.charAt(2));
            }else {
                result = result + (Character.getNumericValue(time.charAt(2)))*10 + Character.getNumericValue(time.charAt(3));
            }
        }else{
            result = result + (Character.getNumericValue(time.charAt(0))*10 + Character.getNumericValue(time.charAt(1)))*60;
            if(length == 4){
                result = result + Character.getNumericValue(time.charAt(3));
            }else {
                result = result + (Character.getNumericValue(time.charAt(3)))*10 + Character.getNumericValue(time.charAt(4));
            }
        }
        return result;
    }

    private void showDialog(Activity activity, String idClassroomClick, String date){
        final Dialog dialog = new Dialog(activity);
        mListAttendance.clear();
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_attendance);
        Button btnUpdate = (Button) dialog.findViewById(R.id.btnUpdateDialog); // Nút bấm cập nhật
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancelDialog); // Nút bấm hủy bỏ
        Button btnSendSMS = (Button) dialog.findViewById(R.id.btnSendSMSDialog); // Nút gửi SMS cho sinh viên/ học sinh trong trường hợp không đi học
        ListView listViewDialog = (ListView) dialog.findViewById(R.id.dialogListView);
        TextView tvTitleDialog = (TextView) dialog.findViewById(R.id.tvTitleDialog);
        tvTitleDialog.setText("Attendance on " + date);

        btnUpdate.setEnabled(false);

        AttendanceAdapter mAttendanceAdapter = new AttendanceAdapter(this.getActivity(), R.layout.dialog_attendance_row, mListAttendance);
        listViewDialog.setAdapter(mAttendanceAdapter);

        mDatabaseStudent.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListStudent.clear();
                for(DataSnapshot dataSnapshot : snapshot.child(idClassroomClick).getChildren()){
                    Student student = dataSnapshot.getValue(Student.class);
                    mListStudent.add(student);
                }


                mDatabaseAttendance.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mListAttendanceUserId.clear();
                        for(DataSnapshot dataSnapshot : snapshot.child(idClassroomClick).child(date).getChildren()){
                            String attendanceUserId = dataSnapshot.getValue(String.class);
                            mListAttendanceUserId.add(attendanceUserId);
                        }
                        generateDataItemAttendance();
                        mAttendanceAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listViewDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(position >= 0){
                    Attendance attendance = mListAttendance.get(position);
                    if(attendance.getState() == 1){
                        attendance.setState(0);
                    }else{
                        attendance.setState(1);
                    }
                    mListAttendance.set(position, attendance);
                    mAttendanceAdapter.notifyDataSetChanged();
                    btnCancel.setEnabled(false);
                    btnUpdate.setEnabled(true);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateDataPush();
                mDatabaseAttendance.child(idClassroomClick).child(date).setValue(mListDataPush);
                Toast.makeText(getActivity(), "Attendance List Updated", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        btnSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListDataPush.clear();
                mListStudentSMS.clear();
                for (Attendance i: mListAttendance){
                    if (i.getState() == 0){
                        String IDStudent_not_Atten = i.getIdStudent();
                        for (int j = 0; j < mListStudent.size(); j++){
                            if (mListStudent.get(j).getId() == IDStudent_not_Atten){
                                // Sau khi kiểm tra giống nhau mới đến bước gửi SMS
                                mListStudentSMS.add(mListStudent.get(j).getPhoneNumber());
                            }
                        }
                    }
                }

                // Bắt đầu quá trình gửi SMS đến từng sinh viên
                SendSMS(mListStudentSMS);
            }
        });

//        sendSMS.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "SMS Sending ....", Toast.LENGTH_SHORT).show();
//                String smsPhoneNumber = "0775170810";
//                String send_sms = "Hôm nay bạn không đi học";
//                final SmsManager sms = SmsManager.getDefault();
//                sms.sendTextMessage("0775170810", null, send_sms, null, null);
//
//
//            }
//        });

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void generateDataItemAttendance(){
        for(Student i : mListStudent){
            Boolean check = false;
            for(String j : mListAttendanceUserId){
                if(i.getId().equals(j)){
                    mListAttendance.add(new Attendance(i.getId(), i.getName(), 1));
                    check = true;
                    break;
                }
            }
            if(check == false){
                mListAttendance.add(new Attendance(i.getId(), i.getName(), 0));
            }
        }
    }

    private void generateDataPush(){
        mListDataPush.clear();
        for(Attendance i : mListAttendance){
            if(i.getState() == 1){
                mListDataPush.add(i.getIdStudent());
            }
        }
    }

    private void SendSMS(List<String> Phones){
        // Hàm này mục đích gửi tin nhắn đến số điện thoại mà học sinh đã nghỉ buổi hôm đó
        String message = "Ban nghi hoc hom nay";
        for (String p: Phones){
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(p,
                        null,
                        message,
                        null,
                        null);
                Toast.makeText(getActivity(), "SMS sent successful", Toast.LENGTH_LONG).show();
            }
            catch(IllegalArgumentException ex)
            {
                Toast.makeText(getActivity(), "Sending SMS failed", Toast.LENGTH_LONG).show();
            }
        }
    }

}