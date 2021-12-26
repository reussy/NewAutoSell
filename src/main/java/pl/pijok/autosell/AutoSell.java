package pl.pijok.autosell;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pl.pijok.autosell.essentials.ConfigUtils;
import pl.pijok.autosell.essentials.Debug;
import pl.pijok.autosell.settings.Lang;
import pl.pijok.autosell.settings.Settings;

public class AutoSell extends JavaPlugin {

    private static AutoSell instance;
    private static Economy economy;

    @Override
    public void onEnable() {

        instance = this;

        Debug.setPrefix("[AutoSell] ");
        ConfigUtils.setPlugin(this);

        if (!setupEconomy()){
            Debug.log(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

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

            if(!reload){
                if(Settings.getComponentUsage().equalsIgnoreCase("RAM")){
                    Controllers.getMinersController().loadAllMinersData();
                }
            }

            Controllers.getSellingController().loadSettings();

        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public static AutoSell getInstance() {
        return instance;
    }

    public static Economy getEconomy() {
        return economy;
    }
}
