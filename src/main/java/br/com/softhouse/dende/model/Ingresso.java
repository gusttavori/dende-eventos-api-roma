package br.com.softhouse.dende.model;

import br.com.softhouse.dende.model.EnumModel.StatusIngresso;

public class Ingresso {
    private int id;
    private Usuario usuario;
    private Evento evento;
    private StatusIngresso statusIngresso;

    public Ingresso(int id, Usuario usuario, Evento evento, StatusIngresso statusIngresso) {
        this.id = id;
        this.usuario = usuario;
        this.evento = evento;
        this.statusIngresso = statusIngresso;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public StatusIngresso getStatusIngresso() {
        return statusIngresso;
    }

    public void setStatusIngresso(StatusIngresso statusIngresso) {
        this.statusIngresso = statusIngresso;
    }

    @Override
    public String toString() {
        return "Ingresso{" +
                "id=" + id +
                ", usuario=" + usuario +
                ", evento=" + evento +
                ", statusIngresso=" + statusIngresso +
                '}';
    }
}
