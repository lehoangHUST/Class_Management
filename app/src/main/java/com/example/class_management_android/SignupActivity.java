package com.example.class_management_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword, edtRePassword;
    private Button btnSignup;
    private String AdminID;

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();
        edtEmail = (EditText) findViewById(R.id.edtEmailAccount);
        edtPassword = (EditText) findViewById(R.id.edtPasswordAccount);
        edtRePassword = (EditText) findViewById(R.id.edtRePasswordAccount);
        btnSignup = (Button) findViewById(R.id.btnSignup);

        // Firebase cho người dùng đăng nhập
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("admin");

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    // Register to database upload (user)
                    String user_email = edtEmail.getText().toString().trim();
                    String user_password = edtPassword.getText().toString().trim();
                    // Kiểm tra xem liệu tài khoản đó đã có trong database hay chưa ?
                    if (TextUtils.isEmpty(AdminID)) {
                        AdminID = mFirebaseDatabase.push().getKey();
                    }

                    Admin admin = new Admin(user_email, user_password, AdminID);
                    mFirebaseDatabase.child(AdminID).setValue(admin);
                }
                startActivity(new Intent(SignupActivity.this, MainActivity.class));
            }

        });

    }

        private Boolean validate() {
            boolean result = false;
            String name = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();

            if(name.isEmpty() && password.isEmpty()){
                Toast.makeText(this,"Please Enter all details",Toast.LENGTH_SHORT).show();
            }else{
                result = true;
            }
            return result;
        }

}
