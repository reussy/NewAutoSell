package pl.pijok.autosell.selling;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.pijok.autosell.AutoSell;
import pl.pijok.autosell.essentials.ConfigUtils;
import pl.pijok.autosell.essentials.Debug;
import pl.pijok.autosell.essentials.Utils;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class SellingController {

    private HashMap<Material, Double> blocksValues;
    private LinkedHashMap<String, Double> multipliers;

    public SellingController(){
        blocksValues = new HashMap<>();
        multipliers = new LinkedHashMap<>();
    }

    public void loadSettings(){

        YamlConfiguration configuration = ConfigUtils.load("blocksValues.yml");

        //Loading blocks values
        for(String materialName : configuration.getConfigurationSection("blocks").getKeys(false)){
            if(!Utils.isMaterial(materialName)){
                Debug.log("&cWrong material name in blocksValues.yml -> blocks section");
                continue;
            }

            blocksValues.put(Material.valueOf(materialName), configuration.getDouble("blocks." + materialName));
        }

        //Loading multipliers
        for(String id : configuration.getConfigurationSection("multipliers").getKeys(false)){
            String permission = configuration.getString("multipliers." + id + ".permission");
            double multiplier = configuration.getDouble("multipliers." + id + ".multiplier");

            multipliers.put(permission, multiplier);
        }
    }

    public void sellPlayerInventory(Player player){
        double value = 0;

        for(ItemStack itemStack : player.getInventory().getContents()){
            if(itemStack == null || itemStack.getType().equals(Material.AIR)){
                continue;
            }

            if(blocksValues.containsKey(itemStack.getType())){
                value += blocksValues.get(itemStack.getType()) * itemStack.getAmount();
            }
        }

        if(value == 0){
            //TODO Add message nothing to sell
            return;
        }

        for(String permission : multipliers.keySet()){
            if(player.hasPermission(permission)){
                value = value * multipliers.get(permission);
                break;
            }
        }

        //TODO Add message about sold items
        AutoSell.getEconomy().depositPlayer(player, value);
    }

    public void sellSingleItem(Player player, ItemStack itemStack){
        if(!blocksValues.containsKey(itemStack.getType())){
            return;
        }

        double value = blocksValues.get(itemStack.getType()) * itemStack.getAmount();;

        for(String permission : multipliers.keySet()){
            if(player.hasPermission(permission)){
                value = value * multipliers.get(permission);
                break;
            }
        }

        AutoSell.getEconomy().depositPlayer(player, value);
    }
}
