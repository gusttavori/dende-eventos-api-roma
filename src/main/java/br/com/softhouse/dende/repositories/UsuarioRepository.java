package br.com.softhouse.dende.repositories;

import br.com.dende.softhouse.annotations.Component;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.enums.Sexo;
import br.com.softhouse.dende.repositories.util.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UsuarioRepository implements CrudRepository<Usuario, Long> {

    private final ConnectionPool connectionPool;

    private final RowMapper<Usuario> mapper = rs -> {
        Usuario u = new Usuario();
        u.setId(rs.getLong("id"));
        u.setNome(rs.getString("nome"));
        u.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
        u.setSexo(Sexo.valueOf(rs.getString("sexo")));
        u.setEmail(rs.getString("email"));
        u.setSenha(rs.getString("senha"));
        u.setAtivo(rs.getBoolean("ativo"));
        return u;
    };

    public UsuarioRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, data_nascimento, sexo, email, senha, ativo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getNome());
            stmt.setDate(2, Date.valueOf(usuario.getDataNascimento()));
            stmt.setString(3, usuario.getSexo().name());
            stmt.setString(4, usuario.getEmail());
            stmt.setString(5, usuario.getSenha());
            stmt.setBoolean(6, usuario.getAtivo());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getLong(1));
                }
            }
            return usuario;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar usuário", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
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
            throw new RuntimeException("Erro ao buscar usuário por ID", e);
        }
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
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
            throw new RuntimeException("Erro ao buscar usuário por email", e);
        }
    }

    public boolean emailExiste(String email) {
        String sql = "SELECT 1 FROM usuarios WHERE email = ?";
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

    @Override
    public List<Usuario> buscarTodos() {
        String sql = "SELECT * FROM usuarios";
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection conn = connectionPool.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                usuarios.add(mapper.mapear(rs));
            }
            return usuarios;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todos os usuários", e);
        }
    }

    @Override
    public void atualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nome = ?, data_nascimento = ?, sexo = ?, email = ?, senha = ?, ativo = ? WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setDate(2, Date.valueOf(usuario.getDataNascimento()));
            stmt.setString(3, usuario.getSexo().name());
            stmt.setString(4, usuario.getEmail());
            stmt.setString(5, usuario.getSenha());
            stmt.setBoolean(6, usuario.getAtivo());
            stmt.setLong(7, usuario.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar usuário", e);
        }
    }

    @Override
    public void deletar(Long id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar usuário", e);
        }
    }

    @Override
    public boolean existePorId(Long id) {
        String sql = "SELECT 1 FROM usuarios WHERE id = ?";
        try (Connection conn = connectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência de usuário por ID", e);
        }
    }
}