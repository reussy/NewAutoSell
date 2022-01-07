package pl.pijok.autosell.database;

public class PreparedStatements {

    //Miners
    public static String createTableMiners = "CREATE TABLE IF NOT EXISTS autosell " +
            "(" +
            "name varchar(36) PRIMARY KEY," +
            "blocksMined int(11)," +
            "autosell boolean," +
            "filter TEXT NULL" +
            ");";

    public static String createFilterColumn = "ALTER TABLE autosell ADD COLUMN IF NOT EXISTS filter TEXT NULL";

    public static String getPlayer = "SELECT * FROM autosell WHERE name=?";
    public static String getAllPlayers = "SELECT * FROM autosell";
    public static String updatePlayer = "INSERT INTO autosell VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE blocksMined=?, autosell=?, filter=?";

    //Boosters
    /*
        Will be included in future updates
     */
    public static String createTableBoosters = "CREATE TABLE IF NOT EXISTS boosters" +
            "(" +
            "name varchar(36) PRIMARY KEY," +
            "multiplier double(11)," +
            "duration int(11)" +
            ");";

    public static String getBooster = "SELECT * FROM boosters WHERE name=?";
    public static String getAllBoosters = "SELECT * FROM boosters";
    public static String updateBooster = "INSERT INTO boosters VALUES(?,?,?) ON DUPLICATE KEY UPDATE multiplier=?, duration=?";
    public static String deleteBooster = "DELETE FROM boosters WHERE name=?";
    public static String clearBoosterTable = "DELETE FROM boosters";


}
