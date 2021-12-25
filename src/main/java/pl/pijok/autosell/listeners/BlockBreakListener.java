package pl.pijok.autosell.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import pl.pijok.autosell.Controllers;
import pl.pijok.autosell.settings.Settings;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent event){

        Player player = event.getPlayer();

        if(Settings.isCountBlocks()){
            Controllers.getMinersController().getMiner(player).increasedMinedBlocks(1);
        }

    }

}
