package config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// IMPORT CHUẨN DÀNH CHO SPRING JDBC & JDK
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.Connection;
import java.sql.SQLException;

@Configuration
public class DatabaseConnection {

    private static DataSource dataSource;

    @Value("${spring.datasource.url:jdbc:postgresql://localhost:5432/shopmanager}")
    private String url;

    @Value("${spring.datasource.username:postgres}")
    private String username;

    @Value("${spring.datasource.password:postgres}")
    private String password;

    @Value("${spring.datasource.driver-class-name:org.postgresql.Driver}")
    private String driverClassName;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(driverClassName);
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);

        DatabaseConnection.dataSource = ds;
        return ds;
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource chua duoc khoi tao! Hay chac chan ung dung Spring Boot da khoi chay thanh cong.");
        }
        return dataSource.getConnection();
    }
}