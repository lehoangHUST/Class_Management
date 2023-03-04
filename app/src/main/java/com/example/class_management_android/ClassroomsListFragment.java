package com.example.class_management_android;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.class_management_android.adapter.ClassroomAdapter;
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
import java.util.List;
public class ClassroomsListFragment extends Fragment implements SearchView.OnQueryTextListener
{
    // variables
    private ListView lvListClassroom;
    private List<Classroom> mListClassroom; // mListClassroom - monitor of list classroom
    private ClassroomAdapter mAdapter;
    public static String mSearchText = null;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_classroom_list, container, false);
        ImageButton button = (ImageButton) v.findViewById(R.id.btn_addi);


        mDatabase = FirebaseDatabase.getInstance().getReference("classroom");


        lvListClassroom = (ListView) v.findViewById(R.id.lvListCLassManagers);
        mListClassroom = new ArrayList<>();
        mAdapter = new ClassroomAdapter(this.getActivity(), R.layout.classroom_row, mListClassroom);
        lvListClassroom.setAdapter(mAdapter);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListClassroom.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Classroom classroom = dataSnapshot.getValue(Classroom.class);
                    mListClassroom.add(classroom);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EditClassroomActivity.class);
                startActivity(i);
            }
        });

        //refreshListClassManagersData();
        addEventListener();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
//        refreshListClassManagersData();
    }

//    public void refreshListClassManagersData()
//    {
//        DbClassroomHelper dbClassManagerHelper = new DbClassroomHelper(getActivity(), null);
//        mListClassroom.clear();
//        mListClassroom.addAll(dbClassManagerHelper.getList());
//        mAdapter.notifyDataSetChanged();
//    }

    private void addEventListener() {
        lvListClassroom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Classroom classroom = mListClassroom.get(position);
                Intent i = new Intent(getActivity(), StudentsListActivity.class);
                i.putExtra("idClassroomClick", classroom.getId());
                startActivity(i);
            }
        });
        lvListClassroom.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position >= 0) {
                    Classroom classroom = mListClassroom.get(position);
                    Intent i = new Intent(getActivity(), EditClassroomActivity.class);
                    i.putExtra("idClassroomClick",classroom.getId());
                    startActivity(i);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String new_text) {
        mSearchText = new_text;
//        DbClassroomHelper dbHelper = new DbClassroomHelper(getActivity(), null);
        mListClassroom.clear();
//        mListClassroom.addAll(dbHelper.search(new_text));
        mAdapter.notifyDataSetChanged();
        return true;
    }
}