package br.com.softhouse.dende.model.builders;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.EnumModel.ModalidadeEvento;
import br.com.softhouse.dende.model.EnumModel.TipoEvento;
import java.time.LocalDateTime;

// Classe builder para facilitar a criação de objetos Evento (padrão de projeto Builder)
public class EventoBuilder {
    // atributos que serão usados para construir o evento
    private int id;
    private Organizador organizador;
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
    private boolean ativo = false; // Evento nasce inativo conforme regra de negócio
    private Evento eventoPrincipal;

    // Construtor privado para forçar o uso do metodo estático builder()
    private EventoBuilder() {}

    // Métodos com nome igual aos atributos (conforme solicitado) para configurar cada campo
    public EventoBuilder id(int id) {
        this.id = id; // define o ID do evento
        return this; // retorna o próprio builder para permitir chamadas encadeadas
    }

    public EventoBuilder organizador(Organizador organizador) {
        this.organizador = organizador; // define o organizador responsável pelo evento
        return this;
    }

    public EventoBuilder nome(String nome) {
        this.nome = nome; // define o nome do evento
        return this;
    }

    public EventoBuilder paginaWeb(String paginaWeb) {
        this.paginaWeb = paginaWeb; // define a página web do evento
        return this;
    }

    public EventoBuilder descricao(String descricao) {
        this.descricao = descricao; // define a descrição do evento
        return this;
    }

    public EventoBuilder dataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio; // define a data e hora de início do evento
        return this;
    }

    public EventoBuilder dataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim; // define a data e hora de término do evento
        return this;
    }

    public EventoBuilder tipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento; // define o tipo do evento (enum)
        return this;
    }

    public EventoBuilder modalidade(ModalidadeEvento modalidade) {
        this.modalidade = modalidade; // define a modalidade do evento (enum)
        return this;
    }

    public EventoBuilder precoUnitarioIngresso(Double precoUnitarioIngresso) {
        this.precoUnitarioIngresso = precoUnitarioIngresso; // define o preço unitário do ingresso
        return this;
    }

    public EventoBuilder taxaCancelamentoIngresso(Double taxaCancelamentoIngresso) {
        this.taxaCancelamentoIngresso = taxaCancelamentoIngresso; // define a taxa de cancelamento do ingresso
        return this;
    }

    public EventoBuilder capacidadeMaxima(int capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima; // define a capacidade máxima de participantes
        return this;
    }

    public EventoBuilder local(String local) {
        this.local = local; // define o local onde o evento será realizado
        return this;
    }

    public EventoBuilder ativo(boolean ativo) {
        this.ativo = ativo; // define se o evento está ativo ou inativo
        return this;
    }

    public EventoBuilder eventoPrincipal(Evento eventoPrincipal) {
        this.eventoPrincipal = eventoPrincipal; // define o evento principal (para sub-eventos)
        return this;
    }

    // Metodo build que cria a instância de Evento com todos os atributos configurados
    public Evento build() {
        Evento evento = new Evento(); // cria uma nova instância de Evento
        // popula todos os atributos do evento com os valores configurados no builder
        evento.setId(this.id);
        evento.setOrganizador(this.organizador);
        evento.setNome(this.nome);
        evento.setPaginaWeb(this.paginaWeb);
        evento.setDescricao(this.descricao);
        evento.setDataInicio(this.dataInicio);
        evento.setDataFim(this.dataFim);
        evento.setTipoEvento(this.tipoEvento);
        evento.setModalidade(this.modalidade);
        evento.setPrecoUnitarioIngresso(this.precoUnitarioIngresso);
        evento.setTaxaCancelamentoIngresso(this.taxaCancelamentoIngresso);
        evento.setCapacidadeMaxima(this.capacidadeMaxima);
        evento.setLocal(this.local);
        evento.setAtivo(this.ativo);
        evento.setEventoPrincipal(this.eventoPrincipal);
        return evento; // retorna o evento construído
    }

    // Metodo estático para iniciar a construção do evento
    public static EventoBuilder builder() {
        return new EventoBuilder(); // retorna uma nova instância do builder
    }
}