package br.com.softhouse.dende.service;

import br.com.softhouse.dende.model.Empresa;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrganizadorService {

    private final Repositorio repositorio = Repositorio.getInstance();
    private final EventoService eventoService = new EventoService();

    // Cadastrar Organizador
    // [ITEM 1] O nome do método cadastrar() é muito genérico — não deixa claro que está cadastrando um Organizador.
    // Sugestão: renomeie para cadastrarOrganizador(Organizador organizador).
    // A nova assinatura ficaria: public Organizador cadastrarOrganizador(Organizador organizador)
    public Organizador cadastrar(Organizador organizador) {
        // [ITEM 3] A verificação "!= null" pode ser substituída por Objects.nonNull():
        // if (Objects.nonNull(repositorio.buscarUsuarioPorEmail(organizador.getEmail()))) { ... }
        if (repositorio.buscarUsuarioPorEmail(organizador.getEmail()) != null) {
            throw new IllegalArgumentException("E-mail já cadastrado!");
        }

        // [ITEM 6] A geração do ID e a atribuição ao organizador estão sendo feitas aqui no Service.
        // O ideal seria o Repositório ser responsável por gerar e atribuir IDs ao persistir,
        // mantendo essa responsabilidade na camada de persistência.
        organizador.setId(repositorio.gerarId());
        repositorio.salvarUsuario(organizador);
        return organizador;
    }

    // Atualizar Organizador
    // [ITEM 1] O nome do método atualizar() é muito genérico.
    // Sugestão: renomeie para atualizarOrganizador(String email, Organizador dadosAtualizados).
    // A nova assinatura ficaria: public Organizador atualizarOrganizador(String email, Organizador dadosAtualizados)
    // [ITEM 4] O parâmetro dadosAtualizados é um Organizador completo. Isso pode expor campos
    // que não deveriam ser alterados (como email e id). Considere criar um DTO de atualização
    // (AtualizarOrganizadorRequest) com apenas nome, dataNascimento, sexo, senha e empresa.
    // [ITEM 5] Um Mapper (ex: OrganizadorMapper) seria uma boa prática para converter entre
    // a entidade Organizador e os DTOs. Não era obrigatório nesta avaliação, mas seria o ideal.
    public Organizador atualizar(String email, Organizador dadosAtualizados) {
        Usuario usuario = repositorio.buscarUsuarioPorEmail(email);

        if (!(usuario instanceof Organizador)) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        Organizador organizador = (Organizador) usuario;

        // [ITEM 6] A atualização dos dados do perfil está sendo feita aqui no Service,
        // chamando diretamente organizador.alterarPerfil(). Isso é aceitável, mas a
        // responsabilidade de persistir a alteração poderia ser mais explícita no repositório,
        // por exemplo com um método repositorio.atualizarUsuario(organizador).
        organizador.alterarPerfil(dadosAtualizados.getNome(), dadosAtualizados.getDataNascimento(), dadosAtualizados.getSexo());

        // [ITEM 3] A verificação "!= null" pode ser substituída por Objects.nonNull(dadosAtualizados.getSenha())
        if (dadosAtualizados.getSenha() != null && !dadosAtualizados.getSenha().isEmpty()) {
            organizador.setSenha(dadosAtualizados.getSenha());
        }

        // [ITEM 3] A verificação "!= null" pode ser substituída por Objects.nonNull(dadosAtualizados.getEmpresa())
        if (dadosAtualizados.getEmpresa() != null) {
            organizador.setEmpresa(dadosAtualizados.getEmpresa());
        }

        return organizador;
    }

    // Desativar / Ativar Organizador
    // [ITEM 1] O nome alterarStatus() é genérico demais — não fica claro que controla ativação/desativação do organizador.
    // Sugestão: renomeie para alterarStatusOrganizador(String email, String status).
    // A nova assinatura ficaria: public void alterarStatusOrganizador(String email, String status)
    // [ITEM — US5] A verificação de eventos ativos ou em execução antes de desativar está presente,
    // o que atende à US5. Porém, note que estaEmExecucao() já está definido na classe Evento —
    // seria mais expressivo usar evento.isAtivo() || evento.estaEmExecucao() diretamente.
    public void alterarStatus(String email, String status) {
        Usuario usuario = repositorio.buscarUsuarioPorEmail(email);

        if (!(usuario instanceof Organizador)) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        Organizador organizador = (Organizador) usuario;

        if ("desativar".equalsIgnoreCase(status)) {
            // [ITEM 6] A filtragem de eventos ativos está sendo feita dentro do Service.
            // Seria mais coeso criar um método no Repositório como:
            // repositorio.organizadorTemEventosAtivos(organizador) para encapsular essa consulta.
            List<Evento> eventosAtivos = eventoService.listarEventosPorOrganizador(email).stream()
                    .filter(e -> e.isAtivo() && e.getDataFim().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toList());

            if (!eventosAtivos.isEmpty()) {
                throw new IllegalStateException("Organizador possui eventos ativos e não pode ser desativado");
            }

            organizador.desativar();
        } else if ("ativar".equalsIgnoreCase(status)) {
            organizador.ativar();
        } else {
            throw new IllegalArgumentException("Status inválido: " + status);
        }
    }

    // Listar Organizadores
    // [ITEM 1] O nome listar() é muito genérico — não deixa claro que lista organizadores.
    // Sugestão: renomeie para listarOrganizadores().
    // A nova assinatura ficaria: public List<Organizador> listarOrganizadores()
    public List<Organizador> listar() {
        return repositorio.listarUsuarios().stream()
                .filter(u -> u instanceof Organizador)
                .map(u -> (Organizador) u)
                .collect(Collectors.toList());
    }

    // Busca um organizador pelo email
    // [ITEM 8] O método retorna null via exceção quando não encontrado. Prefira retornar Optional<Organizador>.
    // Sugestão: public Optional<Organizador> buscarOrganizadorPorEmail(String email)
    // [ITEM 3] A verificação "== null" pode ser substituída por Objects.isNull(usuario)
    public Organizador buscarPorEmail(String email) {
        Usuario usuario = repositorio.buscarUsuarioPorEmail(email);

        if (usuario == null) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        if (!(usuario instanceof Organizador)) {
            throw new IllegalArgumentException("Usuário não é um organizador");
        }

        return (Organizador) usuario;
    }
}