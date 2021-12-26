package pl.pijok.autosell.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.pijok.autosell.Controllers;
import pl.pijok.autosell.essentials.ChatUtils;
import pl.pijok.autosell.essentials.Debug;
import pl.pijok.autosell.miner.Miner;
import pl.pijok.autosell.miner.MinersController;
import pl.pijok.autosell.settings.Lang;

public class AutoSellCommand implements CommandExecutor {

    private final MinersController minersController = Controllers.getMinersController();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)){
            Debug.log("&cCommand only for players!");
            return true;
        }

        Player player = (Player) sender;

        if(args.length == 0){
            if(!player.hasPermission("autosell.autosell")){
                ChatUtils.sendMessage(player, Lang.getText("PERMISSION_DENIED"));
                return true;
            }

            Miner miner = minersController.getMiner(player);
            if(miner.isAutoSell()){
                miner.setAutoSell(false);
                ChatUtils.sendMessage(player, Lang.getText("AUTOSELL_OFF"));
            }
            else{
                miner.setAutoSell(true);
                ChatUtils.sendMessage(player, Lang.getText("AUTOSELL_ON"));
            }

            return true;
        }

        ChatUtils.sendMessage(player, "&7/" + label);
        return true;
    }
}
