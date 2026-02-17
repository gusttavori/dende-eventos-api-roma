package br.com.softhouse.dende.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um usuário organizador de eventos.
 * Atende às User Stories 2, 5, 7, 8, 9, 10 e 11.
 */
public class Organizador extends Usuario {

    private Empresa empresa;
    private List<Evento> eventos = new ArrayList<>();

    public Organizador(Integer id, String nome, LocalDate dataNascimento, String sexo, String email, String senha, Empresa empresa) {
        super(id, nome, dataNascimento, sexo, email, senha);
        this.empresa = empresa;
    }

    public Organizador(Integer id, String nome, LocalDate dataNascimento, String sexo, String email, String senha) {
        super(id, nome, dataNascimento, sexo, email, senha);
    }

    public Organizador() {
    }


    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    /**
     * Retorna a lista de eventos do organizador
     */
    public List<Evento> getEventos() {
        return eventos;
    }

    // =========================
    // REGRAS DE NEGÓCIO
    // =========================

    /**
     * Cadastra um novo evento para este organizador
     */
    public void cadastrarEvento(Evento evento) {
        evento.setOrganizador(this);
        eventos.add(evento);
    }

    /**
     * Verifica se o organizador possui eventos ativos ou em execução
     */
    public boolean temEventoAtivoOuEmExecucao() {
        return eventos.stream()
                .anyMatch(e -> e.isAtivo() || e.estaEmExecucao());
    }

    /**
     * Um organizador só pode ser desativado se não tiver
     * eventos ativos ou em execução
     */
    @Override
    public void desativar() {
        if (temEventoAtivoOuEmExecucao()) {
            throw new IllegalStateException(
                    "Organizador não pode ser desativado com eventos ativos ou em execução"
            );
        }
        super.desativar();
    }

    @Override
    public String toString() {
        return "Organizador: " +
                super.toString() +
                (empresa != null
                        ? "\nEmpresa: " + empresa
                        : "\nNão possui empresa cadastrada") +
                "\nTotal de eventos cadastrados: " + eventos.size();
    }
}
