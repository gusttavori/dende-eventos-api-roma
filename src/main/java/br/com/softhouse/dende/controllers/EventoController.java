package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.service.EventoService;

import java.util.List;

@Controller
@RequestMapping(path = "/api")
public class EventoController {

    private final EventoService service = new EventoService();

    @PostMapping(path = "/organizadores/{email}/eventos")
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

    @PatchMapping(path = "/eventos/{id}/{status}")
    public ResponseEntity<String> alterarStatus(
            @PathVariable(parameter = "id") int id,
            @PathVariable(parameter = "status") String status
    ) {
        try {
            service.alterarStatusEvento(id, status);
            return ResponseEntity.ok("Status alterado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @GetMapping(path = "/eventos/feed")
    public ResponseEntity<List<Evento>> feed() {
        return ResponseEntity.ok(service.listarEventosAtivos());
    }

    @GetMapping(path = "/organizadores/{email}/eventos")
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