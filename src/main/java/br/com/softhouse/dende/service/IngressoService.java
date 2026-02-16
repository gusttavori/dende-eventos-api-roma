package br.com.softhouse.dende.service;

import br.com.softhouse.dende.model.EnumModel.StatusIngresso;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Usuario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class IngressoService {
    private List<Ingresso> ingressosVendidos = new ArrayList<>();
    private int proximoId = 1;

    public List<Ingresso> comprarIngresso(Usuario usuario, Evento eventoSelecionado) {
        List<Ingresso> novosIngressos = new ArrayList<>();

        // Se houver evento principal, gera o ingresso do principal primeiro (Regra US 13)
        if (eventoSelecionado.getEventoPrincipal() != null) {
            Ingresso principal = new Ingresso(
                    proximoId++,
                    usuario,
                    eventoSelecionado.getEventoPrincipal(),
                    StatusIngresso.ATIVO
            );
            ingressosVendidos.add(principal);
            novosIngressos.add(principal);
        }

        // Gera o ingresso do evento selecionado
        Ingresso atual = new Ingresso(
                proximoId++,
                usuario,
                eventoSelecionado,
                StatusIngresso.ATIVO
        );
        ingressosVendidos.add(atual);
        novosIngressos.add(atual);

        return novosIngressos;
    }

    public List<Evento> getFeedEventos(List<Evento> todosEventos) {
        return todosEventos.stream()
                .filter(e -> e.isAtivo()) // Apenas ativos
                .filter(e -> e.getDataFim().isAfter(LocalDateTime.now())) // Que ainda não finalizaram
                .sorted(Comparator.comparing(Evento::getDataInicio) // Ordena por data
                        .thenComparing(Evento::getNome)) // Depois por nome
                .collect(Collectors.toList());
    }
}
