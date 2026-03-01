package com.example.chessgame;

import static java.lang.Math.abs;

public class Knight extends Piece {
    public Knight(Point startIndex, char color, char type)
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
        int rowDifference = abs(getEndIndex().getRow() - getStartIndex().getRow());
        int colDifference = abs(getEndIndex().getCol() - getStartIndex().getCol());

        if ((rowDifference == Constants.DOUBLE_JUMP && colDifference == Constants.ROW_START_WHITE) || (rowDifference == Constants.ROW_START_WHITE && colDifference == Constants.DOUBLE_JUMP))
        {
            return eat(color, board);
        }
        return false;
    }

    @Override
    public boolean eat(char color, Piece[][] board)
    {
        Piece target = board[getEndIndex().getRow()][getEndIndex().getCol()];

        // If the square is empty (null), the move is valid.
        if (target == null) {
            return true;
        }

        // If there is a piece, only move if it's the opposite color.
        return target.getColor() != color;
    }
    @Override
    public boolean inTheWay(char color, Piece[][] board)
    {
        return false;
    }

}
