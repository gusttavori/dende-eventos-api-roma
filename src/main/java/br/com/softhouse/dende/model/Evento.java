package br.com.softhouse.dende.model;

import br.com.softhouse.dende.model.EnumModel.ModalidadeEvento;
import br.com.softhouse.dende.model.EnumModel.TipoEvento;

import java.time.Duration;
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
    private ModalidadeEvento modalidade;  // CAMPO ADICIONADO
    private Double precoUnitarioIngresso;
    private Double taxaCancelamentoIngresso;
    private int capacidadeMaxima;
    private String local;
    private boolean ativo = false;
    private Evento eventoPrincipal;

    public Evento() {
    }

    // Construtor atualizado
    public Evento(
            int id,
            Organizador organizador,
            String nome,
            String paginaWeb,
            String descricao,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            TipoEvento tipoEvento,
            ModalidadeEvento modalidade,  // NOVO PARÂMETRO
            Double precoUnitarioIngresso,
            Double taxaCancelamentoIngresso,
            int capacidadeMaxima,
            String local,
            Evento eventoPrincipal
    ) {
        this.id = id;
        this.organizador = organizador;
        this.nome = nome;
        this.paginaWeb = paginaWeb;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.tipoEvento = tipoEvento;
        this.modalidade = modalidade;  // NOVO CAMPO
        this.precoUnitarioIngresso = precoUnitarioIngresso;
        this.taxaCancelamentoIngresso = taxaCancelamentoIngresso;
        this.capacidadeMaxima = capacidadeMaxima;
        this.local = local;
        this.eventoPrincipal = eventoPrincipal;
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

    public ModalidadeEvento getModalidade() {
        return modalidade;
    }

    public void setModalidade(ModalidadeEvento modalidade) {
        this.modalidade = modalidade;
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
        this.local = local;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Evento getEventoPrincipal() {
        return eventoPrincipal;
    }

    public void setEventoPrincipal(Evento eventoPrincipal) {
        this.eventoPrincipal = eventoPrincipal;
    }
    // =========================
    // REGRAS DE NEGÓCIO
    // =========================

    public void validarEvento() {

        LocalDateTime agora = LocalDateTime.now();

        // Regra: data de início não pode ser anterior à atual
        if (dataInicio.isBefore(agora)) {
            throw new IllegalArgumentException(
                    "A data de início não pode ser anterior à data atual."
            );
        }

        // Regra: data fim não pode ser anterior ao início
        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException(
                    "A data de fim não pode ser anterior à data de início."
            );
        }

        // Regra: duração mínima de 30 minutos
        long duracao = Duration.between(dataInicio, dataFim).toMinutes();
        if (duracao < 30) {
            throw new IllegalArgumentException(
                    "O evento deve ter duração mínima de 30 minutos."
            );
        }
    }

    //Ativa o evento
    public void ativar() {
        this.ativo = true;
    }

    //Desativa o evento
    public void desativar() {
        this.ativo = false;
    }

    /**
     * Verifica se o evento está acontecendo agora
     * Usado para bloquear desativação do organizador
     */
    public boolean estaEmExecucao() {
        LocalDateTime agora = LocalDateTime.now();
        return agora.isAfter(dataInicio) && agora.isBefore(dataFim);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Evento)) return false;
        Evento evento = (Evento) o;
        return id == evento.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Evento{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", dataInicio=" + dataInicio +
                ", dataFim=" + dataFim +
                ", local='" + local + '\'' +
                ", ativo=" + ativo +
                '}';
    }

}
