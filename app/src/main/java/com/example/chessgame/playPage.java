package com.example.chessgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class playPage extends AppCompatActivity implements View.OnClickListener {

    //fields
    private ImageButton backbtn;
    private ChessView chessView;
    private Chess chessGame;

    /*
    The func construct and start the game logic
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playpage);

        // Initialize Chess Logic first
        chessGame = new Chess();

        //updating the games amount
        GlobalStat globalStat = (GlobalStat)getApplicationContext();
        globalStat.userStats.gamesPlayed++;

        // Initialize Views
        chessView = findViewById(R.id.chessBoard);
        backbtn = findViewById(R.id.backbtn);

        //Safety Check
        if (chessView == null) {
            android.util.Log.e("playPage", "chessView not found! Check your XML ID.");
        }

        // Link logic to view
        if (chessView != null) {
            chessView.setGameLogic(chessGame);


            chessView.setOnTimeOutListener(new ChessView.OnTimeOutListener() {
                //if the game stoped on time this handle it to the GameOverDialog
                @Override
                public void onTimeOut(boolean isWhiteTurn) {
                    // If White ran out of time, Black wins (whiteWon = false)
                    // If Black ran out of time, White wins (whiteWon = true)
                    boolean whiteWon = !isWhiteTurn;

                    // Route straight to the GameOverDialog package lane
                    Intent intent = new Intent(playPage.this, GameOverDialog.class);
                    intent.putExtra("IS_STALEMATE", false);
                    intent.putExtra("WHITE_WON", whiteWon);

                    // Pass the dynamic layout polish key we set up
                    intent.putExtra("WIN_REASON", "TIME");

                    // Wipe backstack
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }

        if (backbtn != null) {
            backbtn.setOnClickListener(this);
        }
    }


    //handles the exit button
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backbtn) {
            showExitDialog();
        }
    }

    /*
    The func open a dialog for the player and handle if the player wants to stop the game
    and it handle if the player wants to continue the game
    input: none
    output: none (continue timer/move to lobby)
     */
    private void showExitDialog() {
        // Stop the clock
        if (chessView != null) {
            chessView.stopTimer();
        }

        new AlertDialog.Builder(this)
                .setTitle("Exit Game")
                .setMessage("Are you sure you want to quit?")
                .setCancelable(false)
                .setPositiveButton("Yes, Exit", (dialog, which) -> {
                    Intent intent = new Intent(playPage.this, lobby.class);
                    intent.putExtra("FROM_SCREEN", "GAME");
                    finish();
                })
                .setNegativeButton("No, Resume", (dialog, which) -> {
                    // Resume the timer
                    if (chessView != null) {
                        chessView.startTurnTimer();
                    }
                })
                .show();
    }
}