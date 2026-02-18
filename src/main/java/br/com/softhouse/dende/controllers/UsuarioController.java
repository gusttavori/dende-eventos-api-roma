package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.ReativacaoRequest;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.UsuarioComum;
import br.com.softhouse.dende.service.UsuarioService;

@Controller
@RequestMapping(path = "/api")
public class UsuarioController {

    private final UsuarioService usuarioService = new UsuarioService();

    @PostMapping(path = "/usuarios")
    public ResponseEntity<UsuarioComum> cadastrar(@RequestBody UsuarioComum usuario) {
        // VALIDAÇÃO: senha não pode ser nula
        if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
            return ResponseEntity.status(400, null);
        }

        UsuarioComum novo = usuarioService.cadastrarUsuarioComum(
                usuario.getNome(),
                usuario.getDataNascimento(),
                usuario.getSexo(),
                usuario.getEmail(),
                usuario.getSenha()  // AGORA INCLUI SENHA
        );
        return ResponseEntity.status(201, novo);
    }

    @PutMapping(path = "/usuarios/{email}")
    public ResponseEntity<Usuario> alterar(
            @PathVariable(parameter = "email") String email,
            @RequestBody UsuarioComum usuario  // ← MUDOU PARA UsuarioComum (NÃO ABSTRATO)
    ) {
        try {
            usuarioService.atualizarUsuario(email, usuario);
            return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404, null);
        }
    }

    @GetMapping(path = "/usuarios/{id}")
    public ResponseEntity<Usuario> visualizar(@PathVariable(parameter = "id") Integer id) {
        try {
            return ResponseEntity.ok(usuarioService.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404, null);
        }
    }

    @PatchMapping(path = "/usuarios/{id}/{status}")
    public ResponseEntity<Void> alterarStatus(
            @PathVariable(parameter = "id") Integer id,
            @PathVariable(parameter = "status") String status
    ) {
        try {
            usuarioService.alterarStatus(id, status);
            return ResponseEntity.ok(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404, null);
        }
    }

    @PostMapping(path = "/usuarios/reativar")
    public ResponseEntity<String> reativar(@RequestBody ReativacaoRequest request) {
        try {
            usuarioService.reativar(request.getEmail(), request.getSenha());
            return ResponseEntity.ok("Usuário reativado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401, e.getMessage());
        }
    }
}