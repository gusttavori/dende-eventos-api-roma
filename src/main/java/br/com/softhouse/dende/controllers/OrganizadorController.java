package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.dto.ApiResponse;
import br.com.softhouse.dende.dto.OrganizadorDTO;
import br.com.softhouse.dende.dto.StatusChangeRequestDTO;
import br.com.softhouse.dende.exceptions.EntidadeNaoEncontradaException;
import br.com.softhouse.dende.exceptions.RegraDeNegocioException;
import br.com.softhouse.dende.repositories.EventoRepository;
import br.com.softhouse.dende.repositories.OrganizadorRepository;
import br.com.softhouse.dende.repositories.util.ConfigProperties;
import br.com.softhouse.dende.repositories.util.ConnectionPool;
import br.com.softhouse.dende.services.OrganizadorService;

@Controller
@RequestMapping(path = "/organizadores")
public class OrganizadorController {

    private final OrganizadorService organizadorService;

    // Injeção de dependência via construtor
    public OrganizadorController(OrganizadorService organizadorService) {
        this.organizadorService = organizadorService;
    }

    public OrganizadorController() {
        ConfigProperties config = new ConfigProperties();
        ConnectionPool connectionPool = new ConnectionPool(config);
        OrganizadorRepository organizadorRepository = new OrganizadorRepository(connectionPool);
        EventoRepository eventoRepository = new EventoRepository(connectionPool);
        this.organizadorService = new OrganizadorService(organizadorRepository, eventoRepository);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OrganizadorDTO>> cadastrar(@RequestBody OrganizadorDTO dto) {
        try {
            OrganizadorDTO response = organizadorService.cadastrar(dto);
            ApiResponse<OrganizadorDTO> apiResponse = new ApiResponse<>(
                    response, "Organizador cadastrado com sucesso", 201
            );
            return ResponseEntity.status(201, apiResponse);
        } catch (RegraDeNegocioException e) {
            ApiResponse<OrganizadorDTO> apiResponse = new ApiResponse<>(
                    e.getMessage(), 400, "Bad Request"
            );
            return ResponseEntity.status(400, apiResponse);
        } catch (Exception e) {
            ApiResponse<OrganizadorDTO> apiResponse = new ApiResponse<>(
                    "Erro interno: " + e.getMessage(), 500, "Internal Server Error"
            );
            return ResponseEntity.status(500, apiResponse);
        }
    }

    @PutMapping(path = "/{organizadorId}")
    public ResponseEntity<ApiResponse<OrganizadorDTO>> alterar(
            @PathVariable(parameter = "organizadorId") Long id,
            @RequestBody OrganizadorDTO dto) {
        try {
            OrganizadorDTO response = organizadorService.atualizar(id, dto);
            ApiResponse<OrganizadorDTO> apiResponse = new ApiResponse<>(
                    response, "Organizador atualizado com sucesso", 200
            );
            return ResponseEntity.ok(apiResponse);
        } catch (EntidadeNaoEncontradaException e) {
            ApiResponse<OrganizadorDTO> apiResponse = new ApiResponse<>(
                    e.getMessage(), 404, "Not Found"
            );
            return ResponseEntity.status(404, apiResponse);
        } catch (RegraDeNegocioException e) {
            ApiResponse<OrganizadorDTO> apiResponse = new ApiResponse<>(
                    e.getMessage(), 400, "Bad Request"
            );
            return ResponseEntity.status(400, apiResponse);
        }
    }

    @GetMapping(path = "/{organizadorId}")
    public ResponseEntity<ApiResponse<OrganizadorDTO>> visualizar(@PathVariable(parameter = "organizadorId") Long id) {
        try {
            OrganizadorDTO response = organizadorService.buscarPorId(id);
            ApiResponse<OrganizadorDTO> apiResponse = new ApiResponse<>(
                    response, "Organizador encontrado", 200
            );
            return ResponseEntity.ok(apiResponse);
        } catch (EntidadeNaoEncontradaException e) {
            ApiResponse<OrganizadorDTO> apiResponse = new ApiResponse<>(
                    e.getMessage(), 404, "Not Found"
            );
            return ResponseEntity.status(404, apiResponse);
        }
    }

    @PatchMapping(path = "/{organizadorId}/{status}")
    public ResponseEntity<ApiResponse<OrganizadorDTO>> alterarStatus(
            @PathVariable(parameter = "organizadorId") Long id,
            @PathVariable(parameter = "status") boolean ativar,
            @RequestBody StatusChangeRequestDTO request) {
        try {
            if (request == null || request.getSenha() == null || request.getSenha().trim().isEmpty()) {
                ApiResponse<OrganizadorDTO> apiResponse = new ApiResponse<>(
                        "Senha é obrigatória", 400, "Bad Request"
                );
                return ResponseEntity.status(400, apiResponse);
            }

            OrganizadorDTO response;
            String operacao;

            if (ativar) {
                response = organizadorService.ativarComSenha(id, request.getSenha());
                operacao = "ativado";
            } else {
                response = organizadorService.desativarComSenha(id, request.getSenha());
                operacao = "desativado";
            }

            ApiResponse<OrganizadorDTO> apiResponse = new ApiResponse<>(
                    response, "Organizador " + operacao + " com sucesso", 200
            );
            return ResponseEntity.ok(apiResponse);
        } catch (RegraDeNegocioException e) {
            int status = e.getMessage().contains("Senha incorreta") ? 401 : 400;
            String erro = e.getMessage().contains("Senha incorreta") ? "Unauthorized" : "Bad Request";
            ApiResponse<OrganizadorDTO> apiResponse = new ApiResponse<>(
                    e.getMessage(), status, erro
            );
            return ResponseEntity.status(status, apiResponse);
        }
    }
}