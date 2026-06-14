package com.example.chessgame;

public class Rook extends Piece {
    //constructor
    public Rook(Point startIndex, char color, char type) {
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

    //for rook just return the intheway
    @Override
    public boolean move(char color, Piece[][] board) {
        // Must be a valid straight line and have a clear path
        return inTheWay(color, board);
    }

    //for rook just return the intheway
    @Override
    public boolean eat(char color, Piece[][] board) {
        // To attack/eat, it MUST follow the exact same structural path rules
        return inTheWay(color, board);
    }

    /*
    The func cheks if the move is valid and if any piece in the way of the rook
    input: color of the piece, that board it self
    output: true if move is valid else false
     */
    @Override
    public boolean inTheWay(char color, Piece[][] board) {
        // Strictly enforce positional validation (Same row OR same column)
        if (getStartIndex().getRow() == getEndIndex().getRow() || getStartIndex().getCol() == getEndIndex().getCol()) {

            // Vertical movement logic
            if (getStartIndex().getCol() == getEndIndex().getCol()) {
                int upOrDown = (getStartIndex().getRow() > getEndIndex().getRow()) ? -1 : 1;

                for (int row = getStartIndex().getRow() + upOrDown; row != getEndIndex().getRow(); row += upOrDown) {
                    if (board[row][getStartIndex().getCol()] != null) {
                        return false; // Blocked
                    }
                }
            }
            // Horizontal movement logic
            else if (getStartIndex().getRow() == getEndIndex().getRow()) {
                int leftOrRight = (getStartIndex().getCol() > getEndIndex().getCol()) ? -1 : 1;

                for (int col = getStartIndex().getCol() + leftOrRight; col != getEndIndex().getCol(); col += leftOrRight) {
                    if (board[getStartIndex().getRow()][col] != null) {
                        return false; // Blocked
                    }
                }
            }

            // 2. Check the landing square targeting rule
            Piece target = board[getEndIndex().getRow()][getEndIndex().getCol()];
            if (target == null) return true; // Empty square is fine
            return target.getColor() != color; // Enemy square is fine, teammate is blocked
        }

        return false; // Not a straight line!
    }
}