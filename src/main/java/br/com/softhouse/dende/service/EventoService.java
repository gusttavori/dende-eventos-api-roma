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

    public Evento cadastrarEvento(String emailOrganizador, Evento evento) {
        // 1. Busca como Usuario primeiro (evita ClassCastException)
        Usuario usuario = repositorio.buscarUsuarioPorEmail(emailOrganizador);

        // 2. Valida se existe
        if (usuario == null) {
            throw new IllegalArgumentException("Organizador não encontrado com email: " + emailOrganizador);
        }

        // 3. Verifica se é organizador (instanceof)
        if (!(usuario instanceof Organizador)) {
            throw new IllegalArgumentException("Usuário com email " + emailOrganizador + " não é um organizador");
        }

        // 4. Cast seguro
        Organizador organizador = (Organizador) usuario;

        // 5. Valida as regras do evento
        evento.validarEvento();

        // 6. Associa o organizador e gera ID
        evento.setOrganizador(organizador);
        evento.setId(repositorio.gerarId());

        // 7. Salva no repositório
        repositorio.salvarEvento(evento);
        organizador.cadastrarEvento(evento);

        return evento;
    }

    public void alterarStatusEvento(int eventoId, String status) {
        Evento evento = repositorio.buscarEventoPorId(eventoId);

        if (evento == null) {
            throw new IllegalArgumentException("Evento não encontrado");
        }

        if ("ativar".equalsIgnoreCase(status)) {
            evento.ativar();
        } else if ("desativar".equalsIgnoreCase(status)) {
            evento.desativar();

            // Se tiver ingressos vendidos, cancela todos
            List<Ingresso> ingressos = repositorio.listarIngressosPorEvento(evento);
            if (!ingressos.isEmpty()) {
                ingressos.forEach(i -> ingressoService.cancelarIngresso(i.getId()));
            }
        } else {
            throw new IllegalArgumentException("Status inválido: " + status);
        }
    }

    public List<Evento> listarEventosAtivos() {
        return repositorio.listarEventosAtivos();
    }

    public List<Evento> listarEventosPorOrganizador(String emailOrganizador) {
        // 1. Busca como Usuario primeiro
        Usuario usuario = repositorio.buscarUsuarioPorEmail(emailOrganizador);

        // 2. Valida se existe
        if (usuario == null) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        // 3. Verifica se é organizador
        if (!(usuario instanceof Organizador)) {
            throw new IllegalArgumentException("Usuário com email " + emailOrganizador + " não é um organizador");
        }

        // 4. Cast seguro
        Organizador organizador = (Organizador) usuario;

        // 5. Retorna eventos do organizador
        return repositorio.listarEventosPorOrganizador(organizador);
    }

    public Evento alterarEvento(String emailOrganizador, int eventoId, Evento dadosAtualizados) {
        // 1. Busca e valida organizador
        Usuario usuario = repositorio.buscarUsuarioPorEmail(emailOrganizador);

        if (usuario == null) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        if (!(usuario instanceof Organizador)) {
            throw new IllegalArgumentException("Usuário com email " + emailOrganizador + " não é um organizador");
        }

        Organizador organizador = (Organizador) usuario;

        // 2. Busca e valida evento
        Evento evento = repositorio.buscarEventoPorId(eventoId);
        if (evento == null) {
            throw new IllegalArgumentException("Evento não encontrado");
        }

        // 3. Verifica se o evento pertence ao organizador
        if (!evento.getOrganizador().equals(organizador)) {
            throw new IllegalArgumentException("Evento não pertence a este organizador");
        }

        // 4. Verifica se o evento está ativo (regra de negócio)
        if (!evento.isAtivo()) {
            throw new IllegalArgumentException("Não é possível alterar um evento inativo");
        }

        // 5. Atualiza campos permitidos (mantém organizador e ID)
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

        // 6. Valida novamente as regras do evento
        evento.validarEvento();

        return evento;
    }

    public Evento buscarEventoPorId(int eventoId) {
        Evento evento = repositorio.buscarEventoPorId(eventoId);
        if (evento == null) {
            throw new IllegalArgumentException("Evento não encontrado");
        }
        return evento;
    }

    public List<Evento> listarTodosEventos() {
        return repositorio.listarEventos();
    }
}