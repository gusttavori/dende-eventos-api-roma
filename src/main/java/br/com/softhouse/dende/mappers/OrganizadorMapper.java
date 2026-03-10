package br.com.softhouse.dende.mappers;

import br.com.softhouse.dende.dto.OrganizadorDTO;
import br.com.softhouse.dende.dto.request.UsuarioRequestDTO;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.repositories.Repositorio;

// Classe responsável por converter entre entidades Organizador e DTOs (Data Transfer Objects)
public class OrganizadorMapper {

    // Converte um UsuarioRequestDTO para uma entidade Organizador
    public static Organizador toEntity(UsuarioRequestDTO dto) {
        // verifica se o DTO é nulo e retorna nulo se for
        if (dto == null) return null;

        // cria uma nova instância de Organizador
        Organizador organizador = new Organizador();
        // copia os dados básicos do DTO para a entidade
        organizador.setNome(dto.getNome());
        organizador.setDataNascimento(dto.getDataNascimento());
        organizador.setSexo(dto.getSexo());
        organizador.setEmail(dto.getEmail());
        organizador.setSenha(dto.getSenha());
        // retorna a entidade criada
        return organizador;
    }

    // Converte uma entidade Organizador para um OrganizadorDTO
    public static OrganizadorDTO toDTO(Organizador organizador) {
        // verifica se a entidade é nula e retorna nulo se for
        if (organizador == null) return null;

        // cria uma nova instância de OrganizadorDTO
        OrganizadorDTO dto = new OrganizadorDTO();
        // copia os dados básicos da entidade para o DTO
        dto.setId(organizador.getId());
        dto.setNome(organizador.getNome());
        dto.setDataNascimento(organizador.getDataNascimento());
        dto.setIdade(organizador.getIdade());
        dto.setSexo(organizador.getSexo());
        dto.setEmail(organizador.getEmail());
        dto.setAtivo(organizador.isAtivo());
        dto.setTipo("ORGANIZADOR"); // define o tipo fixo como ORGANIZADOR
        dto.setEmpresa(organizador.getEmpresa()); // copia o nome da empresa do organizador

        // calcula o total de eventos deste organizador acessando o repositório
        int totalEventos = Repositorio.getInstance() // obtém a instância do repositório
                .listarEventosPorOrganizador(organizador).size(); // conta quantos eventos o organizador possui
        dto.setTotalEventos(totalEventos); // define o total de eventos no DTO

        // retorna o DTO criado
        return dto;
    }

    // Atualiza uma entidade Organizador existente com dados de um UsuarioRequestDTO
    public static void updateEntityFromDTO(UsuarioRequestDTO dto, Organizador organizador) {
        // verifica se o DTO ou a entidade são nulos e interrompe o metodo se forem
        if (dto == null || organizador == null) return;

        // atualiza apenas os campos que não são nulos no DTO
        if (dto.getNome() != null) organizador.setNome(dto.getNome());
        if (dto.getDataNascimento() != null) organizador.setDataNascimento(dto.getDataNascimento());
        if (dto.getSexo() != null) organizador.setSexo(dto.getSexo());
        if (dto.getSenha() != null) organizador.setSenha(dto.getSenha());
        // o email não é atualizado pois geralmente é usado como identificador
        // a empresa não é atualizada pois não está presente no UsuarioRequestDTO
    }
}