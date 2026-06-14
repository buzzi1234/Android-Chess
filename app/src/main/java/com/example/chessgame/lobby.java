package com.example.chessgame;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class lobby extends AppCompatActivity implements View.OnClickListener {

    //fields
    ImageButton settingbtn;
    ImageButton playbtn;
    ImageButton instructbtn;
    ImageButton statbtn;
    ImageButton logoutbtn;
    String fromScreen;
    TextView usernameDisplayTv;
    private BroadcastReceiver batteryLowReceiver;

    /*
    The func construct the page start the background music start all the alarms
     */
    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.lobby);
        fromScreen = getIntent().getStringExtra("FROM_SCREEN");

        Intent musicIntent = new Intent(this, BackgroundMusicService.class);
        startService(musicIntent);

        batteryLowReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Intent.ACTION_BATTERY_LOW.equals(intent.getAction())) {

                    // Display a warning alert to the player
                    Toast.makeText(context,
                            "⚠️ Battery is low! Plug in your charger before starting a match.",
                            Toast.LENGTH_LONG).show();
                }
            }
        };

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //start all the notification process
        createNotificationChannel();
        requestNotificationPermission();
        startRepeatingAlarm();

        //the update or load data logic
        if (fromScreen != null) {
            switch (fromScreen) {
                case "LOGIN":
                    Log.d("LOBBY", "Arrived from login");
                    // DO NOTHING HERE — statisticsPage handles its own loading
                    break;

                case "GAME":
                    Log.d("LOBBY", "UPDATE IN FIREBASE");
                    DatabaseManager.saveStatsToFirebase(this); // use this instead of the old method
                    break;

                default:
                    break;
            }
        }

        //finds all the buttons amd text view
        usernameDisplayTv = findViewById(R.id.usernameDisplayTv);
        setUsernameBox(); // set the user name box

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

    //The func creates a channel for the notification process
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "alarm_channel",          // Channel ID
                    "Alarm Notifications",    // Channel name (shown in settings)
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications fired by the alarm");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    //This func request a notification permission from the phone
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        1001
                );
            }
        }
    }

    //The func check if permission is granted and if so
    // he start the all alarm process of alert the client to play some games
    private void startRepeatingAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // check permission before scheduling exact alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Open settings so user can grant permission
                Intent permIntent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(permIntent);
                return;
            }
        }

        // setExactAndAllowWhileIdle + reschedule in receiver
        long triggerAtMillis = SystemClock.elapsedRealtime() + 10_000;
        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerAtMillis,
                pendingIntent
        );
    }

    //move to every screen by the button
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

        //log out process starting
        //stop the music than wipe the cache from the global stat and in last
        //move to the log lobby
        if(logoutbtn == v) {
            Intent musicIntent = new Intent(lobby.this, BackgroundMusicService.class);
            stopService(musicIntent);

            BackgroundMusicService.isMusicMuted = false;

            GlobalStat globalStat = (GlobalStat) getApplicationContext();
            if (globalStat.userStats != null) {
                globalStat.userStats = new UserStatistics(); // Wipes current cache
            }

            Intent intent = new Intent(lobby.this, log_lobby.class);
            startActivity(intent);
            finish();
        }

    }

    //set the username on the textView
    private void setUsernameBox()
    {
        GlobalStat globalStat = (GlobalStat)getApplicationContext();
        usernameDisplayTv.setText(globalStat.userStats.username);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  listening for the low-battery broadcast when the user enters the lobby
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_LOW);
        registerReceiver(batteryLowReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop listening when the user navigates away from the lobby
        // prevent memory leaks!
        if (batteryLowReceiver != null) {
            unregisterReceiver(batteryLowReceiver);
        }
    }

}
