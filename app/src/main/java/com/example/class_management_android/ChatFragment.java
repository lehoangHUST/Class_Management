package com.example.class_management_android;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import android.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.class_management_android.adapter.StudentAdapter;
import com.example.class_management_android.adapter.Student_Chat_Adapter;
import com.example.class_management_android.model.Classroom;
import com.example.class_management_android.model.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private ListView lvListStudents;
    private RecyclerView recyclerView;
    private Student_Chat_Adapter student_chat_adapter;
    private List<Student> mStudent;
    DatabaseReference reference;
    String id_sender;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        id_sender = getArguments().getString("adminID");
        lvListStudents = (ListView) view.findViewById(R.id.list_view_chat);
        mStudent = new ArrayList<>();
        student_chat_adapter = new Student_Chat_Adapter(this.getActivity(), R.layout.user_chat_item, mStudent);
        lvListStudents.setAdapter(student_chat_adapter);

        read_student();

        lvListStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Student student = mStudent.get(position);
                Intent i = new Intent(getActivity(), MessageActivity.class);
                i.putExtra("idStudentClick", student.getId());
                i.putExtra("idSender", id_sender);
                startActivity(i);
            }
        });

        return view;
    }



    private void read_student(){
        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = mFirebaseInstance.getReference();

        databaseReference.child("student").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mStudent.clear();
                for(DataSnapshot dataSnapshot : snapshot.child("1").getChildren()){
                    Student student = dataSnapshot.getValue(Student.class);
                    mStudent.add(student);
                }

                student_chat_adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}