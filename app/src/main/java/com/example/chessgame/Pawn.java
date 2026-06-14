package com.example.chessgame;

import static java.lang.Math.abs;

public class Pawn extends Piece {

    //field
    private int m_jumps;

    //constructor
    public Pawn(Point startIndex, char color, char type) {
        this.m_jumps = Constants.PAWN_JUMPS;
        super.setStartIndex(startIndex);
        super.setEndIndex(startIndex);
        super.setColor(color);
        super.setType(type);
    }

    //destructor
    public void destroy() {
        this.m_jumps = 0;
        super.destroy();
    }

    //check if the pawn can be moved one or two up or down
    @Override
    public boolean move(char color, Piece[][] board) {
        int startRow = getStartIndex().getRow();
        int startCol = getStartIndex().getCol();
        int endRow = getEndIndex().getRow();
        int endCol = getEndIndex().getCol();

        // Straight movement must land on an empty square
        if (board[endRow][endCol] != null) {
            return false;
        }

        // Straight movement cannot change columns
        if (startCol != endCol) {
            return false;
        }

        // Check the path using the corrected inTheWay logic
        if (!inTheWay(color, board)) {
            return false; // Something is blocking the path forward
        }

        // Validate the exact step length allowed based on color direction
        if (color == Constants.WHITE_PIECE) {
            // White moves down (increasing rows): either 1 step, or 2 steps from starting row (row 1)
            if (endRow == startRow + 1) {
                return true;
            }
            if (startRow == 1 && endRow == 3) {
                return true;
            }
        } else {
            // Black moves up (decreasing rows): either 1 step, or 2 steps from starting row (row 6)
            if (endRow == startRow - 1) {
                return true;
            }
            if (startRow == 6 && endRow == 4) {
                return true;
            }
        }

        return false;
    }

    //the func checks if the pawn can eat diagonal
    @Override
    public boolean eat(char color, Piece[][] board) {
        int startRow = getStartIndex().getRow();
        int startCol = getStartIndex().getCol();
        int endRow = getEndIndex().getRow();
        int endCol = getEndIndex().getCol();

        Piece target = board[endRow][endCol];

        // To capture, there must be an enemy piece sitting on the target square
        if (target == null || target.getColor() == color) {
            return false;
        }

        int rowDiff = endRow - startRow;
        int colDiff = abs(endCol - startCol);

        // Capturing must be exactly 1 square diagonally
        if (colDiff != 1) {
            return false;
        }

        if (color == Constants.WHITE_PIECE) {
            return rowDiff == 1;  // White captures moving down
        } else {
            return rowDiff == -1; // Black captures moving up
        }
    }

    //the func checks if there is a player in the way of the pawn moves
    @Override
    public boolean inTheWay(char color, Piece[][] board) {
        int startRow = getStartIndex().getRow();
        int startCol = getStartIndex().getCol();
        int endRow = getEndIndex().getRow();

        // If moving 2 squares, check the intermediate square right in front of the pawn
        if (abs(endRow - startRow) == 2) {
            int direction = (color == Constants.WHITE_PIECE) ? 1 : -1;
            int intermediateRow = startRow + direction;

            // If the intermediate square is occupied, path is blocked
            if (board[intermediateRow][startCol] != null) {
                return false;
            }
        }

        // Path is completely clear
        return true;
    }

    //the func turn the piece type to a queen rook bishop or knight
    public boolean turnPiece(char type) {
        if (type == Constants.QUEEN)       super.setType(Constants.QUEEN);
        else if (type == Constants.ROOK)   super.setType(Constants.ROOK);
        else if (type == Constants.BISHOP) super.setType(Constants.BISHOP);
        else if (type == Constants.KNIGHT) super.setType(Constants.KNIGHT);
        else return false;
        return true;
    }
}