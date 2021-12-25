package pl.pijok.autosell;

import org.bukkit.plugin.java.JavaPlugin;
import pl.pijok.autosell.essentials.ConfigUtils;
import pl.pijok.autosell.essentials.Debug;
import pl.pijok.autosell.settings.Lang;
import pl.pijok.autosell.settings.Settings;

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

        Controllers.getMinersController().saveAllMinersData();

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

            if(Settings.getComponentUsage().equalsIgnoreCase("RAM")){
                Controllers.getMinersController().loadAllMinersData();
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static AutoSell getInstance() {
        return instance;
    }
}
