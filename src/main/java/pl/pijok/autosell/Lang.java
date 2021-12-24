package pl.pijok.autosell;

import java.util.HashMap;

public class Lang {

    private static HashMap<String, String> lang;

    public static void load(){
        lang = new HashMap<>();


    }

    public static String getText(String a){
        return lang.getOrDefault(a, "NULL");
    }

}
