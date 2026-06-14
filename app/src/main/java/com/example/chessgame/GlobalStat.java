package com.example.chessgame;

import android.app.Application;

public class GlobalStat extends Application {
    public UserStatistics userStats = new UserStatistics(); //the store of the userStats
    public boolean statsLoadedFromFirebase = false; //bool var to see if the stats are already loaded
}