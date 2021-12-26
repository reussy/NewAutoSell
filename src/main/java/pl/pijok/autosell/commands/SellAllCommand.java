package pl.pijok.autosell.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.pijok.autosell.Controllers;
import pl.pijok.autosell.essentials.ChatUtils;
import pl.pijok.autosell.essentials.Debug;
import pl.pijok.autosell.selling.SellingController;
import pl.pijok.autosell.settings.Lang;

public class SellAllCommand implements CommandExecutor {

    private final SellingController sellingController = Controllers.getSellingController();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)){
            Debug.log("&cCommand only for players!");
            return true;
        }

        Player player = (Player) sender;

        if(args.length == 0){
            if(!player.hasPermission("autosell.sellall")){
                ChatUtils.sendMessage(player, Lang.getText("PERMISSION_DENIED"));
                return true;
            }

            sellingController.sellPlayerInventory(player);
            return true;
        }

        ChatUtils.sendMessage(player, "&7/" + label);
        return true;
    }
}
