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

    // Cria uma instância do service
    private final EventoService service = new EventoService();

    @PostMapping(path = "/organizadores/{email}/eventos")
    // Metodo que recebe o email do organizador como parâmetro de path e os dados do evento no corpo
    public ResponseEntity<Evento> cadastrar(
            @PathVariable(parameter = "email") String email,
            @RequestBody Evento evento
    ) {
        try {
            // Chama o service para cadastrar o evento
            Evento novoEvento = service.cadastrarEvento(email, evento);
            // Retorna status 201 com o evento criado
            return ResponseEntity.status(201, novoEvento);
        } catch (IllegalArgumentException e) {
            // Se houver erro (organizador não encontrado, dados inválidos), retorna status 400
            return ResponseEntity.status(400, null);
        }
    }

    @PutMapping(path = "/organizadores/{email}/eventos/{id}")
    // Metodo que recebe o email do organizador e o ID do evento como parâmetros de path, e os dados atualizados no corpo
    public ResponseEntity<Evento> alterar(
            @PathVariable(parameter = "email") String email,
            @PathVariable(parameter = "id") int id,
            @RequestBody Evento evento
    ) {
        try {
            // Chama o service para alterar o evento
            Evento eventoAtualizado = service.alterarEvento(email, id, evento);
            // Retorna o evento atualizado com status 200
            return ResponseEntity.ok(eventoAtualizado);
        } catch (IllegalArgumentException e) {
            // Se não encontrar o evento ou organizador, retorna status 404
            return ResponseEntity.status(404, null);
        }
    }

    @PatchMapping(path = "/organizadores/{organizadorId}/eventos/{status}")
    // Recebe o ID do organizador e o status como parâmetros de path
    public ResponseEntity<String> alterarStatusPorOrganizador(
            @PathVariable(parameter = "organizadorId") int organizadorId,
            @PathVariable(parameter = "status") String status
    ) {
        try {
            // Chama o service para alterar o status de todos os eventos do organizador
            service.alterarStatusEventoPorOrganizador(organizadorId, status);
            // Retorna mensagem de sucesso
            return ResponseEntity.ok("Status alterado com sucesso");
        } catch (IllegalArgumentException e) {
            // Se houver erro, retorna status 400 com a mensagem
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    @GetMapping(path = "/eventos/feed")
    // Retorna a lista de eventos ativos
    public ResponseEntity<List<Evento>> feed() {
        // Chama o service para listar eventos ativos e retorna com status 200
        return ResponseEntity.ok(service.listarEventosAtivos());
    }

    @GetMapping(path = "/organizadores/{email}/eventos")
    // Recebe o email do organizador como parâmetro de path
    public ResponseEntity<List<Evento>> listarPorOrganizador(
            @PathVariable(parameter = "email") String email
    ) {
        try {
            // Chama o service para listar eventos do organizador
            return ResponseEntity.ok(service.listarEventosPorOrganizador(email));
        } catch (IllegalArgumentException e) {
            // Se o organizador não for encontrado, retorna status 404
            return ResponseEntity.status(404, null);
        }
    }
}