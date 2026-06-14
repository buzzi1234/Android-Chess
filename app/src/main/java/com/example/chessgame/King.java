package com.example.chessgame;

import static java.lang.Math.abs;
import android.util.Log;

public class King extends Piece {

    //constructor
    public King(Point startIndex, char color, char type) {
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

    //the func checks if the king make a valid move
    // if yes it return true else false
    @Override
    public boolean move(char color, Piece[][] board) {
        if (abs(getStartIndex().getRow() - getEndIndex().getRow()) <= Constants.DIF_ROW &&
                abs(getStartIndex().getCol() - getEndIndex().getCol()) <= Constants.DIF_COL) {
            return inTheWay(color, board);
        }
        return false;
    }

    //the func checks if the king can eat
    // if yes it return true else false
    @Override
    public boolean eat(char color, Piece[][] board) {
        if (abs(getStartIndex().getRow() - getEndIndex().getRow()) <= Constants.DIF_ROW &&
                abs(getStartIndex().getCol() - getEndIndex().getCol()) <= Constants.DIF_COL) {
            return inTheWay(color, board);
        }
        return false;
    }

    //the func checks if theres any piece in the way
    // if yes it return true else false
    @Override
    public boolean inTheWay(char color, Piece[][] board) {
        Piece target = board[getEndIndex().getRow()][getEndIndex().getCol()];
        if (target == null) return true;
        return target.getColor() != color;
    }

    /*
    The func checks if the king is in check
    input: color of the king and the game board
    output: if in check true else false
     */
    public boolean isInCheck(char color, Piece[][] board) {
        int kRow = -1, kCol = -1;

        // Locates the King on the active board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] instanceof King && board[i][j].getColor() == color) {
                    kRow = i;
                    kCol = j;
                    break;
                }
            }
        }

        if (kRow == -1) return false;

        char opponentColor = (color == Constants.WHITE_PIECE) ? Constants.BLACK_PIECE : Constants.WHITE_PIECE;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = board[i][j];
                if (p == null || p.getColor() != opponentColor) continue;

                // Create deep copies to isolate side-effects
                Point savedStart = new Point(p.getStartIndex().getRow(), p.getStartIndex().getCol());
                Point savedEnd   = new Point(p.getEndIndex().getRow(),   p.getEndIndex().getCol());

                p.setStartIndex(new Point(i, j));
                p.setEndIndex(new Point(kRow, kCol));

                // Attack evaluation using enemy context color
                boolean canAttack = p.eat(opponentColor, board);

                p.setStartIndex(savedStart);
                p.setEndIndex(savedEnd);

                if (canAttack) {
                    return true;
                }
            }
        }

        return false;
    }


    /*
    The func checks if the king is in checkmate
    input: color of the king and the game board
    output: if in checkmate true else false
     */
    public boolean checkMate(char color, Piece[][] board) {
        // If the king is not even in check, it cannot be checkmate
        if (!isInCheck(color, board)) return false;

        // Loop through all squares to find current player's pieces
        for (int i = 0; i < Constants.BOARD_LEN; i++) {
            for (int j = 0; j < Constants.BOARD_LEN; j++) {
                Piece p = board[i][j];
                if (p == null || p.getColor() != color) continue;

                // Loop through all potential target destination squares
                for (int x = 0; x < Constants.BOARD_LEN; x++) {
                    for (int y = 0; y < Constants.BOARD_LEN; y++) {
                        if (i == x && j == y) continue;

                        // Check if the piece rules allow moving from (i,j) to (x,y)
                        p.setStartIndex(new Point(i, j));
                        p.setEndIndex(new Point(x, y));

                        boolean legal = p.move(color, board) || p.eat(color, board);

                        if (!legal) {
                            continue; // Not a valid chess move, skip testing it
                        }

                        // Deep copy state attributes before running simulation
                        Piece captured = board[x][y];

                        // Perform Simulation
                        board[x][y] = p;
                        board[i][j] = null;
                        p.setStartIndex(new Point(x, y));
                        p.setEndIndex(new Point(x, y));

                        // Evaluate if this simulated move successfully broke the check line
                        boolean stillInCheck = isInCheck(color, board);

                        // Undo Simulation perfectly to original states
                        board[i][j] = p;
                        board[x][y] = captured;
                        p.setStartIndex(new Point(i, j));
                        p.setEndIndex(new Point(i, j));

                        if (!stillInCheck) {
                            Log.d("CHECKMATE_DEBUG", "Escape route found! Piece " + p.getClass().getSimpleName() +
                                    " can move from (" + i + "," + j + ") to (" + x + "," + y + ")");
                            return false;
                        }
                    }
                }
            }
        }

        // If we exhausted every single friendly piece's move combinations
        // and every single one leaves the king in check, it is Checkmate!
        return true;
    }
}