package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.UsuarioComum;
import br.com.softhouse.dende.service.UsuarioService;

import java.util.List;

@Controller
@RequestMapping(path = "/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService = new UsuarioService();

    public UsuarioController() {}

    @PostMapping
    public ResponseEntity<String> cadastrarUsuarioComum(@RequestBody UsuarioComum usuario) {
        usuarioService.cadastrarUsuarioComum(
                usuario.getNome(),
                usuario.getDataNascimento(),
                usuario.getSexo(),
                usuario.getEmail(),
                usuario.getSenha()
        );
        return ResponseEntity.ok("Usuário " + usuario.getEmail() + " registrado com sucesso!");
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @PutMapping(path = "/{usuarioId}")
    public ResponseEntity<String> atualizarUsuario(
            @PathVariable(parameter = "usuarioId") int usuarioId,
            @RequestBody UsuarioComum usuarioAtualizado) {

        Usuario usuario = usuarioService.listarUsuarios().stream()
                .filter(u -> u.getId() == usuarioId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        usuario.alterarPerfil(
                usuarioAtualizado.getNome(),
                usuarioAtualizado.getDataNascimento(),
                usuarioAtualizado.getSexo()
        );
        usuario.setSenha(usuarioAtualizado.getSenha());

        return ResponseEntity.ok("Usuário " + usuario.getEmail() + " atualizado com sucesso!");
    }

    @PostMapping(path = "/reativar")
    public ResponseEntity<String> reativarUsuario(@RequestBody UsuarioComum usuario) {
        String resultado = usuarioService.reativarPerfil(usuario.getEmail(), usuario.getSenha());
        return ResponseEntity.ok(resultado);
    }
}

