package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.service.EventoService;

import java.util.List;

@Controller
@RequestMapping
public class EventoController {

    private final EventoService service = new EventoService();

    @PostMapping(path = "/organizadores/{email}/eventos")
    // [ITEM 9] Retornar 201 (Created) para evento cadastrado está correto.
    // Para erros de validação (datas inválidas, duração < 30min), retorna 400 (Bad Request) — correto.
    // Para organizador não encontrado, 400 seria incorreto — deveria ser 404 (Not Found).
    // Sugestão: diferencie os erros, retornando 404 quando o organizador não existir.
    // [ITEM 4] O objeto Evento recebido é a entidade completa. Considere criar um DTO de entrada
    // (CadastrarEventoRequest) sem campos gerados automaticamente como id, organizador e ativo.
    // [ITEM 4] O Evento retornado inclui o objeto Organizador completo (com senha).
    // Crie um DTO de saída (EventoResponse) que omita dados sensíveis do organizador.
    // [ITEM 5] Um Mapper (ex: EventoMapper) seria uma boa prática aqui. Não obrigatório nesta avaliação.
    public ResponseEntity<Evento> cadastrar(
            @PathVariable(parameter = "email") String email,
            @RequestBody Evento evento
    ) {
        try {
            Evento novoEvento = service.cadastrarEvento(email, evento);
            return ResponseEntity.status(201, novoEvento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, null);
        }
    }

    @PutMapping(path = "/organizadores/{email}/eventos/{id}")
    // [ITEM 9] Para evento ou organizador não encontrado, retorna 404 — correto.
    // Porém, para "evento inativo não pode ser alterado" (regra de negócio), 404 é inadequado.
    // Deveria ser 422 (Unprocessable Entity) ou 400 (Bad Request) para violação de regra de negócio.
    // [ITEM — US8] A verificação de que o evento é ativo antes de alterar está implementada
    // no service, o que está correto para a US8.
    public ResponseEntity<Evento> alterar(
            @PathVariable(parameter = "email") String email,
            @PathVariable(parameter = "id") int id,
            @RequestBody Evento evento
    ) {
        try {
            Evento eventoAtualizado = service.alterarEvento(email, id, evento);
            return ResponseEntity.ok(eventoAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404, null);
        }
    }

    @PatchMapping(path = "/organizadores/{organizadorId}/eventos/{status}")
    // [ITEM — Mapeamento] ATENÇÃO: O path desta rota está INCORRETO para ativar/desativar
    // um único evento. O path "/organizadores/{organizadorId}/eventos/{status}" mistura
    // o ID do organizador com o status como parâmetros, mas não há um ID de evento específico.
    // Para ativar/desativar um evento específico, o path deveria ser:
    // "/organizadores/{email}/eventos/{eventoId}/status" ou "/eventos/{eventoId}/ativar"
    // [ITEM 9] Para erros, retorna 400 — mas "organizador não encontrado" deveria ser 404.
    // [ITEM — US9/US10] As US9 e US10 exigem ativar/desativar um evento específico.
    // Este endpoint opera sobre TODOS os eventos de um organizador em lote, o que não
    // corresponde diretamente ao mapeado nas User Stories.
    public ResponseEntity<String> alterarStatusPorOrganizador(
            @PathVariable(parameter = "organizadorId") int organizadorId,
            @PathVariable(parameter = "status") String status
    ) {
        try {
            service.alterarStatusEventoPorOrganizador(organizadorId, status);
            return ResponseEntity.ok("Status alterado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @GetMapping(path = "/eventos/feed")
    // [ITEM — US12] O feed retorna eventos ativos e não finalizados. Correto parcialmente.
    // Falta: 1) Filtrar eventos com vagas disponíveis. 2) Ordenar por data de início e nome.
    // Sugestão: adicione no service a ordenação e a filtragem de vagas antes de retornar.
    public ResponseEntity<List<Evento>> feed() {
        return ResponseEntity.ok(service.listarEventosAtivos());
    }

    @GetMapping(path = "/organizadores/{email}/eventos")
    // [ITEM — US11] Lista eventos de um organizador específico — correto para a US11.
    // Porém, falta a ordenação por data de execução e ordem alfabética de nome,
    // conforme exige a US11.
    // [ITEM 4] O Evento retornado inclui o Organizador completo (com senha).
    // Crie um DTO de saída (EventoResumoResponse) com apenas: nome, período, local,
    // preço e capacidade máxima, conforme exige a US11.
    public ResponseEntity<List<Evento>> listarPorOrganizador(
            @PathVariable(parameter = "email") String email
    ) {
        try {
            return ResponseEntity.ok(service.listarEventosPorOrganizador(email));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404, null);
        }
    }
}