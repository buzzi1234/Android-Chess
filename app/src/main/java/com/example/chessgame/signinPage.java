package com.example.chessgame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class signinPage extends AppCompatActivity implements View.OnClickListener {

    ImageButton backbtn;

    //firebase variables
    EditText emailtv, passwordtv;
    ImageButton signbtn;
    Context context;

    FirebaseAuth auth;

    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.signin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.sign), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(this);

        emailtv = findViewById(R.id.emailtv);
        passwordtv = findViewById(R.id.passwordtv);
        signbtn = findViewById(R.id.signbtn);

        context = signinPage.this;
        auth = FirebaseAuth.getInstance();

        signbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(backbtn == v)
        {
            Intent intent = new Intent(signinPage.this,log_lobby.class);
            startActivity(intent);
        }
        if(signbtn == v)
        {
            String txt_email = emailtv.getText().toString();
            String txt_password = passwordtv.getText().toString();
            if(TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password))
            {
                Toast.makeText(context, "fields must not be empty",Toast.LENGTH_SHORT).show();
            }
            else if(txt_password.length() < 6){
                Toast.makeText(context, "password is to short",Toast.LENGTH_SHORT).show();
            }
            else {
                registerUser(txt_email, txt_password);
            }
        }


    }

    private void registerUser(String email, String password)
    {
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(signinPage.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(signinPage.this, lobby.class));
                        }
                        else {
                            Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


}


