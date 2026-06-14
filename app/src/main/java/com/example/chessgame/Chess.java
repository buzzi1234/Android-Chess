package com.example.chessgame;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class Chess {
    //fields
    private char _move;
    public Round _gameRounds;

    //constructor
    public Chess() {
        this._move = Constants.WHITE_PIECE;
        this._gameRounds = new Round(Constants.FRONTEND_MSG);
    }

    /*
    The func is responsible on the game logic first selection of the piece to move
    and then after all of the checks it checks for pawn promotion and if there is
    a win lose or tie it open the game over dialog and finish the game
     */
    public void game(Context context) {
        this._gameRounds.setColor(_move);
        boolean moveSuccessful = this._gameRounds.moveInBoard();

        if (moveSuccessful) {
            // Check for Pawn Promotion FIRST (While it is still the moving player's turn)
            if (isPawnEligibleForPromotion()) {
                // Open the custom dialog, passing the context, current player color ('W' or 'B'), and the choice callback
                PromotionDialog dialog = new PromotionDialog(context, _move, new PromotionDialog.PromotionCallback() {
                    @Override
                    public void onPieceSelected(char chosenPiece) {
                        // Normalize the character case depending on piece color matching your matrix framework rules
                        char absolutePiece = (_move == Constants.WHITE_PIECE)
                                ? Character.toLowerCase(chosenPiece)
                                : Character.toUpperCase(chosenPiece);

                        // Update board array layout cell with the new upgraded piece character type
                        _gameRounds.promotePawnOnBoard(absolutePiece);

                        completeTurnSequence(context);
                    }
                });
                dialog.show();

            } else {
                // No promotion happened! Run normal turn finalization workflows instantly
                completeTurnSequence(context);
            }

        } else {
            // Move was rejected — tell the player why
            if (this._gameRounds.isInCheck()) {
                Toast.makeText(context,
                        "Illegal move — your King is still in check!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*The func complete all the turn event from switching turns
    to check if the kings in check or checkmate or stalemate
     */
    private void completeTurnSequence(Context context) {
        // Switch turn here so we can evaluate the NEW active player's state
        switchTurn();

        boolean inCheck = this._gameRounds.isPlayerInCheck();

        // Warn the new active player if they are now caught in check
        if (inCheck) {
            String colorName = (_move == Constants.WHITE_PIECE) ? "White" : "Black";
            android.util.Log.d("CHESS_GAME", colorName + " is in CHECK!");
            Toast.makeText(context,
                    colorName + " is in CHECK!",
                    Toast.LENGTH_SHORT).show();
        }

        // Evaluate structural end-game check patterns safely
        if (this._gameRounds.isPlayerInCheckMate()) {
            // No legal moves left AND King is in check -> Checkmate!
            finishedGame(context, false);
        } else if (!inCheck && hasNoLegalMovesLeft()) {
            // No legal moves left AND King is NOT in check -> Stalemate Draw!
            finishedGame(context, true);
        }
    }

    // End-Game handling pipeline supporting both Checkmates and Stalemates
    public void finishedGame(Context context, boolean isStalemate) {
        GlobalStat globalState = (GlobalStat) context.getApplicationContext();
        final boolean whiteWon = (this._move == Constants.BLACK_PIECE);

        if (isStalemate) {
            // A stalemate is a draw
            globalState.userStats.stalemates++; // Increment your draws accumulator tracker
            android.util.Log.d("CHESS_GAME", "Game ended in a Stalemate (Draw)!");
        } else {
            if (whiteWon) {
                globalState.userStats.wins++;
                android.util.Log.d("CHESS_GAME", "White wins by Checkmate!");
            } else {
                globalState.userStats.losses++;
                android.util.Log.d("CHESS_GAME", "Black wins by Checkmate!");
            }
        }

        this._move = '0';

        //move to the game over dialog
        Intent dialogIntent = new Intent(context, GameOverDialog.class);
        dialogIntent.putExtra("IS_STALEMATE", isStalemate);
        dialogIntent.putExtra("WHITE_WON", whiteWon);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(dialogIntent);
    }

    //this func switch to white or black or zero ( game ended )
    private void switchTurn() {
        _move = (_move == Constants.WHITE_PIECE) ? Constants.BLACK_PIECE : Constants.WHITE_PIECE;
        _gameRounds.setColor(_move);
    }
    //set a new move
    public void setMove(char move) {
        this._move = move;
    }

    //return the turn
    public char getMove() {
        return _move;
    }

    // Helper checker hook looking up matching row limits inside  Round class wrapper instance
    private boolean isPawnEligibleForPromotion() {
        return this._gameRounds.checkLastMoveForPromotion();
    }

    // Helper wrapper method to determine legal fallback options
    private boolean hasNoLegalMovesLeft() {
        return this._gameRounds.isPlayerInCheckMate();
    }
}