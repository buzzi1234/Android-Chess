package com.example.chessgame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class lobby extends AppCompatActivity implements View.OnClickListener {

    ImageButton settingbtn;
    ImageButton playbtn;
    ImageButton instructbtn;
    ImageButton statbtn;
    ImageButton logoutbtn;


    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.lobby);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        settingbtn = findViewById(R.id.settingbtn);
        settingbtn.setOnClickListener(this);

        playbtn = findViewById(R.id.playbtn);
        playbtn.setOnClickListener(this);

        instructbtn = findViewById(R.id.instructbtn);
        instructbtn.setOnClickListener(this);

        statbtn = findViewById(R.id.statbtn);
        statbtn.setOnClickListener(this);

        logoutbtn = findViewById(R.id.logoutbtn);
        logoutbtn.setOnClickListener(this);




    }

    @Override
    public void onClick(View v) {

        if(settingbtn == v)
        {
            Intent intent = new Intent(lobby.this,settingPage.class);
            startActivity(intent);
        }

        if(playbtn == v)
        {
            Intent intent = new Intent(lobby.this,playPage.class);
            startActivity(intent);
        }

        if(instructbtn == v)
        {
            Intent intent = new Intent(lobby.this,instructionPage.class);
            startActivity(intent);
        }

        if(statbtn == v)
        {
            Intent intent = new Intent(lobby.this,statisticsPage.class);
            startActivity(intent);
        }

        if(logoutbtn == v)
        {
            Intent intent = new Intent(lobby.this,log_lobby.class);
            startActivity(intent);
        }

    }
}
