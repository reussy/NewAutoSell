package pl.pijok.autosell;

import pl.pijok.autosell.commands.AdminCommand;
import pl.pijok.autosell.commands.AutoSellCommand;
import pl.pijok.autosell.commands.SellAllCommand;

public class Commands {

    public static void register(AutoSell plugin){

        plugin.getCommand("autosell").setExecutor(new AutoSellCommand());
        plugin.getCommand("sellall").setExecutor(new SellAllCommand());
        plugin.getCommand("adminautosell").setExecutor(new AdminCommand(plugin));

    }

}
