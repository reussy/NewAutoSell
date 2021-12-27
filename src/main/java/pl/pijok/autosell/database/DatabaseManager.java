package pl.pijok.autosell.database;

import com.zaxxer.hikari.HikariDataSource;
import pl.pijok.autosell.settings.SqlSettings;

public class DatabaseManager {

    private SqlSettings sqlSettings;
    private HikariDataSource hikariDataSource;

    public DatabaseManager(){

    }

    public void createDataSource(){
        hikariDataSource = new HikariDataSource();
        hikariDataSource.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikariDataSource.addDataSourceProperty("serverName", sqlSettings.getHost());
        hikariDataSource.addDataSourceProperty("port", sqlSettings.getPort());
        hikariDataSource.addDataSourceProperty("databaseName", sqlSettings.getDatabase());
        hikariDataSource.addDataSourceProperty("user", sqlSettings.getUser());
        hikariDataSource.addDataSourceProperty("password", sqlSettings.getPassword());
    }

    public SqlSettings getSqlSettings() {
        return sqlSettings;
    }

    public void setSqlSettings(SqlSettings sqlSettings) {
        this.sqlSettings = sqlSettings;
    }

    public HikariDataSource getHikariDataSource() {
        return hikariDataSource;
    }

    public void setHikariDataSource(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }
}
