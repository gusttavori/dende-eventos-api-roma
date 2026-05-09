package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.exceptions.DatabaseException;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.EnumModel.StatusIngresso;
import br.com.softhouse.dende.repositories.core.CrudRepository;
import br.com.softhouse.dende.repositories.core.RowMapper;
import br.com.softhouse.dende.repositories.util.ConnectionPool;
import br.com.softhouse.dende.repositories.util.JdbcUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IngressoRepository implements CrudRepository<Ingresso, Integer> {

    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    private final EventoRepository eventoRepository = new EventoRepository();

    private final RowMapper<Ingresso> ingressoRowMapper = rs -> {
        Ingresso ingresso = new Ingresso();
        ingresso.setId(rs.getInt("id"));
        ingresso.setValorPago(rs.getDouble("valor_pago"));
        ingresso.setStatusIngresso(StatusIngresso.valueOf(rs.getString("status_ingresso")));
        ingresso.setDataCompra(rs.getTimestamp("data_compra").toLocalDateTime());

        int usuarioId = rs.getInt("usuario_id");
        usuarioRepository.findById(usuarioId).ifPresent(ingresso::setUsuario);

        int eventoId = rs.getInt("evento_id");
        eventoRepository.findById(eventoId).ifPresent(ingresso::setEvento);

        return ingresso;
    };

    private int getNextId() {
        String sql = "UPDATE id_generator SET next_id = last_insert_id(next_id + 1) WHERE sequence_name = 'ingresso'";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();

            String selectSql = "SELECT next_id FROM id_generator WHERE sequence_name = 'ingresso'";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                 ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("next_id");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao gerar ID para ingresso", e);
        }
        return (int) (System.currentTimeMillis() % 100000);
    }

    @Override
    public Ingresso save(Ingresso entity) {
        String sql = "INSERT INTO ingressos (id, usuario_id, evento_id, status_ingresso, valor_pago, data_compra) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (entity.getId() == 0) {
                entity.setId(getNextId());
            }

            stmt.setInt(1, entity.getId());
            stmt.setInt(2, entity.getUsuario().getId());
            stmt.setInt(3, entity.getEvento().getId());
            stmt.setString(4, entity.getStatusIngresso().name());
            stmt.setDouble(5, entity.getValorPago());
            stmt.setTimestamp(6, Timestamp.valueOf(entity.getDataCompra()));

            stmt.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao salvar ingresso", e);
        }
    }

    @Override
    public Optional<Ingresso> findById(Integer id) {
        String sql = "SELECT * FROM ingressos WHERE id = ?";

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(ingressoRowMapper.mapRow(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar ingresso por ID", e);
        }
    }

    @Override
    public List<Ingresso> findAll() {
        String sql = "SELECT * FROM ingressos";
        List<Ingresso> ingressos = new ArrayList<>();

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ingressos.add(ingressoRowMapper.mapRow(rs));
            }
            return ingressos;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao listar ingressos", e);
        }
    }

    public List<Ingresso> findByUsuarioId(int usuarioId) {
        String sql = "SELECT * FROM ingressos WHERE usuario_id = ?";
        List<Ingresso> ingressos = new ArrayList<>();

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ingressos.add(ingressoRowMapper.mapRow(rs));
            }
            return ingressos;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao listar ingressos por usuário", e);
        }
    }

    public List<Ingresso> findByEventoId(int eventoId) {
        String sql = "SELECT * FROM ingressos WHERE evento_id = ?";
        List<Ingresso> ingressos = new ArrayList<>();

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ingressos.add(ingressoRowMapper.mapRow(rs));
            }
            return ingressos;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao listar ingressos por evento", e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM ingressos WHERE id = ?";
        JdbcUtils.executeUpdate(sql, id);
    }

    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT 1 FROM ingressos WHERE id = ?";

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao verificar existência de ingresso", e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM ingressos";

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao contar ingressos", e);
        }
    }

    public void updateStatus(int ingressoId, StatusIngresso status) {
        String sql = "UPDATE ingressos SET status_ingresso = ? WHERE id = ?";

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setInt(2, ingressoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao atualizar status do ingresso", e);
        }
    }
}