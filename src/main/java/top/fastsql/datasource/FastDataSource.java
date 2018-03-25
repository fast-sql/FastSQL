package top.fastsql.datasource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class FastDataSource implements DataSource {

    private Driver driver;
    private String url;
    private String username;
    private String password;


    public FastDataSource(Driver driver, String url, String username, String password) {
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Connection getConnection() {
        Properties properties = new Properties();
        properties.setProperty("user", username);
        properties.setProperty("password", password);
        try {
            return driver.connect(url, properties);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException, UnsupportedOperationException {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException, UnsupportedOperationException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException, UnsupportedOperationException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException, UnsupportedOperationException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException, UnsupportedOperationException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException, UnsupportedOperationException {

    }

    @Override
    public int getLoginTimeout() throws SQLException, UnsupportedOperationException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException, UnsupportedOperationException {
        return null;
    }
}
