package br.com.softhouse.dende.service;

import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.UsuarioComum;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDate;
import java.util.List;

public class UsuarioService {

    private final Repositorio repositorio = Repositorio.getInstance();

    public UsuarioComum cadastrarUsuarioComum(
            String nome,
            LocalDate dataNascimento,
            String sexo,
            String email,
            String senha
    ) {
        if (repositorio.buscarUsuarioPorEmail(email) != null) {
            throw new IllegalArgumentException("E-mail já cadastrado");
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

    public Usuario buscarPorId(Integer id) {
        Usuario usuario = repositorio.buscarUsuarioPorId(id);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        return usuario;
    }

    public Usuario buscarPorEmail(String email) {
        Usuario usuario = repositorio.buscarUsuarioPorEmail(email);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        return usuario;
    }

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

    public void reativar(String email, String senha) {
        System.out.println("\n=== INÍCIO DA REATIVAÇÃO ===");
        System.out.println("1. Buscando usuário com email: " + email);

        Usuario usuario = buscarPorEmail(email);

        System.out.println("2. Usuário encontrado: " + usuario.getNome());
        System.out.println("3. Senha ARMAZENADA no sistema: '" + usuario.getSenha() + "'");
        System.out.println("4. Senha FORNECIDA no request: '" + senha + "'");
        System.out.println("5. Comparação (equals): " + usuario.getSenha().equals(senha));
        System.out.println("6. Comparação (==): " + (usuario.getSenha() == senha));
        System.out.println("7. Status atual do usuário: " + (usuario.isAtivo() ? "ATIVO" : "INATIVO"));

        // Verificação 1: Usuário já está ativo?
        if (usuario.isAtivo()) {
            System.out.println("8. ERRO: Usuário já está ativo!");
            throw new IllegalArgumentException("Usuário já está ativo");
        }

        // Verificação 2: Senha (FORÇANDO COMPARAÇÃO)
        System.out.println("9. Chamando método reativar da classe Usuario...");
        boolean resultado = usuario.reativar(senha);
        System.out.println("10. Resultado do método reativar: " + resultado);

        if (!resultado) {
            System.out.println("11. ERRO: Senha incorreta!");
            throw new IllegalArgumentException("Senha incorreta");
        }

        System.out.println("12. SUCESSO! Usuário reativado!");
    }

    public List<Usuario> listarUsuarios() {
        return repositorio.listarUsuarios();
    }

    public void atualizarUsuario(String email, UsuarioComum dados) {  // ← MUDOU PARA UsuarioComum
        Usuario usuario = buscarPorEmail(email);

        // Verifica se o usuário existente é do tipo correto
        if (!(usuario instanceof UsuarioComum)) {
            throw new IllegalArgumentException("Usuário não é do tipo comum");
        }

        usuario.alterarPerfil(
                dados.getNome(),
                dados.getDataNascimento(),
                dados.getSexo()
        );

        if (dados.getSenha() != null && !dados.getSenha().isEmpty()) {
            usuario.setSenha(dados.getSenha());
        }
    }
}