package com.example.chessgame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GameOverDialog extends AppCompatActivity {
    /*
    The func is a constructor the find out which win or lose or stalemate
    happened and show it on the activity by getIntent
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_game_over);
        setFinishOnTouchOutside(false);

        TextView dialogMessage = findViewById(R.id.dialogMessage);
        Button btnGoToLobby   = findViewById(R.id.btnGoToLobby);

        boolean isStalemate = getIntent().getBooleanExtra("IS_STALEMATE", false);
        boolean whiteWon    = getIntent().getBooleanExtra("WHITE_WON", true);

        // Extract the custom finish style parameter context
        String winReason    = getIntent().getStringExtra("WIN_REASON");

        if (isStalemate) {
            dialogMessage.setText("Stalemate! The game ends in a Draw. 🤝");
        } else if ("TIME".equals(winReason)) {
            //Time win
            dialogMessage.setText(whiteWon
                    ? "Time's up! WHITE wins on time! 🏆⏱️"
                    : "Time's up! BLACK wins on time! ♟️⏱️");
        } else {
            // checkmate win
            dialogMessage.setText(whiteWon
                    ? "Congratulations! WHITE wins by Checkmate! 🏆"
                    : "Game Over! BLACK wins by Checkmate! ♟️");
        }

        btnGoToLobby.setOnClickListener(v -> {
            DatabaseManager.saveStatsToFirebase(this);

            Intent intent = new Intent(GameOverDialog.this, lobby.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("FROM_SCREEN", "GAME");
            startActivity(intent);
            finish();
        });
    }
}