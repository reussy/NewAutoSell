package pl.pijok.autosell.miner;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import pl.pijok.autosell.essentials.ConfigUtils;

import java.util.HashMap;

public class MinersController {

    private HashMap<String, Miner> miners;

    public MinersController(){
        miners = new HashMap<>();
    }

    public void load(){
        YamlConfiguration configuration = ConfigUtils.load("players.yml");

        for(String nickname : configuration.getConfigurationSection("players").getKeys(false)){

            long blocksMined = configuration.getLong("players." + nickname + ".blocksMined");
            boolean autoSell = configuration.getBoolean("players." + nickname + ".autoSell");

            miners.put(nickname, new Miner(blocksMined, autoSell));
        }
    }

    public Miner getMiner(Player player){
        return miners.get(player.getName());
    }

    public Miner getMiner(String nickname){
        return miners.get(nickname);
    }

}
