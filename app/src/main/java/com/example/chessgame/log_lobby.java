package com.example.chessgame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class log_lobby extends AppCompatActivity implements View.OnClickListener {

    ImageButton loginbtn;
    ImageButton signinbtn;


    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.log_lobby);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    loginbtn = findViewById(R.id.loginbtn);
    loginbtn.setOnClickListener(this);

    signinbtn = findViewById(R.id.signbtn);
    signinbtn.setOnClickListener(this);




    }

    @Override
    public void onClick(View v) {

        if(loginbtn == v)
        {
            Intent intent = new Intent(log_lobby.this,loginPage.class);
            startActivity(intent);
        }

        if(signinbtn == v)
        {
            Intent intent = new Intent(log_lobby.this,signinPage.class);
            startActivity(intent);
        }

    }
}
