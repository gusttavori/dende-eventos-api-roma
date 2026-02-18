package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.service.OrganizadorService;

import java.util.List;

@Controller
@RequestMapping(path = "/api")
public class OrganizadorController {

    private final OrganizadorService service = new OrganizadorService();

    @PostMapping(path = "/organizadores")
    public ResponseEntity<Organizador> cadastrar(@RequestBody Organizador organizador) {
        try {
            // VALIDAÇÃO: senha não pode ser nula
            if (organizador.getSenha() == null || organizador.getSenha().trim().isEmpty()) {
                return ResponseEntity.status(400, null);
            }
            return ResponseEntity.status(201, service.cadastrar(organizador));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, null);
        }
    }

    @PutMapping(path = "/organizadores/{email}")
    public ResponseEntity<Organizador> alterar(
            @PathVariable(parameter = "email") String email,
            @RequestBody Organizador organizador
    ) {
        try {
            return ResponseEntity.ok(service.atualizar(email, organizador));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404, null);
        }
    }

    @GetMapping(path = "/organizadores")
    public ResponseEntity<List<Organizador>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping(path = "/organizadores/{email}")
    public ResponseEntity<Organizador> visualizar(@PathVariable(parameter = "email") String email) {
        try {
            // Use o service em vez de acessar o repositório diretamente
            Organizador organizador = service.buscarPorEmail(email);
            return ResponseEntity.ok(organizador);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404, null);
        }
    }

    @PatchMapping(path = "/organizadores/{email}/{status}")
    public ResponseEntity<String> alterarStatus(
            @PathVariable(parameter = "email") String email,
            @PathVariable(parameter = "status") String status
    ) {
        try {
            service.alterarStatus(email, status);
            return ResponseEntity.ok("Status alterado com sucesso");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }
}