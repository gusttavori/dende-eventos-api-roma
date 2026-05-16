package br.com.softhouse.dende.repositories;

import br.com.dende.softhouse.annotations.Component;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.enums.Sexo;
import br.com.softhouse.dende.repositories.util.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class OrganizadorRepository implements CrudRepository<Organizador, Long> {

    private final ConnectionPool connectionPool;

    private final RowMapper<Organizador> mapper = rs -> {
        Organizador o = new Organizador();
        o.setId(rs.getLong("id"));
        o.setNome(rs.getString("nome"));
        o.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
        o.setSexo(Sexo.valueOf(rs.getString("sexo")));
        o.setEmail(rs.getString("email"));
        o.setSenha(rs.getString("senha"));
        o.setCnpj(rs.getString("cnpj"));
        o.setRazaoSocial(rs.getString("razao_social"));
        o.setNomeFantasia(rs.getString("nome_fantasia"));
        o.setAtivo(rs.getBoolean("ativo"));
        return o;
    };

    public OrganizadorRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Organizador salvar(Organizador organizador) {
        String sql = "INSERT INTO organizadores (nome, data_nascimento, sexo, email, senha, cnpj, razao_social, nome_fantasia, ativo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, organizador.getNome());
            stmt.setDate(2, Date.valueOf(organizador.getDataNascimento()));
            stmt.setString(3, organizador.getSexo().name());
            stmt.setString(4, organizador.getEmail());
            stmt.setString(5, organizador.getSenha());
            stmt.setString(6, organizador.getCnpj());
            stmt.setString(7, organizador.getRazaoSocial());
            stmt.setString(8, organizador.getNomeFantasia());
            stmt.setBoolean(9, organizador.getAtivo());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    organizador.setId(rs.getLong(1));
                }
            }
            return organizador;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar organizador", e);
        }
    }

    @Override
    public Optional<Organizador> buscarPorId(Long id) {
        String sql = "SELECT * FROM organizadores WHERE id = ?";
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
            throw new RuntimeException("Erro ao buscar organizador por ID", e);
        }
    }

    @Override
    public List<Organizador> buscarTodos() {
        String sql = "SELECT * FROM organizadores";
        List<Organizador> organizadores = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                organizadores.add(mapper.mapear(rs));
            }
            return organizadores;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todos os organizadores", e);
        }
    }

    @Override
    public void atualizar(Organizador organizador) {
        String sql = "UPDATE organizadores SET nome = ?, data_nascimento = ?, sexo = ?, email = ?, " +
                "senha = ?, cnpj = ?, razao_social = ?, nome_fantasia = ?, ativo = ? WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, organizador.getNome());
            stmt.setDate(2, Date.valueOf(organizador.getDataNascimento()));
            stmt.setString(3, organizador.getSexo().name());
            stmt.setString(4, organizador.getEmail());
            stmt.setString(5, organizador.getSenha());
            stmt.setString(6, organizador.getCnpj());
            stmt.setString(7, organizador.getRazaoSocial());
            stmt.setString(8, organizador.getNomeFantasia());
            stmt.setBoolean(9, organizador.getAtivo());
            stmt.setLong(10, organizador.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar organizador", e);
        }
    }

    @Override
    public void deletar(Long id) {
        String sql = "DELETE FROM organizadores WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar organizador", e);
        }
    }

    @Override
    public boolean existePorId(Long id) {
        String sql = "SELECT 1 FROM organizadores WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência de organizador", e);
        }
    }

    // Métodos específicos do OrganizadorRepository
    public Optional<Organizador> buscarPorEmail(String email) {
        String sql = "SELECT * FROM organizadores WHERE email = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapper.mapear(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar organizador por email", e);
        }
    }

    public Optional<Organizador> buscarPorCnpj(String cnpj) {
        String sql = "SELECT * FROM organizadores WHERE cnpj = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cnpj);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapper.mapear(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar organizador por CNPJ", e);
        }
    }

    public boolean emailExiste(String email) {
        String sql = "SELECT 1 FROM organizadores WHERE email = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência de email", e);
        }
    }

    public boolean cnpjExiste(String cnpj) {
        if (cnpj == null || cnpj.isEmpty()) return false;
        String sql = "SELECT 1 FROM organizadores WHERE cnpj = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cnpj);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência de CNPJ", e);
        }
    }
}