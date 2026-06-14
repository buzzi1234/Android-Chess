package com.example.chessgame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class statisticsPage extends AppCompatActivity implements View.OnClickListener {
    //fields
    TextView winstv, losestv, stalematestv, timeplayedtv, gamesplayedtv;
    ImageButton backbtn;

    //constructor
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.statistics);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //bind all the text views and button

        winstv        = findViewById(R.id.winstv);
        losestv       = findViewById(R.id.losestv);
        stalematestv  = findViewById(R.id.stalematestv);
        timeplayedtv  = findViewById(R.id.timeplayedtv);
        gamesplayedtv = findViewById(R.id.gamesplayedtv);
        backbtn       = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(this);

        GlobalStat globalStat = (GlobalStat) getApplicationContext();

        // If we already loaded from Firebase before
        // show the cached values immediately — no "..." flicker
        if (globalStat.statsLoadedFromFirebase && globalStat.userStats != null) {
            updateUI(globalStat.userStats);
        } else {
            showLoadingState();
        }

        // Always fetch fresh data from Firebase to stay up to date
        DatabaseManager.loadStatsFromFirebase(this, new DatabaseManager.DataStatusCallback() {
            @Override
            public void onDataLoaded(UserStatistics stats) {
                updateUI(stats);
            }
        });
    }

    /*
    The func shows lodaing stats if it hasent got anything from the firebase
    input: none
    output: none
     */
    private void showLoadingState() {
        winstv.setText("...");
        losestv.setText("...");
        stalematestv.setText("...");
        gamesplayedtv.setText("...");
        timeplayedtv.setText("...");
    }

    /*
    The func update all the text views by the stats from the globalstat
    input: stats -> all of the user stats
    output: none
     */
    @SuppressLint("SetTextI18n")
    private void updateUI(UserStatistics stats) {
        if (stats == null) return;
        runOnUiThread(() -> {
            winstv.setText(String.valueOf(stats.wins));
            losestv.setText(String.valueOf(stats.losses));
            stalematestv.setText(String.valueOf(stats.stalemates));
            gamesplayedtv.setText(String.valueOf(stats.gamesPlayed));
            timeplayedtv.setText(stats.timespent + " min");
        });
    }

    //the func goes back to the lobby page if the backbtn was pressed
    @Override
    public void onClick(View v) {
        if (backbtn == v) {
            Intent intent = new Intent(statisticsPage.this, lobby.class);
            startActivity(intent);
            finish();
        }
    }
}