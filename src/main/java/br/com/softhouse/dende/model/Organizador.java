package br.com.softhouse.dende.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Organizador extends Usuario {

    private Empresa empresa;

    // [ITEM — Relacionamento] A lista de eventos dentro do Organizador representa um relacionamento
    // de composição correto em OO. Porém, manter essa lista sincronizada manualmente com o repositório
    // (via cadastrarEvento) é frágil. O ideal seria que o repositório fosse a única fonte de verdade.
    @JsonIgnore
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

    // [ITEM 1] O método cadastrarEvento() tem um nome adequado, porém ele apenas adiciona à lista local
    // do organizador. Isso é uma duplicação de estado — o evento já é persistido no Repositorio.
    // Sugestão: avalie se essa lista local é realmente necessária, ou se os eventos devem ser
    // sempre consultados diretamente no repositório para evitar inconsistências.
    public void cadastrarEvento(Evento evento) {
        this.eventos.add(evento);
    }

    @Override
    public String toString() {
        return "Organizador\n" + super.toString() +
                (empresa != null ? "\nEmpresa: " + empresa.getNomeFantasia() : "");
    }
}