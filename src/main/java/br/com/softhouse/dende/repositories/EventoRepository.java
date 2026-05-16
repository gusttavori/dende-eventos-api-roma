package br.com.softhouse.dende.repositories;

import br.com.dende.softhouse.annotations.Component;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.enums.ModalidadeEvento;
import br.com.softhouse.dende.model.enums.TipoEvento;
import br.com.softhouse.dende.repositories.util.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class EventoRepository implements CrudRepository<Evento, Long> {

    private final ConnectionPool connectionPool;

    private final RowMapper<Evento> mapper = rs -> {
        Evento evento = new Evento();
        evento.setId(rs.getLong("id"));
        evento.setOrganizadorId(rs.getLong("organizador_id"));
        evento.setNome(rs.getString("nome"));
        evento.setPagina(rs.getString("pagina"));
        evento.setDescricao(rs.getString("descricao"));

        Timestamp dataInicio = rs.getTimestamp("data_inicio");
        if (dataInicio != null) {
            evento.setDataInicio(dataInicio.toLocalDateTime());
        }

        Timestamp dataFinal = rs.getTimestamp("data_final");
        if (dataFinal != null) {
            evento.setDataFinal(dataFinal.toLocalDateTime());
        }

        evento.setTipoEvento(TipoEvento.valueOf(rs.getString("tipo_evento")));

        Long eventoPrincipalId = rs.getLong("evento_principal_id");
        if (!rs.wasNull()) {
            evento.setEventoPrincipalId(eventoPrincipalId);
        }

        evento.setModalidade(ModalidadeEvento.valueOf(rs.getString("modalidade")));
        evento.setCapacidadeMaxima(rs.getInt("capacidade_maxima"));
        evento.setLocal(rs.getString("local"));
        evento.setAtivo(rs.getBoolean("ativo"));
        evento.setPrecoIngresso(rs.getDouble("preco_ingresso"));
        evento.setEstornaCancelamento(rs.getBoolean("estorna_cancelamento"));
        evento.setTaxaEstorno(rs.getDouble("taxa_estorno"));
        evento.setIngressosVendidos(rs.getInt("ingressos_vendidos"));

        return evento;
    };

    public EventoRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Evento salvar(Evento evento) {
        String sql = "INSERT INTO eventos (organizador_id, nome, pagina, descricao, data_inicio, data_final, " +
                "tipo_evento, evento_principal_id, modalidade, capacidade_maxima, local, ativo, " +
                "preco_ingresso, estorna_cancelamento, taxa_estorno, ingressos_vendidos) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, evento.getOrganizadorId());
            stmt.setString(2, evento.getNome());
            stmt.setString(3, evento.getPagina());
            stmt.setString(4, evento.getDescricao());
            stmt.setTimestamp(5, Timestamp.valueOf(evento.getDataInicio()));
            stmt.setTimestamp(6, Timestamp.valueOf(evento.getDataFinal()));
            stmt.setString(7, evento.getTipoEvento().name());

            if (evento.getEventoPrincipalId() != null) {
                stmt.setLong(8, evento.getEventoPrincipalId());
            } else {
                stmt.setNull(8, Types.BIGINT);
            }

            stmt.setString(9, evento.getModalidade().name());
            stmt.setInt(10, evento.getCapacidadeMaxima());
            stmt.setString(11, evento.getLocal());
            stmt.setBoolean(12, evento.getAtivo());
            stmt.setDouble(13, evento.getPrecoIngresso());
            stmt.setBoolean(14, evento.getEstornaCancelamento());
            stmt.setDouble(15, evento.getTaxaEstorno());
            stmt.setInt(16, evento.getIngressosVendidos());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    evento.setId(rs.getLong(1));
                }
            }
            return evento;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar evento", e);
        }
    }

    @Override
    public Optional<Evento> buscarPorId(Long id) {
        String sql = "SELECT * FROM eventos WHERE id = ?";
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
            throw new RuntimeException("Erro ao buscar evento por ID", e);
        }
    }

    public List<Evento> buscarPorOrganizadorId(Long organizadorId) {
        String sql = "SELECT * FROM eventos WHERE organizador_id = ?";
        List<Evento> eventos = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, organizadorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    eventos.add(mapper.mapear(rs));
                }
            }
            return eventos;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar eventos por organizador", e);
        }
    }

    public List<Evento> listarAtivos() {
        String sql = "SELECT * FROM eventos WHERE ativo = true";
        List<Evento> eventos = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Evento evento = mapper.mapear(rs);
                // Filtros adicionais em memória (já que dependem de lógica de negócio)
                if (!evento.eventoJaAconteceu() && evento.temIngressosDisponiveis()) {
                    eventos.add(evento);
                }
            }
            return eventos;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar eventos ativos", e);
        }
    }

    public boolean organizadorTemEventosAtivosOuEmExecucao(Long organizadorId) {
        String sql = "SELECT COUNT(*) FROM eventos WHERE organizador_id = ? AND (ativo = true OR data_final > NOW())";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, organizadorId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar eventos do organizador", e);
        }
    }

    @Override
    public List<Evento> buscarTodos() {
        String sql = "SELECT * FROM eventos";
        List<Evento> eventos = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                eventos.add(mapper.mapear(rs));
            }
            return eventos;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todos os eventos", e);
        }
    }

    @Override
    public void atualizar(Evento evento) {
        String sql = "UPDATE eventos SET organizador_id = ?, nome = ?, pagina = ?, descricao = ?, " +
                "data_inicio = ?, data_final = ?, tipo_evento = ?, evento_principal_id = ?, " +
                "modalidade = ?, capacidade_maxima = ?, local = ?, ativo = ?, preco_ingresso = ?, " +
                "estorna_cancelamento = ?, taxa_estorno = ?, ingressos_vendidos = ? WHERE id = ?";

        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, evento.getOrganizadorId());
            stmt.setString(2, evento.getNome());
            stmt.setString(3, evento.getPagina());
            stmt.setString(4, evento.getDescricao());
            stmt.setTimestamp(5, Timestamp.valueOf(evento.getDataInicio()));
            stmt.setTimestamp(6, Timestamp.valueOf(evento.getDataFinal()));
            stmt.setString(7, evento.getTipoEvento().name());

            if (evento.getEventoPrincipalId() != null) {
                stmt.setLong(8, evento.getEventoPrincipalId());
            } else {
                stmt.setNull(8, Types.BIGINT);
            }

            stmt.setString(9, evento.getModalidade().name());
            stmt.setInt(10, evento.getCapacidadeMaxima());
            stmt.setString(11, evento.getLocal());
            stmt.setBoolean(12, evento.getAtivo());
            stmt.setDouble(13, evento.getPrecoIngresso());
            stmt.setBoolean(14, evento.getEstornaCancelamento());
            stmt.setDouble(15, evento.getTaxaEstorno());
            stmt.setInt(16, evento.getIngressosVendidos());
            stmt.setLong(17, evento.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar evento", e);
        }
    }

    @Override
    public void deletar(Long id) {
        String sql = "DELETE FROM eventos WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar evento", e);
        }
    }

    @Override
    public boolean existePorId(Long id) {
        String sql = "SELECT 1 FROM eventos WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência de evento", e);
        }
    }
}