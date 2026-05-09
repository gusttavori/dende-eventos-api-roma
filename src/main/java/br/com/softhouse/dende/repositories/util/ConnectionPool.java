package br.com.softhouse.dende.repositories.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionPool {

    private static final String URL = "jdbc:h2:mem:dende_eventos;DB_CLOSE_DELAY=-1;MODE=MySQL";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    static {
        try {
            Class.forName("org.h2.Driver");
            System.out.println("✅ H2 Database driver loaded successfully!");
            criarTabelas();
        } catch (ClassNotFoundException e) {
            System.err.println("❌ H2 Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void criarTabelas() throws SQLException {
        String[] sqls = {
                // Tabela id_generator (primeira, pois é referenciada)
                "CREATE TABLE IF NOT EXISTS id_generator (" +
                        "sequence_name VARCHAR(50) PRIMARY KEY, " +
                        "next_id INT NOT NULL DEFAULT 1)",

                // Inserir sequências
                "MERGE INTO id_generator (sequence_name, next_id) KEY(sequence_name) " +
                        "VALUES ('usuario', 1), ('evento', 1), ('ingresso', 1)",

                // Tabela usuarios
                "CREATE TABLE IF NOT EXISTS usuarios (" +
                        "id INT PRIMARY KEY, " +
                        "nome VARCHAR(100) NOT NULL, " +
                        "data_nascimento DATE NOT NULL, " +
                        "sexo CHAR(1) NOT NULL, " +
                        "email VARCHAR(100) UNIQUE NOT NULL, " +
                        "senha VARCHAR(255) NOT NULL, " +
                        "status_usuario BOOLEAN DEFAULT TRUE, " +
                        "tipo_usuario VARCHAR(20) NOT NULL, " +
                        "cnpj VARCHAR(18), " +
                        "razao_social VARCHAR(200), " +
                        "nome_fantasia VARCHAR(200), " +
                        "data_abertura DATE)",

                // Tabela eventos
                "CREATE TABLE IF NOT EXISTS eventos (" +
                        "id INT PRIMARY KEY, " +
                        "organizador_id INT NOT NULL, " +
                        "nome VARCHAR(200) NOT NULL, " +
                        "pagina_web VARCHAR(500), " +
                        "descricao TEXT, " +
                        "data_inicio TIMESTAMP NOT NULL, " +
                        "data_fim TIMESTAMP NOT NULL, " +
                        "tipo_evento VARCHAR(50) NOT NULL, " +
                        "modalidade VARCHAR(20) NOT NULL, " +
                        "preco_unitario_ingresso DECIMAL(10,2) NOT NULL, " +
                        "taxa_cancelamento_ingresso DECIMAL(5,2) DEFAULT 0.0, " +
                        "capacidade_maxima INT NOT NULL, " +
                        "local_evento VARCHAR(300), " +
                        "ativo BOOLEAN DEFAULT FALSE, " +
                        "evento_principal_id INT)",

                // Tabela ingressos
                "CREATE TABLE IF NOT EXISTS ingressos (" +
                        "id INT PRIMARY KEY, " +
                        "usuario_id INT NOT NULL, " +
                        "evento_id INT NOT NULL, " +
                        "status_ingresso VARCHAR(20) DEFAULT 'ATIVO', " +
                        "valor_pago DECIMAL(10,2) NOT NULL, " +
                        "data_compra TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
        };

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            for (String sql : sqls) {
                stmt.execute(sql);
            }
            System.out.println("✅ All tables created successfully!");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}