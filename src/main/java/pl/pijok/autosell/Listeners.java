package pl.pijok.autosell;

import pl.pijok.autosell.listeners.JoinListener;
import pl.pijok.autosell.listeners.QuitListener;

public class Listeners {

    public static void register(AutoSell plugin){

        plugin.getServer().getPluginManager().registerEvents(new JoinListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new QuitListener(), plugin);

    }

}
