package br.com.softhouse.dende.service;

import br.com.softhouse.dende.dto.request.UsuarioRequestDTO;
import br.com.softhouse.dende.mappers.OrganizadorMapper;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrganizadorService {

    // obtém a instância única do repositório (singleton)
    private final Repositorio repositorio = Repositorio.getInstance();
    // cria uma instância do serviço de eventos para operações relacionadas
    private final EventoService eventoService = new EventoService();

    // mwtodo para cadastrar um novo organizador
    public Organizador cadastrar(Organizador organizador) {
        // verifica se já existe um usuário com o email do organizador
        if (repositorio.buscarUsuarioPorEmail(organizador.getEmail()) != null) {
            // lança exceção se o email já estiver cadastrado
            throw new IllegalArgumentException("E-mail já cadastrado!");
        }

        // define um ID automático para o organizador
        organizador.setId(repositorio.gerarId());
        // salva o organizador no repositório
        repositorio.salvarUsuario(organizador);
        // retorna o organizador cadastrado
        return organizador;
    }

    // metodo para atualizar os dados de um organizador
    public Organizador atualizar(String email, UsuarioRequestDTO dadosAtualizados) {
        // busca o usuário pelo email no repositório
        Usuario usuario = repositorio.buscarUsuarioPorEmail(email);

        // verifica se o usuário encontrado é do tipo Organizador
        if (!(usuario instanceof Organizador)) {
            // lança exceção se não for um organizador
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        // faz o cast do usuário para Organizador
        Organizador organizador = (Organizador) usuario;
        // atualiza os dados do organizador usando o mapper específico
        OrganizadorMapper.updateEntityFromDTO(dadosAtualizados, organizador);

        // retorna o organizador atualizado
        return organizador;
    }

    // metodo para ativar ou desativar um organizador
    public void alterarStatus(String email, String status) {
        // busca o usuário pelo email no repositório
        Usuario usuario = repositorio.buscarUsuarioPorEmail(email);

        // verifica se o usuário encontrado é do tipo Organizador
        if (!(usuario instanceof Organizador)) {
            // lança exceção se não for um organizador
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        // faz o cast do usuário para Organizador
        Organizador organizador = (Organizador) usuario;

        // verifica se o status é "desativar" (ignorando maiúsculas/minúsculas)
        if ("desativar".equalsIgnoreCase(status)) {
            // lista os eventos ativos do organizador que ainda não terminaram
            List<Evento> eventosAtivos = eventoService.listarEventosPorOrganizador(email).stream()
                    .filter(e -> e.isAtivo() && e.getDataFim().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList());

            // verifica se existem eventos ativos
            if (!eventosAtivos.isEmpty()) {
                // impede a desativação se houver eventos ativos
                throw new IllegalStateException("Organizador possui eventos ativos e não pode ser desativado");
            }

            // desativa o organizador
            organizador.desativar();
            // verifica se o status é "ativar" (ignorando maiúsculas/minúsculas)
        } else if ("ativar".equalsIgnoreCase(status)) {
            // ativa o organizador
            organizador.ativar();
        } else {
            // lança exceção se o status não for válido
            throw new IllegalArgumentException("Status inválido: " + status);
        }
    }

    // metodo para listar todos os organizadores
    public List<Organizador> listar() {
        // filtra todos os usuários do repositório, mantendo apenas os do tipo Organizador
        return repositorio.listarUsuarios().stream()
                .filter(u -> u instanceof Organizador)  // mantém apenas Organizadores
                .map(u -> (Organizador) u)               // faz o cast para Organizador
                .collect(Collectors.toList());           // coleta o resultado em uma lista
    }

    // metodo para buscar um organizador pelo email
    public Organizador buscarPorEmail(String email) {
        // busca o usuário pelo email no repositório
        Usuario usuario = repositorio.buscarUsuarioPorEmail(email);

        // verifica se o usuário foi encontrado
        if (usuario == null) {
            // lança exceção se não existir
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        // verifica se o usuário encontrado é do tipo Organizador
        if (!(usuario instanceof Organizador)) {
            // lança exceção se não for um organizador
            throw new IllegalArgumentException("Usuário não é um organizador");
        }

        // retorna o organizador encontrado (fazendo o cast)
        return (Organizador) usuario;
    }
}