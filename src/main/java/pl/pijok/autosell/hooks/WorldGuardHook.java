package pl.pijok.autosell.hooks;

import org.bukkit.Bukkit;
import pl.pijok.autosell.essentials.Debug;

public class WorldGuardHook {

    private static boolean enabled;

    public static void create(){
        if(Bukkit.getPluginManager().getPlugin("WorldGuard") != null){
            Debug.log("&aDetected world guard! Hooking!");
            enabled = true;
        }
        else{
            enabled = false;
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }
}
