package pl.pijok.autosell.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import pl.pijok.autosell.Controllers;
import pl.pijok.autosell.Storage;
import pl.pijok.autosell.essentials.Debug;
import pl.pijok.autosell.essentials.Utils;
import pl.pijok.autosell.miner.MinersController;
import pl.pijok.autosell.miner.Range;
import pl.pijok.autosell.selling.SellingController;
import pl.pijok.autosell.settings.Settings;

public class BlockBreakListener implements Listener {

    private final MinersController minersController = Controllers.getMinersController();
    private final SellingController sellingController = Controllers.getSellingController();

    @EventHandler
    public void onBreak(BlockBreakEvent event){

        Player player = event.getPlayer();

        if(Storage.detectedWorldGuard){
            if(!Settings.isIgnoreWorldGuard()){
                if(!canBuild(player, event.getBlock().getLocation())){
                    Debug.log("Canceling task!");
                    return;
                }
            }
        }

        if(Settings.isCountBlocks()){
            if(Settings.isCountOnlyOnMiningTools()){
                Material material = player.getInventory().getItemInMainHand().getType();

                if(!Settings.getMiningTools().contains(material)){
                    return;
                }
            }
            minersController.getMiner(player).increasedMinedBlocks(1);
        }

        if(minersController.getMiner(player).isAutoSell()){
            if(sellingController.isSellableItem(event.getBlock().getType())){
                sellingController.sellSingleItem(player, createDrop(player, event.getBlock()));
                event.setDropItems(false);
            }
        }
        else if(Settings.isDropToInventory()){
            player.getInventory().addItem(createDrop(player, event.getBlock()));
            event.setDropItems(false);
        }
    }

    private boolean canBuild(Player p, Location l) {
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(l);
        return !query.testState(loc, WorldGuardPlugin.inst().wrapPlayer(p), Flags.BLOCK_BREAK);
    }

    private ItemStack createDrop(Player player, Block block){
        ItemStack itemStack;

        if(Settings.getOreDrops().containsKey(block.getType())){
            itemStack = new ItemStack(Settings.getOreDrops().get(block.getType()));
        }
        else{
            itemStack = new ItemStack(block.getType());
        }

        if(player.getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.LOOT_BONUS_BLOCKS)){
            Range range = Settings.getFortuneDrop(player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS));
            itemStack.setAmount(Utils.getRandomNumber(range.getMin(), range.getMax()));
        }

        return itemStack;
    }

}
