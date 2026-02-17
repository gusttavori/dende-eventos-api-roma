package br.com.softhouse.dende.service;

import br.com.softhouse.dende.model.UsuarioComum;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDate;
import java.util.List;

public class UsuarioService {

    private final Repositorio repositorio = Repositorio.getInstance();

    /**
     * US 1 – Cadastrar Usuário Comum
     * E-mail único
     */
    public UsuarioComum cadastrarUsuarioComum(
            String nome,
            LocalDate dataNascimento,
            String sexo,
            String email,
            String senha
    ) {

        if (repositorio.buscarUsuarioPorEmail(email) != null) {
            throw new IllegalArgumentException("E-mail já cadastrado!");
        }

        UsuarioComum usuario = new UsuarioComum(
                repositorio.gerarId(),
                nome,
                dataNascimento,
                sexo,
                email,
                senha
        );

        repositorio.salvarUsuario(usuario);
        return usuario;
    }

    /**
     * Lista todos os usuários cadastrados
     */
    public List<Usuario> listarUsuarios() {
        return repositorio.listarUsuarios();
    }

    /**
     * US 6 – Reativar Usuário
     */
    public String reativarPerfil(String email, String senha) {
        Usuario usuario = repositorio.buscarUsuarioPorEmail(email);

        if (usuario == null) {
            return "Usuário não encontrado.";
        }

        boolean reativado = usuario.reativar(senha);
        return reativado ? "Perfil reativado com sucesso!" : "Senha incorreta. Não foi possível reativar.";
    }
}
