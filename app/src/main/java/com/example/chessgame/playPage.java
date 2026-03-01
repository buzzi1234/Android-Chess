package com.example.chessgame;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class playPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This connects to playpage.xml
        setContentView(R.layout.playpage);
    }
}