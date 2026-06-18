package br.com.softhouse.dende.service;

import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.Repositorio;

import java.util.List;
import java.util.Objects;

public class EventoService {

    private final Repositorio repositorio = Repositorio.getInstance();
    private final IngressoService ingressoService = new IngressoService();

    // Cadastra um novo evento associado a um organizador identificado por email
    // [ITEM 3] As verificações "== null" podem ser substituídas por Objects.isNull():
    // if (Objects.isNull(usuario)) { throw new IllegalArgumentException(...); }
    // [ITEM 6] A atribuição do organizador e do ID ao evento está sendo feita aqui no Service.
    // Idealmente, o Repositório seria responsável por gerar e atribuir o ID ao persistir.
    // [ITEM — US7] O método não valida se o organizador está ativo antes de cadastrar o evento.
    // Um organizador inativo não deveria poder cadastrar eventos.
    public Evento cadastrarEvento(String emailOrganizador, Evento evento) {
        Usuario usuario = repositorio.buscarUsuarioPorEmail(emailOrganizador);

        if (usuario == null) {
            throw new IllegalArgumentException("Organizador não encontrado com email: " + emailOrganizador);
        }

        if (!(usuario instanceof Organizador)) {
            throw new IllegalArgumentException("Usuário com email " + emailOrganizador + " não é um organizador");
        }

        Organizador organizador = (Organizador) usuario;

        evento.validarEvento();

        // [ITEM 6] Geração e atribuição do ID feitas no Service — o ideal seria delegar ao Repositório.
        evento.setOrganizador(organizador);
        evento.setId(repositorio.gerarId());

        repositorio.salvarEvento(evento);
        organizador.cadastrarEvento(evento);

        return evento;
    }

    // Altera o status de um evento (ativar/desativar) pelo seu ID
    // [ITEM 1] O nome alterarStatusEvento() é razoável, mas poderia ser mais descritivo.
    // Sugestão: separe em dois métodos — ativarEvento(int eventoId) e desativarEvento(int eventoId) —
    // para deixar a intenção explícita em cada chamada.
    // [ITEM 3] A verificação "== null" pode ser substituída por Objects.isNull(evento)
    // [ITEM — US10] Ao desativar, os ingressos são cancelados automaticamente. Correto!
    // Porém, não há reembolso sendo retornado ao usuário — apenas o ingresso é cancelado.
    // A US10 exige que o valor seja estornado. Verifique se o retorno de cancelarIngresso()
    // está sendo tratado e comunicado ao usuário de alguma forma.
    // [ITEM — US12] Ao ativar um evento, não há verificação de disponibilidade de vagas.
    // O feed deve exibir apenas eventos com vagas disponíveis (US12).
    public void alterarStatusEvento(int eventoId, String status) {
        Evento evento = repositorio.buscarEventoPorId(eventoId);

        if (evento == null) {
            throw new IllegalArgumentException("Evento não encontrado");
        }

        if ("ativar".equalsIgnoreCase(status)) {
            evento.ativar();
        } else if ("desativar".equalsIgnoreCase(status)) {
            evento.desativar();

            List<Ingresso> ingressos = repositorio.listarIngressosPorEvento(evento);
            if (!ingressos.isEmpty()) {
                ingressos.forEach(i -> ingressoService.cancelarIngresso(i.getId()));
            }
        } else {
            throw new IllegalArgumentException("Status inválido: " + status);
        }
    }

    // Retorna a lista de eventos ativos (não finalizados)
    // [ITEM — US12] Este método retorna eventos ativos, mas NÃO filtra por vagas disponíveis
    // nem ordena por data de início e nome alfabeticamente, como exige a US12.
    // Sugestão: adicione ordenação:
    // .sorted(Comparator.comparing(Evento::getDataInicio).thenComparing(Evento::getNome))
    // E filtragem por vagas: .filter(e -> ingressosVendidos(e) < e.getCapacidadeMaxima())
    // [ITEM 1] O System.out.println de debug não deve existir em código de produção. Remova-o.
    public List<Evento> listarEventosAtivos() {
        System.out.println("=== LISTANDO EVENTOS ATIVOS ===");
        List<Evento> eventos = repositorio.listarEventosAtivos();
        System.out.println("Eventos encontrados: " + eventos.size());
        for (Evento e : eventos) {
            System.out.println(" - " + e.getNome() + " (ID: " + e.getId() + ")");
        }
        return eventos;
    }

    // Retorna a lista de eventos de um organizador específico identificado por email
    // [ITEM — US11] Este método retorna os eventos do organizador, mas NÃO aplica a ordenação
    // por data de execução e ordem alfabética exigida pela US11.
    // Sugestão: adicione ao final do stream:
    // .sorted(Comparator.comparing(Evento::getDataInicio).thenComparing(Evento::getNome))
    // [ITEM 3] As verificações "== null" podem ser substituídas por Objects.isNull()
    public List<Evento> listarEventosPorOrganizador(String emailOrganizador) {
        Usuario usuario = repositorio.buscarUsuarioPorEmail(emailOrganizador);

        if (usuario == null) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        if (!(usuario instanceof Organizador)) {
            throw new IllegalArgumentException("Usuário com email " + emailOrganizador + " não é um organizador");
        }

        Organizador organizador = (Organizador) usuario;

        return repositorio.listarEventosPorOrganizador(organizador);
    }

    // Altera os dados de um evento existente
    // [ITEM 7] O método chama evento.validarEvento() ao final, que verifica se a dataInicio
    // não é anterior à data atual. Ao alterar um evento sem modificar as datas, essa validação
    // pode rejeitar eventos cujas datas já estão no passado — ferindo a idempotência da alteração.
    // Considere separar a validação de criação da validação de alteração.
    // [ITEM 6] A atualização de todos os campos do evento (setNome, setPaginaWeb, etc.) está sendo
    // feita diretamente no Service. Essa responsabilidade poderia ser encapsulada em um método
    // evento.atualizar(dadosAtualizados) no próprio modelo, ou em repositorio.atualizarEvento().
    // [ITEM — US8] A US8 afirma que apenas eventos ATIVOS podem ser alterados. A verificação
    // está presente (!evento.isAtivo() lança exceção), o que está correto.
    // [ITEM 3] As verificações "== null" podem ser substituídas por Objects.isNull()
    public Evento alterarEvento(String emailOrganizador, int eventoId, Evento dadosAtualizados) {
        Usuario usuario = repositorio.buscarUsuarioPorEmail(emailOrganizador);

        if (usuario == null) {
            throw new IllegalArgumentException("Organizador não encontrado");
        }

        if (!(usuario instanceof Organizador)) {
            throw new IllegalArgumentException("Usuário com email " + emailOrganizador + " não é um organizador");
        }

        Organizador organizador = (Organizador) usuario;

        Evento evento = repositorio.buscarEventoPorId(eventoId);
        if (evento == null) {
            throw new IllegalArgumentException("Evento não encontrado");
        }

        if (!evento.getOrganizador().equals(organizador)) {
            throw new IllegalArgumentException("Evento não pertence a este organizador");
        }

        if (!evento.isAtivo()) {
            throw new IllegalArgumentException("Não é possível alterar um evento inativo");
        }

        // [ITEM 6] Bloco de setters abaixo poderia ser encapsulado em um método
        // evento.atualizarDados(dadosAtualizados) para reduzir o acoplamento no Service.
        evento.setNome(dadosAtualizados.getNome());
        evento.setPaginaWeb(dadosAtualizados.getPaginaWeb());
        evento.setDescricao(dadosAtualizados.getDescricao());
        evento.setDataInicio(dadosAtualizados.getDataInicio());
        evento.setDataFim(dadosAtualizados.getDataFim());
        evento.setTipoEvento(dadosAtualizados.getTipoEvento());
        evento.setModalidade(dadosAtualizados.getModalidade());
        evento.setPrecoUnitarioIngresso(dadosAtualizados.getPrecoUnitarioIngresso());
        evento.setTaxaCancelamentoIngresso(dadosAtualizados.getTaxaCancelamentoIngresso());
        evento.setCapacidadeMaxima(dadosAtualizados.getCapacidadeMaxima());
        evento.setLocal(dadosAtualizados.getLocal());

        // [ITEM 7] validarEvento() aqui pode rejeitar eventos cuja dataInicio já passou,
        // mesmo que ela não tenha sido alterada — ferindo a idempotência da operação de alteração.
        evento.validarEvento();

        return evento;
    }

    // Busca um evento pelo seu ID
    // [ITEM 8] O método retorna null via exceção. Prefira retornar Optional<Evento>.
    // Sugestão: public Optional<Evento> buscarEventoPorId(int eventoId)
    // [ITEM 3] A verificação "== null" pode ser substituída por Objects.isNull(evento)
    public Evento buscarEventoPorId(int eventoId) {
        Evento evento = repositorio.buscarEventoPorId(eventoId);
        if (evento == null) {
            throw new IllegalArgumentException("Evento não encontrado");
        }
        return evento;
    }

    // Retorna todos os eventos cadastrados
    public List<Evento> listarTodosEventos() {
        return repositorio.listarEventos();
    }

    // Altera o status de todos os eventos de um organizador identificado por ID
    // [ITEM 1] O nome alterarStatusEventoPorOrganizador() é mais descritivo, mas ainda confuso
    // pois mistura a busca de organizador por ID com a alteração em lote dos seus eventos.
    // Sugestão: separe em ativarEventosDoOrganizador(int organizadorId) e
    // desativarEventosDoOrganizador(int organizadorId).
    // [ITEM 7] Se o organizador não possui eventos, este método lança IllegalArgumentException.
    // Isso fere a idempotência: um organizador sem eventos que tenta "ativar" seus eventos
    // deveria simplesmente não fazer nada, não lançar exceção.
    // Sugestão: substitua o throw por um retorno silencioso se a lista estiver vazia.
    // [ITEM 3] A verificação "== null" pode ser substituída por Objects.isNull(usuario)
    // [ITEM 2] A variável local "eventos" tem o mesmo nome do pacote/campo interno — isso pode
    // gerar confusão. Sugestão: renomeie para eventosDoOrganizador.
    public void alterarStatusEventoPorOrganizador(int organizadorId, String status) {
        Usuario usuario = repositorio.buscarUsuarioPorId(organizadorId);

        if (usuario == null) {
            throw new IllegalArgumentException("Organizador não encontrado com ID: " + organizadorId);
        }

        if (!(usuario instanceof Organizador)) {
            throw new IllegalArgumentException("Usuário com ID " + organizadorId + " não é um organizador");
        }

        Organizador organizador = (Organizador) usuario;

        // [ITEM 2] Renomeie "eventos" para "eventosDoOrganizador" para evitar ambiguidade.
        List<Evento> eventos = repositorio.listarEventosPorOrganizador(organizador);

        // [ITEM 7] Lançar exceção quando a lista está vazia fere a idempotência.
        // Sugestão: if (eventos.isEmpty()) { return; }
        if (eventos.isEmpty()) {
            throw new IllegalArgumentException("Organizador não possui eventos");
        }

        for (Evento evento : eventos) {
            if ("ativar".equalsIgnoreCase(status)) {
                evento.ativar();
            } else if ("desativar".equalsIgnoreCase(status)) {
                evento.desativar();

                List<Ingresso> ingressos = repositorio.listarIngressosPorEvento(evento);
                if (!ingressos.isEmpty()) {
                    ingressos.forEach(i -> ingressoService.cancelarIngresso(i.getId()));
                }
            }
        }

        System.out.println("Status '" + status + "' aplicado a " + eventos.size() + " eventos");
    }
}