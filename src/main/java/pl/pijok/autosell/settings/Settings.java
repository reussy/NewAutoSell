package pl.pijok.autosell.settings;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.pijok.autosell.Controllers;
import pl.pijok.autosell.database.DatabaseManager;
import pl.pijok.autosell.essentials.ConfigUtils;
import pl.pijok.autosell.essentials.Debug;
import pl.pijok.autosell.essentials.Utils;
import pl.pijok.autosell.miner.Range;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Settings {

    //Saving settings
    private static boolean databaseUsage;
    private static SqlSettings sqlSettings;
    private static String componentUsage;

    //Counting blocks settings
    private static boolean countBlocks;
    private static boolean countOnlyOnMiningTools;
    private static List<Material> miningTools;

    //Selling settings
    private static boolean sellAllEnabled;
    private static boolean autoSellEnabled;

    //Mining settings
    private static boolean ignoreWorldGuard;
    private static boolean dropToInventory;
    private static HashMap<Integer, Range> fortuneDrops;
    private static HashMap<Material, Material> oreDrops;

    //Boosters settings
    private static boolean boosterOnlyOnline;
    private static String newBoosterAction;

    public static void load(){

        YamlConfiguration configuration = ConfigUtils.load("config.yml");

        databaseUsage = configuration.getBoolean("database");
        if(databaseUsage){
            DatabaseManager databaseManager = Controllers.getDatabaseManager();

            String databaseType = configuration.getString("sqlSettings.type");

            if(databaseType.equalsIgnoreCase("MariaDB")){
                databaseManager.getProperties().setProperty("dataSourceClassName", "org.mariadb.jdbc.MariaDbDataSource");
            }
            else if(databaseType.equalsIgnoreCase("Oracle")){
                databaseManager.getProperties().setProperty("dataSourceClassName", "oracle.jdbc.pool.OracleDataSource");
            }
            else if(databaseType.equalsIgnoreCase("PostgreSQL")){
                databaseManager.getProperties().setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
            }
            else if(databaseType.equalsIgnoreCase("H2")){
                databaseManager.getProperties().setProperty("dataSourceClassName", "org.h2.jdbcx.JdbcDataSource");
            }

            databaseManager.getProperties().setProperty("dataSource.serverName", configuration.getString("sqlSettings.host"));
            databaseManager.getProperties().setProperty("dataSource.portNumber", configuration.getString("sqlSettings.port"));
            databaseManager.getProperties().setProperty("dataSource.user", configuration.getString("sqlSettings.user"));
            databaseManager.getProperties().setProperty("dataSource.password", configuration.getString("sqlSettings.password"));
            databaseManager.getProperties().setProperty("dataSource.databaseName", configuration.getString("sqlSettings.database"));

            Controllers.getDatabaseManager().setSqlSettings(sqlSettings);
            if(Controllers.getDatabaseManager().getHikariDataSource() != null){
                try{
                    Controllers.getDatabaseManager().getHikariDataSource().getConnection().close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            }
            Controllers.getDatabaseManager().createDataSource();
        }

        componentUsage = configuration.getString("componentUsage");
        autoSellEnabled = configuration.getBoolean("autoSellEnabled");
        countBlocks = configuration.getBoolean("countingBlocksEnabled");
        countOnlyOnMiningTools = configuration.getBoolean("countOnlyOnMiningTools");
        miningTools = new ArrayList<>();
        for(String materialName : configuration.getStringList("miningTools")){
            if(!Utils.isMaterial(materialName)){
                Debug.log("&cWrong material name " + materialName + " in config.yml -> miningTools section");
                continue;
            }

            miningTools.add(Material.valueOf(materialName));
        }
        sellAllEnabled = configuration.getBoolean("sellAllEnabled");

        ignoreWorldGuard = configuration.getBoolean("ignoreWorldGuard");

        //boosterOnlyOnline = configuration.getBoolean("boosterOnlyOnline");
        //newBoosterAction = configuration.getString("newBoosterAction");

        dropToInventory = configuration.getBoolean("dropToInventory");

        fortuneDrops = new HashMap<>();
        for(String fortuneLevel : configuration.getConfigurationSection("fortuneDrops").getKeys(false)){
            int min = configuration.getInt("fortuneDrops." + fortuneLevel + ".min");
            int max = configuration.getInt("fortuneDrops." + fortuneLevel + ".max");

            fortuneDrops.put(Integer.parseInt(fortuneLevel), new Range(min, max));
        }

        oreDrops = new HashMap<>();
        for(String materialName : configuration.getConfigurationSection("oreDrops").getKeys(false)){
            if(!Utils.isMaterial(materialName)){
                Debug.sendError("&cWrong material name in config.yml -> oreDrops " + materialName);
                continue;
            }

            String resultName = configuration.getString("oreDrops." + materialName);

            if(!Utils.isMaterial(resultName)){
                Debug.sendError("&cWrong material name in config.yml -> oreDrops " + resultName);
                continue;
            }

            oreDrops.put(Material.valueOf(materialName), Material.valueOf(resultName));
        }

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

    public static boolean isCountOnlyOnMiningTools() {
        return countOnlyOnMiningTools;
    }

    public static void setCountOnlyOnMiningTools(boolean countOnlyOnMiningTools) {
        Settings.countOnlyOnMiningTools = countOnlyOnMiningTools;
    }

    public static List<Material> getMiningTools() {
        return miningTools;
    }

    public static void setMiningTools(List<Material> miningTools) {
        Settings.miningTools = miningTools;
    }

    public static boolean isIgnoreWorldGuard() {
        return ignoreWorldGuard;
    }

    public static void setIgnoreWorldGuard(boolean ignoreWorldGuard) {
        Settings.ignoreWorldGuard = ignoreWorldGuard;
    }

    public static boolean isBoosterOnlyOnline() {
        return boosterOnlyOnline;
    }

    public static String getNewBoosterAction() {
        return newBoosterAction;
    }

    public static HashMap<Integer, Range> getFortuneDrops() {
        return fortuneDrops;
    }

    public static boolean isDropToInventory() {
        return dropToInventory;
    }

    public static Range getFortuneDrop(int level){
        return fortuneDrops.getOrDefault(level, null);
    }

    public static HashMap<Material, Material> getOreDrops() {
        return oreDrops;
    }
}
