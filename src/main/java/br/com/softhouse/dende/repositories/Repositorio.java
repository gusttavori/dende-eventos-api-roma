package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.Organizador;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Repositorio {

    private static final Repositorio INSTANCE = new Repositorio();

    private Repositorio() {}

    public static Repositorio getInstance() {
        return INSTANCE;
    }

    private final Map<Integer, Usuario> usuarios = new HashMap<>();
    private final Map<Integer, Evento> eventos = new HashMap<>();
    private final Map<Integer, Ingresso> ingressos = new HashMap<>();

    // [ITEM 13] Existe um único contador (idGenerator) compartilhado entre Usuário, Evento e Ingresso.
    // Isso faz com que os IDs de todas as entidades concorram na mesma sequência — se você criar
    // 3 usuários, o próximo evento terá ID 4, não ID 1. Cada entidade deveria crescer
    // de forma independente.
    // Sugestão: crie contadores separados:
    // private final AtomicInteger contadorUsuario = new AtomicInteger(1);
    // private final AtomicInteger contadorEvento  = new AtomicInteger(1);
    // private final AtomicInteger contadorIngresso = new AtomicInteger(1);
    // E métodos correspondentes: gerarIdUsuario(), gerarIdEvento(), gerarIdIngresso().
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    // [ITEM 12] O repositório usa apenas Map<Integer, Usuario> para usuários.
    // Para otimizar buscas por e-mail (que ocorrem com frequência — login, validação de unicidade),
    // seria interessante ter um segundo mapa indexado pelo e-mail.
    // Porém, se você adicionar Map<String, Usuario> usuariosPorEmail, surgem dois mapas para a mesma entidade.
    // Nesse caso, a boa prática seria criar uma classe de chave composta:
    //   public class UsuarioKey {
    //       private final Integer id;
    //       private final String email;
    //   }
    // E usar Map<UsuarioKey, Usuario>. Todas as inserções e buscas passariam pela UsuarioKey.
    // Exemplo de inserção com chave composta:
    //   usuarios.put(new UsuarioKey(usuario.getId(), usuario.getEmail()), usuario);
    // Exemplo de busca por e-mail:
    //   usuarios.entrySet().stream()
    //       .filter(e -> e.getKey().getEmail().equalsIgnoreCase(email))
    //       .map(Map.Entry::getValue).findFirst().orElse(null);

    // Incrementa e retorna o próximo ID disponível
    public Integer gerarId() {
        return idGenerator.getAndIncrement();
    }

    // ===== USUÁRIOS =====

    // Salva um usuário no mapa usando seu ID como chave
    public void salvarUsuario(Usuario usuario) {
        // [ITEM 12] Se fosse usada a chave composta UsuarioKey, a inserção ficaria:
        // usuarios.put(new UsuarioKey(usuario.getId(), usuario.getEmail()), usuario);
        usuarios.put(usuario.getId(), usuario);
    }

    // Busca um usuário pelo ID, retornando null se não encontrado
    // [ITEM 8] Este método retorna null quando o usuário não é encontrado.
    // Prefira retornar Optional<Usuario> para que o chamador trate explicitamente a ausência.
    // Sugestão: public Optional<Usuario> buscarUsuarioPorId(Integer id) { return Optional.ofNullable(usuarios.get(id)); }
    public Usuario buscarUsuarioPorId(Integer id) {
        // [ITEM 12] Com chave composta, a busca por id seria feita filtrando as chaves:
        // return usuarios.entrySet().stream()
        //     .filter(e -> e.getKey().getId().equals(id))
        //     .map(Map.Entry::getValue).findFirst().orElse(null);
        return usuarios.get(id);
    }

    // Busca um usuário pelo email, ignorando diferenças entre maiúsculas e minúsculas
    // [ITEM 8] Este método retorna null quando o usuário não é encontrado.
    // Prefira retornar Optional<Usuario>:
    // public Optional<Usuario> buscarUsuarioPorEmail(String email)
    public Usuario buscarUsuarioPorEmail(String email) {
        return usuarios.values()
                .stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                // [ITEM 8] Em vez de .orElse(null), use .orElse(null) apenas se necessário,
                // mas prefira retornar Optional<Usuario> para forçar o tratamento no chamador.
                .orElse(null);
    }

    // Retorna uma lista com todos os usuários cadastrados
    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios.values());
    }

    // ===== EVENTOS =====

    // Salva um evento no mapa usando seu ID como chave
    public void salvarEvento(Evento evento) {
        eventos.put(evento.getId(), evento);
    }

    // Busca um evento pelo ID, retornando null se não encontrado
    // [ITEM 8] Este método retorna null quando o evento não é encontrado.
    // Prefira retornar Optional<Evento>:
    // public Optional<Evento> buscarEventoPorId(int id) { return Optional.ofNullable(eventos.get(id)); }
    public Evento buscarEventoPorId(int id) {
        return eventos.get(id);
    }

    // [ITEM 1] O método buscarEvento() é um duplicado de buscarEventoPorId().
    // Ter dois métodos com o mesmo comportamento e nomes diferentes causa confusão.
    // Sugestão: remova buscarEvento() e use apenas buscarEventoPorId() em todo o código.
    public Evento buscarEvento(int id) {
        return buscarEventoPorId(id);
    }

    // Retorna uma lista com todos os eventos cadastrados
    public List<Evento> listarEventos() {
        return new ArrayList<>(eventos.values());
    }

    // [ITEM — US12] listarEventosAtivos() filtra eventos ativos e não finalizados, mas NÃO verifica
    // se o evento ainda tem vagas disponíveis (capacidadeMaxima > ingressos vendidos).
    // A US12 exige que eventos com ingressos esgotados não apareçam no feed.
    // Sugestão: ajuste este método ou crie listarEventosFeed() que aplique o filtro de vagas.
    public List<Evento> listarEventosAtivos() {
        LocalDateTime agora = LocalDateTime.now();
        return eventos.values().stream()
                .filter(Evento::isAtivo)
                .filter(e -> e.getDataFim().isAfter(agora))
                .collect(Collectors.toList());
    }

    // Retorna uma lista de eventos pertencentes a um organizador específico
    public List<Evento> listarEventosPorOrganizador(Organizador organizador) {
        return eventos.values().stream()
                .filter(e -> e.getOrganizador().equals(organizador))
                .collect(Collectors.toList());
    }

    // ===== INGRESSOS =====

    // Salva um ingresso no mapa usando seu ID como chave
    public void salvarIngresso(Ingresso ingresso) {
        ingressos.put(ingresso.getId(), ingresso);
    }

    // Busca um ingresso pelo ID, retornando null se não encontrado
    // [ITEM 8] Este método retorna null quando o ingresso não é encontrado.
    // Prefira retornar Optional<Ingresso>:
    // public Optional<Ingresso> buscarIngressoPorId(int id) { return Optional.ofNullable(ingressos.get(id)); }
    public Ingresso buscarIngressoPorId(int id) {
        return ingressos.get(id);
    }

    // Retorna uma lista com todos os ingressos cadastrados
    public List<Ingresso> listarIngressos() {
        return new ArrayList<>(ingressos.values());
    }

    // Retorna uma lista de ingressos comprados por um usuário específico
    public List<Ingresso> listarIngressosPorUsuario(Usuario usuario) {
        return ingressos.values().stream()
                .filter(i -> i.getUsuario().equals(usuario))
                .collect(Collectors.toList());
    }

    // Retorna uma lista de ingressos vendidos para um evento específico
    public List<Ingresso> listarIngressosPorEvento(Evento evento) {
        return ingressos.values().stream()
                .filter(i -> i.getEvento().equals(evento))
                .collect(Collectors.toList());
    }
}