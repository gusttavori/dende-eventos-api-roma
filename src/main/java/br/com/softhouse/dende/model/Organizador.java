package br.com.softhouse.dende.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Organizador extends Usuario {

    private Empresa empresa;
    private List<Evento> eventos = new ArrayList<>();

    public Organizador() {}

    public Organizador(Integer id, String nome, LocalDate dataNascimento,
                       String sexo, String email, String senha, Empresa empresa) {
        super(id, nome, dataNascimento, sexo, email, senha);
        this.empresa = empresa;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public void cadastrarEvento(Evento evento) {
        this.eventos.add(evento);
    }

    @Override
    public String toString() {
        return "Organizador\n" + super.toString() +
                (empresa != null ? "\nEmpresa: " + empresa.getNomeFantasia() : "");
    }
}