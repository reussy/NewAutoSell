package pl.pijok.autosell.settings;

import org.bukkit.configuration.file.YamlConfiguration;
import pl.pijok.autosell.essentials.ConfigUtils;

import java.util.HashMap;

public class Lang {

    private static HashMap<String, String> lang;

    public static void load(){
        lang = new HashMap<>();

        YamlConfiguration configuration = ConfigUtils.load("lang.yml");

        for(String id : configuration.getConfigurationSection("lang").getKeys(false)){
            lang.put(id, configuration.getString("lang." + id));
        }
    }

    public static String getText(String a){
        return lang.getOrDefault(a, "NULL");
    }

}
