package com.example.chessgame;

public class Rook extends Piece {
    public Rook(Point startIndex, char color, char type)
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
        return inTheWay(getColor(), board);
    }

    @Override
    public boolean eat(char color, Piece[][] board)
    {
        Piece target = board[getEndIndex().getRow()][getEndIndex().getCol()];
        if(target == null) { return true; }
        return target.getColor() != color;
    }
    @Override
    public boolean inTheWay(char color, Piece[][] board)
    {
        // Ensure the move is either strictly vertical or strictly horizontal
        if (getStartIndex().getRow() == getEndIndex().getRow() || getStartIndex().getCol() == getEndIndex().getCol())
        {
            // Vertical movement logic
            if (getStartIndex().getCol() == getEndIndex().getCol())
            {
                int upOrDown = (getStartIndex().getRow() > getEndIndex().getRow()) ? -1 : 1;

                for (int row = getStartIndex().getRow() + upOrDown; row != getEndIndex().getRow(); row += upOrDown)
                {
                    // CHANGE: Check if the square is NOT null instead of calling .getColor()
                    if (board[row][getStartIndex().getCol()] != null)
                    {
                        return false; // Path is blocked, move is illegal
                    }
                }
            }
            // Horizontal movement logic
            else if (getStartIndex().getRow() == getEndIndex().getRow())
            {
                int leftOrRight = (getStartIndex().getCol() > getEndIndex().getCol()) ? -1 : 1;

                for (int col = getStartIndex().getCol() + leftOrRight; col != getEndIndex().getCol(); col += leftOrRight)
                {
                    // CHANGE: Check if the square is NOT null
                    if (board[getStartIndex().getRow()][col] != null)
                    {
                        return false; // Path is blocked, move is illegal
                    }
                }
            }

            // If the path is clear, check if the destination square is valid (empty or enemy)
            return eat(color, board);
        }

        return false; // Not a valid straight line for a Rook
    }

}
