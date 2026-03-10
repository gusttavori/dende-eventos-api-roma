package br.com.softhouse.dende.dto.request;

import br.com.softhouse.dende.model.EnumModel.ModalidadeEvento;
import br.com.softhouse.dende.model.EnumModel.TipoEvento;
import java.time.LocalDateTime;

public class EventoRequestDTO {
    private String nome;
    private String paginaWeb;
    private String descricao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private TipoEvento tipoEvento;
    private ModalidadeEvento modalidade;
    private Double precoUnitarioIngresso;
    private Double taxaCancelamentoIngresso;
    private int capacidadeMaxima;
    private String local;
    private int eventoPrincipalId;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getPaginaWeb() { return paginaWeb; }
    public void setPaginaWeb(String paginaWeb) { this.paginaWeb = paginaWeb; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
    public TipoEvento getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(TipoEvento tipoEvento) { this.tipoEvento = tipoEvento; }
    public ModalidadeEvento getModalidade() { return modalidade; }
    public void setModalidade(ModalidadeEvento modalidade) { this.modalidade = modalidade; }
    public Double getPrecoUnitarioIngresso() { return precoUnitarioIngresso; }
    public void setPrecoUnitarioIngresso(Double precoUnitarioIngresso) { this.precoUnitarioIngresso = precoUnitarioIngresso; }
    public Double getTaxaCancelamentoIngresso() { return taxaCancelamentoIngresso; }
    public void setTaxaCancelamentoIngresso(Double taxaCancelamentoIngresso) { this.taxaCancelamentoIngresso = taxaCancelamentoIngresso; }
    public int getCapacidadeMaxima() { return capacidadeMaxima; }
    public void setCapacidadeMaxima(int capacidadeMaxima) { this.capacidadeMaxima = capacidadeMaxima; }
    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }
    public int getEventoPrincipalId() { return eventoPrincipalId; }
    public void setEventoPrincipalId(int eventoPrincipalId) { this.eventoPrincipalId = eventoPrincipalId; }
}