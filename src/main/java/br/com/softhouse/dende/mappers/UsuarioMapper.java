package br.com.softhouse.dende.mappers;

import br.com.softhouse.dende.dto.UsuarioDTO;
import br.com.softhouse.dende.dto.request.UsuarioRequestDTO;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.UsuarioComum;

// Classe responsável por converter entre entidades Usuario e DTOs (Data Transfer Objects)
public class UsuarioMapper {

    // Converte um UsuarioRequestDTO para uma entidade UsuarioComum
    public static UsuarioComum toEntity(UsuarioRequestDTO dto) {
        // verifica se o DTO é nulo e retorna nulo se for
        if (dto == null) return null;

        // cria uma nova instância de UsuarioComum
        UsuarioComum usuario = new UsuarioComum();
        // copia os dados do DTO para a entidade
        usuario.setNome(dto.getNome());
        usuario.setDataNascimento(dto.getDataNascimento());
        usuario.setSexo(dto.getSexo());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());
        // retorna a entidade criada
        return usuario;
    }

    // Converte uma entidade Usuario para um UsuarioDTO
    public static UsuarioDTO toDTO(Usuario usuario) {
        // verifica se a entidade é nula e retorna nulo se for
        if (usuario == null) return null;

        // cria uma nova instância de UsuarioDTO
        UsuarioDTO dto = new UsuarioDTO();
        // copia os dados da entidade para o DTO
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setDataNascimento(usuario.getDataNascimento());
        dto.setIdade(usuario.getIdade());
        dto.setSexo(usuario.getSexo());
        dto.setEmail(usuario.getEmail());
        dto.setAtivo(usuario.isAtivo());
        // verifica o tipo do usuário (Organizador ou Comum) e define no DTO
        dto.setTipo(usuario instanceof Organizador ? "ORGANIZADOR" : "COMUM");

        // retorna o DTO criado
        return dto;
    }

    // Atualiza uma entidade Usuario existente com dados de um UsuarioRequestDTO
    public static void updateEntityFromDTO(UsuarioRequestDTO dto, Usuario usuario) {
        // verifica se o DTO ou a entidade são nulos e interrompe o metodo se forem
        if (dto == null || usuario == null) return;

        // atualiza apenas os campos que não são nulos no DTO
        if (dto.getNome() != null) usuario.setNome(dto.getNome());
        if (dto.getDataNascimento() != null) usuario.setDataNascimento(dto.getDataNascimento());
        if (dto.getSexo() != null) usuario.setSexo(dto.getSexo());
        if (dto.getSenha() != null) usuario.setSenha(dto.getSenha());
        // o email não é atualizado pois geralmente é usado como identificador
    }
}