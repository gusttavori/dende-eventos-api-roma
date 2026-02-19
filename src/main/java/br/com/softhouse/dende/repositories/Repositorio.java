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

    // Metodo público estático que retorna a única instância do repositório
    public static Repositorio getInstance() {
        return INSTANCE;
    }

    // Armazena os usuários em memória usando um mapa onde a chave é o ID e o valor é o objeto Usuario
    private final Map<Integer, Usuario> usuarios = new HashMap<>();
    // Armazena os eventos em memória usando um mapa onde a chave é o ID e o valor é o objeto Evento
    private final Map<Integer, Evento> eventos = new HashMap<>();
    // Armazena os ingressos em memória usando um mapa onde a chave é o ID e o valor é o objeto Ingresso
    private final Map<Integer, Ingresso> ingressos = new HashMap<>();
    // Gerador atômico de IDs para garantir que cada novo objeto receba um ID único e thread-safe
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    // Incrementa e retorna o próximo ID disponível
    public Integer gerarId() {
        return idGenerator.getAndIncrement();
    }

    // ===== USUÁRIOS =====

    // Salva um usuário no mapa usando seu ID como chave
    public void salvarUsuario(Usuario usuario) {
        usuarios.put(usuario.getId(), usuario);
    }

    // Busca um usuário pelo ID, retornando null se não encontrado
    public Usuario buscarUsuarioPorId(Integer id) {
        return usuarios.get(id);
    }

    // Busca um usuário pelo email, ignorando diferenças entre maiúsculas e minúsculas
    public Usuario buscarUsuarioPorEmail(String email) {
        return usuarios.values()
                .stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
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
    public Evento buscarEventoPorId(int id) {
        return eventos.get(id);
    }

    // Metodo sobrecarregado que faz o mesmo que buscarEventoPorId
    public Evento buscarEvento(int id) {
        return buscarEventoPorId(id);
    }

    // Retorna uma lista com todos os eventos cadastrados
    public List<Evento> listarEventos() {
        return new ArrayList<>(eventos.values());
    }

    // Retorna uma lista apenas com os eventos ativos e que ainda não foram finalizados
    public List<Evento> listarEventosAtivos() {
        // Obtém a data e hora atual para comparação
        LocalDateTime agora = LocalDateTime.now();
        return eventos.values().stream()
                .filter(Evento::isAtivo)  // Filtra apenas eventos com status ativo
                .filter(e -> e.getDataFim().isAfter(agora))  // Filtra eventos cuja data de fim ainda não passou
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