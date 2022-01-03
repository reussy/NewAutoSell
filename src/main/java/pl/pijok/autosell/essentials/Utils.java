package pl.pijok.autosell.essentials;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Utils {

    public static boolean isMaterial(String a){
        try{
            Material.valueOf(a);
            return true;
        }
        catch (IllegalArgumentException e){
            return false;
        }
    }

    public static boolean isInteger(String a){
        try{
            Integer.parseInt(a);
            return true;
        }
        catch (NumberFormatException e){
            return false;
        }
    }

    public static double round(double value, int precision){
        int multiplier = (int) Math.pow(10, precision);
        value = (double) ((int) (value * multiplier)) / multiplier;
        return value;
    }

    public static String formatTime(long duration){
        String formattedTime = "";

        long seconds = duration % 60;
        long minutes = (duration % 3600) / 60;
        long hours = duration / 3600;

        if(hours > 0){
            formattedTime += hours + "h ";
        }
        if(minutes > 0){
            formattedTime += minutes + "m ";
        }
        if(seconds > 0){
            formattedTime += seconds + "s";
        }

        return formattedTime;
    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
