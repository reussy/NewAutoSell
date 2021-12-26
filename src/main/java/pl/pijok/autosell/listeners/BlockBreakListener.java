package pl.pijok.autosell.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import pl.pijok.autosell.Controllers;
import pl.pijok.autosell.essentials.Debug;
import pl.pijok.autosell.miner.MinersController;
import pl.pijok.autosell.selling.SellingController;
import pl.pijok.autosell.settings.Settings;

public class BlockBreakListener implements Listener {

    private final MinersController minersController = Controllers.getMinersController();
    private final SellingController sellingController = Controllers.getSellingController();

    @EventHandler
    public void onBreak(BlockBreakEvent event){

        Player player = event.getPlayer();

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
            //TODO Change new Itemstack to event.getBlock().getDrops(ItemStack);
            if(sellingController.isSellableItem(event.getBlock().getType())){
                Debug.log("Checking collection");
                for(ItemStack itemStack : event.getBlock().getDrops()){
                    Debug.log(itemStack.getType()  + " " + itemStack.getAmount());
                }
                Debug.log("=====");
                sellingController.sellSingleItem(player, new ItemStack(event.getBlock().getType()));
                event.setDropItems(false);
            }

        }

    }

}
