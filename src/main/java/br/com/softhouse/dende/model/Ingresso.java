package br.com.softhouse.dende.model;

import br.com.softhouse.dende.model.EnumModel.StatusIngresso;

import java.time.LocalDateTime;
import java.util.Objects;

public class Ingresso {

    private int id;
    private Usuario usuario;
    private Evento evento;
    private StatusIngresso statusIngresso;
    private Double valorPago;
    private LocalDateTime dataCompra;


    public Ingresso(int id, Usuario usuario, Evento evento, Double valorPago) {
        this.id = id;
        this.usuario = usuario;
        this.evento = evento;
        this.valorPago = valorPago;
        this.statusIngresso = StatusIngresso.ATIVO;
        this.dataCompra = LocalDateTime.now();
    }
    public Ingresso(){
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

    public Double getValorPago() {
        return valorPago;
    }

    public void setValorPago(Double valorPago) {
        this.valorPago = valorPago;
    }

    public LocalDateTime getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(LocalDateTime dataCompra) {
        this.dataCompra = dataCompra;
    }

// =========================
    // REGRAS DE NEGÓCIO
    // =========================

    /**
     * Cancela o ingresso conforme a User Story
     * O ingresso só pode ser cancelado se estiver ATIVO
     */
    public Double cancelar() {

        if (this.statusIngresso == StatusIngresso.CANCELADO) {
            throw new IllegalStateException(
                    "Ingresso já está cancelado."
            );
        }

        this.statusIngresso = StatusIngresso.CANCELADO;

        // Calcula valor de estorno com base na taxa do evento
        double taxa = evento.getTaxaCancelamentoIngresso();
        return valorPago - (valorPago * taxa);
    }

    /**
     * Verifica se o ingresso é de um evento futuro
     * Usado para ordenação
     */
    public boolean eventoAindaNaoOcorreu() {
        return evento.getDataInicio().isAfter(LocalDateTime.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ingresso)) return false;
        Ingresso ingresso = (Ingresso) o;
        return id == ingresso.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Ingresso{" +
                "id=" + id +
                ", evento=" + evento.getNome() +
                ", status=" + statusIngresso +
                ", valorPago=" + valorPago +
                '}';
    }
}
