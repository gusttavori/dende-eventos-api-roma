package br.com.softhouse.dende.dto;

import br.com.softhouse.dende.model.EnumModel.ModalidadeEvento;
import br.com.softhouse.dende.model.EnumModel.TipoEvento;
import java.time.LocalDateTime;

public class EventoDTO {
    private int id;
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
    private int ingressosVendidos;
    private int ingressosDisponiveis;
    private String local;
    private boolean ativo;
    private String organizadorNome;
    private String organizadorEmail;
    private Integer eventoPrincipalId;
    private String eventoPrincipalNome;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
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
    public int getIngressosVendidos() { return ingressosVendidos; }
    public void setIngressosVendidos(int ingressosVendidos) { this.ingressosVendidos = ingressosVendidos; }
    public int getIngressosDisponiveis() { return ingressosDisponiveis; }
    public void setIngressosDisponiveis(int ingressosDisponiveis) { this.ingressosDisponiveis = ingressosDisponiveis; }
    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public String getOrganizadorNome() { return organizadorNome; }
    public void setOrganizadorNome(String organizadorNome) { this.organizadorNome = organizadorNome; }
    public String getOrganizadorEmail() { return organizadorEmail; }
    public void setOrganizadorEmail(String organizadorEmail) { this.organizadorEmail = organizadorEmail; }
    public Integer getEventoPrincipalId() { return eventoPrincipalId; }
    public void setEventoPrincipalId(Integer eventoPrincipalId) { this.eventoPrincipalId = eventoPrincipalId; }
    public String getEventoPrincipalNome() { return eventoPrincipalNome; }
    public void setEventoPrincipalNome(String eventoPrincipalNome) { this.eventoPrincipalNome = eventoPrincipalNome; }
}