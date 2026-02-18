package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.Organizador;

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
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    public Integer gerarId() {
        return idGenerator.getAndIncrement();
    }

    // ===== USUÁRIOS =====
    public void salvarUsuario(Usuario usuario) {
        usuarios.put(usuario.getId(), usuario);
    }

    public Usuario buscarUsuarioPorId(Integer id) {
        return usuarios.get(id);
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        return usuarios.values()
                .stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios.values());
    }

    // ===== EVENTOS =====
    public void salvarEvento(Evento evento) {
        eventos.put(evento.getId(), evento);
    }

    public Evento buscarEventoPorId(int id) {
        return eventos.get(id);
    }

    public Evento buscarEvento(int id) {
        return buscarEventoPorId(id);
    }

    public List<Evento> listarEventos() {
        return new ArrayList<>(eventos.values());
    }

    public List<Evento> listarEventosAtivos() {
        return eventos.values().stream()
                .filter(Evento::isAtivo)
                .filter(e -> e.getDataFim().isAfter(java.time.LocalDateTime.now()))
                .collect(Collectors.toList());
    }

    public List<Evento> listarEventosPorOrganizador(Organizador organizador) {
        return eventos.values().stream()
                .filter(e -> e.getOrganizador().equals(organizador))
                .collect(Collectors.toList());
    }

    // ===== INGRESSOS =====
    public void salvarIngresso(Ingresso ingresso) {
        ingressos.put(ingresso.getId(), ingresso);
    }

    public Ingresso buscarIngressoPorId(int id) {
        return ingressos.get(id);
    }

    public List<Ingresso> listarIngressos() {
        return new ArrayList<>(ingressos.values());
    }

    public List<Ingresso> listarIngressosPorUsuario(Usuario usuario) {
        return ingressos.values().stream()
                .filter(i -> i.getUsuario().equals(usuario))
                .collect(Collectors.toList());
    }

    public List<Ingresso> listarIngressosPorEvento(Evento evento) {
        return ingressos.values().stream()
                .filter(i -> i.getEvento().equals(evento))
                .collect(Collectors.toList());
    }
}