package com.example.chessgame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class loginPage extends AppCompatActivity implements View.OnClickListener {

    ImageButton backbtn;
    EditText emailtv, passwordtv;
    ImageButton loginbtn;

    FirebaseAuth auth;

    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(this);

        emailtv = findViewById(R.id.emailtv);
        passwordtv = findViewById(R.id.passwordtv);
        loginbtn = findViewById(R.id.loginbtn);

        auth = FirebaseAuth.getInstance();

        loginbtn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        if(backbtn == v)
        {
            Intent intent = new Intent(loginPage.this,log_lobby.class);
            startActivity(intent);
        }
        if(loginbtn == v)
        {
            String txt_logemail = emailtv.getText().toString();
            String txt_logPw = passwordtv.getText().toString();
            loginClient(txt_logemail, txt_logPw);
        }

    }

    private void loginClient(String txtLogemail, String txtLogPw) {
        auth.signInWithEmailAndPassword(txtLogemail, txtLogPw).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(loginPage.this, "login succesfull", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(loginPage.this, lobby.class));
            }
        });
    }
}


