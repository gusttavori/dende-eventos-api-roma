package br.com.softhouse.dende.repositories;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Usuario;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Repositorio {

    private final Map<String, Usuario> usuarios = new HashMap<>();
    private final Map<Integer, Evento> eventos = new HashMap<>();
    private final Map<Integer, Ingresso> ingressos = new HashMap<>();

    private Repositorio(){}

    public static Repositorio getInstance() {
        return instance;
    }

    public void salvarUsuario(Usuario u) {
        usuarios.put(u.getEmail(), u);
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        return usuarios.get(email);
    }

    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios.values());
    }
}
