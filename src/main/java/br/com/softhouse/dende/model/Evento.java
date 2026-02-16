package br.com.softhouse.dende.model;

import br.com.softhouse.dende.model.EnumModel.TipoEvento;

import java.time.LocalDate;
import java.util.Objects;

public class Evento {
    private int id;
    private String nome;
    private Organizador organizador;
    private String paginaWeb;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private TipoEvento tipoEvento;
    private Double precoUnitarioIngresso;
    private Double taxaCancelamentoIngresso;
    private int capacidadeMaxima;

    public Evento(int id, String nome, Organizador organizador, String paginaWeb, LocalDate dataInicio, LocalDate dataFim, TipoEvento tipoEvento, Double precoUnitarioIngresso, Double taxaCancelamentoIngresso, int capacidadeMaxima) {

        this.id = id;
        this.nome = nome;
        this.organizador = organizador;
        this.paginaWeb = paginaWeb;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.tipoEvento = tipoEvento;
        this.precoUnitarioIngresso = precoUnitarioIngresso;
        this.taxaCancelamentoIngresso = taxaCancelamentoIngresso;
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public Evento(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Organizador getOrganizador() {
        return organizador;
    }

    public void setOrganizador(Organizador organizador) {
        this.organizador = organizador;
    }

    public String getPaginaWeb() {
        return paginaWeb;
    }

    public void setPaginaWeb(String paginaWeb) {
        this.paginaWeb = paginaWeb;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evento evento = (Evento) o;
        return id == evento.id && capacidadeMaxima == evento.capacidadeMaxima && Objects.equals(nome, evento.nome) && Objects.equals(organizador, evento.organizador) && Objects.equals(paginaWeb, evento.paginaWeb) && Objects.equals(dataInicio, evento.dataInicio) && Objects.equals(dataFim, evento.dataFim) && tipoEvento == evento.tipoEvento && Objects.equals(precoUnitarioIngresso, evento.precoUnitarioIngresso) && Objects.equals(taxaCancelamentoIngresso, evento.taxaCancelamentoIngresso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, organizador, paginaWeb, dataInicio, dataFim, tipoEvento, precoUnitarioIngresso, taxaCancelamentoIngresso, capacidadeMaxima);
    }

    @Override
    public String toString() {
        return "Evento{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", organizador=" + organizador +
                ", paginaWeb='" + paginaWeb + '\'' +
                ", dataInicio=" + dataInicio +
                ", dataFim=" + dataFim +
                ", tipoEvento=" + tipoEvento +
                ", precoUnitarioIngresso=" + precoUnitarioIngresso +
                ", taxaCancelamentoIngresso=" + taxaCancelamentoIngresso +
                ", capacidadeMaxima=" + capacidadeMaxima +
                '}';
    }
}
