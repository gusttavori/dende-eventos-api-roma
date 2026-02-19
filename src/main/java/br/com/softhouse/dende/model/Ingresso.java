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

    // REGRAS DE NEGÓCIO

    // Cancela o ingresso - O ingresso só pode ser cancelado se estiver ATIVO
    public Double cancelar() {
        // Verifica se o ingresso já está cancelado
        if (this.statusIngresso == StatusIngresso.CANCELADO) {
            // Lança exceção se tentar cancelar um ingresso já cancelado
            throw new IllegalStateException("Ingresso já está cancelado.");
        }

        // Altera o status do ingresso para CANCELADO
        this.statusIngresso = StatusIngresso.CANCELADO;
        // Calcula valor de estorno com base na taxa do evento
        // Obtém a taxa de cancelamento definida no evento
        double taxa = evento.getTaxaCancelamentoIngresso();
        double valorEstorno = valorPago - (valorPago * taxa);
        System.out.println("Ingresso " + id + " cancelado. Valor pago: " + valorPago + ", Taxa: " + taxa + "%, Estorno: " + valorEstorno);
        return valorEstorno;
    }

    // Verifica se o ingresso é de um evento futuro usado para ordenação
    public boolean eventoAindaNaoOcorreu() {
        // Compara a data de início do evento com a data e hora atual
        // Retorna verdadeiro se o evento ainda não começou
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