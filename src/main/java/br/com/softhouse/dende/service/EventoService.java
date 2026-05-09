package br.com.softhouse.dende.service;

import br.com.softhouse.dende.dto.request.EventoRequestDTO;
import br.com.softhouse.dende.exceptions.BusinessRuleException;
import br.com.softhouse.dende.exceptions.EntityNotFoundException;
import br.com.softhouse.dende.mappers.EventoMapper;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.EventoRepository;
import br.com.softhouse.dende.repositories.IngressoRepository;
import br.com.softhouse.dende.repositories.UsuarioRepository;
import br.com.softhouse.dende.repositories.Repositorio;

import java.util.List;

public class EventoService {

    private final EventoRepository eventoRepository = new EventoRepository();
    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    private final IngressoRepository ingressoRepository = new IngressoRepository();
    private final IngressoService ingressoService = new IngressoService();

    public Evento cadastrarEvento(String emailOrganizador, EventoRequestDTO request, Organizador organizador) {
        // Converte o DTO para entidade
        Evento evento = EventoMapper.toEntity(request, organizador);

        // Valida os dados do evento
        evento.validarEvento();

        // Salva no repositório (o ID é gerado automaticamente dentro do save)
        Evento savedEvento = eventoRepository.save(evento);

        // Adiciona o evento à lista do organizador (opcional, para manter consistência)
        organizador.cadastrarEvento(savedEvento);

        // Atualiza o organizador no repositório se necessário
        usuarioRepository.update(organizador);

        return savedEvento;
    }

    public void alterarStatusEvento(int eventoId, String status) {
        // Busca o evento pelo ID
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new EntityNotFoundException("Evento", eventoId));

        if ("ativar".equalsIgnoreCase(status)) {
            evento.ativar();
        } else if ("desativar".equalsIgnoreCase(status)) {
            evento.desativar();

            // Cancela todos os ingressos do evento
            List<Ingresso> ingressos = ingressoRepository.findByEventoId(eventoId);
            for (Ingresso ingresso : ingressos) {
                if (ingresso.getStatusIngresso() == br.com.softhouse.dende.model.EnumModel.StatusIngresso.ATIVO) {
                    ingressoService.cancelarIngresso(ingresso.getId());
                }
            }
        } else {
            throw new BusinessRuleException("Status inválido: " + status);
        }

        // Atualiza o evento no banco
        eventoRepository.update(evento);
    }

    public List<Evento> listarEventosAtivos() {
        return eventoRepository.findEventosAtivos();
    }

    public List<Evento> listarEventosPorOrganizador(String emailOrganizador) {
        Usuario usuario = usuarioRepository.findByEmail(emailOrganizador)
                .orElseThrow(() -> new EntityNotFoundException("Organizador não encontrado"));

        if (!(usuario instanceof Organizador)) {
            throw new BusinessRuleException("Usuário não é um organizador");
        }

        Organizador organizador = (Organizador) usuario;
        return eventoRepository.findByOrganizadorId(organizador.getId());
    }

    public Evento alterarEvento(String emailOrganizador, int eventoId, EventoRequestDTO request) {
        // Busca o organizador
        Usuario usuario = usuarioRepository.findByEmail(emailOrganizador)
                .orElseThrow(() -> new EntityNotFoundException("Organizador não encontrado"));

        if (!(usuario instanceof Organizador)) {
            throw new BusinessRuleException("Usuário não é um organizador");
        }

        Organizador organizador = (Organizador) usuario;

        // Busca o evento
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new EntityNotFoundException("Evento", eventoId));

        // Verifica se o evento pertence ao organizador
        if (evento.getOrganizador().getId() != organizador.getId()) {
            throw new BusinessRuleException("Evento não pertence a este organizador");
        }

        // Verifica se o evento está ativo
        if (!evento.isAtivo()) {
            throw new BusinessRuleException("Não é possível alterar um evento inativo");
        }

        // Atualiza os dados
        EventoMapper.updateEntityFromDTO(request, evento);
        evento.validarEvento();

        // Salva no banco
        eventoRepository.update(evento);

        return evento;
    }

    public Evento buscarEventoPorId(int eventoId) {
        return eventoRepository.findById(eventoId)
                .orElseThrow(() -> new EntityNotFoundException("Evento", eventoId));
    }
}