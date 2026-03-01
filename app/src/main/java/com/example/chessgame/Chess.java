package com.example.chessgame;

public class Chess {
    private char _move; // Current turn ('w' or 'b')
    public Round _gameRounds;

    public Chess() {
        this._move = Constants.WHITE_PIECE;
        this._gameRounds = new Round(Constants.FRONTEND_MSG);
    }

    // Inside Chess.java
    public void game() {
        this._gameRounds.setColor(_move);
        boolean moveSuccessful = this._gameRounds.moveInBoard();

        if (moveSuccessful) {

            switchTurn();
            if (this._gameRounds.isPlayerInCheckMate()) {
                finishedGame();
            }
        }
    }

    private void switchTurn() {
        _move = (_move == Constants.WHITE_PIECE) ? Constants.BLACK_PIECE : Constants.WHITE_PIECE;
        _gameRounds.setColor(_move);
    }

    public char getMove() { return _move; }

    public void finishedGame() {
        // Handle Game Over Logic
    }
}