package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Repositorio {

    private static final Repositorio instance = new Repositorio();
    private Repositorio() {}

    public static Repositorio getInstance() {
        return instance;
    }

    // Armazenamento
    private final Map<String, Usuario> usuarios = new HashMap<>();
    private final Map<Integer, Evento> eventos = new HashMap<>();
    private final Map<Integer, Ingresso> ingressos = new HashMap<>();

    // Gerador de ID único
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    public int gerarId() {
        return idGenerator.getAndIncrement();
    }

    // ================= USUÁRIOS =================
    public void salvarUsuario(Usuario usuario) {
        usuarios.put(usuario.getEmail(), usuario);
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        return usuarios.get(email);
    }

    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios.values());
    }

    // ================= EVENTOS =================
    public void salvarEvento(Evento evento) {
        eventos.put(evento.getId(), evento);
    }

    public Evento buscarEventoPorId(int id) {
        return eventos.get(id);
    }

    public List<Evento> listarEventos() {
        return new ArrayList<>(eventos.values());
    }

    // ================= INGRESSOS =================
    public void salvarIngresso(Ingresso ingresso) {
        ingressos.put(ingresso.getId(), ingresso);
    }

    public Ingresso buscarIngressoPorId(int id) {
        return ingressos.get(id);
    }

    public List<Ingresso> listarIngressos() {
        return new ArrayList<>(ingressos.values());
    }
}
