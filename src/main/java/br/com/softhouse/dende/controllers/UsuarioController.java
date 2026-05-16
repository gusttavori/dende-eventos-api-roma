package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.dto.ApiResponse;
import br.com.softhouse.dende.dto.StatusChangeRequestDTO;
import br.com.softhouse.dende.dto.UsuarioDTO;
import br.com.softhouse.dende.exceptions.EntidadeNaoEncontradaException;
import br.com.softhouse.dende.exceptions.RegraDeNegocioException;
import br.com.softhouse.dende.repositories.UsuarioRepository;
import br.com.softhouse.dende.repositories.util.ConfigProperties;
import br.com.softhouse.dende.repositories.util.ConnectionPool;
import br.com.softhouse.dende.services.UsuarioService;

@Controller
@RequestMapping(path = "/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public UsuarioController() {
        ConfigProperties config = new ConfigProperties();
        ConnectionPool connectionPool = new ConnectionPool(config);
        UsuarioRepository usuarioRepository = new UsuarioRepository(connectionPool);
        this.usuarioService = new UsuarioService(usuarioRepository);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UsuarioDTO>> cadastrar(@RequestBody UsuarioDTO dto) {
        try {
            UsuarioDTO response = usuarioService.cadastrar(dto);
            ApiResponse<UsuarioDTO> apiResponse = new ApiResponse<>(
                    response, "Usuário cadastrado com sucesso", 201
            );
            return ResponseEntity.status(201, apiResponse);
        } catch (RegraDeNegocioException e) {
            ApiResponse<UsuarioDTO> apiResponse = new ApiResponse<>(
                    e.getMessage(), 400, "Bad Request"
            );
            return ResponseEntity.status(400, apiResponse);
        } catch (Exception e) {
            ApiResponse<UsuarioDTO> apiResponse = new ApiResponse<>(
                    "Erro interno: " + e.getMessage(), 500, "Internal Server Error"
            );
            return ResponseEntity.status(500, apiResponse);
        }
    }

    @PutMapping(path = "/{usuarioId}")
    public ResponseEntity<ApiResponse<UsuarioDTO>> alterar(
            @PathVariable(parameter = "usuarioId") Long id,
            @RequestBody UsuarioDTO dto) {
        try {
            UsuarioDTO response = usuarioService.atualizar(id, dto);
            ApiResponse<UsuarioDTO> apiResponse = new ApiResponse<>(
                    response, "Usuário atualizado com sucesso", 200
            );
            return ResponseEntity.ok(apiResponse);
        } catch (EntidadeNaoEncontradaException e) {
            ApiResponse<UsuarioDTO> apiResponse = new ApiResponse<>(
                    e.getMessage(), 404, "Not Found"
            );
            return ResponseEntity.status(404, apiResponse);
        } catch (RegraDeNegocioException e) {
            ApiResponse<UsuarioDTO> apiResponse = new ApiResponse<>(
                    e.getMessage(), 400, "Bad Request"
            );
            return ResponseEntity.status(400, apiResponse);
        }
    }

    @GetMapping(path = "/{usuarioId}")
    public ResponseEntity<ApiResponse<UsuarioDTO>> visualizar(@PathVariable(parameter = "usuarioId") Long id) {
        try {
            UsuarioDTO response = usuarioService.buscarPorId(id);
            ApiResponse<UsuarioDTO> apiResponse = new ApiResponse<>(
                    response, "Usuário encontrado", 200
            );
            return ResponseEntity.ok(apiResponse);
        } catch (EntidadeNaoEncontradaException e) {
            ApiResponse<UsuarioDTO> apiResponse = new ApiResponse<>(
                    e.getMessage(), 404, "Not Found"
            );
            return ResponseEntity.status(404, apiResponse);
        }
    }

    @PatchMapping(path = "/{usuarioId}/{status}")
    public ResponseEntity<ApiResponse<UsuarioDTO>> alterarStatus(
            @PathVariable(parameter = "usuarioId") Long id,
            @PathVariable(parameter = "status") boolean ativar,
            @RequestBody StatusChangeRequestDTO request) {
        try {
            if (request == null || request.getSenha() == null || request.getSenha().trim().isEmpty()) {
                ApiResponse<UsuarioDTO> apiResponse = new ApiResponse<>(
                        "Senha é obrigatória", 400, "Bad Request"
                );
                return ResponseEntity.status(400, apiResponse);
            }

            UsuarioDTO response;
            String operacao;

            if (ativar) {
                response = usuarioService.ativarComSenha(id, request.getSenha());
                operacao = "ativado";
            } else {
                response = usuarioService.desativarComSenha(id, request.getSenha());
                operacao = "desativado";
            }

            ApiResponse<UsuarioDTO> apiResponse = new ApiResponse<>(
                    response, "Usuário " + operacao + " com sucesso", 200
            );
            return ResponseEntity.ok(apiResponse);
        } catch (RegraDeNegocioException e) {
            int status = e.getMessage().contains("Senha incorreta") ? 401 : 400;
            String erro = e.getMessage().contains("Senha incorreta") ? "Unauthorized" : "Bad Request";
            ApiResponse<UsuarioDTO> apiResponse = new ApiResponse<>(
                    e.getMessage(), status, erro
            );
            return ResponseEntity.status(status, apiResponse);
        }
    }
}