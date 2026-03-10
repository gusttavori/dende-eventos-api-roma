package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.dto.UsuarioDTO;
import br.com.softhouse.dende.dto.request.UsuarioRequestDTO;
import br.com.softhouse.dende.mappers.UsuarioMapper;
import br.com.softhouse.dende.model.ReativacaoRequest;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.UsuarioComum;
import br.com.softhouse.dende.service.UsuarioService;

import java.util.List;
import java.util.stream.Collectors;

// indica que esta classe é um controller (componente que recebe requisições HTTP)
@Controller
// define o caminho base para todas as rotas deste controller
@RequestMapping
public class UsuarioController {

    // cria uma instância do serviço de usuário para delegar as operações
    private final UsuarioService usuarioService = new UsuarioService();

    // mapeia requisições POST para o caminho "/usuarios"
    @PostMapping(path = "/usuarios")
    public ResponseEntity<UsuarioDTO> cadastrar(@RequestBody UsuarioRequestDTO usuarioRequest) {
        // valida se a senha foi informada (não nula e não vazia)
        if (usuarioRequest.getSenha() == null || usuarioRequest.getSenha().trim().isEmpty()) {
            // retorna status 400 (Bad Request) com corpo vazio
            return ResponseEntity.status(400, null);
        }

        // chama o serviço para cadastrar um novo usuário comum com os dados fornecidos
        UsuarioComum novo = usuarioService.cadastrarUsuarioComum(
                usuarioRequest.getNome(),
                usuarioRequest.getDataNascimento(),
                usuarioRequest.getSexo(),
                usuarioRequest.getEmail(),
                usuarioRequest.getSenha()
        );

        // retorna status 201 (Created) com o DTO do usuário criado no corpo da resposta
        return ResponseEntity.status(201, UsuarioMapper.toDTO(novo));
    }

    // mapeia requisições PUT para o caminho "/usuarios/{email}" onde {email} é uma variável de caminho
    @PutMapping(path = "/usuarios/{email}")
    public ResponseEntity<UsuarioDTO> alterar(
            // extrai o valor da variável "email" do caminho da URL
            @PathVariable(parameter = "email") String email,
            // extrai o corpo da requisição e converte para UsuarioRequestDTO
            @RequestBody UsuarioRequestDTO usuarioRequest
    ) {
        try {
            // tenta atualizar o usuário com os dados fornecidos
            usuarioService.atualizarUsuario(email, usuarioRequest);
            // busca o usuário atualizado pelo email
            Usuario usuario = usuarioService.buscarPorEmail(email);
            // retorna status 200 (OK) com o DTO do usuário atualizado
            return ResponseEntity.ok(UsuarioMapper.toDTO(usuario));
        } catch (IllegalArgumentException e) {
            // captura exceção de usuário não encontrado
            // retorna status 404 (Not Found) com corpo vazio
            return ResponseEntity.status(404, null);
        }
    }

    // mapeia requisições GET para o caminho "/usuarios"
    @GetMapping(path = "/usuarios")
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        // obtém a lista de usuários do serviço, converte cada um para DTO e coleta em uma lista
        List<UsuarioDTO> usuarios = usuarioService.listarUsuarios().stream()
                .map(UsuarioMapper::toDTO) // aplica o mapper para cada usuário
                .collect(Collectors.toList()); // coleta o resultado em uma lista
        // retorna status 200 (OK) com a lista de DTOs no corpo
        return ResponseEntity.ok(usuarios);
    }

    // mapeia requisições GET para o caminho "/usuarios/{id}" onde {id} é uma variável de caminho
    @GetMapping(path = "/usuarios/{id}")
    public ResponseEntity<UsuarioDTO> visualizar(@PathVariable(parameter = "id") Integer id) {
        try {
            // tenta buscar o usuário pelo ID
            Usuario usuario = usuarioService.buscarPorId(id);
            // retorna status 200 (OK) com o DTO do usuário
            return ResponseEntity.ok(UsuarioMapper.toDTO(usuario));
        } catch (IllegalArgumentException e) {
            // captura exceção de usuário não encontrado
            // retorna status 404 (Not Found) com corpo vazio
            return ResponseEntity.status(404, null);
        }
    }

    // mapeia requisições PATCH para o caminho "/usuarios/{id}/{status}"
    @PatchMapping(path = "/usuarios/{id}/{status}")
    public ResponseEntity<Void> alterarStatus(
            @PathVariable(parameter = "id") Integer id,
            @PathVariable(parameter = "status") String status
    ) {
        try {
            // tenta alterar o status do usuário (ativar/desativar)
            usuarioService.alterarStatus(id, status);
            // retorna status 200 (OK) com corpo vazio
            return ResponseEntity.ok(null);
        } catch (IllegalArgumentException e) {
            // captura exceção de usuário não encontrado ou status inválido
            // retorna status 404 (Not Found) com corpo vazio
            return ResponseEntity.status(404, null);
        }
    }

    // mapeia requisições POST para o caminho "/usuarios/reativar"
    @PostMapping(path = "/usuarios/reativar")
    public ResponseEntity<String> reativar(@RequestBody ReativacaoRequest request) {
        try {
            // tenta reativar o usuário com email e senha fornecidos
            usuarioService.reativar(request.getEmail(), request.getSenha());
            // retorna status 200 (OK) com mensagem de sucesso
            return ResponseEntity.ok("Usuário reativado com sucesso");
        } catch (IllegalArgumentException e) {
            // captura exceção de usuário não encontrado, já ativo ou senha incorreta
            // retorna status 401 (Unauthorized) com a mensagem de erro
            return ResponseEntity.status(401, e.getMessage());
        }
    }
}