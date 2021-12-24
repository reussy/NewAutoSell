package pl.pijok.autosell;

import org.bukkit.configuration.file.YamlConfiguration;
import pl.pijok.autosell.essentials.ConfigUtils;

public class Settings {

    private static boolean autoSellEnabled;
    private static boolean countBlocks;
    private static boolean sellAllEnabled;

    public static void load(){

        YamlConfiguration configuration = ConfigUtils.load("config.yml");

        autoSellEnabled = configuration.getBoolean("autoSellEnabled");
        countBlocks = configuration.getBoolean("countingBlocksEnabled");
        sellAllEnabled = configuration.getBoolean("sellAllEnabled");

    }

    public static boolean isAutoSellEnabled() {
        return autoSellEnabled;
    }

    public static void setAutoSellEnabled(boolean autoSellEnabled) {
        Settings.autoSellEnabled = autoSellEnabled;
    }

    public static boolean isCountBlocks() {
        return countBlocks;
    }

    public static void setCountBlocks(boolean countBlocks) {
        Settings.countBlocks = countBlocks;
    }

    public static boolean isSellAllEnabled() {
        return sellAllEnabled;
    }

    public static void setSellAllEnabled(boolean sellAllEnabled) {
        Settings.sellAllEnabled = sellAllEnabled;
    }
}
