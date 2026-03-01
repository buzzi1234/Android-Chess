package com.example.chessgame;

import static java.lang.Math.abs;

public class Queen extends Piece {

    Rook m_rook;
    Bishop m_bishop;
    public Queen(Point startIndex, char color, char type)
    {
        this.m_rook = new Rook(startIndex,color, Constants.ROOK);
        this.m_bishop = new Bishop(startIndex,color, Constants.BISHOP);
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
            this.m_bishop.setStartIndex(getStartIndex());
            this.m_bishop.setEndIndex(getEndIndex());
            return this.m_bishop.move(color, board);
        }
        else if (getStartIndex().getRow() == getEndIndex().getRow() || getStartIndex().getCol() == getEndIndex().getCol())
        {
            this.m_rook.setStartIndex(getStartIndex());
            this.m_rook.setEndIndex(getEndIndex());
            return this.m_bishop.move(color, board);
        }

        return false;

    }

    @Override
    public boolean eat(char color, Piece[][] board) {
        if (abs(getEndIndex().getRow() - getStartIndex().getRow()) == abs(getEndIndex().getCol() - getStartIndex().getCol())) {
            m_bishop.setStartIndex(getStartIndex());
            m_bishop.setEndIndex(getEndIndex());
            return m_bishop.eat(color, board);
        } else if (getStartIndex().getRow() == getEndIndex().getRow() || getStartIndex().getCol() == getEndIndex().getCol()) {
            m_rook.setStartIndex(getStartIndex());
            m_rook.setEndIndex(getEndIndex());
            // FIX: Change m_bishop to m_rook here!
            return m_rook.eat(color, board);
        }
        return false;
    }
    @Override
    public boolean inTheWay(char color, Piece[][] board)
    {
        if (abs(getEndIndex().getRow() - getStartIndex().getRow()) == abs(getEndIndex().getCol() - getStartIndex().getCol()))
        {
            this.m_bishop.setStartIndex(getStartIndex());
            this.m_bishop.setEndIndex(getEndIndex());
            return this.m_bishop.inTheWay(color, board);
        }
        else if (getStartIndex().getRow() == getEndIndex().getRow() || getStartIndex().getCol() == getEndIndex().getCol())
        {
            this.m_rook.setStartIndex(getStartIndex());
            this.m_rook.setEndIndex(getEndIndex());
            return this.m_rook.inTheWay(color, board);
        }

        return false;

    }

}
