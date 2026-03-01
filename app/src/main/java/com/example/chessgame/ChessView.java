package com.example.chessgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ChessView extends View {
    private final Paint paint = new Paint();
    private float squareSize = 0f;
    private Chess chessGame;

    // Tracking clicks: null means we are waiting for the first piece selection
    private Point firstClick = null;

    public ChessView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.chessGame = new Chess();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        squareSize = getWidth() / 8f;

        drawBoardGrid(canvas);
        drawPiecesFromLogic(canvas);
        drawStatusOverlay(canvas);
    }

    private void drawBoardGrid(Canvas canvas) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                // Alternating colors
                paint.setColor((row + col) % 2 == 0 ? Color.parseColor("#EBECD0") : Color.parseColor("#779556"));
                canvas.drawRect(col * squareSize, row * squareSize, (col + 1) * squareSize, (row + 1) * squareSize, paint);

                // Highlight the selected square if it exists
                if (firstClick != null && firstClick.getRow() == row && firstClick.getCol() == col) {
                    paint.setColor(Color.argb(100, 255, 255, 0)); // Semi-transparent yellow
                    canvas.drawRect(col * squareSize, row * squareSize, (col + 1) * squareSize, (row + 1) * squareSize, paint);
                }
            }
        }
    }

    private void drawPiecesFromLogic(Canvas canvas) {
        Piece[][] board = chessGame._gameRounds.getBoard();
        if (board == null) return;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];

                // Only draw if there is a real piece (not an empty slot)
                if (piece != null && piece.getColor() != Constants.EMPTY_SLOT) {
                    paint.setColor(piece.getColor() == Constants.WHITE_PIECE ? Color.WHITE : Color.BLACK);
                    paint.setTextSize(squareSize * 0.7f);
                    paint.setFakeBoldText(true);

                    // Display symbol (Lowercase white, Uppercase black as per your logic)
                    char type = piece.getType();
                    String symbol = (piece.getColor() == Constants.WHITE_PIECE)
                            ? String.valueOf(type).toLowerCase()
                            : String.valueOf(type).toUpperCase();

                    float x = (col * squareSize) + (squareSize / 2);
                    float y = (row * squareSize) + (squareSize / 2) - ((paint.descent() + paint.ascent()) / 2);
                    canvas.drawText(symbol, x, y, paint);
                }
            }
        }
    }

    private void drawStatusOverlay(Canvas canvas) {
        if (chessGame.getMove() == '0') {
            paint.setColor(Color.RED);
            paint.setTextSize(squareSize);
            canvas.drawText("CHECKMATE", getWidth() / 2f, getHeight() / 2f, paint);
        } else if (chessGame.getMove() == '4') {
            paint.setColor(Color.YELLOW);
            paint.setTextSize(40f);
            canvas.drawText("Invalid Move", getWidth() / 2f, 50f, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Only trigger on the initial press
        if (event.getAction() != MotionEvent.ACTION_DOWN) return true;

        try {
            // Use floor to ensure we stay within 0-7
            int col = (int) Math.floor(event.getX() / squareSize);
            int row = (int) Math.floor(event.getY() / squareSize);

            // Strict boundary check
            if (col < 0 || col >= 8 || row < 0 || row >= 8) return true;

            if (firstClick == null) {
                Piece[][] board = chessGame._gameRounds.getBoard();
                Piece clickedPiece = board[row][col];

                // Only select if there is a piece AND it belongs to the current player
                if (clickedPiece != null && clickedPiece.getColor() == chessGame.getMove()) {
                    firstClick = new Point(row, col);
                    invalidate();
                }
            } else {
                Point secondClick = new Point(row, col);

                // Logic: If user clicks the same piece again, deselect it
                if (firstClick.getRow() == row && firstClick.getCol() == col) {
                    firstClick = null;
                } else {
                    // Execute move
                    chessGame._gameRounds.setMovePoints(firstClick, secondClick);
                    chessGame.game();
                    firstClick = null;
                }
                invalidate();
            }
        } catch (Exception e) {
            // This stops the app from exiting to lobby; check Logcat for the error!
            android.util.Log.e("ChessView", "Touch Error: " + e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    private String getCurrentBoardStateAsString() {
        // This generates the string your turnToBoard() expects
        StringBuilder sb = new StringBuilder();
        Piece[][] board = chessGame._gameRounds.getBoard();
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece p = board[r][c];
                if (p == null || p.getColor() == Constants.EMPTY_SLOT) {
                    sb.append(Constants.EMPTY_SLOT);
                } else {
                    sb.append(p.getType());
                }
            }
        }
        // Append current turn ('0' for white, '1' for black, etc., based on your logic)
        sb.append(chessGame._gameRounds.getColor() == Constants.WHITE_PIECE ? '0' : '1');
        return sb.toString();
    }

    public void setGameLogic(Chess chessGame) {
        this.chessGame = chessGame;
        invalidate();
    }
}