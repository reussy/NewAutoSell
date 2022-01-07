package pl.pijok.autosell.miner;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
                            String filters = resultSet.getString("filter");

                            List<Material> filterMaterial = null;

                            if(filters != null){
                                filterMaterial = Controllers.getSellingController().stringToList(filters);
                            }

                            if(filterMaterial != null){
                                miners.put(nickname, new Miner(blocksMined, autoSellEnabled, filterMaterial));
                            }
                            else{
                                miners.put(nickname, new Miner(blocksMined, autoSellEnabled));
                            }
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

                List<Material> filterMaterials = null;
                if(configuration.contains("players." + nickname + ".autoSellFilter")){
                    filterMaterials = new ArrayList<>();
                    for(String id : configuration.getStringList("players." + nickname + ".autoSellFilter")){
                        filterMaterials.add(Material.valueOf(id));
                    }
                }

                if(filterMaterials != null){
                    miners.put(nickname, new Miner(blocksMined, autoSell, filterMaterials));
                }
                else{
                    miners.put(nickname, new Miner(blocksMined, autoSell));
                }

            }
        }
    }

    //TODO Saving with filter (everywhere) D:
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

                        List<Material> filter = null;

                        if(resultSet.next()){
                            Debug.log("Getting miner!");
                            blocksMined = resultSet.getInt("blocksMined");
                            autosell = resultSet.getBoolean("autosell");

                            String filterString = resultSet.getString("filter");

                            if(filterString != null){
                                filter = Controllers.getSellingController().stringToList(filterString);
                            }

                            getMiner.close();
                        }

                        resultSet.close();
                        if(filter != null){
                            miners.put(nickname, new Miner(blocksMined, autosell, filter));
                        }
                        else{
                            miners.put(nickname, new Miner(blocksMined, autosell));
                        }


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
            List<Material> filter = null;

            if(configuration.contains("players." + nickname)){
                blocksMined = configuration.getLong("players." + nickname + ".blocksMined");
                autoSell = configuration.getBoolean("players." + nickname + ".autoSell");

                if(configuration.contains("players." + nickname + ".autoSellFilter")){
                    filter = new ArrayList<>();

                    for(String key : configuration.getStringList("players." + nickname + ".autoSellFilter")){
                        filter.add(Material.valueOf(key));
                    }
                }
            }

            if(filter == null){
                miners.put(nickname, new Miner(blocksMined, autoSell));
            }
            else{
                miners.put(nickname, new Miner(blocksMined, autoSell, filter));
            }
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
                            statement.setString(4, Controllers.getSellingController().listToString(miner.getAutoSellFilter()));
                            //Update
                            statement.setLong(5, miner.getMinedBlocks());
                            statement.setBoolean(6, miner.isAutoSell());
                            statement.setString(7, Controllers.getSellingController().listToString(miner.getAutoSellFilter()));

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

                List<String> filter = new ArrayList<>();

                for(Material material : miner.getAutoSellFilter()){
                    filter.add(material.name());
                }

                configuration.set("players." + nickname + ".blocksMined", miner.getMinedBlocks());
                configuration.set("players." + nickname + ".autoSell", miner.isAutoSell());
                configuration.set("players." + nickname + ".autoSellFilter", filter);
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
                statement.setString(4, Controllers.getSellingController().listToString(miner.getAutoSellFilter()));
                //Update
                statement.setLong(5, miner.getMinedBlocks());
                statement.setBoolean(6, miner.isAutoSell());
                statement.setString(7,Controllers.getSellingController().listToString(miner.getAutoSellFilter()));

                statement.execute();

                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else{
            YamlConfiguration configuration = ConfigUtils.load("players.yml");

            Miner miner = miners.get(nickname);

            List<String> filter = new ArrayList<>();

            for(Material material : miner.getAutoSellFilter()){
                filter.add(material.name());
            }

            configuration.set("players." + nickname + ".blocksMined", miner.getMinedBlocks());
            configuration.set("players." + nickname + ".autoSell", miner.isAutoSell());
            configuration.set("players." + nickname + ".autoSellFilter", filter);

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
