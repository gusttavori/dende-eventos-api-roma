package br.com.softhouse.dende.repositories;

import br.com.dende.softhouse.annotations.Component;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.enums.StatusIngresso;
import br.com.softhouse.dende.repositories.util.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class IngressoRepository implements CrudRepository<Ingresso, Long> {

    private final ConnectionPool connectionPool;

    private final RowMapper<Ingresso> mapper = rs -> {
        Ingresso ingresso = new Ingresso();
        ingresso.setId(rs.getLong("id"));
        ingresso.setUsuarioId(rs.getLong("usuario_id"));
        ingresso.setEventoId(rs.getLong("evento_id"));

        Long eventoVinculadoId = rs.getLong("evento_vinculado_id");
        if (!rs.wasNull()) {
            ingresso.setEventoVinculadoId(eventoVinculadoId);
        }

        ingresso.setCodigo(rs.getString("codigo"));

        Timestamp dataCompra = rs.getTimestamp("data_compra");
        if (dataCompra != null) {
            ingresso.setDataCompra(dataCompra.toLocalDateTime());
        }

        ingresso.setValorPago(rs.getDouble("valor_pago"));
        ingresso.setStatus(StatusIngresso.valueOf(rs.getString("status")));
        ingresso.setIngressoPrincipal(rs.getBoolean("ingresso_principal"));

        return ingresso;
    };

    public IngressoRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Ingresso salvar(Ingresso ingresso) {
        String sql = "INSERT INTO ingressos (usuario_id, evento_id, evento_vinculado_id, codigo, " +
                "data_compra, valor_pago, status, ingresso_principal) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, ingresso.getUsuarioId());
            stmt.setLong(2, ingresso.getEventoId());

            if (ingresso.getEventoVinculadoId() != null) {
                stmt.setLong(3, ingresso.getEventoVinculadoId());
            } else {
                stmt.setNull(3, Types.BIGINT);
            }

            stmt.setString(4, ingresso.getCodigo());
            stmt.setTimestamp(5, Timestamp.valueOf(ingresso.getDataCompra()));
            stmt.setDouble(6, ingresso.getValorPago());
            stmt.setString(7, ingresso.getStatus().name());
            stmt.setBoolean(8, ingresso.getIngressoPrincipal());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    ingresso.setId(rs.getLong(1));
                }
            }
            return ingresso;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar ingresso", e);
        }
    }

    @Override
    public Optional<Ingresso> buscarPorId(Long id) {
        String sql = "SELECT * FROM ingressos WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapper.mapear(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar ingresso por ID", e);
        }
    }

    public List<Ingresso> buscarPorUsuarioId(Long usuarioId) {
        String sql = "SELECT * FROM ingressos WHERE usuario_id = ?";
        List<Ingresso> ingressos = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ingressos.add(mapper.mapear(rs));
                }
            }
            return ingressos;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar ingressos por usuário", e);
        }
    }

    public List<Ingresso> buscarPorEventoId(Long eventoId) {
        String sql = "SELECT * FROM ingressos WHERE evento_id = ?";
        List<Ingresso> ingressos = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, eventoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ingressos.add(mapper.mapear(rs));
                }
            }
            return ingressos;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar ingressos por evento", e);
        }
    }

    public boolean existeIngressoAtivo(Long usuarioId, Long eventoId) {
        String sql = "SELECT 1 FROM ingressos WHERE usuario_id = ? AND evento_id = ? AND status = 'ATIVO'";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, usuarioId);
            stmt.setLong(2, eventoId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar ingresso ativo", e);
        }
    }

    public void reembolsarIngressosDoEvento(Long eventoId) {
        String sql = "UPDATE ingressos SET status = 'REEMBOLSADO' WHERE evento_id = ? AND status = 'ATIVO'";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, eventoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao reembolsar ingressos do evento", e);
        }
    }

    @Override
    public List<Ingresso> buscarTodos() {
        String sql = "SELECT * FROM ingressos";
        List<Ingresso> ingressos = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ingressos.add(mapper.mapear(rs));
            }
            return ingressos;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todos os ingressos", e);
        }
    }

    @Override
    public void atualizar(Ingresso ingresso) {
        String sql = "UPDATE ingressos SET usuario_id = ?, evento_id = ?, evento_vinculado_id = ?, " +
                "codigo = ?, data_compra = ?, valor_pago = ?, status = ?, ingresso_principal = ? WHERE id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, ingresso.getUsuarioId());
            stmt.setLong(2, ingresso.getEventoId());

            if (ingresso.getEventoVinculadoId() != null) {
                stmt.setLong(3, ingresso.getEventoVinculadoId());
            } else {
                stmt.setNull(3, Types.BIGINT);
            }

            stmt.setString(4, ingresso.getCodigo());
            stmt.setTimestamp(5, Timestamp.valueOf(ingresso.getDataCompra()));
            stmt.setDouble(6, ingresso.getValorPago());
            stmt.setString(7, ingresso.getStatus().name());
            stmt.setBoolean(8, ingresso.getIngressoPrincipal());
            stmt.setLong(9, ingresso.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar ingresso", e);
        }
    }

    @Override
    public void deletar(Long id) {
        String sql = "DELETE FROM ingressos WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar ingresso", e);
        }
    }

    @Override
    public boolean existePorId(Long id) {
        String sql = "SELECT 1 FROM ingressos WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência de ingresso", e);
        }
    }
}