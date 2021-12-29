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

    private SqlSettings sqlSettings;
    private DataSource hikariDataSource;
    private Properties properties;

    public DatabaseManager(){
        properties = new Properties();
    }

    public void createDataSource(){
        /*hikariDataSource = new HikariDataSource();
        hikariDataSource.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikariDataSource.addDataSourceProperty("serverName", sqlSettings.getHost());
        hikariDataSource.addDataSourceProperty("port", sqlSettings.getPort());
        hikariDataSource.addDataSourceProperty("databaseName", sqlSettings.getDatabase());
        hikariDataSource.addDataSourceProperty("user", sqlSettings.getUser());
        hikariDataSource.addDataSourceProperty("password", sqlSettings.getPassword());*/

        hikariDataSource = new HikariDataSource(new HikariConfig(properties));
    }

    public void createTables(){
        Bukkit.getScheduler().runTaskAsynchronously(AutoSell.getInstance(), new Runnable() {
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
