package com.ShomazzApp.drumhero.game;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.ShomazzApp.drumhero.ConstructorActivity;
import com.ShomazzApp.drumhero.GameEndedActivity;
import com.ShomazzApp.drumhero.R;
import com.ShomazzApp.drumhero.SongsActivity;
import com.ShomazzApp.drumhero.utils.DBManager;
import com.ShomazzApp.drumhero.utils.MySurfaceView;

import java.util.ArrayList;

public class Game implements OnTouchListener {

    public static final int EASY = 1;
    public static final int MEDIUM = 2;
    public static final int HARD = 3;
    public static final int ONLYMUSIC = 1;
    public static final int GAME = 2;
    public static final int ONLYDRUMS = 3;
    private static final float heightOfBtn = 155;
    private static final float widthOfBtn = 160;
    private static final float XofBtn = 1715;
    private static final float YofBtn = 28;
    public static String LOG = "Surface";
    public static Bitmap background;
    public static Bitmap btn;
    public static Bitmap btnPause;
    public static Bitmap btnPauseHolded;
    public static Bitmap scoreBarBitmap;
    public static Bitmap desTapperRed;
    public static Bitmap desTapperGreen;
    public static Bitmap desTapperYellow;
    public static Bitmap desTapperBlue;
    public static Bitmap crash1Red;
    public static Bitmap crash2Red;
    public static Bitmap noteRed;
    public static Bitmap crash1Green;
    public static Bitmap crash2Green;
    public static Bitmap noteGreen;
    public static Bitmap crash1Yellow;
    public static Bitmap crash2Yellow;
    public static Bitmap noteYellow;
    public static Bitmap crash1Blue;
    public static Bitmap crash2Blue;
    public static Bitmap noteBlue;
    public static Bitmap desLineRed;
    public static Bitmap touchedDesLineRed;
    public static Bitmap desLineGreen;
    public static Bitmap touchedDesLineGreen;
    public static Bitmap desLineYellow;
    public static Bitmap touchedDesLineYellow;
    public static Bitmap desLineBlue;
    public static Bitmap touchedDesLineBlue;
    public static Bitmap scoreIndicator0;
    public static Bitmap scoreIndicator1;
    public static Bitmap scoreIndicator2;
    public static Bitmap scoreIndicator3;
    public static Bitmap scoreIndicator4;
    public static Bitmap scoreIndicator5;
    public static Bitmap scoreIndicator6;
    public static Bitmap scoreIndicator7;
    public static Bitmap scoreIndicator8;
    public static Bitmap scoreIndicator9;
    public static Bitmap scoreIndicator10;
    public static Bitmap scoreIndicator11;
    public static Bitmap scoreIndicator12;
    public static Bitmap[] explosion = new Bitmap[4];
    private static RectF stopButtonRect;
    private static String titleByName;
    private static String tableName;
    private static String filePath;
    private static int difficulty;
    private static int mode;
    private static ArrayList<Object> trashCan = new ArrayList<>();
    public boolean noTappersMode;
    public boolean gameEnded = false;
    public boolean gameStarted = false;
    public boolean playing = false;
    public boolean isMidi;
    private Activity activity;
    private MediaPlayer mpDrumsOnly;
    private MediaPlayer mpMusicOnly;
    private MediaPlayer mpAllMusic;
    private DBManager db;
    private ScoreBar scoreBar;
    private CleanThread delNotesThread;
    private Intent intentGameEnded;
    private ArrayList<Note> notes = new ArrayList<>();
    private ArrayList<Note> notesClone = new ArrayList<>();
    private ArrayList<Explosion> explosions = new ArrayList<>();
    private ArrayList<DestroyLine> destroyLines = new ArrayList<>();
    private ArrayList<DestroyTapper> destroyTappers = new ArrayList<>();
    private long startSongSecond;
    private long currentSecond;
    private long pauseSecond;
    private long lastTime = -1;
    private long deltaTime;
    private int duration;
    private int i = 0;

    public Game(Activity activity, String filePath,
                String tableName, String titleByName, int difficulty, int mode, boolean noTappersMode) {
        if (filePath != null && tableName != null && titleByName != null) {
            this.titleByName = titleByName;
            this.tableName = tableName;
            this.filePath = filePath;
        }
        this.difficulty = difficulty;
        this.mode = mode;
        System.out.println("From Game mode == " + mode);
        this.activity = activity;
        this.noTappersMode = noTappersMode;
        notes = new ArrayList<>();
        notesClone = new ArrayList<>();
        intentGameEnded = new Intent(activity, GameEndedActivity.class);
        isMidi = createMediaPlayers(this.filePath);
        delNotesThread = new CleanThread();
        scoreBar = new ScoreBar(this, SongsActivity.beginnerMode);
        for (int i = 0; i < 4; i++) {
            destroyLines.add(new DestroyLine(i + 1, this));
            if (!noTappersMode)
                destroyTappers.add(new DestroyTapper(i + 1));
        }
        db = new DBManager(activity.getApplicationContext(), this);
        notes = db.getNotesWithDifficulty(tableName, false, this.difficulty);
        System.out.println("From game notes.size() == " + notes.size());
        loadBitmaps();
        btn = btnPause;
    }

    public static void removeObject(Object obj) {
        trashCan.add(obj);
    }

    private void loadBitmaps() {
        background = noTappersMode ?
                MySurfaceView.getBitmap("drawable/game_background_no_tappers") :
                MySurfaceView.getBitmap("drawable/game_background");
        scoreIndicator0 = MySurfaceView.getBitmap("drawable/score_indicator_0");
        scoreIndicator1 = MySurfaceView.getBitmap("drawable/score_indicator_1");
        scoreIndicator2 = MySurfaceView.getBitmap("drawable/score_indicator_2");
        scoreIndicator3 = MySurfaceView.getBitmap("drawable/score_indicator_3");
        scoreIndicator4 = MySurfaceView.getBitmap("drawable/score_indicator_4");
        scoreIndicator5 = MySurfaceView.getBitmap("drawable/score_indicator_5");
        scoreIndicator6 = MySurfaceView.getBitmap("drawable/score_indicator_6");
        scoreIndicator7 = MySurfaceView.getBitmap("drawable/score_indicator_7");
        scoreIndicator8 = MySurfaceView.getBitmap("drawable/score_indicator_8");
        scoreIndicator9 = MySurfaceView.getBitmap("drawable/score_indicator_9");
        scoreIndicator10 = MySurfaceView
                .getBitmap("drawable/score_indicator_10");
        scoreIndicator11 = MySurfaceView
                .getBitmap("drawable/score_indicator_11");
        scoreIndicator12 = MySurfaceView
                .getBitmap("drawable/score_indicator_12");
        desTapperRed = MySurfaceView.getBitmap("drawable/red_destapper");
        desTapperGreen = MySurfaceView.getBitmap("drawable/green_destapper");
        desTapperYellow = MySurfaceView.getBitmap("drawable/yellow_destapper");
        desTapperBlue = MySurfaceView.getBitmap("drawable/blue_destapper");
        noteBlue = MySurfaceView.getBitmap("drawable/blue_note");
        noteRed = MySurfaceView.getBitmap("drawable/red_note");
        noteGreen = MySurfaceView.getBitmap("drawable/green_note");
        noteYellow = MySurfaceView.getBitmap("drawable/yellow_note");
        crash1Blue = MySurfaceView.getBitmap("drawable/blue_crash1");
        crash1Red = MySurfaceView.getBitmap("drawable/red_crash1");
        crash1Green = MySurfaceView.getBitmap("drawable/green_crash1");
        crash1Yellow = MySurfaceView.getBitmap("drawable/yellow_crash1");
        crash2Red = MySurfaceView.getBitmap("drawable/red_crash2");
        crash2Green = MySurfaceView.getBitmap("drawable/green_crash2");
        crash2Yellow = MySurfaceView.getBitmap("drawable/yellow_crash2");
        crash2Blue = MySurfaceView.getBitmap("drawable/blue_crash2");
        desLineRed = MySurfaceView.getBitmap("drawable/red_desline");
        touchedDesLineRed = MySurfaceView.getBitmap("drawable/red_desline2");
        desLineGreen = MySurfaceView.getBitmap("drawable/green_desline");
        touchedDesLineGreen = MySurfaceView
                .getBitmap("drawable/green_desline2");
        desLineYellow = MySurfaceView.getBitmap("drawable/yellow_desline");
        touchedDesLineYellow = MySurfaceView
                .getBitmap("drawable/yellow_desline2");
        desLineBlue = MySurfaceView.getBitmap("drawable/blue_desline");
        touchedDesLineBlue = MySurfaceView.getBitmap("drawable/blue_desline2");
        scoreBarBitmap = MySurfaceView.getBitmap("drawable/score_bar");
        btnPause = MySurfaceView.getBitmap("drawable/stop_button");
        btnPauseHolded = MySurfaceView.getBitmap("drawable/stop_button_holded");
        explosion[0] = MySurfaceView.getBitmap("drawable/starred");
        explosion[1] = MySurfaceView.getBitmap("drawable/stargreen");
        explosion[2] = MySurfaceView.getBitmap("drawable/staryellow");
        explosion[3] = MySurfaceView.getBitmap("drawable/starblue");
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int actionMask = event.getActionMasked();
        int index = event.getActionIndex();
        if (playing) {
            switch (actionMask) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    if (!noTappersMode) {
                        for (int i = 0; i < destroyTappers.size(); i++) {
                            if (destroyTappers.get(i).in(event.getX(index),
                                    event.getY(index))) {
                                destroyLines.get(i).onTouchDown();
                                destroyTappers.get(i).onTouchDown();
                            }
                        }
                    } else {
                        for (int i = 0; i < destroyLines.size(); i++) {
                            if (destroyLines.get(i).in(event.getX(index),
                                    event.getY(index))) {
                                destroyLines.get(i).onTouchDown();
                            }
                        }
                    }
                    if (event.getX(index) > stopButtonRect.left && event.getY(index) > stopButtonRect.top
                            && event.getX(index) < stopButtonRect.right
                            && event.getY(index) < stopButtonRect.bottom) {
                        btn = btnPauseHolded;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!noTappersMode) {
                        for (int i = 0; i < destroyTappers.size(); i++) {
                            if (destroyTappers.get(i).isHolded()
                                    && (event.getY() <= destroyTappers.get(i).getMainY() - destroyTappers.get(i).getHeight()
                                    || event.getY() >= destroyTappers.get(i).getMainY() + destroyTappers.get(i).getHeight())) {
                                destroyLines.get(i).onTouchUp();
                                destroyTappers.get(i).onTouchUp();
                            }
                        }
                    } else {
                        for (int i = 0; i < destroyLines.size(); i++) {
                            if (destroyLines.get(i).isHolded()
                                    && (event.getY() <= destroyLines.get(i).getY()
                                    || event.getY() >= destroyLines.get(i).getY() + destroyLines.get(i).getHeight())) {
                                destroyLines.get(i).onTouchUp();
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    if (!noTappersMode) {
                        for (int i = 0; i < destroyTappers.size(); i++) {
                            if (destroyTappers.get(i).in(event.getX(index),
                                    event.getY(index))) {
                                destroyLines.get(i).onTouchUp();
                                destroyTappers.get(i).onTouchUp();
                            }
                        }
                    } else {
                        for (int i = 0; i < destroyLines.size(); i++) {
                            if (destroyLines.get(i).in(event.getX(index),
                                    event.getY(index))) {
                                destroyLines.get(i).onTouchUp();
                            }
                        }
                    }
                    if (event.getX(index) > stopButtonRect.left && event.getY(index) > stopButtonRect.top
                            && event.getX(index) < stopButtonRect.right
                            && event.getY(index) < stopButtonRect.bottom) {
                        btn = btnPause;
                        activity.onBackPressed();
                    }
                    break;
            }
        }
        return true;
    }

    public void draw(Canvas canvas) {
        drawAllArray(canvas, destroyLines);
        drawAllNotes(canvas, notesClone);
        if (!noTappersMode) {
            drawAllArray(canvas, destroyTappers);
        }
        drawAllArray(canvas, explosions);
        if (mode != ONLYMUSIC) {
            scoreBar.draw(canvas);
        }
        stopButtonRect = new RectF(XofBtn / MySurfaceView.sizeWidthCoff, YofBtn / MySurfaceView.sizeHeightCoff,
                (XofBtn + widthOfBtn) / MySurfaceView.sizeWidthCoff, (YofBtn + heightOfBtn)
                / MySurfaceView.sizeHeightCoff);
        MySurfaceView.drawImage(btn, stopButtonRect, canvas);
    }

    public void  update() {
        if (playing) {
            if (notes.size() > i
                    && notes.get(i).getNoteY() <= DestroyLine.getDestroyLineY(noTappersMode)) {
                currentSecond = System.currentTimeMillis() - startSongSecond;
                if (!gameStarted &&
                        //notes.get(0).getNoteY() - Note.getNoteHeight(noTappersMode) / 2 >= DestroyLine.getDestroyLineY(noTappersMode)
                        notes.get(0).getNoteY() - Note.getNoteHeight(noTappersMode) / 2 >= DestroyLine.getDestroyLineY(noTappersMode)
                        ) {
                    if (isMidi) {
                        switch (mode) {
                            case ONLYDRUMS:
                                mpDrumsOnly.start();
                                break;
                            case ONLYMUSIC:
                                mpMusicOnly.start();
                                break;
                            case GAME:
                                mpMusicOnly.start();
                                mpDrumsOnly.start();
                                break;
                        }
                    } else {
                        mpAllMusic.start();
                    }
                    gameStarted = true;
                }
                while (i < notes.size()
                        && notes.get(i).startSecond <= currentSecond) {
                    notesClone.add(notes.get(i));
                    i++;
                }
            }
            if (lastTime == -1)
                lastTime = System.currentTimeMillis();
            deltaTime = System.currentTimeMillis() - lastTime;
            if (deltaTime > 0) {
                lastTime = System.currentTimeMillis();
                updateAllNotes(notesClone, deltaTime);
                updateAllArray(explosions);
            }
            if (gameStarted)
                delNotesThread.run();
            // GAME ENDS
            if (gameStarted
                    && System.currentTimeMillis() - startSongSecond >= duration + 1500) {
                playing = false;
                setGameEnded();
                onGameEnded(true);
            }
        }
    }

    public boolean createMediaPlayers(String filePath) {
        if (true) { // modes
            isMidi = true;
            mpMusicOnly = MediaPlayer.create(activity, Uri.parse(Environment.getExternalStorageDirectory()
                    .getPath() + ConstructorActivity.MUSICFOLDER + "/"
                    + Song.makeFileNameOfPath(filePath, ConstructorActivity.musicOnlyString)));
            mpDrumsOnly = MediaPlayer.create(activity, Uri.parse(Environment.getExternalStorageDirectory()
                    .getPath() + ConstructorActivity.MUSICFOLDER + "/"
                    + Song.makeFileNameOfPath(filePath, ConstructorActivity.drumsOnlyString)));
            switch (mode) {
                case GAME:
                    try {
                        mpMusicOnly.prepare();
                        mpDrumsOnly.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mpDrumsOnly.getDuration() > mpMusicOnly.getDuration()) {
                        duration = mpDrumsOnly.getDuration();
                        System.out.println("From Game mpDrumsOnly.getDuration() == " + mpDrumsOnly.getDuration());
                    } else {
                        duration = mpMusicOnly.getDuration();
                        System.out.println("From Game mpMusicOnly.getDuration() == " + mpMusicOnly.getDuration());
                    }
                    break;
                case ONLYDRUMS:
                    duration = mpDrumsOnly.getDuration();
                    try {
                        mpDrumsOnly.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ONLYMUSIC:
                    duration = mpMusicOnly.getDuration();
                    try {
                        mpMusicOnly.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
            mpDrumsOnly.setVolume(1.1f, 1.1f);
            mpMusicOnly.setVolume(0.9f, 0.9f);
        } else {
            mpAllMusic = MediaPlayer.create(activity, Uri.parse(filePath));
            System.out.println("From Game MediaPlayer allMusic path == " + filePath);
            duration = mpAllMusic.getDuration();
        }
        return isMidi;
    }

    public void drawAllArray(Canvas canvas, ArrayList arr) {
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i) != null && arr.get(i) instanceof IDrawable) {
                ((IDrawable) arr.get(i)).draw(canvas);
            }
        }
    }

    public void updateAllArray(ArrayList arr) {
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i) != null && arr.get(i) instanceof IUpdatable) {
                ((IUpdatable) arr.get(i)).update(deltaTime);
            }
        }
    }

    public void drawAllNotes(Canvas canvas, ArrayList<Note> arr) {
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i) != null) {
                ((IDrawable) arr.get(i)).draw(canvas);
            }
        }
    }

    public void updateAllNotes(ArrayList<Note> arr, long deltaTime) {
        for (int i = 0; i < arr.size(); i++)
            if (arr.get(i) != null && arr.get(i) instanceof IUpdatable)
                ((IUpdatable) arr.get(i)).update(deltaTime);
    }

    public void start() {
        Log.d(LOG, "Game start() called");
        startSongSecond = System.currentTimeMillis();
        playing = true;
    }

    void destroyNote(Note note, boolean touched) {
        if (gameStarted) {
            if (!touched) {
                notesClone.remove(note);
                scoreBar.resetStrikeScore();
                scoreBar.resetMultiplier();
                if (isMidi && mode != ONLYMUSIC) {
                    mpDrumsOnly.setVolume(0f, 0f);
                }
            } else {
                note.onDestroy();
                removeObject(note);
                if (isMidi && mode != ONLYMUSIC) {
                    mpDrumsOnly.setVolume(1f, 1f);
                }
                if (mode != ONLYMUSIC) {
                    scoreBar.incDestroyNotes();
                    scoreBar.increaseScore(10);
                    scoreBar.increaseStrikeScore();
                    switch (scoreBar.getStrikeScore()) {
                        case 14:
                        case 28:
                        case 42:
                            scoreBar.increaseMultiplier(1);
                            break;
                    }
                }
            }
        }
    }

    public void onDestroy() {
        Log.d(LOG, "Game onDestroy() called");
        playing = false;
        if (isMidi) {
            if (mpDrumsOnly != null) {
                mpDrumsOnly.stop();
                mpDrumsOnly.release();
            }
            if (mpMusicOnly != null) {
                mpMusicOnly.stop();
                mpMusicOnly.release();
            }
        } else {
            mpAllMusic.stop();
            mpAllMusic.release();
        }
        scoreBar.onDestroy();
        notes.clear();
        notesClone.clear();
    }

    public void onPause() {
        Log.d(LOG, "Game onPause() called");
        pauseSecond = System.currentTimeMillis();
        playing = false;
        if (isMidi) {
            switch (mode) {
                case GAME:
                    mpDrumsOnly.pause();
                    mpMusicOnly.pause();
                    break;
                case ONLYDRUMS:
                    mpDrumsOnly.pause();
                    break;
                case ONLYMUSIC:
                    mpMusicOnly.pause();
                    break;
            }
        } else {
            mpAllMusic.pause();
        }
    }

    public void onResume() {
        Log.d(LOG, "Game onResume() called");
        notesClone.clear();
        playing = true;
        startSongSecond += System.currentTimeMillis() - pauseSecond;
        if (!isMidi) {
            //mpAllMusic.seekTo((int) (currentSecond));
            mpAllMusic.start();
        } else {
            switch (mode) {
                case GAME:
                    mpDrumsOnly.start();
                    mpMusicOnly.start();
                    break;
                case ONLYDRUMS:
                    mpDrumsOnly.start();
                    break;
                case ONLYMUSIC:
                    mpMusicOnly.start();
                    break;
            }
        }
    }

    public void setGameEnded() {
        Log.d(LOG, "Game setGameEnded() called");
        onPause();
        gameEnded = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.putScore(titleByName, scoreBar.getScore());
            }
        }).run();
    }

    public void onGameEnded(boolean win) {
        Log.d(LOG, "Game onGameEnded() called");
        intentGameEnded.putExtra(activity.getString(R.string.GameEndedIntentWin), win);
        intentGameEnded.putExtra(activity.getString(R.string.GameEndedIntentScore), scoreBar.getScore());
        intentGameEnded.putExtra(activity.getString(R.string.GameIntentTitleByName), titleByName);
        intentGameEnded.putExtra(activity.getString(R.string.GameEndedIntentBestScore), db.getBestScore(titleByName));
        activity.startActivity(intentGameEnded);
    }

    public void addExplosion(Note note) {
        explosions.add(new Explosion(note, this));
    }

    public ArrayList<Note> getNotesClone() {
        return notesClone;
    }

    public MediaPlayer getDrumsMediaPlayer() {
        return mpDrumsOnly;
    }

    public ScoreBar getScoreBar() {
        return scoreBar;
    }

    //This thread will clean trash
    class CleanThread extends Thread {

        @Override
        public void run() {
            try {/*
                System.out.println("From Game notesClone size == "+notesClone.size()
                        +"; explosions size == "+ explosions.size()+"; trashCanSize == " + trashCan.size());*/
                explosions.removeAll(trashCan);
                notesClone.removeAll(trashCan);/*
                System.out.println("From Game notesClone size == "+notesClone.size()
                        +"; explosions size == "+ explosions.size()+"; trashCanSize == " + trashCan.size());*/
                trashCan.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}