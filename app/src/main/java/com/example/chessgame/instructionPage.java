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

public class instructionPage extends AppCompatActivity implements View.OnClickListener {

    ImageButton backbtn;

    //constructor
    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.instructionpage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //find the button
        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(this);


    }

    //The func make the back button work by set the intent to the lobby
    @Override
    public void onClick(View v) {

        if(backbtn == v)
        {
            Intent intent = new Intent(instructionPage.this,lobby.class);
            startActivity(intent);
        }

    }
}


