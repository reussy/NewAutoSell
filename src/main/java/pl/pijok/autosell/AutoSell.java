package pl.pijok.autosell;

import org.bukkit.plugin.java.JavaPlugin;
import pl.pijok.autosell.essentials.ConfigUtils;
import pl.pijok.autosell.essentials.Debug;

public class AutoSell extends JavaPlugin {

    private static AutoSell instance;

    @Override
    public void onEnable() {

        instance = this;

        Debug.setPrefix("[AutoSell] ");
        ConfigUtils.setPlugin(this);

        if(!loadStuff(false)){
            Debug.sendError("Something went wrong while loading plugin " + getDescription().getName() + "! Disabling...");
            getServer().getPluginManager().disablePlugin(this);
        }
        else{
            Debug.log("&aEverything loaded!");
            Debug.log("&aHave a nice day :D");
        }

    }

    @Override
    public void onDisable() {

    }

    public boolean loadStuff(boolean reload){

        try{
            if(!reload){
                Controllers.create();
                Listeners.register(this);
                Commands.register(this);
            }

            Settings.load();
            Lang.load();
        }
        catch (Exception e){

        }

        return true;
    }

    public static AutoSell getInstance() {
        return instance;
    }
}
