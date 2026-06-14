package com.example.chessgame;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageButton;

public class PromotionDialog extends Dialog {

    public interface PromotionCallback {
        void onPieceSelected(char chosenPiece);
    }

    private final PromotionCallback callback;
    private final char playerColor;

    // Constructor that pass the color of the current turn player to load the correct assets
    public PromotionDialog(Context context, char playerColor, PromotionCallback callback) {
        super(context);
        this.playerColor = playerColor;
        this.callback = callback;
    }

    /*
    The func bind the image buttons and set their images by the turn color
    and open the dialog so the player have to choose which piece he wants to promote
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_promotion);
        setCancelable(false); // Make sure they can't dismiss without choosing

        ImageButton btnQueen = findViewById(R.id.img_btn_queen);
        ImageButton btnRook = findViewById(R.id.img_btn_rook);
        ImageButton btnKnight = findViewById(R.id.img_btn_knight);
        ImageButton btnBishop = findViewById(R.id.img_btn_bishop);

        // Set the images programmatically depending on whose turn it is
        if (playerColor == Constants.WHITE_PIECE) {
            btnQueen.setImageResource(R.drawable.queenw);
            btnRook.setImageResource(R.drawable.rookw);
            btnKnight.setImageResource(R.drawable.knightw);
            btnBishop.setImageResource(R.drawable.bishopw);
        } else {
            btnQueen.setImageResource(R.drawable.queenb);
            btnRook.setImageResource(R.drawable.rookb);
            btnKnight.setImageResource(R.drawable.knightb);
            btnBishop.setImageResource(R.drawable.bishopb);
        }

        // Set up the click listeners to trigger selection
        btnQueen.setOnClickListener(v -> select('Q'));
        btnRook.setOnClickListener(v -> select('R'));
        btnKnight.setOnClickListener(v -> select('N'));
        btnBishop.setOnClickListener(v -> select('B'));
    }

    //The func send the desire piece to the callback and close the dialog
    private void select(char piece) {
        callback.onPieceSelected(piece);
        dismiss();
    }
}