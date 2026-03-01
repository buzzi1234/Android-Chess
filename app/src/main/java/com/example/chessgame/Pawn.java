package com.example.chessgame;

public class Pawn extends Piece{

    private int m_jumps;

    public Pawn(Point startIndex, char color, char type)
    {
        this.m_jumps = Constants.PAWN_JUMPS;
        super.setStartIndex(startIndex);
        super.setEndIndex(startIndex);
        super.setColor(color);
        super.setType(type);
    }

    public void destroy()
    {
        this.m_jumps = 0;
        super.destroy();
    }

    public boolean turnPiece(char type)
    {
        if (type == Constants.QUEEN)
        {
            super.setType(Constants.QUEEN);
        }
        else if (type == Constants.ROOK)
        {
            super.setType(Constants.ROOK);
        }
        else if (type == Constants.BISHOP)
        {
            super.setType(Constants.BISHOP);
        }
        else if (type == Constants.KNIGHT)
        {
            super.setType(Constants.KNIGHT);
        }
        else
        {
            return false;
        }
        return true;
    }

    public boolean firstMove(char color, Piece[][] board)
    {
        switch (color)
        {
            case Constants.BLACK_PIECE:
                if (getStartIndex().getRow() == Constants.ROW_START_BLACK)
                {
                    if (inTheWay(color, board))
                    {
                        this.m_jumps = Constants.DOUBLE_JUMP;
                        if (inTheWay(color, board))
                        {
                            this.m_jumps = Constants.PAWN_JUMPS;
                            return true;
                        }
                        this.m_jumps = Constants.PAWN_JUMPS;
                    }
                }
                break;
            case Constants.WHITE_PIECE:
                if (getStartIndex().getRow() == Constants.ROW_START_WHITE)
                {
                    if (inTheWay(color, board))
                    {
                        this.m_jumps = Constants.DOUBLE_JUMP;
                        if (inTheWay(color, board))
                        {
                            this.m_jumps = Constants.PAWN_JUMPS;
                            return true;
                        }
                        this.m_jumps = Constants.PAWN_JUMPS;
                    }
                }
                break;
            default:
                return false;
        }

        return false;

    }

    @Override
    public boolean move(char color, Piece[][] board)
    {
        switch (getColor())
        {
            case Constants.BLACK_PIECE:
                //Eating diagonally to the left
                if ((getStartIndex().getCol() - Constants.PAWN_JUMPS == getEndIndex().getCol()) && (getStartIndex().getRow() - Constants.PAWN_JUMPS == getEndIndex().getRow()))
                {
                    return eat(color, board);
                }
                //Eating diagonally to the right
                if ((getStartIndex().getCol() + Constants.PAWN_JUMPS == getEndIndex().getCol()) && (getStartIndex().getRow() - Constants.PAWN_JUMPS == getEndIndex().getRow()))
                {
                    return eat(color, board);
                }
                //walking one step
                if (getStartIndex().getRow() - Constants.PAWN_JUMPS == getEndIndex().getRow())
                {
                    return inTheWay(color, board);
                }
                //Walking for the first time two steps
                if (getStartIndex().getRow() - Constants.DOUBLE_JUMP == getEndIndex().getRow() && getStartIndex().getCol() == getEndIndex().getCol())
                {
                    return firstMove(color, board);
                }
                break;
            case Constants.WHITE_PIECE:
                //Eating diagonally to the left
                if ((getStartIndex().getCol() - Constants.PAWN_JUMPS == getEndIndex().getCol()) && (getStartIndex().getRow() + Constants.PAWN_JUMPS == getEndIndex().getRow()))
                {
                    return eat(color, board);
                }
                //Eating diagonally to the right
                if ((getStartIndex().getCol() + Constants.PAWN_JUMPS == getEndIndex().getCol()) && (getStartIndex().getRow() + Constants.PAWN_JUMPS == getEndIndex().getRow()))
                {
                    return eat(color, board);
                }
                //walking one step
                if (getStartIndex().getRow() + Constants.PAWN_JUMPS == getEndIndex().getRow())
                {
                    return inTheWay(color, board);
                }
                //Walking for the first time two steps
                if (getStartIndex().getRow() + Constants.DOUBLE_JUMP == getEndIndex().getRow() && getStartIndex().getCol() == getEndIndex().getCol())
                {
                    return firstMove(color, board);
                }
                break;
            default:
                return false;
        }

        return false;

    }

    @Override
    public boolean eat(char color, Piece[][] board) {
        Piece target = board[getEndIndex().getRow()][getEndIndex().getCol()];
        if (target == null) return false; // Cannot eat an empty square
        return target.getColor() != color;
    }
    @Override
    public boolean inTheWay(char color, Piece[][] board) {
        int row = getStartIndex().getRow();
        int col = getStartIndex().getCol();
        int direction = (color == Constants.BLACK_PIECE) ? -1 : 1;

        // Use a safe check for null
        Piece pieceInFront = board[row + direction][col];
        if (pieceInFront != null) {
            // There is a piece in front, so the path is NOT clear
            return false;
        }

        // Path is clear
        return true;
    }


}
