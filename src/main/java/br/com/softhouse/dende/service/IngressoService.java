package br.com.softhouse.dende.service;

import br.com.softhouse.dende.model.*;
import br.com.softhouse.dende.model.EnumModel.StatusIngresso;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class IngressoService {

    private final Repositorio repositorio = Repositorio.getInstance();

    public Ingresso comprarIngresso(String emailUsuario, int eventoId) {

        Usuario usuario = repositorio.buscarUsuarioPorEmail(emailUsuario);
        Evento evento = repositorio.buscarEventoPorId(eventoId);

        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }

        if (!(usuario instanceof UsuarioComum)) {
            throw new IllegalArgumentException("Apenas usuários comuns podem comprar ingressos");
        }

        if (evento == null) {
            throw new IllegalArgumentException("Evento não encontrado");
        }

        if (!evento.isAtivo()) {
            throw new IllegalStateException("Evento não está ativo");
        }

        if (evento.getDataFim().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Evento já finalizado");
        }

        // Verificar capacidade
        long ingressosVendidos = repositorio.listarIngressosPorEvento(evento).stream()
                .filter(i -> i.getStatusIngresso() == StatusIngresso.ATIVO)
                .count();

        if (ingressosVendidos >= evento.getCapacidadeMaxima()) {
            throw new IllegalStateException("Ingressos esgotados para este evento");
        }

        Ingresso ingresso = new Ingresso(
                repositorio.gerarId(),
                usuario,
                evento,
                evento.getPrecoUnitarioIngresso()
        );

        repositorio.salvarIngresso(ingresso);
        return ingresso;
    }

    public void cancelarIngresso(int ingressoId) {
        Ingresso ingresso = repositorio.buscarIngressoPorId(ingressoId);

        if (ingresso == null) {
            throw new IllegalArgumentException("Ingresso não encontrado");
        }

        ingresso.cancelar();
    }

    public List<Ingresso> listarIngressosUsuario(String emailUsuario) {
        Usuario usuario = repositorio.buscarUsuarioPorEmail(emailUsuario);

        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }

        return repositorio.listarIngressosPorUsuario(usuario).stream()
                .sorted((i1, i2) -> {
                    // Primeiro: eventos ativos e não realizados
                    boolean i1Ativo = i1.getStatusIngresso() == StatusIngresso.ATIVO &&
                            i1.getEvento().getDataFim().isAfter(LocalDateTime.now());
                    boolean i2Ativo = i2.getStatusIngresso() == StatusIngresso.ATIVO &&
                            i2.getEvento().getDataFim().isAfter(LocalDateTime.now());

                    if (i1Ativo && !i2Ativo) return -1;
                    if (!i1Ativo && i2Ativo) return 1;

                    // Depois: ordena por data do evento
                    int compareData = i1.getEvento().getDataInicio()
                            .compareTo(i2.getEvento().getDataInicio());
                    if (compareData != 0) return compareData;

                    // Por fim: ordena alfabeticamente
                    return i1.getEvento().getNome()
                            .compareTo(i2.getEvento().getNome());
                })
                .collect(Collectors.toList());
    }
}