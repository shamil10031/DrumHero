package com.ShomazzApp.drumhero.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ShomazzApp.drumhero.game.Game;
import com.ShomazzApp.drumhero.game.Note;
import com.ShomazzApp.drumhero.game.Song;

import java.util.ArrayList;

public class DBManager {

    private static DBManager dbManager;
    private static Game game;
    public static final String DB_NAME = "data.db";
    public static final String SCORE_TABLE_NAME = "SCORE";
    public static final String SONGSALL_TABLE_NAME = "SONGS";
    public static final int DB_VERSION = 2;
    private SQLiteDatabase db;
    private Context context;

    public DBManager(Context context, Game game) {
        this.context = context;
        this.game = game;
        db = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
    }

    public static DBManager getInstance(Context context) {
        if (dbManager == null) {
            dbManager = new DBManager(context, game);
        }
        return dbManager;
    }

    public ArrayList<Note> getAllNotes(String tableName, boolean doDelay) {
        //in mp3 files there are little delay
        ArrayList<Note> dbNotes = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            dbNotes.add(new Note(i, 0, game));
        }
        int delay = 0;
        if (doDelay) delay = 3500;
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);
        boolean hasMoreData = cursor.moveToFirst();
        while (hasMoreData) {
            int numberOfLine = cursor.getInt(cursor.getColumnIndex("NUMBEROFLINE"));
            int startSecond = (int) (Double.parseDouble(cursor.getString(cursor.getColumnIndex("STARTSECOND"))) * 1000);
            dbNotes.add(new Note(numberOfLine, startSecond + delay, game));
            hasMoreData = cursor.moveToNext();
        }
        return dbNotes;
    }


    public ArrayList<Note> getNotesWithDifficulty(String tableName, boolean doDelay, int difficulty) {
        //in mp3 files there are little delay
        ArrayList<Note> allNotes = getAllNotes(tableName, doDelay);
        ArrayList<Note> notes = new ArrayList<>();

        switch (difficulty) {
            case Game.HARD:
                for (int i = 0; i < allNotes.size(); i++) {
                    if (i < 4) {
                        notes.add(allNotes.get(i));
                    } else if (getDifferenceOfNotesSecondInLine(notes, allNotes.get(i), i) >= 80
                            && !isNoteInOneLine(allNotes.get(i), allNotes)) {
                        notes.add(allNotes.get(i));
                    }
                }
                break;
            case Game.MEDIUM:
                for (int i = 0; i < allNotes.size(); i++) {
                    if (i < 4) {
                        notes.add(allNotes.get(i));
                    } else if (getDifferenceOfNotesSecondInLine(notes, allNotes.get(i), i) >= 350
                            && !isNoteInOneLine(allNotes.get(i), allNotes)) {
                        notes.add(allNotes.get(i));
                    }
                }
                break;
            case Game.EASY:
                for (int i = 0; i < allNotes.size(); i++) {
                    if (i < 4) {
                        notes.add(allNotes.get(i));
                    } else if (getDifferenceOfNotesSecondInLine(notes, allNotes.get(i), i) >= 750
                            && !isNoteInOneLine(allNotes.get(i), allNotes)) {
                        notes.add(allNotes.get(i));
                    }
                }
                break;
        }
        return notes;
    }

    public long getDifferenceOfNotesSecondInLine(ArrayList<Note> notes, Note note, int index) {
        long second = note.getStartSecond();
        int i = index - 1;
        if (i != 0) {
            for (; true; i--) {
                if (i <= 0) {
                    second = note.getStartSecond();
                    break;
                } else {
                    if (i >= notes.size()) {
                        i = notes.size() - 1;
                    }
                    if (note.getNumberOfLine() == notes.get(i).getNumberOfLine()) {
                        second = note.getStartSecond() - notes.get(i).getStartSecond();
                        break;
                    }
                }
            }
        } else {
            second = notes.get(i).getStartSecond();
        }
        return second;
    }

    public boolean isNoteInOneLine(Note note, ArrayList<Note> notes) {
        boolean b = false;
        int index = notes.indexOf(note);
        if (index < notes.size() - 1) {
            if (note.getStartSecond() - notes.get(index - 1).getStartSecond() <= 80
                    && notes.get(index + 1).getStartSecond() - note.getStartSecond() <= 80) {
                b = true;
            } else {
                b = false;
            }
        } else {
            b = false;
        }
        return b;
    }

    public long getDifferenceOfNotesSecond(ArrayList<Note> notes, Note note) {
        long second = note.getStartSecond();
        int i = notes.size();
        if (i > 0) {
            second = note.getStartSecond() - notes.get(i - 1).getStartSecond();
        }
        System.out.println("note - note(-1) = " + note.getStartSecond() + " - "
                + notes.get(i - 1).getStartSecond() + " = " + second);
        return second;
    }

    public int getBestScore(String songName) {
        int score = 0;
        Cursor cursor = db.rawQuery("SELECT * FROM " + SCORE_TABLE_NAME + ";",
                null);
        boolean hasMoreData = cursor.moveToFirst();
        while (hasMoreData) {
            if (songName.equals(cursor.getString(cursor
                    .getColumnIndex("SONGTITLE")))) {
                if (cursor.getInt(cursor.getColumnIndex("SCORES")) > score) {
                    score = cursor.getInt(cursor.getColumnIndex("SCORES"));
                }
            }
            hasMoreData = cursor.moveToNext();
        }
        return score;
    }

    public ArrayList<String> getAllScores(ArrayList<String> songs) {
        ArrayList<String> scores = new ArrayList<>();
        for (int i = 0; i < songs.size(); i++) {
            scores.add(getBestScore(songs.get(i)) + "");
        }
        return scores;
    }

    public void putScore(String songName, int score) {
        db.execSQL("INSERT INTO " + SCORE_TABLE_NAME + " VALUES ('" + songName
                + "', " + score + ");");
    }

    public void deleteSong(Song song) {
        db.execSQL("DROP TABLE " + song.getTableName() + ";");
        db.execSQL("DELETE FROM " + SONGSALL_TABLE_NAME + " WHERE TABLENAME='" + song.getTableName() + "';");
        db.execSQL("DELETE FROM " + SCORE_TABLE_NAME + " WHERE SONGTITLE='" + song.getTitleByName() + "';");
    }

    public void addSongToDB(Song song, ArrayList<LineToTable> arr) {
        song.setTableName(Song.makeTableName(song.getTitleByName()));
        db.execSQL("INSERT INTO " + SONGSALL_TABLE_NAME + " VALUES ('"
                + song.getTableName() + "', '" + song.getArtistName() + "', '"
                + song.getSongName() + "', '" + song.getFileName() + "', "
                + song.getId() + ");");
        System.out.println("From addSongToDB " + "VALUES ('"
                + song.getTableName() + "', '" + song.getArtistName() + "', '"
                + song.getSongName() + "', '" + song.getFileName() + "', "
                + song.getId() + ");");
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                + song.getTableName()
                + " (NUMBEROFLINE TEXT, STARTSECOND INTEGER);");
        for (int i = 0; i < arr.size(); i++) {
            db.execSQL("INSERT INTO " + song.getTableName() + " VALUES ('"
                    + arr.get(i).numberOfLine + "', "
                    + arr.get(i).getStartSecond() + ");");
        }

    }

    public ArrayList<Song> getAllSongsFromDB() {
        ArrayList<Song> arr = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + SONGSALL_TABLE_NAME
                    + ";", null);
            System.out.println("Form getAllSongsFromDB cursor == " + cursor);
            System.out.println("Form getAllSongsFromDB is cursor == null : " + (cursor == null));
            boolean hasMoreData = cursor.moveToFirst();
            while (hasMoreData) {
                System.out.println("From getAllSongsFromDB Song(" + cursor.getString(cursor
                        .getColumnIndex("SONGNAME")) + ", " + cursor.getString(cursor
                        .getColumnIndex("ARTIST")) + ", " + cursor.getString(cursor
                        .getColumnIndex("TABLENAME")) + ", " + cursor.getString(cursor
                        .getColumnIndex("FILENAME")) + ", " + cursor.getInt(cursor
                        .getColumnIndex("ID")) + ")");
                Song s = new Song(cursor.getString(cursor
                        .getColumnIndex("SONGNAME")), cursor.getString(cursor
                        .getColumnIndex("ARTIST")), cursor.getString(cursor
                        .getColumnIndex("TABLENAME")), cursor.getString(cursor
                        .getColumnIndex("FILENAME")), cursor.getInt(cursor
                        .getColumnIndex("ID")));
                arr.add(s);
                hasMoreData = cursor.moveToNext();
            }
            return arr;
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return null;
        }
    }

    public void clearTable(String tableName) {
        db.execSQL("DELETE FROM " + tableName);
        System.out.println("From clearTable(DELETE FROM " + tableName + ");");
    }
}
