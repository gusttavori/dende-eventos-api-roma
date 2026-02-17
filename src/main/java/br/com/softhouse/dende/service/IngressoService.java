package br.com.softhouse.dende.service;

import br.com.softhouse.dende.model.EnumModel.StatusIngresso;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsável pelas regras de negócio de ingressos.
 */
public class IngressoService {

    private final Repositorio repositorio = Repositorio.getInstance();

    /**
     * US 13 – Comprar Ingresso
     */
    public List<Ingresso> comprarIngresso(Usuario usuario, Evento eventoSelecionado) {

        // Regra: evento deve estar ativo
        if (!eventoSelecionado.isAtivo()) {
            throw new IllegalStateException("Evento não está ativo.");
        }

        // Regra: evento não pode ter finalizado
        if (eventoSelecionado.getDataFim().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Evento já foi finalizado.");
        }

        // Lista de ingressos gerados na compra
        List<Ingresso> ingressosGerados = new java.util.ArrayList<>();

        // Se existir evento principal, compra primeiro o ingresso dele (US 13)
        if (eventoSelecionado.getEventoPrincipal() != null) {
            Ingresso ingressoPrincipal = new Ingresso(
                    repositorio.gerarId(),
                    usuario,
                    eventoSelecionado.getEventoPrincipal(),
                    eventoSelecionado.getEventoPrincipal().getPrecoUnitarioIngresso()
            );
            repositorio.salvarIngresso(ingressoPrincipal);
            ingressosGerados.add(ingressoPrincipal);
        }

        // Ingresso do evento selecionado
        Ingresso ingressoAtual = new Ingresso(
                repositorio.gerarId(),
                usuario,
                eventoSelecionado,
                eventoSelecionado.getPrecoUnitarioIngresso()
        );

        repositorio.salvarIngresso(ingressoAtual);
        ingressosGerados.add(ingressoAtual);

        return ingressosGerados;
    }

    /**
     * US 14 – Cancelar Ingresso
     */
    public void cancelarIngresso(Ingresso ingresso) {

        if (ingresso.getStatusIngresso() == StatusIngresso.CANCELADO) {
            throw new IllegalStateException("Ingresso já está cancelado.");
        }

        ingresso.setStatusIngresso(StatusIngresso.CANCELADO);

        // Aqui futuramente você pode:
        // - calcular estorno
        // - devolver vaga ao evento
    }

    /**
     * US 15 – Listar ingressos do usuário
     */
    public List<Ingresso> listarIngressosUsuario(Usuario usuario) {
        return repositorio.listarIngressos().stream()
                .filter(i -> i.getUsuario().equals(usuario))
                .sorted(Comparator
                        .comparing((Ingresso i) -> i.getEvento().getDataInicio())
                        .thenComparing(i -> i.getEvento().getNome()))
                .collect(Collectors.toList());
    }

    /**
     * US 12 – Feed de Eventos
     */
    public List<Evento> getFeedEventos(List<Evento> todosEventos) {
        return todosEventos.stream()
                .filter(Evento::isAtivo) // apenas ativos
                .filter(e -> e.getDataFim().isAfter(LocalDateTime.now())) // não finalizados
                .sorted(Comparator
                        .comparing(Evento::getDataInicio)
                        .thenComparing(Evento::getNome))
                .collect(Collectors.toList());
    }
}
