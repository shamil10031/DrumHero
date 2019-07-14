package com.ShomazzApp.drumhero.game;

public class Song {

    private String name;
    private String artist;
    private String tableName;
    private String fileName;
    private int id;

    public Song(String name, String artist, String tableName, String fileName,
                int id) {
        this.name = toStringForSQL(name);
        this.id = id;
        this.artist = artist;
        this.tableName = tableName;
        this.fileName = fileName;
    }

    public static String toStringFileName(String s) {
        String word = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) >= 'A' && s.charAt(i) <= 'Z') {
                word += Character.toLowerCase(s.charAt(i));
            } else if (s.charAt(i) == ' ') {
                word += '_';
            } else {
                word += s.charAt(i);
            }
        }
        return word;
    }

    //A–Z, a–z, 0–9, _ (подчеркивание), $ и #

    public static String toStringForSQL(String w){
        String word = "";
        for (int i = 0; i < w.length(); i++){
            if ((w.charAt(i) >= 'A' && w.charAt(i) <= 'Z')
                    || (w.charAt(i) >= 'a' && w.charAt(i) <= 'z')
                    || (w.charAt(i) >= '0' && w.charAt(i) <= '9')
                    || w.charAt(i) == '_' || w.charAt(i) == '$'
                    || w.charAt(i) =='#' || w.charAt(i) == ' '){
                if(w.charAt(i) == '_'){
                    word += ' ';
                } else {
                    word += w.charAt(i);
                }
            }
        }
        return word;
    }

    public static String makeFileNameOfPath(String path, String addedWord) {
        String word = "";
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '/' || path.charAt(i) == '\\') {
                word = "";
            } else {
                word += path.charAt(i);
            }
        }
        System.out.println("From makeFileNameOfPath path == " + path + "\n"
                + "addedWord == " + addedWord + "\n"
                + "return word == " + word.replace(".mid", addedWord));
        if (!word.endsWith(".mid")) word += ".mid";
        return word.replace(".mid", addedWord);
    }

    public static String makeTableName(String s) {
        String word = "";
        for (int i = 0; i < s.length(); i++) {
            if ((s.charAt(i) >= '0' && s.charAt(i) <= 'z')
                    || (s.charAt(i) >= 'А' && s.charAt(i) <= 'я')) {
                word += Character.toTitleCase(s.charAt(i));
            }
        }
        System.out.println("From makeTableName" + "\n"
                + " from " + s + " to " + word);
        return word;
    }

    public String getSongName() {
        return toStringFileName(name);
    }

    public String getTitleByName() {
        return name + " - " + artist;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String s) {
        this.tableName = s;
    }

    public void setFileName (String s) {
        this.fileName = s;
    }

    public int getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getArtistName() {
        return artist;
    }

}
