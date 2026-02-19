package br.com.softhouse.dende.service;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.Repositorio;

import java.util.List;

public class EventoService {

    private final Repositorio repositorio = Repositorio.getInstance();
    private final IngressoService ingressoService = new IngressoService();

    // Cadastra um novo evento associado a um organizador identificado por email
    public Evento cadastrarEvento(String emailOrganizador, Evento evento) {
        // 1. Busca como Usuario primeiro para evitar ClassCastException
        Usuario usuario = repositorio.buscarUsuarioPorEmail(emailOrganizador);

        // 2. Valida se o usuário existe
        if (usuario == null) {
            throw new IllegalArgumentException("Organizador não encontrado com email: " + emailOrganizador);
        }

        // 3. Verifica se o usuário é um organizador
        if (!(usuario instanceof Organizador)) {
            throw new IllegalArgumentException("Usuário com email " + emailOrganizador + " não é um organizador");
        }

        // 4. Faz o cast seguro para Organizador
        Organizador organizador = (Organizador) usuario;

        // 5. Valida as regras de negócio do evento
        evento.validarEvento();

        // 6. Associa o organizador ao evento e gera um ID único
        evento.setOrganizador(organizador);
        evento.setId(repositorio.gerarId());

        // 7. Persiste o evento no repositório e adiciona à lista do organizador
        repositorio.salvarEvento(evento);
        organizador.cadastrarEvento(evento);

        return evento;
    }

    // Altera o status de um evento (ativar/desativar) pelo seu ID
    public void alterarStatusEvento(int eventoId, String status) {
        // Busca o evento pelo ID
        Evento evento = repositorio.buscarEventoPorId(eventoId);

        if (evento == null) {
            throw new IllegalArgumentException("Evento não encontrado");
        }

        // Verifica o status desejado e aplica a ação correspondente
        if ("ativar".equalsIgnoreCase(status)) {
            evento.ativar();
        } else if ("desativar".equalsIgnoreCase(status)) {
            evento.desativar();

            // Se houver ingressos vendidos, cancela todos automaticamente
            List<Ingresso> ingressos = repositorio.listarIngressosPorEvento(evento);
            if (!ingressos.isEmpty()) {
                ingressos.forEach(i -> ingressoService.cancelarIngresso(i.getId()));
            }
        } else {
            throw new IllegalArgumentException("Status inválido: " + status);
        }
    }

    // Retorna a lista de eventos ativos (não finalizados)
    public List<Evento> listarEventosAtivos() {
        System.out.println("=== LISTANDO EVENTOS ATIVOS ===");
        List<Evento> eventos = repositorio.listarEventosAtivos();
        System.out.println("Eventos encontrados: " + eventos.size());
        for (Evento e : eventos) {
            System.out.println(" - " + e.getNome() + " (ID: " + e.getId() + ")");
        }
        return eventos;
    }

    // Retorna a lista de eventos de um organizador específico identificado por email
    public List<Evento> listarEventosPorOrganizador(String emailOrganizador) {
        // 1. Busca o usuário pelo email
        Usuario usuario = repositorio.buscarUsuarioPorEmail(emailOrganizador);

        // 2. Valida se o usuário existe
        if (usuario == null) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        // 3. Verifica se é um organizador
        if (!(usuario instanceof Organizador)) {
            throw new IllegalArgumentException("Usuário com email " + emailOrganizador + " não é um organizador");
        }

        // 4. Faz o cast seguro
        Organizador organizador = (Organizador) usuario;

        // 5. Retorna os eventos do organizador
        return repositorio.listarEventosPorOrganizador(organizador);
    }

    // Altera os dados de um evento existente
    public Evento alterarEvento(String emailOrganizador, int eventoId, Evento dadosAtualizados) {
        // 1. Busca e valida o organizador
        Usuario usuario = repositorio.buscarUsuarioPorEmail(emailOrganizador);

        if (usuario == null) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        if (!(usuario instanceof Organizador)) {
            throw new IllegalArgumentException("Usuário com email " + emailOrganizador + " não é um organizador");
        }

        Organizador organizador = (Organizador) usuario;

        // 2. Busca e valida o evento
        Evento evento = repositorio.buscarEventoPorId(eventoId);
        if (evento == null) {
            throw new IllegalArgumentException("Evento não encontrado");
        }

        // 3. Verifica se o evento pertence ao organizador informado
        if (!evento.getOrganizador().equals(organizador)) {
            throw new IllegalArgumentException("Evento não pertence a este organizador");
        }

        // 4. Verifica se o evento está ativo (eventos inativos não podem ser alterados)
        if (!evento.isAtivo()) {
            throw new IllegalArgumentException("Não é possível alterar um evento inativo");
        }

        // 5. Atualiza os campos permitidos, mantendo organizador e ID originais
        evento.setNome(dadosAtualizados.getNome());
        evento.setPaginaWeb(dadosAtualizados.getPaginaWeb());
        evento.setDescricao(dadosAtualizados.getDescricao());
        evento.setDataInicio(dadosAtualizados.getDataInicio());
        evento.setDataFim(dadosAtualizados.getDataFim());
        evento.setTipoEvento(dadosAtualizados.getTipoEvento());
        evento.setModalidade(dadosAtualizados.getModalidade());
        evento.setPrecoUnitarioIngresso(dadosAtualizados.getPrecoUnitarioIngresso());
        evento.setTaxaCancelamentoIngresso(dadosAtualizados.getTaxaCancelamentoIngresso());
        evento.setCapacidadeMaxima(dadosAtualizados.getCapacidadeMaxima());
        evento.setLocal(dadosAtualizados.getLocal());

        // 6. Valida novamente as regras de negócio do evento
        evento.validarEvento();

        return evento;
    }

    // Busca um evento pelo seu ID
    public Evento buscarEventoPorId(int eventoId) {
        Evento evento = repositorio.buscarEventoPorId(eventoId);
        if (evento == null) {
            throw new IllegalArgumentException("Evento não encontrado");
        }
        return evento;
    }

    // Retorna todos os eventos cadastrados
    public List<Evento> listarTodosEventos() {
        return repositorio.listarEventos();
    }

    // Altera o status de todos os eventos de um organizador identificado por ID
    public void alterarStatusEventoPorOrganizador(int organizadorId, String status) {
        // Busca o organizador pelo ID
        Usuario usuario = repositorio.buscarUsuarioPorId(organizadorId);

        if (usuario == null) {
            throw new IllegalArgumentException("Organizador não encontrado com ID: " + organizadorId);
        }

        if (!(usuario instanceof Organizador)) {
            throw new IllegalArgumentException("Usuário com ID " + organizadorId + " não é um organizador");
        }

        Organizador organizador = (Organizador) usuario;

        // Busca todos os eventos deste organizador
        List<Evento> eventos = repositorio.listarEventosPorOrganizador(organizador);

        if (eventos.isEmpty()) { // Verifica se a coleção chamada "eventos" está vazia, ou seja, se não contém nenhum elemento
            throw new IllegalArgumentException("Organizador não possui eventos");
        }

        // Aplica o status (ativar/desativar) a todos os eventos do organizador
        for (Evento evento : eventos) {
            if ("ativar".equalsIgnoreCase(status)) {
                evento.ativar();
            } else if ("desativar".equalsIgnoreCase(status)) {
                evento.desativar();

                // Se houver ingressos vendidos, cancela todos
                List<Ingresso> ingressos = repositorio.listarIngressosPorEvento(evento);
                if (!ingressos.isEmpty()) {
                    ingressos.forEach(i -> ingressoService.cancelarIngresso(i.getId()));
                }
            }
        }

        System.out.println("Status '" + status + "' aplicado a " + eventos.size() + " eventos");
    }
}