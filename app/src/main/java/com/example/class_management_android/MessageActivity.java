package com.example.class_management_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.class_management_android.adapter.ClassroomAdapter;
import com.example.class_management_android.adapter.MessageAdapter;
import com.example.class_management_android.model.Chat;
import com.example.class_management_android.model.Classroom;
import com.example.class_management_android.model.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    CircleImageView profile_image;
    TextView username;
    ImageButton btn_send;
    EditText text_send;
    Intent intent;
    DatabaseReference reference;
    DatabaseReference reference_chat;
    MessageAdapter messageAdapter;
    List<Chat> mChat;
    ListView lvChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send_message);
        text_send = findViewById(R.id.text_send);
        lvChat = findViewById(R.id.window_chat);
        reference_chat = FirebaseDatabase.getInstance().getReference("chat");


        intent = getIntent();
        String userid = intent.getStringExtra("idStudentClick");
        String sendid = intent.getStringExtra("idSender");

        mChat = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, R.layout.chat_item_right, mChat, sendid);
        lvChat.setAdapter(messageAdapter);

        reference_chat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getSender().equals(sendid) && chat.getReceiver().equals(userid) ||
                        chat.getReceiver().equals(sendid) && chat.getSender().equals(userid)){
                        mChat.add(chat);
                    }
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if (!msg.equals("")){
                    sendMessage(sendid, userid, msg);
                }else{
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });

        reference = FirebaseDatabase.getInstance().getReference().child("student").child("1").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Student student = snapshot.getValue(Student.class);
                username.setText(student.getName());
                profile_image.setImageResource(R.drawable.image_user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        reference.child("chat").push().setValue(hashMap);
    }
}