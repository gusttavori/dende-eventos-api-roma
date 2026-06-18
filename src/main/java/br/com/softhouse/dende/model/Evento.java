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
    private ModalidadeEvento modalidade;
    // [ITEM 14] precoUnitarioIngresso é um valor financeiro e deveria ser BigDecimal em vez de Double.
    // Double pode causar erros de arredondamento em operações financeiras.
    // Sugestão: private BigDecimal precoUnitarioIngresso;
    private Double precoUnitarioIngresso;
    // [ITEM 14] taxaCancelamentoIngresso é um valor financeiro/percentual e também deveria ser BigDecimal.
    // Sugestão: private BigDecimal taxaCancelamentoIngresso;
    private Double taxaCancelamentoIngresso;
    private int capacidadeMaxima;
    private String local;
    private boolean ativo = false;    // Define o status inicial como falso, evento nasce inativo conforme regra de negócio
    private Evento eventoPrincipal;

    public Evento() {
    }

    public Evento(
            int id,
            Organizador organizador,
            String nome,
            String paginaWeb,
            String descricao,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            TipoEvento tipoEvento,
            ModalidadeEvento modalidade,
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
        this.modalidade = modalidade;
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

    // REGRAS DE NEGÓCIO

    // Valida as regras de negócio para criação/alteração de evento
    // [ITEM 6] O método validarEvento() contém lógica de validação de regras de negócio
    // diretamente no modelo. Essa responsabilidade poderia estar no EventoService,
    // que é a camada adequada para orquestrar as regras de negócio.
    // Deixar no modelo é aceitável para um projeto simples, mas em sistemas maiores
    // separe as validações no service.
    // [ITEM 7] Este método é chamado tanto no cadastro quanto na alteração de eventos.
    // Ao alterar um evento existente cujas datas já foram definidas anteriormente,
    // a validação "dataInicio.isBefore(agora)" pode lançar exceção mesmo para
    // eventos legítimos que não tiveram suas datas modificadas — ferindo a idempotência
    // da operação de alteração. Considere distinguir os dois casos ou aceitar datas
    // inalteradas como válidas.
    public void validarEvento() {
        // Obtém a data e hora atual para comparações
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
        // Calcula a diferença em minutos entre a data de início e fim
        long duracao = Duration.between(dataInicio, dataFim).toMinutes();
        if (duracao < 30) {
            throw new IllegalArgumentException(
                    "O evento deve ter duração mínima de 30 minutos."
            );
        }
    }

    // Ativa o evento alterando o status para verdadeiro
    public void ativar() {
        this.ativo = true;
    }

    // Desativa o evento alterando o status para falso
    public void desativar() {
        this.ativo = false;
    }


     // Verifica se o evento está acontecendo agora
     // Usado para bloquear desativação do organizador
    public boolean estaEmExecucao() {
        LocalDateTime agora = LocalDateTime.now();
        return agora.isAfter(dataInicio) && agora.isBefore(dataFim);// Retorna verdadeiro se a data atual estiver entre o início e o fim do evento
    }

    // [ITEM — Capacidade] Não há atributo para controlar quantos ingressos já foram vendidos
    // (ingressosVendidos ou vagasDisponiveis). A US12 exige que eventos com ingressos esgotados
    // não apareçam no feed. Atualmente essa contagem é feita externamente via repositório,
    // o que é correto, mas o modelo poderia ter um método utilitário que receba a contagem.
    // Sugestão: public boolean temVagasDisponiveis(int ingressosVendidos) { return ingressosVendidos < capacidadeMaxima; }

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