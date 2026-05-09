package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.exceptions.DatabaseException;
import br.com.softhouse.dende.model.Empresa;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.UsuarioComum;
import br.com.softhouse.dende.repositories.core.CrudRepository;
import br.com.softhouse.dende.repositories.core.RowMapper;
import br.com.softhouse.dende.repositories.util.ConnectionPool;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository implements CrudRepository<Usuario, Integer> {

    private int getNextId() {
        String sql = "UPDATE id_generator SET next_id = next_id + 1 WHERE sequence_name = 'usuario'";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                // Se não existe, cria
                String insertSql = "INSERT INTO id_generator (sequence_name, next_id) VALUES ('usuario', 2)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.executeUpdate();
                    return 1;
                }
            }

            String selectSql = "SELECT next_id FROM id_generator WHERE sequence_name = 'usuario'";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                 ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("next_id") - 1;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao gerar ID para usuário", e);
        }
        return (int) (System.currentTimeMillis() % 100000);
    }

    private RowMapper<Usuario> getUsuarioRowMapper() {
        return rs -> {
            int id = rs.getInt("id");
            String nome = rs.getString("nome");
            LocalDate dataNascimento = rs.getDate("data_nascimento").toLocalDate();
            String sexo = rs.getString("sexo");
            String email = rs.getString("email");
            String senha = rs.getString("senha");
            boolean status = rs.getBoolean("status_usuario");
            String tipo = rs.getString("tipo_usuario");

            Usuario usuario;
            if ("ORGANIZADOR".equals(tipo)) {
                String cnpj = rs.getString("cnpj");
                String razaoSocial = rs.getString("razao_social");
                String nomeFantasia = rs.getString("nome_fantasia");
                LocalDate dataAbertura = rs.getDate("data_abertura") != null
                        ? rs.getDate("data_abertura").toLocalDate() : null;

                Empresa empresa = new Empresa(cnpj, razaoSocial, nomeFantasia, dataAbertura);
                usuario = new Organizador(id, nome, dataNascimento, sexo, email, senha, empresa);
            } else {
                usuario = new UsuarioComum(id, nome, dataNascimento, sexo, email, senha);
            }
            usuario.setStatusUsuario(status);
            return usuario;
        };
    }

    @Override
    public Usuario save(Usuario entity) {
        String sql = "INSERT INTO usuarios (id, nome, data_nascimento, sexo, email, senha, " +
                "status_usuario, tipo_usuario, cnpj, razao_social, nome_fantasia, data_abertura) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (entity.getId() == null) {
                entity.setId(getNextId());
            }

            stmt.setInt(1, entity.getId());
            stmt.setString(2, entity.getNome());
            stmt.setDate(3, Date.valueOf(entity.getDataNascimento()));
            stmt.setString(4, entity.getSexo());
            stmt.setString(5, entity.getEmail());
            stmt.setString(6, entity.getSenha());
            stmt.setBoolean(7, entity.isAtivo());
            stmt.setString(8, entity instanceof Organizador ? "ORGANIZADOR" : "COMUM");

            if (entity instanceof Organizador) {
                Organizador org = (Organizador) entity;
                Empresa empresa = org.getEmpresa();
                stmt.setString(9, empresa != null ? empresa.getCnpj() : null);
                stmt.setString(10, empresa != null ? empresa.getRazaoSocial() : null);
                stmt.setString(11, empresa != null ? empresa.getNomeFantasia() : null);
                stmt.setDate(12, empresa != null && empresa.getDataAbertura() != null
                        ? Date.valueOf(empresa.getDataAbertura()) : null);
            } else {
                stmt.setNull(9, Types.VARCHAR);
                stmt.setNull(10, Types.VARCHAR);
                stmt.setNull(11, Types.VARCHAR);
                stmt.setNull(12, Types.DATE);
            }

            stmt.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao salvar usuário: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Usuario> findById(Integer id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(getUsuarioRowMapper().mapRow(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar usuário por ID", e);
        }
    }

    public Optional<Usuario> findByEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(getUsuarioRowMapper().mapRow(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao buscar usuário por email", e);
        }
    }

    @Override
    public List<Usuario> findAll() {
        String sql = "SELECT * FROM usuarios";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(getUsuarioRowMapper().mapRow(rs));
            }
            return usuarios;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao listar usuários", e);
        }
    }

    public List<Organizador> findAllOrganizadores() {
        String sql = "SELECT * FROM usuarios WHERE tipo_usuario = 'ORGANIZADOR'";
        List<Organizador> organizadores = new ArrayList<>();

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                organizadores.add((Organizador) getUsuarioRowMapper().mapRow(rs));
            }
            return organizadores;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao listar organizadores", e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao deletar usuário", e);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT 1 FROM usuarios WHERE id = ?";

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao verificar existência de usuário", e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM usuarios";

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao contar usuários", e);
        }
    }

    public void update(Usuario usuario) {
        String sql = "UPDATE usuarios SET nome = ?, data_nascimento = ?, sexo = ?, " +
                "senha = ?, status_usuario = ? WHERE id = ?";

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setDate(2, Date.valueOf(usuario.getDataNascimento()));
            stmt.setString(3, usuario.getSexo());
            stmt.setString(4, usuario.getSenha());
            stmt.setBoolean(5, usuario.isAtivo());
            stmt.setInt(6, usuario.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao atualizar usuário", e);
        }
    }
}