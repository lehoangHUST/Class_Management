package com.example.class_management_android;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity
{
    private static final String FILE_EMAIL = "remember";
    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnLogin;
    private CheckBox checkBox;
    private TextView tvAccount;
    private boolean isValid = false;
    private String email, password, adminID;

    private ArrayList<Admin> admins = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActivityCompat.requestPermissions(this, new String []{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        tvAccount = (TextView)findViewById(R.id.tvAccount);
        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtPassword = (EditText)findViewById(R.id.edtPassword);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        checkBox = (CheckBox)findViewById(R.id.show_password);

        // Save email and password
        CheckBox checkRemember = (CheckBox) findViewById(R.id.remember_me);
        SharedPreferences sharedPreferences = getSharedPreferences(FILE_EMAIL, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String email = sharedPreferences.getString("svEmail", "");
        String password = sharedPreferences.getString("svPassword", "");
        if (sharedPreferences.contains("checked") && sharedPreferences.getBoolean("checked", false) == true){
            checkRemember.setChecked(true);
        }
        else{
            checkRemember.setChecked(false);
        }
        edtEmail.setText(email);
        edtPassword.setText(password);
        
        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                
                if (isConnected() == false)
                {
                    Toast.makeText(LoginActivity.this, "Internet not connected sucessful.", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(LoginActivity.this, "Internet avaliable.", Toast.LENGTH_SHORT).show();
                    readData();
                    if (checkRemember.isChecked()) {
                        editor.putBoolean("checked", true);
                        editor.apply();
                        StoreDataUsingSharedPref(email, password);
                    } else {
                        getSharedPreferences(FILE_EMAIL, MODE_PRIVATE).edit().clear().commit();
                    }

                    isValid = checkLogin(edtEmail.getText().toString(), edtPassword.getText().toString());

                    if (isValid) {
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        i.putExtra("AdminID", adminID);
                        startActivity(i);
                    } else {
                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        // Nút bấm đăng kí khi người dùng không có tài khoản và mật khẩu
        tvAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        // Che hoặc hiện thị mật khẩu lên cho người dùng xem
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    // Show password (hiện thị mật khẩu)
                    edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else
                {
                    // Hide password (che mật khẩu
                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    private void StoreDataUsingSharedPref(String email, String password) {
        SharedPreferences.Editor editor =  getSharedPreferences(FILE_EMAIL, MODE_PRIVATE).edit();
        editor.putString("svEmail", email);
        editor.putString("svPassword", password);
        editor.apply();
    }

    // Hàm check login đăng nhập của app điểm danh
    private boolean checkLogin(String email, String password)
    {
        // So sánh với kết quả đã có trong firebase hay chưa
        for (Admin admin: admins)
        {
            if (admin.getAdminUser().equals(email) && admin.getAdminPass().equals(password)) {
                adminID = admin.getAdminID();
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

        databaseReference.child("admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                admins.clear();
                for (DataSnapshot snap:snapshot.getChildren()){
                    Admin admin = snap.getValue(Admin.class);
                    admins.add(admin);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    
    private boolean isConnected(){
        // Kiểm tra kết nối internet
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
        
        return connected;
    }
}