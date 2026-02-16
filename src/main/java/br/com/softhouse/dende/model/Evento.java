package br.com.softhouse.dende.model;

import br.com.softhouse.dende.model.EnumModel.TipoEvento;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Evento {
    private int id;
    private Organizador organizador;
    private String nome;
    private String paginaWeb;
    private String descricao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private TipoEvento tipoEvento;
    private Double precoUnitarioIngresso;
    private Double taxaCancelamentoIngresso;
    private int capacidadeMaxima;
    private String local;
    private boolean ativo;
    private Evento eventoPrincipal;

    public Evento(){
    }

    public Evento(int id, Organizador organizador, String nome, String paginaWeb, String descricao, LocalDateTime dataInicio, LocalDateTime dataFim, TipoEvento tipoEvento, Double precoUnitarioIngresso, Double taxaCancelamentoIngresso, int capacidadeMaxima, String local, Evento eventoPrincipal, boolean ativo) {
        this.id = id;
        this.organizador = organizador;
        this.nome = nome;
        this.paginaWeb = paginaWeb;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.tipoEvento = tipoEvento;
        this.precoUnitarioIngresso = precoUnitarioIngresso;
        this.taxaCancelamentoIngresso = taxaCancelamentoIngresso;
        this.capacidadeMaxima = capacidadeMaxima;
        this.local = local;
        this.eventoPrincipal = eventoPrincipal;
        this.ativo = ativo;
    }

    public Evento getEventoPrincipal() {
        return eventoPrincipal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Organizador getOrganizador() {
        return organizador;
    }

    public void setOrganizador(Organizador organizador) {
        this.organizador = organizador;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPaginaWeb() {
        return paginaWeb;
    }

    public void setPaginaWeb(String paginaWeb) {
        this.paginaWeb = paginaWeb;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public Double getPrecoUnitarioIngresso() {
        return precoUnitarioIngresso;
    }

    public void setPrecoUnitarioIngresso(Double precoUnitarioIngresso) {
        this.precoUnitarioIngresso = precoUnitarioIngresso;
    }

    public Double getTaxaCancelamentoIngresso() {
        return taxaCancelamentoIngresso;
    }

    public void setTaxaCancelamentoIngresso(Double taxaCancelamentoIngresso) {
        this.taxaCancelamentoIngresso = taxaCancelamentoIngresso;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(int capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        local = local;
    }



    @Override
    public String toString() {
        return "Evento{" +
                "id=" + id +
                ", organizador=" + organizador +
                ", nome='" + nome + '\'' +
                ", paginaWeb='" + paginaWeb + '\'' +
                ", Descricao='" + descricao + '\'' +
                ", dataInicio=" + dataInicio +
                ", dataFim=" + dataFim +
                ", tipoEvento=" + tipoEvento +
                ", precoUnitarioIngresso=" + precoUnitarioIngresso +
                ", taxaCancelamentoIngresso=" + taxaCancelamentoIngresso +
                ", capacidadeMaxima=" + capacidadeMaxima +
                ", Local='" + local + '\'' +
                ", ativo=" + ativo +
                '}';
    }

    public void validarEvento() throws Exception {
        LocalDateTime agora = LocalDateTime.now();

        if (this.dataInicio.isBefore(agora)) {
            throw new Exception("A data de início não pode ser anterior à atual.");
        }

        if (this.dataFim.isBefore(this.dataInicio)) {
            throw new Exception("A data de fim não pode ser anterior ao início.");
        }

        long duracao = Duration.between(dataInicio, dataFim).toMinutes();
        if (duracao < 30) {
            throw new Exception("O evento deve ter no mínimo 30 minutos de duração.");
        }
    }

    public boolean isAtivo() {
        return ativo;
    }

    // Metodo para verificar se o evento está ocorrendo agora (US 5)
    public boolean estaEmExecucao() {
        LocalDateTime agora = LocalDateTime.now();
        return agora.isAfter(dataInicio) && agora.isBefore(dataFim);
    }
}
