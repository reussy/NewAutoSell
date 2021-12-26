package pl.pijok.autosell.miner;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import pl.pijok.autosell.settings.Settings;
import pl.pijok.autosell.essentials.ConfigUtils;

import java.util.HashMap;

public class MinersController {

    private HashMap<String, Miner> miners;

    public MinersController(){
        miners = new HashMap<>();
    }

    public void loadAllMinersData(){
        if(Settings.isDatabaseUsage()){
            //TODO LOADING DATA FROM DATABASE
        }
        else{
            YamlConfiguration configuration = ConfigUtils.load("players.yml");

            for(String nickname : configuration.getConfigurationSection("players").getKeys(false)){

                long blocksMined = configuration.getLong("players." + nickname + ".blocksMined");
                boolean autoSell = configuration.getBoolean("players." + nickname + ".autoSell");

                miners.put(nickname, new Miner(blocksMined, autoSell));
            }
        }
    }

    public void loadMiner(String nickname){
        if(Settings.isDatabaseUsage()){
            //TODO LOADING DATA FROM DATABASE
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

    public void saveAllMinersData(){
        if(Settings.isDatabaseUsage()){
            //TODO SAVING DATA TO DATABASE
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
            //TODO SAVING DATA TO DATABASE
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
