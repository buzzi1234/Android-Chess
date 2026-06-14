package com.example.chessgame;

public class Bishop extends Piece {
    //constructor
    public Bishop(Point startIndex, char color, char type) {
        super.setStartIndex(startIndex);
        super.setEndIndex(startIndex);
        super.setColor(color);
        super.setType(type);
    }

    //destructor
    @Override
    public void destroy() {
        super.destroy();
    }

    //for bishop it just in the way
    @Override
    public boolean move(char color, Piece[][] board) {
        return inTheWay(color, board);
    }

    //for bishop it just in the way
    @Override
    public boolean eat(char color, Piece[][] board) {
        return inTheWay(color, board);
    }

    /*
    The func cheks if the move is valid and if any piece in the way of the bishop
    input: color of the piece, that board it self
    output: true if move is valid else false
     */
    @Override
    public boolean inTheWay(char color, Piece[][] board) {
        // 1. Strictly enforce diagonal geometry validation
        if (Math.abs(getEndIndex().getRow() - getStartIndex().getRow()) ==
                Math.abs(getEndIndex().getCol() - getStartIndex().getCol())) {

            int rowStep = Integer.compare(getEndIndex().getRow(), getStartIndex().getRow());
            int colStep = Integer.compare(getEndIndex().getCol(), getStartIndex().getCol());

            int row = getStartIndex().getRow() + rowStep;
            int col = getStartIndex().getCol() + colStep;

            // Check intermediate squares
            while (row != getEndIndex().getRow() || col != getEndIndex().getCol()) {
                if (board[row][col] != null) {
                    return false; // Blocked
                }
                row += rowStep;
                col += colStep;
            }

            // Check landing target validity
            Piece target = board[getEndIndex().getRow()][getEndIndex().getCol()];
            if (target == null) return true;
            return target.getColor() != color;
        }

        return false; // Not a diagonal move!
    }
}