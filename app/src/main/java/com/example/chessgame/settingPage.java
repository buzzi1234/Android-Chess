package com.example.chessgame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class settingPage extends AppCompatActivity implements View.OnClickListener {

    //fields
    ImageButton backbtn;
    Switch switchmusiccontrol;
    EditText editusername;
    AppCompatButton btn_delete_account;
    SeekBar seekbarvolume;

    //this func construct and check if the seekbar is changing
    // if it does he will increase or decrease the volume of the background music
    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.settingpage);

        seekbarvolume = findViewById(R.id.seekbarvolume);

        int initialProgress = (int) (BackgroundMusicService.currentVolume * 100);
        seekbarvolume.setProgress(initialProgress);

        seekbarvolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Map the integer progression scale back down to fractional volume ranges
                float scalarVolume = progress / 100f;

                // Ship the updated calculation directly down to your background Service pipeline
                Intent volumeIntent = new Intent(settingPage.this, BackgroundMusicService.class);
                volumeIntent.setAction(BackgroundMusicService.ACTION_SET_VOLUME);
                volumeIntent.putExtra(BackgroundMusicService.EXTRA_VOLUME_VALUE, scalarVolume);
                startService(volumeIntent);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize widgets
        switchmusiccontrol = findViewById(R.id.switchmusiccontrol);
        editusername = findViewById(R.id.editusername);

        // Initialize Button and Bind Listeners
        btn_delete_account = findViewById(R.id.btn_delete_account);
        btn_delete_account.setOnClickListener(this);

        // Click Compression Effect (scale: 0.92)
        btn_delete_account.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.animate().scaleX(0.92f).scaleY(0.92f).setDuration(150).start();
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(600).start();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        v.performClick();
                    }
                }
                return false;
            }
        });


        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(this);

        // Populate local cache inputs
        GlobalStat globalStat = (GlobalStat) getApplicationContext();
        if (globalStat.userStats != null && globalStat.userStats.username != null) {
            editusername.setText(globalStat.userStats.username);
        }

        // Music toggle engine system state sync
        switchmusiccontrol.setChecked(!BackgroundMusicService.isMusicMuted);
        switchmusiccontrol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent musicIntent = new Intent(settingPage.this, BackgroundMusicService.class);
                if (isChecked) {
                    BackgroundMusicService.isMusicMuted = false;
                    startService(musicIntent);
                } else {
                    BackgroundMusicService.isMusicMuted = true;
                    stopService(musicIntent);
                }
            }
        });
    }

    //this func move and act by the logic of the button that got pressed
    @Override
    public void onClick(View v) {
        if (backbtn == v) {
            String newUsername = editusername.getText().toString().trim();
            if (!newUsername.isEmpty()) {
                GlobalStat globalStat = (GlobalStat) getApplicationContext();
                globalStat.userStats.username = newUsername;
                DatabaseManager.updateUsername(globalStat.userStats.Uid, newUsername);
            } else {
                Toast.makeText(this, "Username cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(settingPage.this, lobby.class);
            startActivity(intent);
            finish();
        }

        if (btn_delete_account == v) {
            // Stop background audio before destroying session states
            Intent musicIntent = new Intent(settingPage.this, BackgroundMusicService.class);
            stopService(musicIntent);
            BackgroundMusicService.isMusicMuted = false;

            // deletes data from Firestore & Auth table inside the DatabaseManager
            DatabaseManager.deleteUserAccount(this, new DatabaseManager.DeleteAccountCallback() {
                @Override
                public void onAccountDeleted(boolean success, String errorMessage) {
                    if (success) {
                        Toast.makeText(settingPage.this, "Account deleted permanently.", Toast.LENGTH_LONG).show();

                        // Route to log_lobby initialization lane
                        Intent intent = new Intent(settingPage.this, log_lobby.class);
                        // Wipe backstack completely so hardware arrows cannot re-enter back into active states
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(settingPage.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}