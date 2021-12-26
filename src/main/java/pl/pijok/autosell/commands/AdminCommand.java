package pl.pijok.autosell.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.pijok.autosell.AutoSell;
import pl.pijok.autosell.Controllers;
import pl.pijok.autosell.essentials.ChatUtils;
import pl.pijok.autosell.miner.MinersController;
import pl.pijok.autosell.selling.SellingController;
import pl.pijok.autosell.settings.Lang;

public class AdminCommand implements CommandExecutor {

    private final SellingController sellingController = Controllers.getSellingController();
    private final MinersController minersController = Controllers.getMinersController();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player){
            Player player = (Player) sender;
            if(!player.hasPermission("autosell.admin")){
                ChatUtils.sendMessage(player, Lang.getText("PERMISSION_DENIED"));
                return true;
            }
        }

        if(args.length == 1){
            if(args[0].equalsIgnoreCase("reload")){
                if(AutoSell.getInstance().loadStuff(true)){
                    ChatUtils.sendMessage(sender, "&aReloaded!");
                }
                else{
                    ChatUtils.sendMessage(sender, "&cSomething went wrong while reloading! Check console for errors");
                }
                return true;
            }
        }

        ChatUtils.sendMessage(sender, "&7/" + label + " reload");
        return true;
    }
}
