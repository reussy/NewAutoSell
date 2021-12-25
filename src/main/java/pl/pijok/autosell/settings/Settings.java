package pl.pijok.autosell.settings;

import org.bukkit.configuration.file.YamlConfiguration;
import pl.pijok.autosell.essentials.ConfigUtils;

public class Settings {

    private static boolean databaseUsage;
    private static SqlSettings sqlSettings;
    private static String componentUsage;
    private static boolean autoSellEnabled;
    private static boolean countBlocks;
    private static boolean sellAllEnabled;

    public static void load(){

        YamlConfiguration configuration = ConfigUtils.load("config.yml");

        databaseUsage = configuration.getBoolean("database");
        if(databaseUsage){
            sqlSettings = new SqlSettings(
                    configuration.getString("sqlSettings.user"),
                    configuration.getString("sqlSettings.password"),
                    configuration.getString("sqlSettings.port"),
                    configuration.getString("sqlSettings.host"),
                    configuration.getString("sqlSettings.database")
            );
        }
        componentUsage = configuration.getString("componentUsage");
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

    public static String getComponentUsage() {
        return componentUsage;
    }

    public static void setComponentUsage(String usage) {
        Settings.componentUsage = usage;
    }

    public static boolean isDatabaseUsage() {
        return databaseUsage;
    }

    public static void setDatabaseUsage(boolean databaseUsage) {
        Settings.databaseUsage = databaseUsage;
    }

    public static SqlSettings getSqlSettings() {
        return sqlSettings;
    }

    public static void setSqlSettings(SqlSettings sqlSettings) {
        Settings.sqlSettings = sqlSettings;
    }
}
