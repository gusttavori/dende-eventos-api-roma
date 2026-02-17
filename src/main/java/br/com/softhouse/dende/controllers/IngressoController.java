package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.Repositorio;
import br.com.softhouse.dende.service.IngressoService;

import java.util.List;

@Controller
@RequestMapping(path = "/ingressos")
public class IngressoController {

    private final IngressoService ingressoService = new IngressoService();
    private final Repositorio repositorio = Repositorio.getInstance();

    @PostMapping(path = "/comprar/{usuarioId}/{eventoId}")
    public ResponseEntity<List<Ingresso>> comprarIngresso(
            @PathVariable(parameter = "usuarioId") int usuarioId,
            @PathVariable(parameter = "eventoId") int eventoId) {

        Usuario usuario = repositorio.listarUsuarios().stream()
                .filter(u -> u.getId() == usuarioId)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Usuário não encontrado"));

        Evento evento = repositorio.buscarEventoPorId(eventoId);

        if (evento == null) {
            throw new IllegalArgumentException("Evento não encontrado");
        }

        List<Ingresso> ingressos =
                ingressoService.comprarIngresso(usuario, evento);

        return ResponseEntity.ok(ingressos);
    }

    @PostMapping(path = "/cancelar/{id}")
    public ResponseEntity<String> cancelarIngresso(
            @PathVariable(parameter = "id") int id) {

        Ingresso ingresso = repositorio.buscarIngressoPorId(id);

        if (ingresso == null) {
            throw new IllegalArgumentException("Ingresso não encontrado");
        }

        ingressoService.cancelarIngresso(ingresso);
        return ResponseEntity.ok("Ingresso cancelado com sucesso!");
    }
}
