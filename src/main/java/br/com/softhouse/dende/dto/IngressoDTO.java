package br.com.softhouse.dende.dto;

import br.com.softhouse.dende.model.EnumModel.StatusIngresso;
import java.time.LocalDateTime;

public class IngressoDTO {
    private int id;
    private String usuarioNome;
    private String usuarioEmail;
    private String eventoNome;
    private int eventoId;
    private StatusIngresso status;
    private Double valorPago;
    private LocalDateTime dataCompra;
    private boolean eventoFuturo;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsuarioNome() { return usuarioNome; }
    public void setUsuarioNome(String usuarioNome) { this.usuarioNome = usuarioNome; }
    public String getUsuarioEmail() { return usuarioEmail; }
    public void setUsuarioEmail(String usuarioEmail) { this.usuarioEmail = usuarioEmail; }
    public String getEventoNome() { return eventoNome; }
    public void setEventoNome(String eventoNome) { this.eventoNome = eventoNome; }
    public int getEventoId() { return eventoId; }
    public void setEventoId(int eventoId) { this.eventoId = eventoId; }
    public StatusIngresso getStatus() { return status; }
    public void setStatus(StatusIngresso status) { this.status = status; }
    public Double getValorPago() { return valorPago; }
    public void setValorPago(Double valorPago) { this.valorPago = valorPago; }
    public LocalDateTime getDataCompra() { return dataCompra; }
    public void setDataCompra(LocalDateTime dataCompra) { this.dataCompra = dataCompra; }
    public boolean isEventoFuturo() { return eventoFuturo; }
    public void setEventoFuturo(boolean eventoFuturo) { this.eventoFuturo = eventoFuturo; }
}