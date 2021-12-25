package pl.pijok.autosell.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.pijok.autosell.Controllers;
import pl.pijok.autosell.settings.Settings;

public class QuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();

        if(Settings.getComponentUsage().equalsIgnoreCase("CPU")){
            Controllers.getMinersController().saveMiner(player.getName());
        }
    }

}
