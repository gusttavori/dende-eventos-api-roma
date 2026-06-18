package br.com.softhouse.dende.service;

import br.com.softhouse.dende.model.*;
import br.com.softhouse.dende.model.EnumModel.StatusIngresso;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class IngressoService {

    private final Repositorio repositorio = Repositorio.getInstance();

    // Processa a compra de um ingresso para um evento
    // [ITEM 1] O nome comprarIngresso() está adequado, mas o comentário interno diz
    // "Validações (código omitido para brevidade)" — isso indica que validações importantes
    // foram deixadas de fora. Veja os itens abaixo.
    // [ITEM — US13] ATENÇÃO: Faltam validações críticas neste método:
    //   1. Verificar se o usuário é do tipo UsuarioComum (não organizadores podem comprar).
    //   2. Verificar se o evento está ativo.
    //   3. Verificar se ainda há vagas disponíveis (ingressos vendidos < capacidadeMaxima).
    //   4. O valor total cobrado deveria ser o preço do evento + o preço do evento principal,
    //      mas o ingresso principal está sendo criado com apenas o preço do evento secundário,
    //      sem somar o preço do evento pai. Isso não atende à US13.
    // [ITEM 3] A verificação implícita de nulo deveria ser explicitada com Objects.isNull()
    //   após buscar o usuário e o evento.
    // [ITEM 6] A criação e persistência de ingressos está no Service — aceitável —
    //   mas a geração de ID deveria ser responsabilidade do Repositório.
    // [ITEM 1] O System.out.println de debug não deve existir em código de produção. Remova-o.
    public List<Ingresso> comprarIngresso(String emailUsuario, int eventoId) {
        System.out.println("\n=== COMPRANDO INGRESSO ===");
        System.out.println("Email usuário: " + emailUsuario);
        System.out.println("Evento ID: " + eventoId);

        Usuario usuario = repositorio.buscarUsuarioPorEmail(emailUsuario);
        Evento evento = repositorio.buscarEventoPorId(eventoId);

        // [ITEM — US13] As validações abaixo estão AUSENTES e são obrigatórias:
        // if (!(usuario instanceof UsuarioComum)) throw new IllegalArgumentException("Apenas usuários comuns podem comprar ingressos");
        // if (!evento.isAtivo()) throw new IllegalArgumentException("Evento não está ativo");
        // long vendidos = repositorio.listarIngressosPorEvento(evento).stream().filter(i -> i.getStatusIngresso() == StatusIngresso.ATIVO).count();
        // if (vendidos >= evento.getCapacidadeMaxima()) throw new IllegalArgumentException("Evento sem vagas disponíveis");

        // Validações (código omitido para brevidade)

        List<Ingresso> ingressosComprados = new ArrayList<>();

        // [ITEM — US13] O ingresso principal deveria ter o valor = precoEvento + precoEventoPrincipal.
        // Atualmente, cada ingresso é criado com o preço individual do seu evento, sem somar os valores.
        // A US13 exige que o valor total seja cobrado como a soma dos dois.
        Ingresso ingressoPrincipal = new Ingresso(
                repositorio.gerarId(),
                usuario,
                evento,
                evento.getPrecoUnitarioIngresso()
        );
        repositorio.salvarIngresso(ingressoPrincipal);
        ingressosComprados.add(ingressoPrincipal);
        System.out.println("Ingresso principal criado: " + ingressoPrincipal.getId());

        // [ITEM — US13] A nomenclatura está invertida: o ingresso chamado "ingressoSecundario"
        // é gerado para o "eventoPrincipal". Os nomes estão confusos e podem induzir erro.
        // Sugestão: renomeie para ingressoEventoVinculado e ingressoEventoPai para
        // deixar clara a hierarquia.
        if (evento.getEventoPrincipal() != null) {
            Evento eventoPrincipal = evento.getEventoPrincipal();
            Ingresso ingressoSecundario = new Ingresso(
                    repositorio.gerarId(),
                    usuario,
                    eventoPrincipal,
                    eventoPrincipal.getPrecoUnitarioIngresso()
            );
            repositorio.salvarIngresso(ingressoSecundario);
            ingressosComprados.add(ingressoSecundario);
            System.out.println("Ingresso do evento principal criado: " + ingressoSecundario.getId());
        }

        System.out.println("Total de ingressos comprados: " + ingressosComprados.size());
        return ingressosComprados;
    }

    // Cancela um ingresso existente
    // [ITEM 3] A verificação "== null" pode ser substituída por Objects.isNull(ingresso):
    // if (Objects.isNull(ingresso)) { throw new IllegalArgumentException("Ingresso não encontrado"); }
    // [ITEM — US14] O método cancela o ingresso, mas não incrementa as vagas do evento de volta.
    // A US14 exige que "o evento deve ter livre mais um ingresso para venda" após o cancelamento.
    // Não há nenhuma lógica aqui que atualize o contador de vagas disponíveis no evento.
    // [ITEM 8] O método retorna void mas poderia retornar o valor do estorno (Double/BigDecimal)
    // para que o controller informe ao usuário o valor reembolsado. A US14 exige que o valor
    // seja estornado — sem retorno, o usuário não sabe quanto receberá de volta.
    // Sugestão: public Double cancelarIngresso(int ingressoId)
    public void cancelarIngresso(int ingressoId) {
        Ingresso ingresso = repositorio.buscarIngressoPorId(ingressoId);

        if (ingresso == null) {
            throw new IllegalArgumentException("Ingresso não encontrado");
        }

        ingresso.cancelar();
    }

    // Lista todos os ingressos de um usuário, ordenados conforme regras de negócio
    // [ITEM 3] A verificação "== null" pode ser substituída por Objects.isNull(usuario)
    // [ITEM — US15] A ordenação implementada está correta em estrutura, mas usa
    // getDataFim() para verificar se o evento ainda está ativo, quando deveria usar
    // também o status do ingresso (ATIVO) E o evento não ter finalizado.
    // A lógica atual está próxima do correto, mas merece revisão cuidadosa.
    // [ITEM 2] O comparador lambda inline é longo e difícil de ler. Sugestão: extraia-o
    // para um Comparator nomeado, por exemplo:
    // Comparator<Ingresso> ordenacaoIngressos = Comparator
    //     .comparing((Ingresso i) -> !(i.getStatusIngresso() == StatusIngresso.ATIVO && i.getEvento().getDataFim().isAfter(LocalDateTime.now())))
    //     .thenComparing(i -> i.getEvento().getDataInicio())
    //     .thenComparing(i -> i.getEvento().getNome());
    public List<Ingresso> listarIngressosUsuario(String emailUsuario) {
        Usuario usuario = repositorio.buscarUsuarioPorEmail(emailUsuario);

        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }

        return repositorio.listarIngressosPorUsuario(usuario).stream()
                .sorted((i1, i2) -> {
                    boolean i1Ativo = i1.getStatusIngresso() == StatusIngresso.ATIVO &&
                            i1.getEvento().getDataFim().isAfter(LocalDateTime.now());
                    boolean i2Ativo = i2.getStatusIngresso() == StatusIngresso.ATIVO &&
                            i2.getEvento().getDataFim().isAfter(LocalDateTime.now());

                    if (i1Ativo && !i2Ativo) return -1;
                    if (!i1Ativo && i2Ativo) return 1;

                    int compareData = i1.getEvento().getDataInicio()
                            .compareTo(i2.getEvento().getDataInicio());
                    if (compareData != 0) return compareData;

                    return i1.getEvento().getNome()
                            .compareTo(i2.getEvento().getNome());
                })
                .collect(Collectors.toList());
    }
}