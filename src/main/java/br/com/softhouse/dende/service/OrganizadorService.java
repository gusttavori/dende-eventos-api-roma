package br.com.softhouse.dende.service;

import br.com.softhouse.dende.model.Empresa;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class OrganizadorService {

    private final Repositorio repositorio = Repositorio.getInstance();

    /**
     * US 2 – Cadastrar Organizador
     */
    public Organizador cadastrarOrganizador(
            String nome,
            LocalDate dataNascimento,
            String sexo,
            String email,
            String senha,
            Empresa empresa
    ) {
        if (repositorio.buscarUsuarioPorEmail(email) != null) {
            throw new IllegalArgumentException("E-mail já cadastrado!");
        }

        Organizador organizador = new Organizador(
                repositorio.gerarId(),
                nome,
                dataNascimento,
                sexo,
                email,
                senha,
                empresa
        );

        repositorio.salvarUsuario(organizador);
        return organizador;
    }

    /**
     * US 3 – Atualizar Organizador
     */
    public void atualizarOrganizador(
            String email,
            String novoNome,
            LocalDate novaDataNascimento,
            String novoSexo,
            String novaSenha,
            Empresa novaEmpresa
    ) {
        Usuario usuario = repositorio.buscarUsuarioPorEmail(email);

        if (!(usuario instanceof Organizador)) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        Organizador organizador = (Organizador) usuario;

        // Atualiza os dados do perfil usando o método seguro da classe Usuario
        organizador.alterarPerfil(novoNome, novaDataNascimento, novoSexo);

        // Atualiza senha e empresa
        organizador.setSenha(novaSenha);
        organizador.setEmpresa(novaEmpresa);
    }

    /**
     * US 5 – Desativar Organizador
     */
    public void desativarOrganizador(String email) {
        Usuario usuario = repositorio.buscarUsuarioPorEmail(email);

        if (!(usuario instanceof Organizador)) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        ((Organizador) usuario).desativar();
    }

    /**
     * US 4 – Listar Organizadores
     */
    public List<Organizador> listarOrganizadores() {
        return repositorio.listarUsuarios().stream()
                .filter(u -> u instanceof Organizador)
                .map(u -> (Organizador) u)
                .collect(Collectors.toList());
    }
}
