package br.com.softhouse.dende.repositories.util;

import br.com.dende.softhouse.annotations.Component;
import java.io.InputStream;
import java.util.Properties;

@Component
public class ConfigProperties {

    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private String dbDriverClassName;
    private int hikariMaxPoolSize;
    private int hikariMinimumIdle;
    private long hikariConnectionTimeout;

    public ConfigProperties() {
        carregarPropriedades();
    }

    private void carregarPropriedades() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            Properties props = new Properties();
            props.load(input);

            this.dbUrl = props.getProperty("datasource.url");
            this.dbUsername = props.getProperty("datasource.username");
            this.dbPassword = props.getProperty("datasource.password");
            this.dbDriverClassName = props.getProperty("datasource.driver-class-name");
            this.hikariMaxPoolSize = Integer.parseInt(props.getProperty("datasource.hikari.maximum-pool-size", "10"));
            this.hikariMinimumIdle = Integer.parseInt(props.getProperty("datasource.hikari.minimum-idle", "2"));
            this.hikariConnectionTimeout = Long.parseLong(props.getProperty("datasource.hikari.connection-timeout", "30000"));

        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar arquivo application.properties", e);
        }
    }

    // Getters
    public String getDbUrl() { return dbUrl; }
    public String getDbUsername() { return dbUsername; }
    public String getDbPassword() { return dbPassword; }
    public String getDbDriverClassName() { return dbDriverClassName; }
    public int getHikariMaxPoolSize() { return hikariMaxPoolSize; }
    public int getHikariMinimumIdle() { return hikariMinimumIdle; }
    public long getHikariConnectionTimeout() { return hikariConnectionTimeout; }
}