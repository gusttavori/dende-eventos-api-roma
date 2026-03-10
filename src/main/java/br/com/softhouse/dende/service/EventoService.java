package br.com.softhouse.dende.service;

import br.com.softhouse.dende.dto.request.EventoRequestDTO;
import br.com.softhouse.dende.mappers.EventoMapper;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.Repositorio;

import java.util.List;

public class EventoService {

    // obtém a instância única do repositório (singleton)
    private final Repositorio repositorio = Repositorio.getInstance();
    // cria uma instância do serviço de ingressos para operações relacionadas
    private final IngressoService ingressoService = new IngressoService();

    // metodo para cadastrar um novo evento
    public Evento cadastrarEvento(String emailOrganizador, EventoRequestDTO request, Organizador organizador) {
        // converte o DTO de requisição para uma entidade Evento
        Evento evento = EventoMapper.toEntity(request, organizador);
        // valida os dados do evento (datas, capacidade, etc)
        evento.validarEvento();
        // define um ID automático para o evento
        evento.setId(repositorio.gerarId());

        // salva o evento no repositório
        repositorio.salvarEvento(evento);
        // adiciona o evento à lista de eventos do organizador
        organizador.cadastrarEvento(evento);

        // retorna o evento cadastrado
        return evento;
    }

    // metodo para ativar ou desativar um evento
    public void alterarStatusEvento(int eventoId, String status) {
        // busca o evento pelo ID no repositório
        Evento evento = repositorio.buscarEventoPorId(eventoId);

        // verifica se o evento foi encontrado
        if (evento == null) {
            // lança exceção se não existir
            throw new IllegalArgumentException("Evento não encontrado");
        }

        // verifica se o status é "ativar" (ignorando maiúsculas/minúsculas)
        if ("ativar".equalsIgnoreCase(status)) {
            // ativa o evento
            evento.ativar();
            // verifica se o status é "desativar" (ignorando maiúsculas/minúsculas)
        } else if ("desativar".equalsIgnoreCase(status)) {
            // desativa o evento
            evento.desativar();

            // lista todos os ingressos vendidos para este evento
            List<Ingresso> ingressos = repositorio.listarIngressosPorEvento(evento);
            // verifica se existem ingressos vendidos
            if (!ingressos.isEmpty()) {
                // cancela todos os ingressos do evento
                ingressos.forEach(i -> ingressoService.cancelarIngresso(i.getId()));
            }
        } else {
            // lança exceção se o status não for válido
            throw new IllegalArgumentException("Status inválido: " + status);
        }
    }

    // metodo para listar todos os eventos ativos
    public List<Evento> listarEventosAtivos() {
        // retorna a lista de eventos ativos do repositório
        return repositorio.listarEventosAtivos();
    }

    // metodo para listar eventos de um organizador específico
    public List<Evento> listarEventosPorOrganizador(String emailOrganizador) {
        // busca o usuário pelo email no repositório
        Usuario usuario = repositorio.buscarUsuarioPorEmail(emailOrganizador);

        // verifica se o usuário foi encontrado
        if (usuario == null) {
            // lança exceção se não existir
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        // verifica se o usuário é do tipo Organizador
        if (!(usuario instanceof Organizador)) {
            // lança exceção se não for um organizador
            throw new IllegalArgumentException("Usuário não é um organizador");
        }

        // faz o cast do usuário para Organizador
        Organizador organizador = (Organizador) usuario;
        // retorna a lista de eventos do organizador
        return repositorio.listarEventosPorOrganizador(organizador);
    }

    // metodo para alterar os dados de um evento existente
    public Evento alterarEvento(String emailOrganizador, int eventoId, EventoRequestDTO request) {
        // busca o usuário pelo email no repositório
        Usuario usuario = repositorio.buscarUsuarioPorEmail(emailOrganizador);

        // verifica se o usuário foi encontrado
        if (usuario == null) {
            // lança exceção se não existir
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        // verifica se o usuário é do tipo Organizador
        if (!(usuario instanceof Organizador)) {
            // lança exceção se não for um organizador
            throw new IllegalArgumentException("Usuário não é um organizador");
        }

        // faz o cast do usuário para Organizador
        Organizador organizador = (Organizador) usuario;
        // busca o evento pelo ID no repositório
        Evento evento = repositorio.buscarEventoPorId(eventoId);

        // verifica se o evento foi encontrado
        if (evento == null) {
            // lança exceção se não existir
            throw new IllegalArgumentException("Evento não encontrado");
        }

        // verifica se o evento pertence ao organizador informado
        if (!evento.getOrganizador().equals(organizador)) {
            // lança exceção se o evento for de outro organizador
            throw new IllegalArgumentException("Evento não pertence a este organizador");
        }

        // verifica se o evento está ativo
        if (!evento.isAtivo()) {
            // impede alteração em eventos inativos
            throw new IllegalArgumentException("Não é possível alterar um evento inativo");
        }

        // atualiza os dados do evento usando o mapper
        EventoMapper.updateEntityFromDTO(request, evento);
        // valida os novos dados do evento
        evento.validarEvento();

        // retorna o evento alterado
        return evento;
    }

    // metodo para buscar um evento pelo ID
    public Evento buscarEventoPorId(int eventoId) {
        // busca o evento pelo ID no repositório
        Evento evento = repositorio.buscarEventoPorId(eventoId);
        // verifica se o evento foi encontrado
        if (evento == null) {
            // lança exceção se não existir
            throw new IllegalArgumentException("Evento não encontrado");
        }
        // retorna o evento encontrado
        return evento;
    }
}