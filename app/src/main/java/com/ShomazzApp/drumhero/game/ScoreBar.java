package com.ShomazzApp.drumhero.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.ShomazzApp.drumhero.utils.MySurfaceView;

import java.util.ArrayList;

public class ScoreBar implements IDrawable {

    private Game game;
    private static RectF scoreBarRect;
    private static RectF scoreIndicatorRect;
    private final static float scoreBarHeight = 255;
    private final static float scoreBarWidth = 535;
    private final static float scoreTextSize = 84;
    private final static float strikeScoreTextSize = 67;
    private final static float scoreIndicatorWidth = 320;
    private final static float scoreIndicatorHeight = 360;
    private final static int maxScoreToOver = 24;
    private int score = 0;
    private int currentScoreToOver = 20;
    private int strikeScore = 0;
    private int multiplier = 1;
    private int destroyedNotes;
    private float percent;
    private boolean beginnerMode;
    private static final float scoreBarX = 50;
    private static final float scoreBarY = 55;
    private static final float scoreIndicatorX = 1280;
    private static final float scoreIndicatorY = 320;
    private static final float scoreTextX = 327;
    private static final float scoreTextY = 137;
    private final static float strikeScoreY = 280;
    private final static float strikeScoreX = 77;
    private final static float multiplierX = 495;
    private static final int textColor = 0xFFFFFFFF;
    private static Paint scorePaint;

    public ScoreBar(Game game, boolean beginnerMode) {
        this.game = game;
        this.beginnerMode = beginnerMode;

        scorePaint = new Paint();
        scorePaint.setColor(textColor);

    }

    @Override
    public void draw(Canvas canvas) {
        scoreBarRect = new RectF(scoreBarX / MySurfaceView.sizeWidthCoff,
                scoreBarY / MySurfaceView.sizeHeightCoff,
                (scoreBarX + scoreBarWidth) / MySurfaceView.sizeWidthCoff,
                (scoreBarY + scoreBarHeight) / MySurfaceView.sizeHeightCoff);
        if (!game.noTappersMode) {
            scoreIndicatorRect = new RectF(scoreIndicatorX / MySurfaceView.sizeWidthCoff,
                    scoreIndicatorY / MySurfaceView.sizeHeightCoff,
                    (scoreIndicatorX + scoreIndicatorWidth) / MySurfaceView.sizeWidthCoff,
                    (scoreIndicatorY + scoreIndicatorHeight) / MySurfaceView.sizeHeightCoff);
        } else {
            scoreIndicatorRect = new RectF((scoreIndicatorX - 50) / MySurfaceView.sizeWidthCoff,
                    (scoreIndicatorY * 3 - 90) / MySurfaceView.sizeHeightCoff,
                    (scoreIndicatorX + scoreIndicatorWidth) / MySurfaceView.sizeWidthCoff,
                    (scoreIndicatorY * 3 + scoreIndicatorHeight - 30) / MySurfaceView.sizeHeightCoff);
        }
        MySurfaceView.drawImage(Game.scoreBarBitmap, scoreBarRect, canvas);
        if (game.noTappersMode) {
            canvas.save();
            canvas.rotate(340);
            switch (currentScoreToOver) {
                case 0:
                    MySurfaceView.drawImage(Game.scoreIndicator0, scoreIndicatorRect,
                            canvas);
                    break;
                case 1:
                case 2:
                    MySurfaceView.drawImage(Game.scoreIndicator1, scoreIndicatorRect,
                            canvas);
                    break;
                case 3:
                case 4:
                    MySurfaceView.drawImage(Game.scoreIndicator2, scoreIndicatorRect,
                            canvas);
                    break;
                case 5:
                case 6:
                    MySurfaceView.drawImage(Game.scoreIndicator3, scoreIndicatorRect,
                            canvas);
                    break;
                case 7:
                case 8:
                    MySurfaceView.drawImage(Game.scoreIndicator4, scoreIndicatorRect,
                            canvas);
                    break;
                case 9:
                case 10:
                    MySurfaceView.drawImage(Game.scoreIndicator5, scoreIndicatorRect,
                            canvas);
                    break;
                case 11:
                case 12:
                    MySurfaceView.drawImage(Game.scoreIndicator6, scoreIndicatorRect,
                            canvas);
                    break;
                case 13:
                case 14:
                    MySurfaceView.drawImage(Game.scoreIndicator7, scoreIndicatorRect,
                            canvas);
                    break;
                case 15:
                case 16:
                    MySurfaceView.drawImage(Game.scoreIndicator8, scoreIndicatorRect,
                            canvas);
                    break;
                case 17:
                case 18:
                    MySurfaceView.drawImage(Game.scoreIndicator9, scoreIndicatorRect,
                            canvas);
                    break;
                case 19:
                case 20:
                    MySurfaceView.drawImage(Game.scoreIndicator10, scoreIndicatorRect,
                            canvas);
                    break;
                case 21:
                case 22:
                    MySurfaceView.drawImage(Game.scoreIndicator11, scoreIndicatorRect,
                            canvas);
                    break;
                case 23:
                case 24:
                    MySurfaceView.drawImage(Game.scoreIndicator12, scoreIndicatorRect,
                            canvas);
                    break;
            }
            canvas.restore();
        } else {
            switch (currentScoreToOver) {
                case 0:
                    MySurfaceView.drawImage(Game.scoreIndicator0, scoreIndicatorRect,
                            canvas);
                    break;
                case 1:
                case 2:
                    MySurfaceView.drawImage(Game.scoreIndicator1, scoreIndicatorRect,
                            canvas);
                    break;
                case 3:
                case 4:
                    MySurfaceView.drawImage(Game.scoreIndicator2, scoreIndicatorRect,
                            canvas);
                    break;
                case 5:
                case 6:
                    MySurfaceView.drawImage(Game.scoreIndicator3, scoreIndicatorRect,
                            canvas);
                    break;
                case 7:
                case 8:
                    MySurfaceView.drawImage(Game.scoreIndicator4, scoreIndicatorRect,
                            canvas);
                    break;
                case 9:
                case 10:
                    MySurfaceView.drawImage(Game.scoreIndicator5, scoreIndicatorRect,
                            canvas);
                    break;
                case 11:
                case 12:
                    MySurfaceView.drawImage(Game.scoreIndicator6, scoreIndicatorRect,
                            canvas);
                    break;
                case 13:
                case 14:
                    MySurfaceView.drawImage(Game.scoreIndicator7, scoreIndicatorRect,
                            canvas);
                    break;
                case 15:
                case 16:
                    MySurfaceView.drawImage(Game.scoreIndicator8, scoreIndicatorRect,
                            canvas);
                    break;
                case 17:
                case 18:
                    MySurfaceView.drawImage(Game.scoreIndicator9, scoreIndicatorRect,
                            canvas);
                    break;
                case 19:
                case 20:
                    MySurfaceView.drawImage(Game.scoreIndicator10, scoreIndicatorRect,
                            canvas);
                    break;
                case 21:
                case 22:
                    MySurfaceView.drawImage(Game.scoreIndicator11, scoreIndicatorRect,
                            canvas);
                    break;
                case 23:
                case 24:
                    MySurfaceView.drawImage(Game.scoreIndicator12, scoreIndicatorRect,
                            canvas);
                    break;
            }
        }
        scorePaint.setTextSize(scoreTextSize / MySurfaceView.sizeHeightCoff);
        canvas.drawText(score + "",
                scoreTextX / MySurfaceView.sizeWidthCoff,
                (scoreTextY) / MySurfaceView.sizeHeightCoff,
                scorePaint);
        scorePaint.setTextSize(strikeScoreTextSize / MySurfaceView.sizeHeightCoff);
        canvas.drawText(strikeScore + "",
                strikeScoreX / MySurfaceView.sizeWidthCoff,
                strikeScoreY / MySurfaceView.sizeHeightCoff,
                scorePaint);
        canvas.drawText(multiplier + "x",
                multiplierX / MySurfaceView.sizeWidthCoff,
                strikeScoreY / MySurfaceView.sizeHeightCoff,
                scorePaint);
    }

    public void increaseScore(int score) {
        this.score += score * multiplier;
        //Log.v("SCORE", "increaseScore (" + score + ") multiplier = "
        //		+ multiplier + "x  score = " + this.score);
    }

    public void increaseMultiplier() {
        if (multiplier < 4) {
            this.multiplier ++;
            //	Log.v("SCORE", "increaseMultiplier (" + factor + ") multiplier = "
            //		+ multiplier + "x");
        }
    }

    public void increaseStrikeScore() {
        increaseScoreToOver(true);
        strikeScore++;
        //Log.v("SCORE", "increaseStrikeScore() strikeScore = " + strikeScore);
    }

    public void resetStrikeScore() {
        increaseScoreToOver(false);
        strikeScore = 0;
        //Log.v("SCORE", "resetStrikeScore()");
    }

    public void resetMultiplier() {
        multiplier = 1;
        //Log.v("SCORE", "resetMultiplier()");
    }

    public void increaseScoreToOver(boolean increase) {
        if (increase && currentScoreToOver < maxScoreToOver) {
            currentScoreToOver++;
        } else if (!increase && currentScoreToOver > 0) {
            currentScoreToOver--;
        }
        if (currentScoreToOver == 0 && !beginnerMode) {
            game.onGameEnded(false);
        }
    }

    public void resetScoreToOver() {
        currentScoreToOver = 12;
    }

    public void resetScore() {
        score = 0;
    }

    public void onDestroy() {
        resetScore();
        resetMultiplier();
        resetScoreToOver();
        resetStrikeScore();
    }

    public int getScore() {
        return score;
    }

    public int getStrikeScore() {
        return strikeScore;
    }

    public int getPercent(ArrayList arr) {
        percent = (float) destroyedNotes / (arr.size() - 4) * 100;
        return (int) percent;
    }

    public void incDestroyNotes() {
        destroyedNotes++;
    }

}
