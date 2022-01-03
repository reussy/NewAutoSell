package pl.pijok.autosell.miner;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import pl.pijok.autosell.AutoSell;
import pl.pijok.autosell.Controllers;
import pl.pijok.autosell.database.PreparedStatements;
import pl.pijok.autosell.essentials.Debug;
import pl.pijok.autosell.settings.Settings;
import pl.pijok.autosell.essentials.ConfigUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class MinersController {

    private final AutoSell plugin;
    private HashMap<String, Miner> miners;

    public MinersController(AutoSell plugin){
        this.plugin = plugin;
        miners = new HashMap<>();
    }

    public void loadAllMinersData(){
        if(Settings.isDatabaseUsage()){
            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    try(Connection connection = Controllers.getDatabaseManager().getHikariDataSource().getConnection()){
                        PreparedStatement getAll = connection.prepareStatement(PreparedStatements.getAllPlayers);

                        ResultSet resultSet = getAll.executeQuery();

                        while(resultSet.next()){
                            String nickname = resultSet.getString("name");
                            long blocksMined = resultSet.getLong("blocksMined");
                            boolean autoSellEnabled = resultSet.getBoolean("autosell");

                            miners.put(nickname, new Miner(blocksMined, autoSellEnabled));
                        }

                        resultSet.close();

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                        Debug.log("&cCouldn't connect to database!");
                    }
                }
            });
        }
        else{
            YamlConfiguration configuration = ConfigUtils.load("players.yml");

            for(String nickname : configuration.getConfigurationSection("players").getKeys(false)){

                long blocksMined = configuration.getInt("players." + nickname + ".blocksMined");
                boolean autoSell = configuration.getBoolean("players." + nickname + ".autoSell");

                miners.put(nickname, new Miner(blocksMined, autoSell));
            }
        }
    }

    public void loadMiner(String nickname){
        if(Settings.isDatabaseUsage()){
            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    try(Connection connection = Controllers.getDatabaseManager().getHikariDataSource().getConnection()) {
                        PreparedStatement getMiner = connection.prepareStatement(PreparedStatements.getPlayer);
                        getMiner.setString(1, nickname);

                        long blocksMined = 0;
                        boolean autosell = false;

                        ResultSet resultSet = getMiner.executeQuery();
                        if(resultSet.next()){
                            Debug.log("Getting miner!");
                            blocksMined = resultSet.getInt("blocksMined");
                            autosell = resultSet.getBoolean("autosell");

                            getMiner.close();
                        }
                        /*else{
                            Debug.log("New miner!");
                            PreparedStatement insertMiner = connection.prepareStatement(PreparedStatements.insertPlayer);
                            insertMiner.setString(1, nickname);
                            insertMiner.setInt(2, 0);
                            insertMiner.setBoolean(3, false);
                            insertMiner.execute();

                            insertMiner.close();
                        }*/

                        resultSet.close();
                        miners.put(nickname, new Miner(blocksMined, autosell));

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            });
        }
        else{
            YamlConfiguration configuration = ConfigUtils.load("players.yml");

            long blocksMined = 0;
            boolean autoSell = false;

            if(configuration.contains("players." + nickname)){
                blocksMined = configuration.getLong("players." + nickname + ".blocksMined");
                autoSell = configuration.getBoolean("players." + nickname + ".autoSell");
            }
            miners.put(nickname, new Miner(blocksMined, autoSell));
        }
    }

    public void createNewMiner(Player player){
        miners.put(player.getName(), new Miner(0, false));
    }

    public void saveAllMinersData(boolean serverDisabling){
        if(Settings.isDatabaseUsage()){
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try(Connection connection = Controllers.getDatabaseManager().getHikariDataSource().getConnection()) {
                        for(String nickname : miners.keySet()){
                            Miner miner = miners.get(nickname);
                            PreparedStatement statement = connection.prepareStatement(PreparedStatements.updatePlayer);
                            //Insert
                            statement.setString(1, nickname);
                            statement.setLong(2, miner.getMinedBlocks());
                            statement.setBoolean(3, miner.isAutoSell());
                            //Update
                            statement.setLong(4, miner.getMinedBlocks());
                            statement.setBoolean(5, miner.isAutoSell());

                            //To remove
                            /*statement.setLong(1, miner.getMinedBlocks());
                            statement.setBoolean(2, miner.isAutoSell());
                            statement.setString(3, nickname);*/
                            statement.execute();

                            statement.close();
                        }

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            };
            if(serverDisabling){
                runnable.run();
            }
            else{
                Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
            }

        }
        else{
            YamlConfiguration configuration = ConfigUtils.load("players.yml");

            for(String nickname : miners.keySet()){
                Miner miner = miners.get(nickname);

                configuration.set("players." + nickname + ".blocksMined", miner.getMinedBlocks());
                configuration.set("players." + nickname + ".autoSell", miner.isAutoSell());
            }

            ConfigUtils.save(configuration, "players.yml");
        }

        miners.clear();
    }

    public void saveMiner(String nickname){
        if(Settings.isDatabaseUsage()) {
            try(Connection connection = Controllers.getDatabaseManager().getHikariDataSource().getConnection()){
                Miner miner = miners.get(nickname);
                PreparedStatement statement = connection.prepareStatement(PreparedStatements.updatePlayer);

                //Insert
                statement.setString(1, nickname);
                statement.setLong(2, miner.getMinedBlocks());
                statement.setBoolean(3, miner.isAutoSell());
                //Update
                statement.setLong(4, miner.getMinedBlocks());
                statement.setBoolean(5, miner.isAutoSell());

                //To remove
                /*statement.setLong(1, miner.getMinedBlocks());
                statement.setBoolean(2, miner.isAutoSell());
                statement.setString(3, nickname);*/
                statement.execute();

                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else{
            YamlConfiguration configuration = ConfigUtils.load("players.yml");

            Miner miner = miners.get(nickname);

            configuration.set("players." + nickname + ".blocksMined", miner.getMinedBlocks());
            configuration.set("players." + nickname + ".autoSell", miner.isAutoSell());

            ConfigUtils.save(configuration, "players.yml");
        }

        miners.remove(nickname);
    }

    public Miner getMiner(Player player){
        return miners.get(player.getName());
    }

    public Miner getMiner(String nickname){
        return miners.get(nickname);
    }

}
