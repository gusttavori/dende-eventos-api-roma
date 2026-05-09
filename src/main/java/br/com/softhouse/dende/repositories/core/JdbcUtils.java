package br.com.softhouse.dende.repositories.util;

import br.com.softhouse.dende.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcUtils {

    public static void closeQuietly(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                // Log silencioso
            }
        }
    }

    public static Long getGeneratedId(PreparedStatement stmt) {
        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao obter ID gerado", e);
        }
    }

    public static void executeUpdate(String sql, Object... params) {
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao executar update: " + sql, e);
        }
    }
}