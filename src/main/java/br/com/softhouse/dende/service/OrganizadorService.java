package br.com.softhouse.dende.service;

import br.com.softhouse.dende.model.Empresa;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrganizadorService {

    private final Repositorio repositorio = Repositorio.getInstance();
    private final EventoService eventoService = new EventoService();

    /**
     * US 2 – Cadastrar Organizador
     */
    public Organizador cadastrar(Organizador organizador) {
        if (repositorio.buscarUsuarioPorEmail(organizador.getEmail()) != null) {
            throw new IllegalArgumentException("E-mail já cadastrado!");
        }

        organizador.setId(repositorio.gerarId());
        repositorio.salvarUsuario(organizador);
        return organizador;
    }

    /**
     * US 3 – Atualizar Organizador
     */
    public Organizador atualizar(String email, Organizador dadosAtualizados) {
        Usuario usuario = repositorio.buscarUsuarioPorEmail(email);

        if (!(usuario instanceof Organizador)) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        Organizador organizador = (Organizador) usuario;

        // Atualiza os dados do perfil
        organizador.alterarPerfil(
                dadosAtualizados.getNome(),
                dadosAtualizados.getDataNascimento(),
                dadosAtualizados.getSexo()
        );

        // Atualiza senha se fornecida
        if (dadosAtualizados.getSenha() != null && !dadosAtualizados.getSenha().isEmpty()) {
            organizador.setSenha(dadosAtualizados.getSenha());
        }

        // Atualiza empresa se fornecida
        if (dadosAtualizados.getEmpresa() != null) {
            organizador.setEmpresa(dadosAtualizados.getEmpresa());
        }

        return organizador;
    }

    /**
     * US 5 – Desativar Organizador
     */
    public void alterarStatus(String email, String status) {
        Usuario usuario = repositorio.buscarUsuarioPorEmail(email);

        if (!(usuario instanceof Organizador)) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        Organizador organizador = (Organizador) usuario;

        if ("desativar".equalsIgnoreCase(status)) {
            // Verifica se tem eventos ativos
            List<Evento> eventosAtivos = eventoService.listarEventosPorOrganizador(email).stream()
                    .filter(e -> e.isAtivo() && e.getDataFim().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList());

            if (!eventosAtivos.isEmpty()) {
                throw new IllegalStateException("Organizador possui eventos ativos e não pode ser desativado");
            }

            organizador.desativar();
        } else if ("ativar".equalsIgnoreCase(status)) {
            organizador.ativar();
        } else {
            throw new IllegalArgumentException("Status inválido: " + status);
        }
    }

    /**
     * US 4 – Listar Organizadores
     */
    public List<Organizador> listar() {
        return repositorio.listarUsuarios().stream()
                .filter(u -> u instanceof Organizador)
                .map(u -> (Organizador) u)
                .collect(Collectors.toList());
    }

    public Organizador buscarPorEmail(String email) {
        Usuario usuario = repositorio.buscarUsuarioPorEmail(email);

        if (usuario == null) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        if (!(usuario instanceof Organizador)) {
            throw new IllegalArgumentException("Usuário não é um organizador");
        }

        return (Organizador) usuario;
    }
}