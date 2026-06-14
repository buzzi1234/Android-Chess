package com.example.chessgame;

public class Queen extends Piece {

    //fields
    private final Rook m_rook;
    private final Bishop m_bishop;

    //constructor
    public Queen(Point startIndex, char color, char type) {
        this.m_rook = new Rook(new Point(startIndex.getRow(), startIndex.getCol()), color, Constants.ROOK);
        this.m_bishop = new Bishop(new Point(startIndex.getRow(), startIndex.getCol()), color, Constants.BISHOP);
        super.setStartIndex(startIndex);
        super.setEndIndex(startIndex);
        super.setColor(color);
        super.setType(type);
    }

    //This func sync the pieces points
    private void syncSubPieces() {
        // Pass brand new Point objects so mutations don't alter shared data structures
        Point currentStart = new Point(getStartIndex().getRow(), getStartIndex().getCol());
        Point currentEnd = new Point(getEndIndex().getRow(), getEndIndex().getCol());

        m_rook.setStartIndex(currentStart);
        m_rook.setEndIndex(currentEnd);

        m_bishop.setStartIndex(currentStart);
        m_bishop.setEndIndex(currentEnd);
    }

    //this func checks if the queen can move diagonal or in straight line
    @Override
    public boolean move(char color, Piece[][] board) {
        syncSubPieces();

        // Diagonal check
        if (Math.abs(getEndIndex().getRow() - getStartIndex().getRow()) == Math.abs(getEndIndex().getCol() - getStartIndex().getCol())) {
            return m_bishop.move(color, board);
        }
        // Straight line check
        else if (getStartIndex().getRow() == getEndIndex().getRow() || getStartIndex().getCol() == getEndIndex().getCol()) {
            return m_rook.move(color, board);
        }
        return false;
    }

    //this func checks if the queen can eat diagonal or in straight line
    @Override
    public boolean eat(char color, Piece[][] board) {
        syncSubPieces();

        if (Math.abs(getEndIndex().getRow() - getStartIndex().getRow()) == Math.abs(getEndIndex().getCol() - getStartIndex().getCol())) {
            return m_bishop.eat(color, board);
        } else if (getStartIndex().getRow() == getEndIndex().getRow() || getStartIndex().getCol() == getEndIndex().getCol()) {
            return m_rook.eat(color, board);
        }
        return false;
    }

    //this func checks if the queen have any pieces int the way  diagonal or in straight line
    @Override
    public boolean inTheWay(char color, Piece[][] board) {
        syncSubPieces();

        if (Math.abs(getEndIndex().getRow() - getStartIndex().getRow()) == Math.abs(getEndIndex().getCol() - getStartIndex().getCol())) {
            return m_bishop.inTheWay(color, board);
        } else if (getStartIndex().getRow() == getEndIndex().getRow() || getStartIndex().getCol() == getEndIndex().getCol()) {
            return m_rook.inTheWay(color, board);
        }
        return false;
    }
}