package com.example.chessgame;

public class Knight extends Piece {
    //constructor
    public Knight(Point startIndex, char color, char type) {
        super.setStartIndex(startIndex);
        super.setEndIndex(startIndex);
        super.setColor(color);
        super.setType(type);
    }

    //check just if the endpoint is valid
    @Override
    public boolean move(char color, Piece[][] board) {
        return isValidKnightMove(color, board);
    }

    //check just if the endpoint is valid
    @Override
    public boolean eat(char color, Piece[][] board) {
        return isValidKnightMove(color, board);
    }


    @Override
    public boolean inTheWay(char color, Piece[][] board) {
        // Knights jump over everything, so the path is never "blocked"
        return true;
    }

    /*
    The func if the knight move is valid by geometry
    input: color of the piece and the board
    output: true if valid else false
     */
    private boolean isValidKnightMove(char color, Piece[][] board) {
        int rowDiff = Math.abs(getEndIndex().getRow() - getStartIndex().getRow());
        int colDiff = Math.abs(getEndIndex().getCol() - getStartIndex().getCol());

        // Strict L-shape calculation
        if ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)) {

            // Validate the final landing square
            Piece target = board[getEndIndex().getRow()][getEndIndex().getCol()];
            if (target == null) return true; // Empty square is valid
            return target.getColor() != color; // Enemy square is valid, teammate blocks it
        }

        return false; // Not a valid L-shape
    }
}