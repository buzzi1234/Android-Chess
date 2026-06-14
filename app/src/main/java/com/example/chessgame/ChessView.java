package com.example.chessgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChessView extends View {
    //fields
    private final Paint paint = new Paint();
    private final Paint textPaint = new Paint();
    private float squareSize = 0f;
    private float boardTopOffset = 0f;

    private Chess chessGame;
    private Point firstClick = null;
    private final Map<String, Bitmap> pieceBitmaps = new HashMap<>();

    private long whiteTimeLeft = 300000;
    private long blackTimeLeft = 300000;
    private CountDownTimer activeTimer;
    private MediaPlayer tickPlayer;

    public interface OnTimeOutListener {
        void onTimeOut(boolean isWhiteTurn);
    }

    private OnTimeOutListener timeOutListener;

    // Setter method so playPage can listen to this event
    public void setOnTimeOutListener(OnTimeOutListener listener) {
        this.timeOutListener = listener;
    }

    //constructor that start the timers reset the tick sound and load the images to the board
    public ChessView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.chessGame = new Chess();
        paint.setAntiAlias(true);
        textPaint.setAntiAlias(true);
        textPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));

        loadPieceBitmaps();
        try {
            tickPlayer = MediaPlayer.create(context, R.raw.tick);
        } catch (Exception e) {
            android.util.Log.e("ChessView", "Sound file missing");
        }
        startTurnTimer();
    }

    //This func load all the images if the pieces on to the board
    private void loadPieceBitmaps() {
        int[] resIds = {
                R.drawable.pawnw, R.drawable.rookw, R.drawable.knightw, R.drawable.bishopw, R.drawable.queenw, R.drawable.kingw,
                R.drawable.pawnb, R.drawable.rookb, R.drawable.knightb, R.drawable.bishopb, R.drawable.queenb, R.drawable.kingb
        };
        String[] keys = {"wp", "wr", "wn", "wb", "wq", "wk", "bp", "br", "bn", "bb", "bq", "bk"};
        for (int i = 0; i < resIds.length; i++) {
            pieceBitmaps.put(keys[i], BitmapFactory.decodeResource(getResources(), resIds[i]));
        }
    }

    //This func draw the board and clock on to the screen
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Reserve at least 160 pixels at top and bottom for clocks
        float verticalPadding = 160f;
        float availableHeightForBoard = getHeight() - (verticalPadding * 2);
        float availableWidth = getWidth();

        // The square size is the smaller of the two to ensure it's a perfect square that fits
        squareSize = Math.min(availableWidth / 8f, availableHeightForBoard / 8f);

        float totalBoardHeight = squareSize * 8;
        float totalBoardWidth = squareSize * 8;

        // Center the board vertically and horizontally
        boardTopOffset = (getHeight() - totalBoardHeight) / 2f;
        float boardLeftOffset = (getWidth() - totalBoardWidth) / 2f;

        // Draw Clocks (positioned relative to board, not screen edges)
        drawClockCard(canvas, whiteTimeLeft, boardTopOffset - 140, true);
        drawClockCard(canvas, blackTimeLeft, boardTopOffset + totalBoardHeight + 20, false);

        // Draw the Board
        canvas.save();
        canvas.translate(boardLeftOffset, boardTopOffset);
        drawBoardGrid(canvas);
        drawPiecesFromLogic(canvas);
        canvas.restore();

        drawStatusOverlay(canvas);
    }

    //This func draw on to the screen the clock card and when the timer gets to 15 sec
    //the clock card turn from yellow to red and if the turn isnt his the card color turn to white
    private void drawClockCard(Canvas canvas, long timeLeft, float yPos, boolean isWhiteClock) {
        String timeStr = String.format(Locale.getDefault(), "%02d:%02d",
                (timeLeft / 1000) / 60, (timeLeft / 1000) % 60);

        float cardWidth = getWidth() * 0.5f;
        float cardHeight = 110f;
        float xPos = (getWidth() - cardWidth) / 2;

        RectF card = new RectF(xPos, yPos, xPos + cardWidth, yPos + cardHeight);

        boolean isCurrentTurn = (isWhiteClock && chessGame.getMove() == Constants.WHITE_PIECE) ||
                (!isWhiteClock && chessGame.getMove() == Constants.BLACK_PIECE);


        if (timeLeft <= 15000) {
            // Intense warning red when 15 seconds or less remain
            paint.setColor(Color.parseColor("#EF5350"));
        } else if (isCurrentTurn) {
            // Bright yellow to denote active normal thinking turn state
            paint.setColor(Color.parseColor("#FFEB3B"));
        } else {
            // Plain white for the resting opponent card lane
            paint.setColor(Color.parseColor("#FFFFFF"));
        }

        // Render card background element
        paint.setShadowLayer(8, 0, 4, Color.parseColor("#22000000"));
        canvas.drawRoundRect(card, 20, 20, paint);
        paint.clearShadowLayer();

        textPaint.setTextSize(25f);
        textPaint.setColor(timeLeft <= 15000 ? Color.WHITE : Color.DKGRAY);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(isWhiteClock ? "WHITE" : "BLACK", xPos + (cardWidth/2), yPos + 35, textPaint);

        textPaint.setTextSize(55f);
        textPaint.setColor(timeLeft <= 15000 ? Color.WHITE : Color.BLACK);
        canvas.drawText(timeStr, xPos + (cardWidth/2), yPos + 95, textPaint);
    }

    //This function stop the timer from countdown
    public void stopTimer() {
        if (activeTimer != null) {
            activeTimer.cancel();
        }
    }
    /**
     * Starts or resumes the countdown for the player whose turn it currently is.
     * This is public so it can be called from the Activity when a dialog is closed.
     */
    public void startTurnTimer() {
        // cancel the previous timer before starting a new one
        if (activeTimer != null) {
            activeTimer.cancel();
        }

        // Determine whose turn it is from the chess engine
        final boolean isWhiteTurn = (chessGame.getMove() == Constants.WHITE_PIECE);
        long currentTime = isWhiteTurn ? whiteTimeLeft : blackTimeLeft;


        // Create the new countdown
        // millisInFuture: the time remaining for the active player
        // countDownInterval: 1000ms (1 second) per tick
        activeTimer = new CountDownTimer(currentTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the correct time variable
                if (isWhiteTurn) {
                    whiteTimeLeft = millisUntilFinished;
                    GlobalStat globalStat = (GlobalStat) getContext().getApplicationContext();
                    globalStat.userStats.timespent += 300000 - whiteTimeLeft;
                    globalStat.userStats.timespent /= 60000; //get mins
                } else {
                    blackTimeLeft = millisUntilFinished;

                }

                // Play a warning sound if 15 seconds or less remain
                if (millisUntilFinished <= 15000 && tickPlayer != null) {
                    if (!tickPlayer.isPlaying()) {
                        tickPlayer.start();
                    }
                }

                // Redraw the view to update the clock UI
                invalidate();
            }

            @Override
            public void onFinish() {
                // Logic for when time runs out
                final boolean whiteLost = isWhiteTurn;

                if (isWhiteTurn) whiteTimeLeft = 0;
                else blackTimeLeft = 0;

                // Stop the game interaction by changing the engine state to '0'
                chessGame.setMove('0');
                invalidate();

                android.util.Log.d("ChessTimer", "Time is up for " + (whiteLost ? "White" : "Black"));

                //open the game over dialog
                if (timeOutListener != null) {
                    timeOutListener.onTimeOut(whiteLost);
                }
            }
        }.start(); // Start the timer immediately
    }

    //this func draws the board grid on to the screen
    private void drawBoardGrid(Canvas canvas) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                paint.setColor((row + col) % 2 == 0 ? Color.parseColor("#EBECD0") : Color.parseColor("#779556"));
                canvas.drawRect(col * squareSize, row * squareSize, (col + 1) * squareSize, (row + 1) * squareSize, paint);

                if (firstClick != null && firstClick.getRow() == row && firstClick.getCol() == col) {
                    paint.setColor(Color.argb(160, 186, 202, 68));
                    canvas.drawRect(col * squareSize, row * squareSize, (col + 1) * squareSize, (row + 1) * squareSize, paint);
                }
            }
        }
    }

    //this func draw the pieces from the game board in the chess var on to the screen
    //so the screen can be update every turn
    private void drawPiecesFromLogic(Canvas canvas) {
        Piece[][] board = chessGame._gameRounds.getBoard();
        if (board == null) return;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece != null && piece.getColor() != Constants.EMPTY_SLOT) {
                    String colorPrefix = (piece.getColor() == Constants.WHITE_PIECE) ? "w" : "b";
                    String typeKey = String.valueOf(piece.getType()).toLowerCase();
                    String key = colorPrefix + typeKey;

                    Bitmap bitmap = pieceBitmaps.get(key);
                    if (bitmap != null) {
                        RectF dst = new RectF(col * squareSize, row * squareSize, (col + 1) * squareSize, (row + 1) * squareSize);
                        canvas.drawBitmap(bitmap, null, dst, paint);
                    }
                }
            }
        }
    }

    //This func draw the status of the game
    private void drawStatusOverlay(Canvas canvas) {
        if (chessGame.getMove() == '0') {
            textPaint.setColor(Color.RED);
            textPaint.setTextSize(squareSize);
            textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("CHECKMATE", getWidth() / 2f, getHeight() / 2f, textPaint);
        }
    }

    //this func handle the touch when a player moves one piece in a square into another
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) return true;
        try {
            // Horizontal offset calculation for centering
            float boardLeftOffset = (getWidth() - (squareSize * 8)) / 2f;

            int col = (int) ((event.getX() - boardLeftOffset) / squareSize);
            int row = (int) ((event.getY() - boardTopOffset) / squareSize);

            if (col < 0 || col >= 8 || row < 0 || row >= 8) return true;

            if (firstClick == null) {
                Piece[][] board = chessGame._gameRounds.getBoard();
                Piece clickedPiece = board[row][col];
                if (clickedPiece != null && clickedPiece.getColor() == chessGame.getMove()) {
                    firstClick = new Point(row, col);
                    invalidate();
                }
            } else {
                if (firstClick.getRow() == row && firstClick.getCol() == col) {
                    firstClick = null;
                } else {
                    chessGame._gameRounds.setMovePoints(firstClick, new Point(row, col));
                    chessGame.game(getContext());
                    firstClick = null;
                    if (chessGame.getMove() != '0') {
                        startTurnTimer();
                    } else {
                        stopTimer();
                    }
                }
                invalidate();
            }
        } catch (Exception e) { e.printStackTrace(); }
        return true;
    }

    //this func help to release the timer and the tick sound when the game stops/crushing
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (activeTimer != null) activeTimer.cancel();
        if (tickPlayer != null) { tickPlayer.release(); tickPlayer = null; }
    }

    //this func get a game logic and set it
    public void setGameLogic(Chess chessGame) {
        this.chessGame = chessGame;
        startTurnTimer();
        invalidate();
    }
}