package pl.pijok.autosell.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.pijok.autosell.Controllers;
import pl.pijok.autosell.miner.MinersController;
import pl.pijok.autosell.settings.Settings;

public class JoinListener implements Listener {

    private final MinersController minersController = Controllers.getMinersController();

    @EventHandler
    public void onJoin(PlayerJoinEvent event){

        Player player = event.getPlayer();


        if(Settings.getComponentUsage().equalsIgnoreCase("CPU")){
            Controllers.getMinersController().loadMiner(player.getName());
        }
        else{
            if(minersController.getMiner(player) == null){
                minersController.createNewMiner(player);
            }
        }

    }

}
