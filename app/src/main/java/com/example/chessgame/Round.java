package com.example.chessgame;

import android.util.Log;

public class Round {
    //fields
    private final Piece[][] _board;
    private char _color;
    private Point _p, _p1;
    String _inputBoard;
    private boolean _isInCheck = false;

    //constructor
    public Round(String inputBoard) {
        if (inputBoard == null || inputBoard.length() < 64) {
            Log.e("Round", "Invalid board string length!");
            this._inputBoard = "0".repeat(64);
        } else {
            this._inputBoard = inputBoard;
        }

        this._board = new Piece[Constants.BOARD_LEN][Constants.BOARD_LEN];
        this._p = new Point();
        this._p1 = new Point();
        this._color = Constants.WHITE_PIECE;

        turnToBoard();
    }

    /*
    The func get to points and reset the p and p1 points
     */
    public void setMovePoints(Point start, Point end) {
        this._p = start;
        this._p1 = end;
    }

    //the function returns a bool var of the isInCheck
    public boolean isInCheck() {
        return _isInCheck;
    }

    /*
    The func move the piece that locates in p to p1 but
    first it simulate to see if that cause any check
     */
    public boolean moveInBoard() {
        if (_p == null || _p1 == null) return false;

        Piece pieceToMove = _board[_p.getRow()][_p.getCol()];

        if (pieceToMove == null) {
            Log.d("CHESS_DEBUG", "No piece at " + _p.getRow() + "," + _p.getCol());
            return false;
        }

        if (pieceToMove.getColor() != this._color) {
            Log.d("CHESS_DEBUG", "Wrong color. Piece=" + pieceToMove.getColor() + " Turn=" + this._color);
            return false;
        }

        pieceToMove.setStartIndex(new Point(_p.getRow(), _p.getCol()));
        pieceToMove.setEndIndex(new Point(_p1.getRow(), _p1.getCol()));

        boolean canMove = pieceToMove.move(this._color, _board);
        boolean canEat  = pieceToMove.eat(this._color, _board);

        Log.d("CHESS_DEBUG", "Move from (" + _p.getRow() + "," + _p.getCol() + ") to ("
                + _p1.getRow() + "," + _p1.getCol() + ")");
        Log.d("CHESS_DEBUG", "canMove=" + canMove + " canEat=" + canEat);

        if (!canMove && !canEat) {
            Log.d("CHESS_DEBUG", "Piece says move is illegal by its own rules");
            return false;
        }

        // Simulate
        Piece captured = _board[_p1.getRow()][_p1.getCol()];
        _board[_p1.getRow()][_p1.getCol()] = pieceToMove;
        _board[_p.getRow()][_p.getCol()] = null;

        pieceToMove.setStartIndex(new Point(_p1.getRow(), _p1.getCol()));
        pieceToMove.setEndIndex(new Point(_p1.getRow(), _p1.getCol()));

        King king = findKing(this._color);
        Log.d("CHESS_DEBUG", "King found: " + (king != null));
        if (king != null) {
            Log.d("CHESS_DEBUG", "King position: " + king.getStartIndex().getRow() + "," + king.getStartIndex().getCol());
        }

        boolean stillInCheck = (king != null) && king.isInCheck(this._color, _board);
        Log.d("CHESS_DEBUG", "stillInCheck after simulation=" + stillInCheck);

        // Undo
        _board[_p.getRow()][_p.getCol()] = pieceToMove;
        _board[_p1.getRow()][_p1.getCol()] = captured;
        pieceToMove.setStartIndex(new Point(_p.getRow(), _p.getCol()));
        pieceToMove.setEndIndex(new Point(_p.getRow(), _p.getCol()));

        if (stillInCheck) {
            _isInCheck = true;
            Log.d("CHESS_DEBUG", "REJECTED: king still in check");
            return false;
        }

        // Apply
        _board[_p1.getRow()][_p1.getCol()] = pieceToMove;
        _board[_p.getRow()][_p.getCol()] = null;
        pieceToMove.setStartIndex(new Point(_p1.getRow(), _p1.getCol()));
        pieceToMove.setEndIndex(new Point(_p1.getRow(), _p1.getCol()));

        _isInCheck = false;
        return true;
    }

    //The func try to find the king by the color its given
    public King findKing(char color) {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if (_board[i][j] instanceof King && _board[i][j].getColor() == color)
                    return (King) _board[i][j];
        return null;
    }

    /*
    The func return true if the player king is in checkmate else false
     */
    public boolean isPlayerInCheckMate() {
        King activeKing = findKing(this._color);
        if (activeKing != null)
            return activeKing.checkMate(this._color, this._board);
        return false;
    }

    /*
    The func return true if the player king is in check else false
     */
    public boolean isPlayerInCheck() {
        King activeKing = findKing(this._color);
        if (activeKing != null)
            return activeKing.isInCheck(this._color, this._board);
        return false;
    }

    /*
    The func takes a list of chars and turn it to a full board of pieces
     */
    public void turnToBoard() {
        if (this._inputBoard == null || this._inputBoard.length() < 64) {
            Log.e("CHESS_ERROR", "Board string is too short! Length: " +
                    (this._inputBoard == null ? 0 : this._inputBoard.length()));
            return;
        }

        //the turn board logic
        int txtIndex = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                char type = this._inputBoard.charAt(txtIndex);
                Point p = new Point(row, col);

                if (type == ' ' || type == '0') {
                    this._board[row][col] = null;
                } else {
                    char color = Character.isLowerCase(type) ? Constants.WHITE_PIECE : Constants.BLACK_PIECE;
                    char lowerType = Character.toLowerCase(type);

                    if (lowerType == Constants.KING)        this._board[row][col] = new King(p, color, type);
                    else if (lowerType == Constants.KNIGHT) this._board[row][col] = new Knight(p, color, type);
                    else if (lowerType == Constants.PAWN)   this._board[row][col] = new Pawn(p, color, type);
                    else if (lowerType == Constants.QUEEN)  this._board[row][col] = new Queen(p, color, type);
                    else if (lowerType == Constants.ROOK)   this._board[row][col] = new Rook(p, color, type);
                    else if (lowerType == Constants.BISHOP) this._board[row][col] = new Bishop(p, color, type);
                }
                txtIndex++;
            }
        }
    }

    // checks if the piece that just successfully moved is a pawn landing on its promotion row
    public boolean checkLastMoveForPromotion() {
        if (_p1 == null) return false;

        int targetRow = _p1.getRow();
        Piece lastMovedPiece = _board[targetRow][_p1.getCol()];

        if (lastMovedPiece instanceof Pawn) {

            // White Pawns move DOWN to row index 7
            if (targetRow == 7 && lastMovedPiece.getColor() == Constants.WHITE_PIECE) {
                Log.d("CHESS_PROMOTION", "White pawn reached row 7! Opening Dialog...");
                return true;
            }

            // Black Pawns move UP to row index 0
            if (targetRow == 0 && lastMovedPiece.getColor() == Constants.BLACK_PIECE) {
                Log.d("CHESS_PROMOTION", "Black pawn reached row 0! Opening Dialog...");
                return true;
            }
        }
        return false;
    }

    /*
    The func promote the pawn on the board by
    turning the pawn to queen, bishop, knight or a rook
     */
    public void promotePawnOnBoard(char absolutePiece) {
        if (_p1 == null) return; // Exit early safely if there's no point context

        int row = _p1.getRow();
        int col = _p1.getCol();
        Point p = new Point(row, col);

        // Determine color context by checking the case format style of the character parameter
        char pieceColor = Character.isLowerCase(absolutePiece) ? Constants.WHITE_PIECE : Constants.BLACK_PIECE;
        char lowerType = Character.toLowerCase(absolutePiece);

        if (lowerType == Constants.QUEEN)        this._board[row][col] = new Queen(p, pieceColor, absolutePiece);
        else if (lowerType == Constants.ROOK)   this._board[row][col] = new Rook(p, pieceColor, absolutePiece);
        else if (lowerType == Constants.KNIGHT) this._board[row][col] = new Knight(p, pieceColor, absolutePiece);
        else if (lowerType == Constants.BISHOP) this._board[row][col] = new Bishop(p, pieceColor, absolutePiece);

        Log.d("CHESS_PROMOTION", "Pawn at (" + row + "," + col + ") promoted to char target: " + absolutePiece);
    }

    //set the color
    public void setColor(char c) { this._color = c; }
    //return the turn by the color
    public char getColor() { return _color; }
    //return the game board
    public Piece[][] getBoard() { return _board; }
}