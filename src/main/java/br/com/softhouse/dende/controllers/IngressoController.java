package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.service.IngressoService;

import java.util.List;

@Controller
@RequestMapping(path = "/api")
public class IngressoController {

    private final IngressoService service = new IngressoService();

    @PostMapping(path = "/usuarios/{email}/eventos/{eventoId}/ingressos")
    public ResponseEntity<Ingresso> comprar(
            @PathVariable(parameter = "email") String email,
            @PathVariable(parameter = "eventoId") int eventoId
    ) {
        try {
            Ingresso ingresso = service.comprarIngresso(email, eventoId);
            return ResponseEntity.status(201, ingresso);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(400, null);
        }
    }

    @PostMapping(path = "/ingressos/{id}/cancelar")
    public ResponseEntity<String> cancelar(@PathVariable(parameter = "id") int id) {
        try {
            service.cancelarIngresso(id);
            return ResponseEntity.ok("Ingresso cancelado com sucesso");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @GetMapping(path = "/usuarios/{email}/ingressos")
    public ResponseEntity<List<Ingresso>> listar(@PathVariable(parameter = "email") String email) {
        try {
            return ResponseEntity.ok(service.listarIngressosUsuario(email));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404, null);
        }
    }
}