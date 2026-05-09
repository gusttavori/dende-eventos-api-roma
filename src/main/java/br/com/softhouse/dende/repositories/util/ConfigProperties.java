package br.com.softhouse.dende.repositories.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigProperties {

    private final Properties properties = new Properties();

    // Valores padrão (fallback)
    private String datasourceUrl;
    private String datasourceUsername;
    private String datasourcePassword;
    private String datasourceDriverClassName;
    private int hikariMaximumPoolSize;
    private int hikariMinimumIdle;
    private long hikariConnectionTimeout;
    private long hikariIdleTimeout;
    private long hikariMaxLifetime;

    public ConfigProperties() {
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.err.println("Arquivo application.properties não encontrado. Usando valores padrão.");
                setDefaultValues();
                return;
            }

            properties.load(input);

            // Carrega os valores
            datasourceUrl = properties.getProperty("datasource.url", "jdbc:mysql://localhost:3306/dende_eventos?useSSL=false&serverTimezone=UTC");
            datasourceUsername = properties.getProperty("datasource.username", "root");
            datasourcePassword = properties.getProperty("datasource.password", "root");
            datasourceDriverClassName = properties.getProperty("datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");

            hikariMaximumPoolSize = Integer.parseInt(properties.getProperty("datasource.hikari.maximum-pool-size", "10"));
            hikariMinimumIdle = Integer.parseInt(properties.getProperty("datasource.hikari.minimum-idle", "2"));
            hikariConnectionTimeout = Long.parseLong(properties.getProperty("datasource.hikari.connection-timeout", "30000"));
            hikariIdleTimeout = Long.parseLong(properties.getProperty("datasource.hikari.idle-timeout", "600000"));
            hikariMaxLifetime = Long.parseLong(properties.getProperty("datasource.hikari.max-lifetime", "1800000"));

        } catch (IOException e) {
            System.err.println("Erro ao carregar application.properties: " + e.getMessage());
            setDefaultValues();
        }
    }

    private void setDefaultValues() {
        datasourceUrl = "jdbc:mysql://localhost:3306/dende_eventos?useSSL=false&serverTimezone=UTC";
        datasourceUsername = "root";
        datasourcePassword = "root";
        datasourceDriverClassName = "com.mysql.cj.jdbc.Driver";
        hikariMaximumPoolSize = 10;
        hikariMinimumIdle = 2;
        hikariConnectionTimeout = 30000;
        hikariIdleTimeout = 600000;
        hikariMaxLifetime = 1800000;
    }

    // Getters
    public String getDatasourceUrl() { return datasourceUrl; }
    public String getDatasourceUsername() { return datasourceUsername; }
    public String getDatasourcePassword() { return datasourcePassword; }
    public String getDatasourceDriverClassName() { return datasourceDriverClassName; }
    public int getHikariMaximumPoolSize() { return hikariMaximumPoolSize; }
    public int getHikariMinimumIdle() { return hikariMinimumIdle; }
    public long getHikariConnectionTimeout() { return hikariConnectionTimeout; }
    public long getHikariIdleTimeout() { return hikariIdleTimeout; }
    public long getHikariMaxLifetime() { return hikariMaxLifetime; }
}