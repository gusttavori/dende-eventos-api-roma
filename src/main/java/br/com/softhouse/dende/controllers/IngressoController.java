package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.dto.*;
import br.com.softhouse.dende.exceptions.EntidadeNaoEncontradaException;
import br.com.softhouse.dende.exceptions.RegraDeNegocioException;
import br.com.softhouse.dende.repositories.EventoRepository;
import br.com.softhouse.dende.repositories.IngressoRepository;
import br.com.softhouse.dende.repositories.UsuarioRepository;
import br.com.softhouse.dende.repositories.util.ConfigProperties;
import br.com.softhouse.dende.repositories.util.ConnectionPool;
import br.com.softhouse.dende.services.IngressoService;
import java.util.List;

@Controller
@RequestMapping(path = "")
public class IngressoController {

    private final IngressoService ingressoService;

    // Injeção de dependência via construtor
    public IngressoController(IngressoService ingressoService) {
        this.ingressoService = ingressoService;
    }

    public IngressoController() {
        ConfigProperties config = new ConfigProperties();
        ConnectionPool connectionPool = new ConnectionPool(config);
        IngressoRepository ingressoRepository = new IngressoRepository(connectionPool);
        EventoRepository eventoRepository = new EventoRepository(connectionPool);
        UsuarioRepository usuarioRepository = new UsuarioRepository(connectionPool);
        this.ingressoService = new IngressoService(ingressoRepository, eventoRepository, usuarioRepository);
    }

    @PostMapping(path = "/organizadores/{organizadorId}/eventos/{eventoId}/ingressos")
    public ResponseEntity<ApiResponse<CompraResponseDTO>> comprar(
            @PathVariable(parameter = "organizadorId") Long organizadorId,
            @PathVariable(parameter = "eventoId") Long eventoId,
            @RequestBody CompraRequestDTO request) {
        try {
            if (request == null || request.getUsuarioEmail() == null || request.getUsuarioEmail().trim().isEmpty()) {
                throw new RegraDeNegocioException("Email do usuário é obrigatório");
            }

            CompraResponseDTO compraResponse = ingressoService.comprar(organizadorId, eventoId, request);
            ApiResponse<CompraResponseDTO> apiResponse = new ApiResponse<>(
                    compraResponse, "Compra processada com sucesso", 201
            );
            return ResponseEntity.status(201, apiResponse);
        } catch (RegraDeNegocioException e) {
            ApiResponse<CompraResponseDTO> apiResponse = new ApiResponse<>(
                    e.getMessage(), 400, "Bad Request"
            );
            return ResponseEntity.status(400, apiResponse);
        } catch (EntidadeNaoEncontradaException e) {
            ApiResponse<CompraResponseDTO> apiResponse = new ApiResponse<>(
                    e.getMessage(), 404, "Not Found"
            );
            return ResponseEntity.status(404, apiResponse);
        } catch (Exception e) {
            ApiResponse<CompraResponseDTO> apiResponse = new ApiResponse<>(
                    "Erro interno ao processar compra: " + e.getMessage(),
                    500, "Internal Server Error"
            );
            return ResponseEntity.status(500, apiResponse);
        }
    }

    @PostMapping(path = "/usuarios/{usuarioId}/ingressos/{ingressoId}")
    public ResponseEntity<ApiResponse<CancelamentoResponseDTO>> cancelar(
            @PathVariable(parameter = "usuarioId") Long usuarioId,
            @PathVariable(parameter = "ingressoId") Long ingressoId) {
        try {
            if (usuarioId == null || ingressoId == null) {
                throw new RegraDeNegocioException("ID do usuário e do ingresso são obrigatórios");
            }

            CancelamentoResponseDTO cancelamentoResponse = ingressoService.cancelar(usuarioId, ingressoId);
            ApiResponse<CancelamentoResponseDTO> apiResponse = new ApiResponse<>(
                    cancelamentoResponse, "Cancelamento realizado com sucesso", 200
            );
            return ResponseEntity.ok(apiResponse);
        } catch (RegraDeNegocioException e) {
            ApiResponse<CancelamentoResponseDTO> apiResponse = new ApiResponse<>(
                    e.getMessage(), 400, "Bad Request"
            );
            return ResponseEntity.status(400, apiResponse);
        } catch (EntidadeNaoEncontradaException e) {
            ApiResponse<CancelamentoResponseDTO> apiResponse = new ApiResponse<>(
                    e.getMessage(), 404, "Not Found"
            );
            return ResponseEntity.status(404, apiResponse);
        } catch (Exception e) {
            ApiResponse<CancelamentoResponseDTO> apiResponse = new ApiResponse<>(
                    "Erro interno ao cancelar ingresso: " + e.getMessage(),
                    500, "Internal Server Error"
            );
            return ResponseEntity.status(500, apiResponse);
        }
    }

    @GetMapping(path = "/usuarios/{usuarioId}/ingressos")
    public ResponseEntity<ApiResponse<List<IngressoDTO>>> listar(@PathVariable(parameter = "usuarioId") Long usuarioId) {
        try {
            if (usuarioId == null) {
                throw new RegraDeNegocioException("ID do usuário é obrigatório");
            }

            List<IngressoDTO> ingressos = ingressoService.listarPorUsuario(usuarioId);
            ApiResponse<List<IngressoDTO>> apiResponse = new ApiResponse<>(
                    ingressos, "Ingressos listados com sucesso", 200
            );
            return ResponseEntity.ok(apiResponse);
        } catch (EntidadeNaoEncontradaException e) {
            ApiResponse<List<IngressoDTO>> apiResponse = new ApiResponse<>(
                    e.getMessage(), 404, "Not Found"
            );
            return ResponseEntity.status(404, apiResponse);
        } catch (Exception e) {
            ApiResponse<List<IngressoDTO>> apiResponse = new ApiResponse<>(
                    "Erro interno ao listar ingressos: " + e.getMessage(),
                    500, "Internal Server Error"
            );
            return ResponseEntity.status(500, apiResponse);
        }
    }
}