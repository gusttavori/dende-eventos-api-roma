package br.com.softhouse.dende.service;

import br.com.softhouse.dende.dto.request.UsuarioRequestDTO;
import br.com.softhouse.dende.exceptions.BusinessRuleException;
import br.com.softhouse.dende.exceptions.EntityNotFoundException;
import br.com.softhouse.dende.mappers.OrganizadorMapper;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.EventoRepository;
import br.com.softhouse.dende.repositories.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrganizadorService {

    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    private final EventoRepository eventoRepository = new EventoRepository();

    public Organizador cadastrar(Organizador organizador) {
        if (usuarioRepository.findByEmail(organizador.getEmail()).isPresent()) {
            throw new BusinessRuleException("E-mail já cadastrado!");
        }
        return (Organizador) usuarioRepository.save(organizador);
    }

    public Organizador atualizar(String email, UsuarioRequestDTO dadosAtualizados) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Organizador não encontrado"));

        if (!(usuario instanceof Organizador)) {
            throw new BusinessRuleException("Organizador não encontrado");
        }

        Organizador organizador = (Organizador) usuario;
        OrganizadorMapper.updateEntityFromDTO(dadosAtualizados, organizador);

        usuarioRepository.update(organizador);

        return organizador;
    }

    public void alterarStatus(String email, String status) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Organizador não encontrado"));

        if (!(usuario instanceof Organizador)) {
            throw new BusinessRuleException("Organizador não encontrado");
        }

        Organizador organizador = (Organizador) usuario;

        if ("desativar".equalsIgnoreCase(status)) {
            List<Evento> eventosAtivos = eventoRepository.findByOrganizadorId(organizador.getId()).stream()
                    .filter(e -> e.isAtivo() && e.getDataFim().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList());

            if (!eventosAtivos.isEmpty()) {
                throw new BusinessRuleException("Organizador possui eventos ativos e não pode ser desativado");
            }
            organizador.desativar();
        } else if ("ativar".equalsIgnoreCase(status)) {
            organizador.ativar();
        } else {
            throw new BusinessRuleException("Status inválido: " + status);
        }

        usuarioRepository.update(organizador);
    }

    public List<Organizador> listar() {
        return usuarioRepository.findAllOrganizadores();
    }

    public Organizador buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Organizador não encontrado"));

        if (!(usuario instanceof Organizador)) {
            throw new BusinessRuleException("Usuário não é um organizador");
        }

        return (Organizador) usuario;
    }
}