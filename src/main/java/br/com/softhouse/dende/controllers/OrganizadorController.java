package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.service.OrganizadorService;

import java.util.List;

@Controller
@RequestMapping
public class OrganizadorController {

    // Cria uma instância do service
    private final OrganizadorService service = new OrganizadorService();

    @PostMapping(path = "/organizadores")
    // Recebe os dados do organizador no corpo da requisição
    public ResponseEntity<Organizador> cadastrar(@RequestBody Organizador organizador) {
        try {
            // VALIDAÇÃO: verifica se a senha foi fornecida e não está vazia
            if (organizador.getSenha() == null || organizador.getSenha().trim().isEmpty()) {
                // Retorna status 400 se a senha for inválida
                return ResponseEntity.status(400, null);
            }
            // Chama o service para cadastrar e retorna status 201 com o organizador criado
            return ResponseEntity.status(201, service.cadastrar(organizador));
        } catch (IllegalArgumentException e) {
            // Se o email já existir, retorna status 400
            return ResponseEntity.status(400, null);
        }
    }

    @PutMapping(path = "/organizadores/{email}")
    // Metodo que recebe o email como parâmetro de path e os dados atualizados no corpo
    public ResponseEntity<Organizador> alterar(
            @PathVariable(parameter = "email") String email,
            @RequestBody Organizador organizador
    ) {
        try {
            // Chama o service para atualizar e retorna o organizador atualizado
            return ResponseEntity.ok(service.atualizar(email, organizador));
        } catch (IllegalArgumentException e) {
            // Se não encontrar, retorna status 404
            return ResponseEntity.status(404, null);
        }
    }

    @GetMapping(path = "/organizadores")
    // Metodo que retorna a lista de todos os organizadores
    public ResponseEntity<List<Organizador>> listar() {
        // Chama o service para listar e retorna com status 200
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping(path = "/organizadores/{email}")
    // recebe o email como parâmetro de path
    public ResponseEntity<Organizador> visualizar(@PathVariable(parameter = "email") String email) {
        try {
            // Chama o service para buscar o organizador pelo email
            Organizador organizador = service.buscarPorEmail(email);
            // Retorna o organizador encontrado com status 200
            return ResponseEntity.ok(organizador);
        } catch (IllegalArgumentException e) {
            // Se não encontrar, retorna status 404
            return ResponseEntity.status(404, null);
        }
    }

    @PatchMapping(path = "/organizadores/{email}/{status}")
    // Function que recebe o email e o status como parâmetros de path
    public ResponseEntity<String> alterarStatus(
            @PathVariable(parameter = "email") String email,
            @PathVariable(parameter = "status") String status
    ) {
        try {
            // Chama o service para alterar o status do organizador
            service.alterarStatus(email, status);
            // Retorna mensagem de sucesso
            return ResponseEntity.ok("Status alterado com sucesso");
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Se houver erro (organizador não encontrado ou com eventos ativos), retorna status 400 com a mensagem
            return ResponseEntity.status(400, e.getMessage());
        }
    }
}