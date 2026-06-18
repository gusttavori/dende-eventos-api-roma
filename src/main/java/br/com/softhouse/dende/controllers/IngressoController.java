package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.service.IngressoService;

import java.util.List;

@Controller
@RequestMapping
public class IngressoController {

    private final IngressoService service = new IngressoService();

    @PostMapping(path = "/usuarios/{email}/eventos/{eventoId}/ingressos")
    // [ITEM 9] Retornar 201 (Created) para compra bem-sucedida está correto.
    // Para erros, retorna 400 (Bad Request) — adequado para validações de negócio.
    // Porém, se o usuário ou evento não for encontrado, 404 seria mais preciso que 400.
    // Sugestão: diferencie os erros de "não encontrado" (404) dos erros de regra de negócio (400/422).
    // [ITEM — US13] ATENÇÃO: As validações obrigatórias estão ausentes no service (ver IngressoService).
    // Sem validar se o usuário é UsuarioComum, se o evento está ativo e se há vagas,
    // qualquer usuário consegue "comprar" ingresso de qualquer evento.
    // [ITEM 4] O Ingresso retornado contém o objeto Usuario completo (com senha) e o Evento completo
    // (com Organizador e senha). Crie um DTO de saída (IngressoResponse) com apenas:
    // id, nome do evento, data, valorPago, statusIngresso.
    // [ITEM 5] Um IngressoMapper seria a boa prática. Não obrigatório nesta avaliação.
    public ResponseEntity<List<Ingresso>> comprar(
            @PathVariable(parameter = "email") String email,
            @PathVariable(parameter = "eventoId") int eventoId
    ) {
        try {
            List<Ingresso> ingressos = service.comprarIngresso(email, eventoId);
            return ResponseEntity.status(201, ingressos);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(400, null);
        }
    }

    @PostMapping(path = "/ingressos/{id}/cancelar")
    // [ITEM 9] Para ingresso não encontrado, retorna 400 — deveria ser 404 (Not Found).
    // Para ingresso já cancelado (IllegalStateException), 400 é aceitável (ou 409 Conflict).
    // Sugestão: diferencie: 404 para "não encontrado" e 400/409 para "já cancelado".
    // [ITEM — US14] O endpoint cancela o ingresso, mas não retorna o valor do estorno ao usuário.
    // A US14 exige que o valor seja comunicado. Sugestão: retorne o valor do estorno no corpo
    // da resposta, por exemplo: ResponseEntity<Double> com o valor reembolsado.
    // [ITEM — US14] Falta liberar a vaga no evento após o cancelamento (incrementar vagas disponíveis).
    public ResponseEntity<String> cancelar(@PathVariable(parameter = "id") int id) {
        try {
            service.cancelarIngresso(id);
            return ResponseEntity.ok("Ingresso cancelado com sucesso");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @GetMapping(path = "/usuarios/{email}/ingressos")
    // [ITEM 9] Para usuário não encontrado, retorna 404 — correto.
    // [ITEM — US15] A listagem ordenada de ingressos está implementada no service.
    // A ordenação está estruturalmente correta (ativos primeiro, depois por data e nome).
    // [ITEM 4] O Ingresso retornado expõe o Usuario completo (com senha) e o Evento completo
    // (com o Organizador e sua senha). Crie um DTO de saída com apenas os dados necessários
    // para a listagem: nome do evento, data de início, status do ingresso, valor pago.
    public ResponseEntity<List<Ingresso>> listar(@PathVariable(parameter = "email") String email) {
        try {
            return ResponseEntity.ok(service.listarIngressosUsuario(email));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404, null);
        }
    }
}