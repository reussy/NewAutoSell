package pl.pijok.autosell.database;

public class PreparedStatements {

    public static String createTableMiners = "CREATE TABLE IF NOT EXISTS autosell " +
            "(" +
            "name varchar(36)," +
            "blocksMined int(11)," +
            "autosell boolean" +
            ");";

    public static String getPlayer = "SELECT * FROM autosell WHERE name=?";
    public static String getAllPlayers = "SELECT * FROM autosell";
    public static String insertPlayer = "INSERT INTO autosell VALUES(?,?,?)";
    public static String updatePlayer = "UPDATE autosell SET blocksMined=?, autosell=? WHERE name=?";
}
