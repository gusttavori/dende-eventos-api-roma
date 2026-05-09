package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.dto.OrganizadorDTO;
import br.com.softhouse.dende.dto.request.UsuarioRequestDTO;
import br.com.softhouse.dende.mappers.OrganizadorMapper;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.service.OrganizadorService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping
public class OrganizadorController {

    private final OrganizadorService service = new OrganizadorService();

    @PostMapping(path = "/organizadores")
    public ResponseEntity<OrganizadorDTO> cadastrar(@RequestBody UsuarioRequestDTO request) {
        if (request.getSenha() == null || request.getSenha().trim().isEmpty()) {
            return ResponseEntity.status(400, null);
        }

        try {
            Organizador organizador = OrganizadorMapper.toEntity(request);
            Organizador novo = service.cadastrar(organizador);
            return ResponseEntity.status(201, OrganizadorMapper.toDTO(novo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, null);
        }
    }

    @PutMapping(path = "/organizadores/{email}")
    public ResponseEntity<OrganizadorDTO> alterar(
            @PathVariable(parameter = "email") String email,
            @RequestBody UsuarioRequestDTO request
    ) {
        try {
            Organizador organizador = service.atualizar(email, request);
            return ResponseEntity.ok(OrganizadorMapper.toDTO(organizador));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404, null);
        }
    }

    @GetMapping(path = "/organizadores")
    public ResponseEntity<List<OrganizadorDTO>> listar() {
        List<OrganizadorDTO> organizadores = service.listar().stream()
                .map(OrganizadorMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(organizadores);
    }

    @GetMapping(path = "/organizadores/{email}")
    public ResponseEntity<OrganizadorDTO> visualizar(@PathVariable(parameter = "email") String email) {
        try {
            Organizador organizador = service.buscarPorEmail(email);
            return ResponseEntity.ok(OrganizadorMapper.toDTO(organizador));
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