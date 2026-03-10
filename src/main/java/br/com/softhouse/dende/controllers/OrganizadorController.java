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

// indica que esta classe é um controller (componente que recebe requisições HTTP)
@Controller
// define o caminho base para todas as rotas deste controller (vazio = raiz)
@RequestMapping
public class OrganizadorController {

    // cria uma instância do serviço de organizador para delegar as operações
    private final OrganizadorService service = new OrganizadorService();

    // mapeia requisições POST para o caminho "/organizadores"
    @PostMapping(path = "/organizadores")
    public ResponseEntity<OrganizadorDTO> cadastrar(@RequestBody UsuarioRequestDTO request) {
        // valida se a senha foi informada (não nula e não vazia)
        if (request.getSenha() == null || request.getSenha().trim().isEmpty()) {
            // retorna status 400 (Bad Request) com corpo vazio
            return ResponseEntity.status(400, null);
        }

        try {
            // converte o DTO de requisição para uma entidade Organizador
            Organizador organizador = OrganizadorMapper.toEntity(request);
            // chama o serviço para cadastrar o organizador
            Organizador novo = service.cadastrar(organizador);
            // retorna status 201 (Created) com o DTO do organizador criado
            return ResponseEntity.status(201, OrganizadorMapper.toDTO(novo));
        } catch (IllegalArgumentException e) {
            // captura exceção de email já cadastrado
            // retorna status 400 (Bad Request) com corpo vazio
            return ResponseEntity.status(400, null);
        }
    }

    // mapeia requisições PUT para o caminho "/organizadores/{email}" onde {email} é variável de caminho
    @PutMapping(path = "/organizadores/{email}")
    public ResponseEntity<OrganizadorDTO> alterar(
            // extrai o valor da variável "email" do caminho da URL
            @PathVariable(parameter = "email") String email,
            // extrai o corpo da requisição e converte para UsuarioRequestDTO
            @RequestBody UsuarioRequestDTO request
    ) {
        try {
            // tenta atualizar o organizador com os dados fornecidos
            Organizador organizador = service.atualizar(email, request);
            // retorna status 200 (OK) com o DTO do organizador atualizado
            return ResponseEntity.ok(OrganizadorMapper.toDTO(organizador));
        } catch (IllegalArgumentException e) {
            // captura exceção de organizador não encontrado
            // retorna status 404 (Not Found) com corpo vazio
            return ResponseEntity.status(404, null);
        }
    }

    // mapeia requisições GET para o caminho "/organizadores"
    @GetMapping(path = "/organizadores")
    public ResponseEntity<List<OrganizadorDTO>> listar() {
        // obtém a lista de organizadores do serviço, converte cada um para DTO e coleta em lista
        List<OrganizadorDTO> organizadores = service.listar().stream()
                .map(OrganizadorMapper::toDTO) // aplica o mapper para cada organizador
                .collect(Collectors.toList()); // coleta o resultado em uma lista
        // retorna status 200 (OK) com a lista de DTOs no corpo
        return ResponseEntity.ok(organizadores);
    }

    // mapeia requisições GET para o caminho "/organizadores/{email}" onde {email} é variável de caminho
    @GetMapping(path = "/organizadores/{email}")
    public ResponseEntity<OrganizadorDTO> visualizar(@PathVariable(parameter = "email") String email) {
        try {
            // tenta buscar o organizador pelo email
            Organizador organizador = service.buscarPorEmail(email);
            // retorna status 200 (OK) com o DTO do organizador
            return ResponseEntity.ok(OrganizadorMapper.toDTO(organizador));
        } catch (IllegalArgumentException e) {
            // captura exceção de organizador não encontrado
            // retorna status 404 (Not Found) com corpo vazio
            return ResponseEntity.status(404, null);
        }
    }

    // mapeia requisições PATCH para o caminho "/organizadores/{email}/{status}"
    @PatchMapping(path = "/organizadores/{email}/{status}")
    public ResponseEntity<String> alterarStatus(
            // extrai o valor da variável "email" do caminho da URL
            @PathVariable(parameter = "email") String email,
            // extrai o valor da variável "status" do caminho da URL (ativar/desativar)
            @PathVariable(parameter = "status") String status
    ) {
        try {
            // tenta alterar o status do organizador
            service.alterarStatus(email, status);
            // retorna status 200 (OK) com mensagem de sucesso
            return ResponseEntity.ok("Status alterado com sucesso");
        } catch (IllegalArgumentException | IllegalStateException e) {
            // captura exceções de organizador não encontrado, status inválido ou eventos ativos
            // retorna status 400 (Bad Request) com a mensagem de erro
            return ResponseEntity.status(400, e.getMessage());
        }
    }
}