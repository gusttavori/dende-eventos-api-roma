package br.com.softhouse.dende.mappers;

import br.com.softhouse.dende.dto.IngressoDTO;
import br.com.softhouse.dende.model.Ingresso;
import java.time.LocalDateTime;

// Classe responsável por converter entre entidades Ingresso e DTOs (Data Transfer Objects)
public class IngressoMapper {

    // Converte uma entidade Ingresso para um IngressoDTO
    public static IngressoDTO toDTO(Ingresso ingresso) {
        // verifica se a entidade é nula e retorna nulo se for
        if (ingresso == null) return null;

        // cria uma nova instância de IngressoDTO
        IngressoDTO dto = new IngressoDTO();
        // copia o ID do ingresso
        dto.setId(ingresso.getId());
        // copia o nome do usuário comprador (acessando o objeto Usuario relacionado)
        dto.setUsuarioNome(ingresso.getUsuario().getNome());
        // copia o email do usuário comprador
        dto.setUsuarioEmail(ingresso.getUsuario().getEmail());
        // copia o nome do evento (acessando o objeto Evento relacionado)
        dto.setEventoNome(ingresso.getEvento().getNome());
        // copia o ID do evento
        dto.setEventoId(ingresso.getEvento().getId());
        // copia o status do ingresso (ATIVO, CANCELADO, UTILIZADO, etc)
        dto.setStatus(ingresso.getStatusIngresso());
        // copia o valor pago pelo ingresso
        dto.setValorPago(ingresso.getValorPago());
        // copia a data e hora da compra
        dto.setDataCompra(ingresso.getDataCompra());
        // verifica se a data de início do evento é posterior ao momento atual
        // define true se o evento ainda não aconteceu, false se já passou
        dto.setEventoFuturo(ingresso.getEvento().getDataInicio().isAfter(LocalDateTime.now()));

        // retorna o DTO criado com todos os dados preenchidos
        return dto;
    }
}