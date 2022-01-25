package pl.pijok.autosell.hooks;

import at.pcgamingfreaks.Minepacks.Bukkit.API.MinepacksPlugin;
import org.bukkit.Bukkit;
import pl.pijok.autosell.essentials.Debug;

public class MinepacksHook {

    private static MinepacksPlugin minepacksPlugin;
    private static boolean enabled;

    //Settings
    private static boolean sellFromBackpack;

    public static void create(){
        if(Bukkit.getPluginManager().getPlugin("Minepacks") != null){
            Debug.log("&aDetected Minepacks! Hooking!");
            minepacksPlugin = (MinepacksPlugin) Bukkit.getPluginManager().getPlugin("Minepacks");
            enabled = true;
        }
        else{
            enabled = false;
        }
    }

    public static MinepacksPlugin getMinepacksPlugin() {
        return minepacksPlugin;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean isSellFromBackpack() {
        return sellFromBackpack;
    }

    public static void setSellFromBackpack(boolean sellFromBackpack) {
        MinepacksHook.sellFromBackpack = sellFromBackpack;
    }
}
