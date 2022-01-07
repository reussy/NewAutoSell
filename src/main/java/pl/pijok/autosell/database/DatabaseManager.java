package pl.pijok.autosell.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import pl.pijok.autosell.AutoSell;
import pl.pijok.autosell.settings.SqlSettings;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {

    private final AutoSell plugin;
    private SqlSettings sqlSettings;
    private DataSource hikariDataSource;
    private Properties properties;

    public DatabaseManager(AutoSell plugin){
        this.plugin = plugin;
        properties = new Properties();
    }

    public void createDataSource(){
        hikariDataSource = new HikariDataSource(new HikariConfig(properties));
    }

    public void createTables(){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try(Connection connection = hikariDataSource.getConnection()){
                    PreparedStatement createTable = connection.prepareStatement(PreparedStatements.createTableMiners);
                    createTable.execute();
                    createTable.close();

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try(Connection connection = hikariDataSource.getConnection()){

                    PreparedStatement createColumns = connection.prepareStatement(PreparedStatements.createFilterColumn);
                    createColumns.execute();
                    createColumns.close();

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
    }

    public SqlSettings getSqlSettings() {
        return sqlSettings;
    }

    public void setSqlSettings(SqlSettings sqlSettings) {
        this.sqlSettings = sqlSettings;
    }

    public DataSource getHikariDataSource() {
        return hikariDataSource;
    }

    public void setHikariDataSource(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    public Properties getProperties() {
        return properties;
    }
}
