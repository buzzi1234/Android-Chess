package com.example.chessgame;

import static java.lang.Math.abs;

public class King extends Piece {
    public King(Point startIndex, char color, char type)
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
        if (abs(getStartIndex().getRow() - getEndIndex().getRow()) <= Constants.DIF_ROW && abs(getStartIndex().getCol() - getEndIndex().getCol()) <= Constants.DIF_COL)
        {
            return inTheWay(color, board);
        }
        return false;

    }

    @Override
    public boolean eat(char color, Piece[][] board)
    {
        return true;
    }
    @Override
    public boolean inTheWay(char color, Piece[][] board)
    {
        Piece target = board[getEndIndex().getRow()][getEndIndex().getCol()];
        if (target == null) return true; // Path is clear
        return target.getColor() != color; // Can move if it's an enemy
    }


    public boolean isInCheck(char color, Piece[][] board) {
        int kRow = -1, kCol = -1;

        // 1. Find the King on the current board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] instanceof King && board[i][j].getColor() == color) {
                    kRow = i; kCol = j;
                    break;
                }
            }
        }
        if (kRow == -1) return false;

        char opponentColor = (color == Constants.WHITE_PIECE) ? Constants.BLACK_PIECE : Constants.WHITE_PIECE;

        // 2. See if any enemy can hit that square
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = board[i][j];
                if (p != null && p.getColor() == opponentColor) {
                    p.setStartIndex(new Point(i, j));
                    p.setEndIndex(new Point(kRow, kCol));
                    // Only call eat() if it doesn't call isInCheck() internally
                    if (p.eat(opponentColor, board)) return true;
                }
            }
        }
        return false;
    }
    public boolean checkMate(char color, Piece[][] board) {
        // 1. If not even in check, it can't be checkmate
        if (!isInCheck(color, board)) return false;

        // 2. Try every piece belonging to 'color'
        for (int i = 0; i < Constants.BOARD_LEN; i++) {
            for (int j = 0; j < Constants.BOARD_LEN; j++) {
                Piece p = board[i][j];
                if (p != null && p.getColor() == color) {

                    // 3. Try moving this piece to every possible square on the board
                    for (int x = 0; x < Constants.BOARD_LEN; x++) {
                        for (int y = 0; y < Constants.BOARD_LEN; y++) {
                            p.setStartIndex(new Point(i, j));
                            p.setEndIndex(new Point(x, y));

                            // Check if the move is legal (either a move or an eat)
                            if (p.move(color, board) || p.eat(color, board)) {

                                // 4. SIMULATE THE MOVE
                                Piece temp = board[x][y];
                                board[x][y] = p;
                                board[i][j] = null; // Create an empty slot (you might need a new Piece() if you don't use nulls)

                                boolean stillInCheck = isInCheck(color, board);

                                // 5. UNDO THE MOVE (Critical!)
                                board[i][j] = p;
                                board[x][y] = temp;

                                // If we found a move that removes the check, it's not checkmate
                                if (!stillInCheck) return false;
                            }
                        }
                    }
                }
            }
        }
        return true; // No moves could save the King
    }
}
