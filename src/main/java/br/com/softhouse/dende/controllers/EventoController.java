package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.dto.ApiResponse;
import br.com.softhouse.dende.dto.EventoDTO;
import br.com.softhouse.dende.dto.EventoResumoDTO;
import br.com.softhouse.dende.exceptions.EntidadeNaoEncontradaException;
import br.com.softhouse.dende.exceptions.RegraDeNegocioException;
import br.com.softhouse.dende.repositories.EventoRepository;
import br.com.softhouse.dende.repositories.IngressoRepository;
import br.com.softhouse.dende.repositories.OrganizadorRepository;
import br.com.softhouse.dende.repositories.util.ConfigProperties;
import br.com.softhouse.dende.repositories.util.ConnectionPool;
import br.com.softhouse.dende.services.EventoService;

import java.util.List;

@Controller
@RequestMapping(path = "")
public class EventoController {

    private final EventoService eventoService;

    // Construtor padrão - usado pelo framework
    public EventoController() {
        // Instancia as dependências manualmente
        ConfigProperties config = new ConfigProperties();
        ConnectionPool connectionPool = new ConnectionPool(config);
        EventoRepository eventoRepository = new EventoRepository(connectionPool);
        OrganizadorRepository organizadorRepository = new OrganizadorRepository(connectionPool);
        IngressoRepository ingressoRepository = new IngressoRepository(connectionPool);
        this.eventoService = new EventoService(eventoRepository, organizadorRepository, ingressoRepository);
    }

    // Construtor para injeção de dependência (opcional)
    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping(path = "/organizadores/{organizadorId}/eventos")
    public ResponseEntity<ApiResponse<EventoDTO>> cadastrar(
            @PathVariable(parameter = "organizadorId") Long organizadorId,
            @RequestBody EventoDTO dto) {
        try {
            EventoDTO response = eventoService.cadastrar(organizadorId, dto);
            ApiResponse<EventoDTO> apiResponse = new ApiResponse<>(
                    response, "Evento cadastrado com sucesso", 201
            );
            return ResponseEntity.status(201, apiResponse);
        } catch (RegraDeNegocioException e) {
            ApiResponse<EventoDTO> apiResponse = new ApiResponse<>(
                    e.getMessage(), 400, "Bad Request"
            );
            return ResponseEntity.status(400, apiResponse);
        } catch (EntidadeNaoEncontradaException e) {
            ApiResponse<EventoDTO> apiResponse = new ApiResponse<>(
                    e.getMessage(), 404, "Not Found"
            );
            return ResponseEntity.status(404, apiResponse);
        } catch (Exception e) {
            ApiResponse<EventoDTO> apiResponse = new ApiResponse<>(
                    "Erro interno: " + e.getMessage(), 500, "Internal Server Error"
            );
            return ResponseEntity.status(500, apiResponse);
        }
    }

    @PutMapping(path = "/organizadores/{organizadorId}/eventos/{eventoId}")
    public ResponseEntity<ApiResponse<EventoDTO>> alterar(
            @PathVariable(parameter = "organizadorId") Long organizadorId,
            @PathVariable(parameter = "eventoId") Long eventoId,
            @RequestBody EventoDTO dto) {
        try {
            EventoDTO response = eventoService.atualizar(organizadorId, eventoId, dto);
            ApiResponse<EventoDTO> apiResponse = new ApiResponse<>(
                    response, "Evento atualizado com sucesso", 200
            );
            return ResponseEntity.ok(apiResponse);
        } catch (RegraDeNegocioException e) {
            ApiResponse<EventoDTO> apiResponse = new ApiResponse<>(
                    e.getMessage(), 400, "Bad Request"
            );
            return ResponseEntity.status(400, apiResponse);
        } catch (EntidadeNaoEncontradaException e) {
            ApiResponse<EventoDTO> apiResponse = new ApiResponse<>(
                    e.getMessage(), 404, "Not Found"
            );
            return ResponseEntity.status(404, apiResponse);
        } catch (Exception e) {
            ApiResponse<EventoDTO> apiResponse = new ApiResponse<>(
                    "Erro interno: " + e.getMessage(), 500, "Internal Server Error"
            );
            return ResponseEntity.status(500, apiResponse);
        }
    }

    @PatchMapping(path = "/organizadores/{organizadorId}/eventos/{eventoId}/{status}")
    public ResponseEntity<ApiResponse<EventoDTO>> alterarStatusEvento(
            @PathVariable(parameter = "organizadorId") Long organizadorId,
            @PathVariable(parameter = "eventoId") Long eventoId,
            @PathVariable(parameter = "status") boolean ativar) {
        try {
            EventoDTO response;
            String operacao;

            if (ativar) {
                response = eventoService.ativar(organizadorId, eventoId);
                operacao = "ativado";
            } else {
                response = eventoService.desativar(organizadorId, eventoId);
                operacao = "desativado";
            }

            ApiResponse<EventoDTO> apiResponse = new ApiResponse<>(
                    response, "Evento " + operacao + " com sucesso", 200
            );
            return ResponseEntity.ok(apiResponse);
        } catch (RegraDeNegocioException e) {
            ApiResponse<EventoDTO> apiResponse = new ApiResponse<>(
                    e.getMessage(), 400, "Bad Request"
            );
            return ResponseEntity.status(400, apiResponse);
        } catch (EntidadeNaoEncontradaException e) {
            ApiResponse<EventoDTO> apiResponse = new ApiResponse<>(
                    e.getMessage(), 404, "Not Found"
            );
            return ResponseEntity.status(404, apiResponse);
        } catch (Exception e) {
            ApiResponse<EventoDTO> apiResponse = new ApiResponse<>(
                    "Erro interno: " + e.getMessage(), 500, "Internal Server Error"
            );
            return ResponseEntity.status(500, apiResponse);
        }
    }

    @GetMapping(path = "/organizadores/{organizadorId}/eventos")
    public ResponseEntity<ApiResponse<List<EventoResumoDTO>>> listarDoOrganizador(
            @PathVariable(parameter = "organizadorId") Long organizadorId) {
        try {
            List<EventoResumoDTO> resumos = eventoService.listarPorOrganizador(organizadorId);
            ApiResponse<List<EventoResumoDTO>> apiResponse = new ApiResponse<>(
                    resumos, "Eventos listados com sucesso", 200
            );
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse<List<EventoResumoDTO>> apiResponse = new ApiResponse<>(
                    "Erro ao listar eventos: " + e.getMessage(), 400, "Bad Request"
            );
            return ResponseEntity.status(400, apiResponse);
        }
    }

    @GetMapping(path = "/eventos")
    public ResponseEntity<ApiResponse<List<EventoDTO>>> feed() {
        try {
            List<EventoDTO> response = eventoService.feedAtivos();
            ApiResponse<List<EventoDTO>> apiResponse = new ApiResponse<>(
                    response, "Feed de eventos carregado", 200
            );
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse<List<EventoDTO>> apiResponse = new ApiResponse<>(
                    "Erro ao carregar feed: " + e.getMessage(), 400, "Bad Request"
            );
            return ResponseEntity.status(400, apiResponse);
        }
    }
}