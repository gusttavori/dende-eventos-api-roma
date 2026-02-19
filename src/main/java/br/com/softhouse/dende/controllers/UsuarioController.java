package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.ReativacaoRequest;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.UsuarioComum;
import br.com.softhouse.dende.service.UsuarioService;

@Controller
@RequestMapping
public class UsuarioController {

    // Cria uma instância do service
    private final UsuarioService usuarioService = new UsuarioService();

    @PostMapping(path = "/usuarios")
    // Metodo que recebe os dados do usuário comum no corpo da requisição e retorna o usuário criado
    public ResponseEntity<UsuarioComum> cadastrar(@RequestBody UsuarioComum usuario) {
        // VALIDAÇÃO: verifica se a senha foi fornecida e não está vazia
        if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
            // Retorna status 400 (Bad Request) se a senha for inválida
            return ResponseEntity.status(400, null);
        }

        // Chama o service para cadastrar o usuário com os dados fornecidos
        UsuarioComum novo = usuarioService.cadastrarUsuarioComum(
                usuario.getNome(),
                usuario.getDataNascimento(),
                usuario.getSexo(),
                usuario.getEmail(),
                usuario.getSenha()
        );
        // Retorna status 201 (Created) com o usuário criado no corpo da resposta
        return ResponseEntity.status(201, novo);
    }

    @PutMapping(path = "/usuarios/{email}")
    // Mrtodo que recebe o email como parâmetro de path e os dados atualizados no corpo da requisição
    public ResponseEntity<Usuario> alterar(
            @PathVariable(parameter = "email") String email,
            @RequestBody UsuarioComum usuario
    ) {
        try {
            // Chama o service para atualizar o usuário com os dados fornecidos
            usuarioService.atualizarUsuario(email, usuario);
            // Retorna status 200 (OK) com o usuário atualizado
            return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
        } catch (IllegalArgumentException e) {
            // Se ocorrer erro (usuário não encontrado), retorna status 404 (Not Found)
            return ResponseEntity.status(404, null);
        }
    }

    @GetMapping(path = "/usuarios/{id}")
    // Recebe o ID do usuário como parâmetro de path
    public ResponseEntity<Usuario> visualizar(@PathVariable(parameter = "id") Integer id) {
        try {
            // Chama o service para buscar o usuário pelo ID e retorna com status 200
            return ResponseEntity.ok(usuarioService.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            // Se não encontrar, retorna status 404
            return ResponseEntity.status(404, null);
        }
    }

    @PatchMapping(path = "/usuarios/{id}/{status}")
    // Metodo que recebe o ID e o status (ativar/desativar) como parâmetros de path
    public ResponseEntity<Void> alterarStatus(
            @PathVariable(parameter = "id") Integer id,
            @PathVariable(parameter = "status") String status
    ) {
        try {
            // Chama o service para alterar o status do usuário
            usuarioService.alterarStatus(id, status);
            // Retorna status 200 com corpo vazio
            return ResponseEntity.ok(null);
        } catch (IllegalArgumentException e) {
            // Se ocorrer erro, retorna status 404
            return ResponseEntity.status(404, null);
        }
    }

    @PostMapping(path = "/usuarios/reativar")
    // Metodo que recebe as credenciais de reativação no corpo da requisição
    public ResponseEntity<String> reativar(@RequestBody ReativacaoRequest request) {
        try {
            // Chama o service para reativar o usuário com email e senha fornecidos
            usuarioService.reativar(request.getEmail(), request.getSenha());
            // Retorna mensagem de sucesso com status 200
            return ResponseEntity.ok("Usuário reativado com sucesso");
        } catch (IllegalArgumentException e) {
            // Se as credenciais forem inválidas, retorna status 401 com a mensagem de erro
            return ResponseEntity.status(401, e.getMessage());
        }
    }
}