package br.com.softhouse.dende.service;

import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.UsuarioComum;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDate;
import java.util.List;

public class UsuarioService {

    private final Repositorio repositorio = Repositorio.getInstance();

    // Cadastra um novo usuário comum
    public UsuarioComum cadastrarUsuarioComum(
            String nome,
            LocalDate dataNascimento,
            String sexo,
            String email,
            String senha
    ) {
        // Verifica se o email já está cadastrado
        if (repositorio.buscarUsuarioPorEmail(email) != null) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }

        // Cria o novo usuário com ID gerado
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

    // Busca um usuário pelo ID
    public Usuario buscarPorId(Integer id) {
        Usuario usuario = repositorio.buscarUsuarioPorId(id);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        return usuario;
    }

    // Busca um usuário pelo email
    public Usuario buscarPorEmail(String email) {
        Usuario usuario = repositorio.buscarUsuarioPorEmail(email);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        return usuario;
    }

    // Atualiza dados de um usuário (versão com ID)
    public void atualizarUsuario(Integer id, Usuario dados) {
        Usuario usuario = buscarPorId(id);
        usuario.alterarPerfil(
                dados.getNome(),
                dados.getDataNascimento(),
                dados.getSexo()
        );

        if (dados.getSenha() != null && !dados.getSenha().isEmpty()) {
            usuario.setSenha(dados.getSenha());
        }
    }

    // Altera o status de um usuário (ativar/desativar)
    public void alterarStatus(Integer id, String status) {
        Usuario usuario = buscarPorId(id);

        if ("ativar".equalsIgnoreCase(status)) {
            usuario.ativar();
        } else if ("desativar".equalsIgnoreCase(status)) {
            usuario.desativar();
        } else {
            throw new IllegalArgumentException("Status inválido. Use 'ativar' ou 'desativar'");
        }
    }

    // Reativa um usuário após validação de senha
    public void reativar(String email, String senha) {
        Usuario usuario = buscarPorEmail(email);

        // Verifica se o usuário já está ativo
        if (usuario.isAtivo()) {
            System.out.println("8. ERRO: Usuário já está ativo!");
            throw new IllegalArgumentException("Usuário já está ativo");
        }

        boolean resultado = usuario.reativar(senha);

        if (!resultado) {
            throw new IllegalArgumentException("Senha incorreta");
        }
    }

    // Lista todos os usuários
    public List<Usuario> listarUsuarios() {
        return repositorio.listarUsuarios();
    }

    // Atualiza dados de um usuário (versão com email)
    public void atualizarUsuario(String email, UsuarioComum dados) {
        Usuario usuario = buscarPorEmail(email);

        // Verifica se o usuário é do tipo comum
        if (!(usuario instanceof UsuarioComum)) {
            throw new IllegalArgumentException("Usuário não é do tipo comum");
        }

        usuario.alterarPerfil(dados.getNome(), dados.getDataNascimento(), dados.getSexo()
        );

        if (dados.getSenha() != null && !dados.getSenha().isEmpty()) {
            usuario.setSenha(dados.getSenha());
        }
    }
}