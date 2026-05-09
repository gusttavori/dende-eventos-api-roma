package br.com.softhouse.dende.service;

import br.com.softhouse.dende.exceptions.BusinessRuleException;
import br.com.softhouse.dende.exceptions.EntityNotFoundException;
import br.com.softhouse.dende.model.*;
import br.com.softhouse.dende.model.EnumModel.StatusIngresso;
import br.com.softhouse.dende.repositories.EventoRepository;
import br.com.softhouse.dende.repositories.IngressoRepository;
import br.com.softhouse.dende.repositories.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IngressoService {

    private final IngressoRepository ingressoRepository = new IngressoRepository();
    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    private final EventoRepository eventoRepository = new EventoRepository();

    public List<Ingresso> comprarIngresso(String emailUsuario, int eventoId) {
        System.out.println("\n=== COMPRANDO INGRESSO ===");
        System.out.println("Email usuário: " + emailUsuario);
        System.out.println("Evento ID: " + eventoId);

        // Busca o usuário
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        // Busca o evento
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new EntityNotFoundException("Evento", eventoId));

        // Validações
        if (!evento.isAtivo()) {
            throw new BusinessRuleException("Evento está inativo");
        }

        if (evento.getDataFim().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("Evento já realizado");
        }

        // Verifica capacidade
        int ingressosVendidos = ingressoRepository.findByEventoId(eventoId).size();
        if (ingressosVendidos >= evento.getCapacidadeMaxima()) {
            throw new BusinessRuleException("Evento está com capacidade esgotada");
        }

        List<Ingresso> ingressosComprados = new ArrayList<>();

        // Cria o ingresso para o evento principal
        Ingresso ingressoPrincipal = new Ingresso(
                0,  // ID será gerado pelo repositório
                usuario,
                evento,
                evento.getPrecoUnitarioIngresso()
        );
        Ingresso savedPrincipal = ingressoRepository.save(ingressoPrincipal);
        ingressosComprados.add(savedPrincipal);
        System.out.println("Ingresso principal criado: " + savedPrincipal.getId());

        // Se o evento tiver um evento principal, cria ingresso também para ele
        if (evento.getEventoPrincipal() != null) {
            Evento eventoPrincipal = evento.getEventoPrincipal();
            Ingresso ingressoSecundario = new Ingresso(
                    0,
                    usuario,
                    eventoPrincipal,
                    eventoPrincipal.getPrecoUnitarioIngresso()
            );
            Ingresso savedSecundario = ingressoRepository.save(ingressoSecundario);
            ingressosComprados.add(savedSecundario);
            System.out.println("Ingresso do evento principal criado: " + savedSecundario.getId());
        }

        System.out.println("Total de ingressos comprados: " + ingressosComprados.size());
        return ingressosComprados;
    }

    public void cancelarIngresso(int ingressoId) {
        Ingresso ingresso = ingressoRepository.findById(ingressoId)
                .orElseThrow(() -> new EntityNotFoundException("Ingresso", ingressoId));

        // Verifica se o evento já passou
        if (ingresso.getEvento().getDataFim().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("Não é possível cancelar ingresso de evento já realizado");
        }

        ingresso.cancelar();

        // Atualiza o status no banco
        ingressoRepository.updateStatus(ingressoId, StatusIngresso.CANCELADO);
    }

    public List<Ingresso> listarIngressosUsuario(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        return ingressoRepository.findByUsuarioId(usuario.getId()).stream()
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