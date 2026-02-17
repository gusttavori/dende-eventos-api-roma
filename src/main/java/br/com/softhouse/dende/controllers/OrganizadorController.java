package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.service.OrganizadorService;

import java.util.List;


@Controller
@RequestMapping(path = "/organizadores")
public class OrganizadorController {

    private final OrganizadorService organizadorService = new OrganizadorService();

    public OrganizadorController() {
    }

    @PostMapping
    public ResponseEntity<String> cadastrarOrganizador(@RequestBody Organizador organizador) {
        organizadorService.cadastrarOrganizador(
                organizador.getNome(),
                organizador.getDataNascimento(),
                organizador.getSexo(),
                organizador.getEmail(),
                organizador.getSenha(),
                organizador.getEmpresa()
        );
        return ResponseEntity.ok("Organizador " + organizador.getEmail() + " registrado com sucesso!");
    }

    @PutMapping(path = "/{email}")
    public ResponseEntity<String> atualizarOrganizador(
            @PathVariable(parameter = "email") String email,
            @RequestBody Organizador organizadorAtualizado) {

        organizadorService.atualizarOrganizador(
                email,
                organizadorAtualizado.getNome(),
                organizadorAtualizado.getDataNascimento(),
                organizadorAtualizado.getSexo(),
                organizadorAtualizado.getSenha(),
                organizadorAtualizado.getEmpresa()
        );

        return ResponseEntity.ok("Organizador " + email + " atualizado com sucesso!");
    }

    @DeleteMapping(path = "/{email}")
    public ResponseEntity<String> desativarOrganizador(@PathVariable(parameter = "email") String email) {
        organizadorService.desativarOrganizador(email);
        return ResponseEntity.ok("Organizador " + email + " desativado com sucesso!");
    }

    @GetMapping
    public ResponseEntity<List<Organizador>> listarOrganizadores() {
        return ResponseEntity.ok(organizadorService.listarOrganizadores());
    }

    // =========================
    // Eventos do Organizador
    // =========================

    @PostMapping(path = "/{email}/eventos")
    public ResponseEntity<String> cadastrarEvento(
            @PathVariable(parameter = "email") String email,
            @RequestBody Evento evento) {

        Organizador organizador = organizadorService.listarOrganizadores().stream()
                .filter(o -> o.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Organizador não encontrado"));

        organizador.cadastrarEvento(evento);
        return ResponseEntity.ok("Evento " + evento.getNome() + " cadastrado para o organizador " + email);
    }
}


