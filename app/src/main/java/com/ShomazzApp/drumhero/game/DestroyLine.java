package com.ShomazzApp.drumhero.game;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.ShomazzApp.drumhero.game.Note;
import com.ShomazzApp.drumhero.game.Game;
import com.ShomazzApp.drumhero.game.IDrawable;
import com.ShomazzApp.drumhero.utils.MySurfaceView;

public class DestroyLine implements IDrawable {

    private Game game;
    private RectF desLineRect;
    private int y;
    private static final int yNoTappers = 800;
    private static final int yDefault = 680;
    private int currentX;
    private static final int[] allX = {560, 758, 950, 1150};
    private static final int[] allXNoTappers = {310, 655, 975, 1310};
    private int width;
    private int height;
    private boolean holded = false;
    private int numberOfDestroyLine;
    public static final int sizeDifferenceFromNote = 30;

    public DestroyLine(int numberOfDestroyLine, Game game) {

        this.numberOfDestroyLine = numberOfDestroyLine;
        this.game = game;

        height = getDestroyLineHeight(game.noTappersMode);
        width = getDestroyLineWidth(game.noTappersMode);
        if (!game.noTappersMode) {
            currentX = allX[numberOfDestroyLine - 1];
            y = yDefault;
        } else {
            currentX = allXNoTappers[numberOfDestroyLine - 1];
            y = yNoTappers;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        desLineRect = new RectF(currentX / MySurfaceView.sizeWidthCoff, y / MySurfaceView.sizeHeightCoff, (currentX + width)
                / MySurfaceView.sizeWidthCoff, (y + height) / MySurfaceView.sizeHeightCoff);
        switch (numberOfDestroyLine) {
            case 1:
                if (holded) {
                    MySurfaceView.drawImage(Game.touchedDesLineRed, desLineRect, canvas);
                } else {
                    MySurfaceView.drawImage(Game.desLineRed, desLineRect, canvas);
                }
                break;
            case 2:
                if (holded) {
                    MySurfaceView.drawImage(Game.touchedDesLineGreen, desLineRect, canvas);
                } else {
                    MySurfaceView.drawImage(Game.desLineGreen, desLineRect, canvas);
                }
                break;
            case 3:
                if (holded) {
                    MySurfaceView.drawImage(Game.touchedDesLineYellow, desLineRect, canvas);
                } else {
                    MySurfaceView.drawImage(Game.desLineYellow, desLineRect, canvas);
                }
                break;
            case 4:
                if (holded) {
                    MySurfaceView.drawImage(Game.touchedDesLineBlue, desLineRect, canvas);
                } else {
                    MySurfaceView.drawImage(Game.desLineBlue, desLineRect, canvas);
                }
                break;
        }
    }

    public void onTouchDown() {
        holded = true;
        // if when you touch desTapper there is no note's
        // destroying, then boolean destroyed == false
        boolean destroyed = false;
        for (int i = 0; i < game.getNotesClone().size(); i++) {
            if (game.getNotesClone().get(i).getNoteY() > y
                    && game.getNotesClone().get(i).getNoteY() < y + height + Note.getNoteHeight(game.noTappersMode) / 2
                    && game.getNotesClone().get(i).getNumberOfLine() == numberOfDestroyLine) {
                game.destroyNote(game.getNotesClone().get(i), true);
                destroyed = true;
                break;
            }
        }
        if (!destroyed) {
            if (game.getDrumsMediaPlayer() != null) {
                game.getDrumsMediaPlayer().setVolume(0f, 0f);
            }
            game.getScoreBar().resetMultiplier();
            game.getScoreBar().resetStrikeScore();
        }
    }

    public int getY() {
        return y;
    }

    public boolean isHolded() {
        return holded;
    }

    public boolean in(float x, float y) {
        if (x > (this.currentX - sizeDifferenceFromNote) / MySurfaceView.sizeWidthCoff
                && x < (this.currentX + this.width + sizeDifferenceFromNote) / MySurfaceView.sizeWidthCoff
                && y > (this.y - sizeDifferenceFromNote) / MySurfaceView.sizeHeightCoff
                && y < (this.y + this.height + sizeDifferenceFromNote * 2) / MySurfaceView.sizeHeightCoff)
            return true;
        return false;
    }

    public int getHeight() {
        return height;
    }

    public void onTouchUp() {
        holded = false;
    }

    public static int getDestroyLineY(boolean noTappersMode) {
            return noTappersMode ? yNoTappers : yDefault ;
    }

    public static int getDestroyLineWidth(boolean noTappersMode) {
        return Note.getNoteWidth(noTappersMode) + sizeDifferenceFromNote;
    }

    public static int getDestroyLineHeight(boolean noTappersMode) {
        return Note.getNoteHeight(noTappersMode) + sizeDifferenceFromNote;
    }

    public static int getDestroyLineX(int number, boolean noTappersMode) {
        if (!noTappersMode) {
            return allX[number - 1];
        } else {
            return allXNoTappers[number - 1];
        }
    }
}