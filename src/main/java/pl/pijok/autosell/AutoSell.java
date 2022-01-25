package pl.pijok.autosell;

import at.pcgamingfreaks.Minepacks.Bukkit.API.MinepacksPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pl.pijok.autosell.essentials.ConfigUtils;
import pl.pijok.autosell.essentials.Debug;
import pl.pijok.autosell.hooks.Hooks;
import pl.pijok.autosell.settings.Lang;
import pl.pijok.autosell.settings.Settings;

public class AutoSell extends JavaPlugin {

    private static Economy economy;

    //bStats
    private final int pluginID = 13757;
    private Metrics metrics;

    //Update checker
    private final int spigotID = 98930;

    @Override
    public void onEnable() {

        Debug.setPrefix("[AutoSell] ");
        ConfigUtils.setPlugin(this);

        if(!loadStuff(false)){
            Debug.sendError("Something went wrong while loading plugin " + getDescription().getName() + "! Disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        else{
            Debug.log("&aEverything loaded!");
            Debug.log("&aHave a nice day :D");
        }

        if (!setupEconomy()){
            Debug.log(String.format("[%s] - Disabled due to no Vault dependency found or no valid economy plugin!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new Placeholders(this).register();
            Debug.log("&aHooked into PlaceholderAPI!");
        }

        metrics = new Metrics(this, pluginID);

        new UpdateChecker(this, spigotID).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                Debug.log("&aYour version is up-to-date!");
            } else {
                Debug.log("&cThere is new update!");
                Debug.log("&cCheck https://www.spigotmc.org/resources/pijoks-new-autosell.98930/");
            }
        });

        Debug.log("&aThank you for your support!");
    }

    @Override
    public void onDisable() {

        Debug.log("&cSaving data!");

        Controllers.getMinersController().saveAllMinersData(true);

        Debug.log("&aSaved!");

    }

    public boolean loadStuff(boolean reload){

        try{
            if(!reload){
                Controllers.create(this);
                Listeners.register(this);
                Commands.register(this);

                Hooks.create();
            }

            Settings.load();
            Lang.load();

            if(!reload){
                if(Settings.isDatabaseUsage()){
                    Controllers.getDatabaseManager().createTables();
                }
            }

            if(!reload){
                if(Settings.getComponentUsage().equalsIgnoreCase("RAM")){
                    Controllers.getMinersController().loadAllMinersData();
                }
            }

            Controllers.getSellingController().loadSettings();
            Controllers.getSellingController().loadGui();

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

    public static Economy getEconomy() {
        return economy;
    }
}
