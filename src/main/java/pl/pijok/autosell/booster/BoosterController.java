package pl.pijok.autosell.booster;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import pl.pijok.autosell.AutoSell;
import pl.pijok.autosell.Controllers;
import pl.pijok.autosell.database.PreparedStatements;
import pl.pijok.autosell.essentials.ChatUtils;
import pl.pijok.autosell.essentials.ConfigUtils;
import pl.pijok.autosell.essentials.Utils;
import pl.pijok.autosell.settings.Lang;
import pl.pijok.autosell.settings.Settings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class BoosterController {

    private AutoSell plugin;
    private HashMap<String, Booster> playersBoosters;

    public BoosterController(AutoSell plugin){
        this.plugin = plugin;
        playersBoosters = new HashMap<>();
    }

    public void loadBoosters(){
        if(Settings.isDatabaseUsage()){
            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    try(Connection connection = Controllers.getDatabaseManager().getHikariDataSource().getConnection()){

                        PreparedStatement preparedStatement = connection.prepareStatement(PreparedStatements.getAllBoosters);

                        ResultSet resultSet = preparedStatement.executeQuery();

                        while(resultSet.next()){
                            String nickname = resultSet.getString("name");
                            playersBoosters.put(nickname, new Booster(
                                    resultSet.getDouble("multiplier"),
                                    resultSet.getLong("duration")
                            ));
                        }

                        resultSet.close();

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            });
        }
        else{
            YamlConfiguration configuration = ConfigUtils.load("boosters.yml");

            for(String nickname : configuration.getConfigurationSection("boosters").getKeys(false)){
                long duration = configuration.getLong("boosters." + nickname + ".duration");
                double multiplier = configuration.getDouble("boosters." + nickname + ".multiplier");

                playersBoosters.put(nickname, new Booster(multiplier, duration));
            }
        }
    }

    public void loadPlayerBooster(String nickname){
        if(Settings.isDatabaseUsage()){
            //TODO Loading booster from database
            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    try(Connection connection = Controllers.getDatabaseManager().getHikariDataSource().getConnection()) {

                        PreparedStatement preparedStatement = connection.prepareStatement(PreparedStatements.getBooster);
                        preparedStatement.setString(1, nickname);

                        ResultSet resultSet = preparedStatement.executeQuery();

                        if(resultSet.next()){
                            String nickname = resultSet.getString("name");
                            playersBoosters.put(nickname, new Booster(
                                    resultSet.getDouble("multiplier"),
                                    resultSet.getLong("duration")
                            ));
                        }

                        resultSet.close();

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            });
        }
        else{
            YamlConfiguration configuration = ConfigUtils.load("boosters.yml");

            if(configuration.contains("boosters." + nickname)){
                playersBoosters.put(nickname, new Booster(
                        configuration.getDouble("boosters." + nickname + ".multiplier"),
                        configuration.getLong("boosters." + nickname + ".duration")

                ));
            }
        }
    }

    public void saveBoosters(){
        if(Settings.isDatabaseUsage()){
            //TODO Saving boosters to database
            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    try(Connection connection = Controllers.getDatabaseManager().getHikariDataSource().getConnection()) {

                        PreparedStatement preparedStatement = connection.prepareStatement(PreparedStatements.clearBoosterTable);
                        preparedStatement.execute();

                        for(String nickname : playersBoosters.keySet()){
                            Booster booster = playersBoosters.get(nickname);


                        }

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            });
        }
        else{
            YamlConfiguration configuration = ConfigUtils.load("boosters.yml");

            configuration.set("boosters", null);

            for(String nickname : playersBoosters.keySet()){
                Booster booster = playersBoosters.get(nickname);

                configuration.set("boosters." + nickname + ".duration", booster.getDuration());
                configuration.set("boosters." + nickname + ".multiplier", booster.getMultiplier());
            }

            ConfigUtils.save(configuration, "boosters.yml");
        }
    }

    public void savePlayerBooster(String nickname){
        if(Settings.isDatabaseUsage()){
            //TODO Saving booster to database
        }
        else{

        }
    }

    public void deleteBoosterFromDatabase(String nickname){

    }

    public void deleteBoosterFromConfig(String nickname){

    }

    public void initTimer(){
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
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
                        Player target = Bukkit.getPlayer(nickname);

                        if(target != null && target.isOnline()){
                            ChatUtils.sendMessage(target, Lang.getText("BOOSTER_END"));
                        }
                        playersBoosters.remove(nickname);
                    }
                }

            }
        },20L, 20L);
    }

    public Booster getPlayerBooster(String nickname){
        return playersBoosters.getOrDefault(nickname, null);
    }

    public void giveBooster(CommandSender sender, String nickname, double multiplier, long duration){
        Player target = Bukkit.getPlayer(nickname);

        if(target == null || !target.isOnline()){
            if(sender != null){
                ChatUtils.sendMessage(sender, Lang.getText("PLAYER_OFFLINE"));
            }
            return;
        }

        if(!playersBoosters.containsKey(nickname)){
            playersBoosters.put(nickname, new Booster(multiplier, duration));
        }
        else{
            Booster booster = playersBoosters.get(nickname);

            if(Settings.getNewBoosterAction().equalsIgnoreCase("ADD")){
                booster.increaseDuration(duration);
                booster.increaseMultiplier(multiplier);
            }
            else{
                booster.setDuration(duration);
                booster.setMultiplier(multiplier);
            }
        }

        String message = Lang.getText("BOOSTER_START");
        message = message.replace("%multiplier%", "" + multiplier).replace("%time%", Utils.formatTime(duration));
        ChatUtils.sendMessage(target, message);

        if(sender != null){
            ChatUtils.sendMessage(sender, "&aDone!");
        }
    }

    public void removeBooster(CommandSender sender, String nickname){
        Player target = Bukkit.getPlayer(nickname);

        if(target == null || !target.isOnline()){
            if(sender != null){
                ChatUtils.sendMessage(sender, Lang.getText("PLAYER_OFFLINE"));
            }
            return;
        }

        playersBoosters.remove(nickname);
        ChatUtils.sendMessage(target, Lang.getText("BOOSTER_END"));
    }

    public Booster getBooster(String nickname){
        return playersBoosters.getOrDefault(nickname, null);
    }

}
