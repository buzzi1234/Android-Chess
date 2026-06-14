package com.example.chessgame;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

public class BackgroundMusicService extends Service {
    //fields
    private MediaPlayer mediaPlayer;

    public static boolean isMusicMuted = false;

    // Holds current track volume level safely (0.0f to 1.0f)
    public static float currentVolume = 0.5f;

    // Action constant key for processing slider event signals
    public static final String ACTION_SET_VOLUME = "com.example.chessgame.SET_VOLUME";
    public static final String EXTRA_VOLUME_VALUE = "volume_value";

    //bind the music to an intent
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
    The func set up the background music
    input: none
    output: none
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.backgroundmusic); //create the basic music

        if (mediaPlayer != null) {
            mediaPlayer.setLooping(true);
            // Initialize volume from our shared global state flag
            mediaPlayer.setVolume(currentVolume, currentVolume);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Intercept incoming volume updates broadcasted from UI sliders
        if (intent != null && ACTION_SET_VOLUME.equals(intent.getAction())) {
            float incomingVol = intent.getFloatExtra(EXTRA_VOLUME_VALUE, 0.5f);
            currentVolume = incomingVol; // Cache runtime memory state

            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                // Instantly update the native audio playback frame mix wrapper properties
                mediaPlayer.setVolume(currentVolume, currentVolume);
            }
            return START_STICKY; // Process action payload early and terminate control stream mapping
        }

        if (mediaPlayer != null && !mediaPlayer.isPlaying() && !isMusicMuted) {
            mediaPlayer.setVolume(currentVolume, currentVolume); // Sync before playing
            mediaPlayer.start();
            Log.d("MUSIC_SERVICE", "Background music started.");
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            Log.d("MUSIC_SERVICE", "Background music stopped and resources freed.");
        }
        super.onDestroy();
    }
}