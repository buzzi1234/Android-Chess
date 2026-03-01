package com.example.chessgame;

import android.util.Log;

public class Round {
    private final Piece[][] _board;
    private char _color;
    private Point _p, _p1; // Start and End points
    String _inputBoard;

    public Round(String inputBoard) {
        // 1. Check if the string is valid BEFORE starting the loops
        if (inputBoard == null || inputBoard.length() < 64) {
            Log.e("Round", "Invalid board string length!");
            this._inputBoard = "0".repeat(64); // Fallback to empty board
        } else {
            this._inputBoard = inputBoard;
        }

        this._board = new Piece[Constants.BOARD_LEN][Constants.BOARD_LEN];
        this._p = new Point();
        this._p1 = new Point();

        // Default turn to White
        this._color = Constants.WHITE_PIECE;

        turnToBoard();
    }

    public void setMovePoints(Point start, Point end) {
        this._p = start;
        this._p1 = end;
    }

    // Inside Round.java
    public boolean moveInBoard() {
        if (_p == null || _p1 == null) return false;

        // 2. Get the piece from the board
        Piece pieceToMove = _board[_p.getRow()][_p.getCol()];

        // 3. SAFETY CHECK: If the square is empty, stop immediately
        if (pieceToMove == null) {
            android.util.Log.d("ChessLogic", "No piece found at " + _p.getRow() + "," + _p.getCol());
            return false;
        }

        // 4. Now it's safe to check color and type
        pieceToMove.setEndIndex(_p1);

        // Rest of your logic...
        if (pieceToMove.getColor() != this._color) return false;

        pieceToMove.setStartIndex(_p);
        pieceToMove.setEndIndex(_p1);

        if (pieceToMove.move(this._color, _board)) {
            _board[_p1.getRow()][_p1.getCol()] = pieceToMove;
            pieceToMove.setStartIndex(new Point(_p1.getRow(), _p1.getCol()));
            _board[_p.getRow()][_p.getCol()] = null;
            return true;
        }
        return false;
    }
    public boolean isPlayerInCheckMate() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (_board[i][j] instanceof King && _board[i][j].getColor() == _color) {
                    return ((King) _board[i][j]).checkMate(_color, _board);
                }
            }
        }
        return false;
    }

    public void turnToBoard() {
        if (this._inputBoard == null || this._inputBoard.length() < 64) {
            Log.e("CHESS_ERROR", "Board string is too short! Length: " +
                    (this._inputBoard == null ? 0 : this._inputBoard.length()));
            return;
        }

        int txtIndex = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                char type = this._inputBoard.charAt(txtIndex);
                Point p = new Point(row, col);

                // Use NULL for empty slots to save memory and avoid "Pawn" logic confusion
                if (type == ' ' || type == '0') {
                    this._board[row][col] = null;
                } else {
                    char color = Character.isLowerCase(type) ? Constants.WHITE_PIECE : Constants.BLACK_PIECE;
                    char lowerType = Character.toLowerCase(type);

                    // Initialize pieces (Make sure your Piece constructors handle the TAG/Points correctly)
                    if (lowerType == Constants.KING) this._board[row][col] = new King(p, color, type);
                    else if (lowerType == Constants.KNIGHT) this._board[row][col] = new Knight(p, color, type);
                    else if (lowerType == Constants.PAWN) this._board[row][col] = new Pawn(p, color, type);
                    else if (lowerType == Constants.QUEEN) this._board[row][col] = new Queen(p, color, type);
                    else if (lowerType == Constants.ROOK) this._board[row][col] = new Rook(p, color, type);
                    else if (lowerType == Constants.BISHOP) this._board[row][col] = new Bishop(p, color, type);
                }
                txtIndex++;
            }
        }
    }

    public void setColor(char c) { this._color = c; }
    public char getColor() { return _color; }
    public Piece[][] getBoard() { return _board; }
}