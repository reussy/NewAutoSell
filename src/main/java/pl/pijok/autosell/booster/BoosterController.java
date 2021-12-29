package pl.pijok.autosell.booster;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import pl.pijok.autosell.AutoSell;
import pl.pijok.autosell.essentials.ConfigUtils;
import pl.pijok.autosell.settings.Settings;

import java.util.*;

public class BoosterController {

    private HashMap<String, Booster> playersBoosters;

    public BoosterController(){
        playersBoosters = new HashMap<>();
    }

    public void loadBoosters(){
        YamlConfiguration configuration = ConfigUtils.load("boosters.yml");

        for(String nickname : configuration.getConfigurationSection("boosters").getKeys(false)){
            long duration = configuration.getLong("boosters." + nickname + ".duration");
            double multiplier = configuration.getDouble("boosters." + nickname + ".multiplier");

            playersBoosters.put(nickname, new Booster(multiplier, duration));
        }
    }

    public void initTimer(){
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(AutoSell.getInstance(), new Runnable() {
            @Override
            public void run() {

                Set<String> toCheck;

                if(Settings.isBoosterOnlyOnline()){
                    toCheck = new HashSet<>();
                    for(Player player : Bukkit.getOnlinePlayers()){
                        toCheck.add(player.getName());
                    }
                }
                else{
                    toCheck = playersBoosters.keySet();
                }

                for(String nickname : toCheck){
                    Booster booster = playersBoosters.get(nickname);
                    booster.decreaseDuration(1);
                    if(booster.getDuration() <= 0){
                        //TODO END BOOSTER MESSAGE
                    }
                }

            }
        },20L, 20L);
    }

    public Booster getPlayerBooster(String nickname){
        return playersBoosters.getOrDefault(nickname, null);
    }

    public void giveBooster(String nickname, double multiplier, long duration){

    }

    public void removeBooster(String nickname){

    }

}
