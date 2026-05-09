package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.exceptions.DatabaseException;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.EnumModel.ModalidadeEvento;
import br.com.softhouse.dende.model.EnumModel.TipoEvento;
import br.com.softhouse.dende.repositories.core.CrudRepository;
import br.com.softhouse.dende.repositories.core.RowMapper;
import br.com.softhouse.dende.repositories.util.ConnectionPool;
import br.com.softhouse.dende.repositories.util.JdbcUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EventoRepository implements CrudRepository<Evento, Integer> {

    private final UsuarioRepository usuarioRepository = new UsuarioRepository();

    private final RowMapper<Evento> eventoRowMapper = rs -> {
        Evento evento = new Evento();
        evento.setId(rs.getInt("id"));
        evento.setNome(rs.getString("nome"));
        evento.setPaginaWeb(rs.getString("pagina_web"));
        evento.setDescricao(rs.getString("descricao"));
        evento.setDataInicio(rs.getTimestamp("data_inicio").toLocalDateTime());
        evento.setDataFim(rs.getTimestamp("data_fim").toLocalDateTime());
        evento.setTipoEvento(TipoEvento.valueOf(rs.getString("tipo_evento")));
        evento.setModalidade(ModalidadeEvento.valueOf(rs.getString("modalidade")));
        evento.setPrecoUnitarioIngresso(rs.getDouble("preco_unitario_ingresso"));
        evento.setTaxaCancelamentoIngresso(rs.getDouble("taxa_cancelamento_ingresso"));
        evento.setCapacidadeMaxima(rs.getInt("capacidade_maxima"));
        evento.setLocal(rs.getString("local_evento"));
        evento.setAtivo(rs.getBoolean("ativo"));

        int organizadorId = rs.getInt("organizador_id");
        usuarioRepository.findById(organizadorId).ifPresent(org ->
                evento.setOrganizador((Organizador) org));

        int eventoPrincipalId = rs.getInt("evento_principal_id");
        if (!rs.wasNull() && eventoPrincipalId > 0) {
            findById(eventoPrincipalId).ifPresent(evento::setEventoPrincipal);
        }

        return evento;
    };

    private int getNextId() {
        String sql = "UPDATE id_generator SET next_id = last_insert_id(next_id + 1) WHERE sequence_name = 'evento'";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();

            String selectSql = "SELECT next_id FROM id_generator WHERE sequence_name = 'evento'";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                 ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("next_id");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao gerar ID para evento", e);
        }
        return (int) (System.currentTimeMillis() % 100000);
    }

    @Override
    public Evento save(Evento entity) {
        String sql = "INSERT INTO eventos (id, organizador_id, nome, pagina_web, descricao, " +
                "data_inicio, data_fim, tipo_evento, modalidade, preco_unitario_ingresso, " +
                "taxa_cancelamento_ingresso, capacidade_maxima, local_evento, ativo, evento_principal_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (entity.getId() == 0) {
                entity.setId(getNextId());
            }

            stmt.setInt(1, entity.getId());
            stmt.setInt(2, entity.getOrganizador().getId());
            stmt.setString(3, entity.getNome());
            stmt.setString(4, entity.getPaginaWeb());
            stmt.setString(5, entity.getDescricao());
            stmt.setTimestamp(6, Timestamp.valueOf(entity.getDataInicio()));
            stmt.setTimestamp(7, Timestamp.valueOf(entity.getDataFim()));
            stmt.setString(8, entity.getTipoEvento().name());
            stmt.setString(9, entity.getModalidade().name());
            stmt.setDouble(10, entity.getPrecoUnitarioIngresso());
            stmt.setDouble(11, entity.getTaxaCancelamentoIngresso());
            stmt.setInt(12, entity.getCapacidadeMaxima());
            stmt.setString(13, entity.getLocal());
            stmt.setBoolean(14, entity.isAtivo());

            if (entity.getEventoPrincipal() != null) {
                stmt.setInt(15, entity.getEventoPrincipal().getId());
            } else {
                stmt.setNull(15, Types.INTEGER);
            }

            stmt.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao salvar evento", e);
        }
    }

    @Override
    public Optional<Evento> findById(Integer id) {
        String sql = "SELECT * FROM eventos WHERE id = ?";

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(eventoRowMapper.mapRow(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar evento por ID", e);
        }
    }

    @Override
    public List<Evento> findAll() {
        String sql = "SELECT * FROM eventos";
        List<Evento> eventos = new ArrayList<>();

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                eventos.add(eventoRowMapper.mapRow(rs));
            }
            return eventos;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao listar eventos", e);
        }
    }

    public List<Evento> findEventosAtivos() {
        String sql = "SELECT * FROM eventos WHERE ativo = TRUE AND data_fim > NOW()";
        List<Evento> eventos = new ArrayList<>();

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                eventos.add(eventoRowMapper.mapRow(rs));
            }
            return eventos;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao listar eventos ativos", e);
        }
    }

    public List<Evento> findByOrganizadorId(int organizadorId) {
        String sql = "SELECT * FROM eventos WHERE organizador_id = ?";
        List<Evento> eventos = new ArrayList<>();

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, organizadorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                eventos.add(eventoRowMapper.mapRow(rs));
            }
            return eventos;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao listar eventos por organizador", e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM eventos WHERE id = ?";
        JdbcUtils.executeUpdate(sql, id);
    }

    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT 1 FROM eventos WHERE id = ?";

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao verificar existência de evento", e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM eventos";

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao contar eventos", e);
        }
    }

    public void update(Evento evento) {
        String sql = "UPDATE eventos SET nome = ?, pagina_web = ?, descricao = ?, " +
                "data_inicio = ?, data_fim = ?, tipo_evento = ?, modalidade = ?, " +
                "preco_unitario_ingresso = ?, taxa_cancelamento_ingresso = ?, " +
                "capacidade_maxima = ?, local_evento = ?, ativo = ? WHERE id = ?";

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, evento.getNome());
            stmt.setString(2, evento.getPaginaWeb());
            stmt.setString(3, evento.getDescricao());
            stmt.setTimestamp(4, Timestamp.valueOf(evento.getDataInicio()));
            stmt.setTimestamp(5, Timestamp.valueOf(evento.getDataFim()));
            stmt.setString(6, evento.getTipoEvento().name());
            stmt.setString(7, evento.getModalidade().name());
            stmt.setDouble(8, evento.getPrecoUnitarioIngresso());
            stmt.setDouble(9, evento.getTaxaCancelamentoIngresso());
            stmt.setInt(10, evento.getCapacidadeMaxima());
            stmt.setString(11, evento.getLocal());
            stmt.setBoolean(12, evento.isAtivo());
            stmt.setInt(13, evento.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao atualizar evento", e);
        }
    }
}