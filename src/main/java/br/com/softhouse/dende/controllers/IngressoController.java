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
    // Cria uma instância do service
    private final IngressoService service = new IngressoService();

    @PostMapping(path = "/usuarios/{email}/eventos/{eventoId}/ingressos")
    // Recebe o email do usuário e o ID do evento como parâmetros de path
    public ResponseEntity<List<Ingresso>> comprar(
            @PathVariable(parameter = "email") String email,
            @PathVariable(parameter = "eventoId") int eventoId
    ) {
        try {
            // Chama o service para comprar o ingresso e retorna a lista de ingressos gerados
            List<Ingresso> ingressos = service.comprarIngresso(email, eventoId);
            // Retorna status 201 com a lista de ingressos
            return ResponseEntity.status(201, ingressos);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Se houver erro (usuário não encontrado, evento inativo, etc), retorna status 400
            return ResponseEntity.status(400, null);
        }
    }

    @PostMapping(path = "/ingressos/{id}/cancelar")
    // Recebe o ID do ingresso como parâmetro de path
    public ResponseEntity<String> cancelar(@PathVariable(parameter = "id") int id) {
        try {
            // Chama o service para cancelar o ingresso
            service.cancelarIngresso(id);
            // Retorna mensagem de sucesso
            return ResponseEntity.ok("Ingresso cancelado com sucesso");
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Se houver erro (ingresso não encontrado ou já cancelado), retorna status 400 com a mensagem
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @GetMapping(path = "/usuarios/{email}/ingressos")
    // Recebe o email do usuário como parâmetro de path
    public ResponseEntity<List<Ingresso>> listar(@PathVariable(parameter = "email") String email) {
        try {
            // Chama o service para listar os ingressos do usuário e retorna com status 200
            return ResponseEntity.ok(service.listarIngressosUsuario(email));
        } catch (IllegalArgumentException e) {
            // Se o usuário não for encontrado, retorna status 404
            return ResponseEntity.status(404, null);
        }
    }
}