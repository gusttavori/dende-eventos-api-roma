package br.com.softhouse.dende.mappers;

import br.com.softhouse.dende.dto.EventoDTO;
import br.com.softhouse.dende.dto.request.EventoRequestDTO;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.repositories.Repositorio;

// Classe responsável por converter entre entidades Evento e DTOs (Data Transfer Objects)
public class EventoMapper {

    // Converte um EventoRequestDTO para uma entidade Evento, associando a um organizador
    public static Evento toEntity(EventoRequestDTO dto, Organizador organizador) {
        // verifica se o DTO é nulo e retorna nulo se for
        if (dto == null) return null;

        // cria uma nova instância de Evento
        Evento evento = new Evento();
        // copia os dados básicos do DTO para a entidade
        evento.setNome(dto.getNome());
        evento.setPaginaWeb(dto.getPaginaWeb());
        evento.setDescricao(dto.getDescricao());
        evento.setDataInicio(dto.getDataInicio());
        evento.setDataFim(dto.getDataFim());
        evento.setTipoEvento(dto.getTipoEvento());
        evento.setModalidade(dto.getModalidade());
        evento.setPrecoUnitarioIngresso(dto.getPrecoUnitarioIngresso());
        evento.setTaxaCancelamentoIngresso(dto.getTaxaCancelamentoIngresso());
        evento.setCapacidadeMaxima(dto.getCapacidadeMaxima());
        evento.setLocal(dto.getLocal());
        evento.setOrganizador(organizador); // associa o organizador ao evento
        evento.setAtivo(false); // evento sempre começa inativo (regra de negócio)

        // verifica se foi informado um ID de evento principal (para sub-eventos)
        if (dto.getEventoPrincipalId() > 0) {
            // busca o evento principal no repositório pelo ID
            Evento eventoPrincipal = Repositorio.getInstance()
                    .buscarEventoPorId(dto.getEventoPrincipalId());
            evento.setEventoPrincipal(eventoPrincipal); // associa o evento principal
        }

        // retorna a entidade criada
        return evento;
    }

    // Converte uma entidade Evento para um EventoDTO
    public static EventoDTO toDTO(Evento evento) {
        // verifica se a entidade é nula e retorna nulo se for
        if (evento == null) return null;

        // cria uma nova instância de EventoDTO
        EventoDTO dto = new EventoDTO();
        // copia os dados básicos da entidade para o DTO
        dto.setId(evento.getId());
        dto.setNome(evento.getNome());
        dto.setPaginaWeb(evento.getPaginaWeb());
        dto.setDescricao(evento.getDescricao());
        dto.setDataInicio(evento.getDataInicio());
        dto.setDataFim(evento.getDataFim());
        dto.setTipoEvento(evento.getTipoEvento());
        dto.setModalidade(evento.getModalidade());
        dto.setPrecoUnitarioIngresso(evento.getPrecoUnitarioIngresso());
        dto.setTaxaCancelamentoIngresso(evento.getTaxaCancelamentoIngresso());
        dto.setCapacidadeMaxima(evento.getCapacidadeMaxima());
        dto.setLocal(evento.getLocal());
        dto.setAtivo(evento.isAtivo());

        // verifica se o evento possui organizador associado
        if (evento.getOrganizador() != null) {
            // copia o nome e email do organizador
            dto.setOrganizadorNome(evento.getOrganizador().getNome());
            dto.setOrganizadorEmail(evento.getOrganizador().getEmail());
        }

        // verifica se o evento possui um evento principal associado
        if (evento.getEventoPrincipal() != null) {
            // copia o ID e nome do evento principal
            dto.setEventoPrincipalId(evento.getEventoPrincipal().getId());
            dto.setEventoPrincipalNome(evento.getEventoPrincipal().getNome());
        }

        // calcula o total de ingressos vendidos para este evento
        int vendidos = Repositorio.getInstance()
                .listarIngressosPorEvento(evento).size();
        dto.setIngressosVendidos(vendidos); // define a quantidade vendida
        // calcula os ingressos disponíveis (capacidade - vendidos)
        dto.setIngressosDisponiveis(evento.getCapacidadeMaxima() - vendidos);

        // retorna o DTO criado
        return dto;
    }

    // Atualiza uma entidade Evento existente com dados de um EventoRequestDTO
    public static void updateEntityFromDTO(EventoRequestDTO dto, Evento evento) {
        // verifica se o DTO ou a entidade são nulos e interrompe o metodo se forem
        if (dto == null || evento == null) return;

        // atualiza apenas os campos que não são nulos no DTO
        if (dto.getNome() != null) evento.setNome(dto.getNome());
        if (dto.getPaginaWeb() != null) evento.setPaginaWeb(dto.getPaginaWeb());
        if (dto.getDescricao() != null) evento.setDescricao(dto.getDescricao());
        if (dto.getDataInicio() != null) evento.setDataInicio(dto.getDataInicio());
        if (dto.getDataFim() != null) evento.setDataFim(dto.getDataFim());
        if (dto.getTipoEvento() != null) evento.setTipoEvento(dto.getTipoEvento());
        if (dto.getModalidade() != null) evento.setModalidade(dto.getModalidade());
        if (dto.getPrecoUnitarioIngresso() != null) evento.setPrecoUnitarioIngresso(dto.getPrecoUnitarioIngresso());
        if (dto.getTaxaCancelamentoIngresso() != null) evento.setTaxaCancelamentoIngresso(dto.getTaxaCancelamentoIngresso());
        if (dto.getCapacidadeMaxima() > 0) evento.setCapacidadeMaxima(dto.getCapacidadeMaxima());
        if (dto.getLocal() != null) evento.setLocal(dto.getLocal());
        // o ID do evento principal não é atualizado por este método
        // o organizador não é atualizado por este método
    }
}