package com.example.chessgame;

import static java.lang.Math.abs;

public class Bishop extends Piece {
    public Bishop(Point startIndex, char color, char type)
    {
        super.setStartIndex(startIndex);
        super.setEndIndex(startIndex);
        super.setColor(color);
        super.setType(type);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
    @Override
    public boolean move(char color, Piece[][] board)
    {
        if (abs(getEndIndex().getRow() - getStartIndex().getRow()) == abs(getEndIndex().getCol() - getStartIndex().getCol()))
        {
            return eat(color, board);

        }
        return false;
    }

    @Override
    public boolean eat(char color, Piece[][] board)
    {
        Piece target  = board[getEndIndex().getRow()][getEndIndex().getCol()];

        if (target == null)
        {
            if (target.getColor() == color)
            {
                return false;
            }
            return inTheWay(color, board);
        }
        return false;
    }
    @Override
    public boolean inTheWay(char color, Piece[][] board) {

        int rowStep = Integer.compare(getEndIndex().getRow(), getStartIndex().getRow());
        int colStep = Integer.compare(getEndIndex().getCol(), getStartIndex().getCol());

        int row = getStartIndex().getRow() + rowStep;
        int col = getStartIndex().getCol() + colStep;


        // Inside Bishop.java while loop
        while (row != getEndIndex().getRow() || col != getEndIndex().getCol()) {
            // CRITICAL FIX: Check for null before calling .getColor()
            if (board[row][col] != null) {
                return false; // Path is blocked by a piece
            }
            row += rowStep;
            col += colStep;
        }
        return true;
    }

}
