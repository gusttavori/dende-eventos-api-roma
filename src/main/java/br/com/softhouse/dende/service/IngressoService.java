package br.com.softhouse.dende.service;

import br.com.softhouse.dende.model.*;
import br.com.softhouse.dende.model.EnumModel.StatusIngresso;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IngressoService {

    private final Repositorio repositorio = Repositorio.getInstance();

    // Processa a compra de um ingresso para um evento
    public List<Ingresso> comprarIngresso(String emailUsuario, int eventoId) {
        System.out.println("\n=== COMPRANDO INGRESSO ===");
        System.out.println("Email usuário: " + emailUsuario);
        System.out.println("Evento ID: " + eventoId);

        // Busca o usuário e o evento
        Usuario usuario = repositorio.buscarUsuarioPorEmail(emailUsuario);
        Evento evento = repositorio.buscarEventoPorId(eventoId);

        // Validações (código omitido para brevidade)

        List<Ingresso> ingressosComprados = new ArrayList<>();

        // Cria o ingresso para o evento principal
        Ingresso ingressoPrincipal = new Ingresso(
                repositorio.gerarId(),
                usuario,
                evento,
                evento.getPrecoUnitarioIngresso()
        );
        repositorio.salvarIngresso(ingressoPrincipal);
        ingressosComprados.add(ingressoPrincipal);
        System.out.println("Ingresso principal criado: " + ingressoPrincipal.getId());

        // Se o evento tiver um evento principal, cria ingresso também para ele
        if (evento.getEventoPrincipal() != null) {
            Evento eventoPrincipal = evento.getEventoPrincipal();
            Ingresso ingressoSecundario = new Ingresso(
                    repositorio.gerarId(),
                    usuario,
                    eventoPrincipal,
                    eventoPrincipal.getPrecoUnitarioIngresso()
            );
            repositorio.salvarIngresso(ingressoSecundario);
            ingressosComprados.add(ingressoSecundario);
            System.out.println("Ingresso do evento principal criado: " + ingressoSecundario.getId());
        }

        System.out.println("Total de ingressos comprados: " + ingressosComprados.size());
        return ingressosComprados;
    }

    // Cancela um ingresso existente
    public void cancelarIngresso(int ingressoId) {
        Ingresso ingresso = repositorio.buscarIngressoPorId(ingressoId);

        if (ingresso == null) {
            throw new IllegalArgumentException("Ingresso não encontrado");
        }

        ingresso.cancelar();
    }

    // Lista todos os ingressos de um usuário, ordenados conforme regras de negócio
    public List<Ingresso> listarIngressosUsuario(String emailUsuario) {
        // Busca o usuário pelo email
        Usuario usuario = repositorio.buscarUsuarioPorEmail(emailUsuario);

        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }

        // Filtra ingressos do usuário e aplica ordenação
        // OBS: Me perdi aqui e acabei apelando pro chat
        return repositorio.listarIngressosPorUsuario(usuario).stream()
                .sorted((i1, i2) -> {
                    // Primeiro critério: eventos ativos e não realizados vêm antes
                    boolean i1Ativo = i1.getStatusIngresso() == StatusIngresso.ATIVO &&
                            i1.getEvento().getDataFim().isAfter(LocalDateTime.now());
                    boolean i2Ativo = i2.getStatusIngresso() == StatusIngresso.ATIVO &&
                            i2.getEvento().getDataFim().isAfter(LocalDateTime.now());

                    if (i1Ativo && !i2Ativo) return -1;
                    if (!i1Ativo && i2Ativo) return 1;

                    // Segundo critério: ordena por data do evento
                    int compareData = i1.getEvento().getDataInicio()
                            .compareTo(i2.getEvento().getDataInicio());
                    if (compareData != 0) return compareData;

                    // Terceiro critério: ordena alfabeticamente por nome do evento
                    return i1.getEvento().getNome()
                            .compareTo(i2.getEvento().getNome());
                })
                .collect(Collectors.toList());
    }
}