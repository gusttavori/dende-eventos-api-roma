package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.dto.EventoDTO;
import br.com.softhouse.dende.dto.request.EventoRequestDTO;
import br.com.softhouse.dende.mappers.EventoMapper;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.service.EventoService;
import br.com.softhouse.dende.service.OrganizadorService;

import java.util.List;
import java.util.stream.Collectors;

// indica que esta classe é um controller (componente que recebe requisições HTTP)
@Controller
// define o caminho base para todas as rotas deste controller (vazio = raiz)
@RequestMapping
public class EventoController {

    // cria instâncias dos serviços necessários para operações com eventos e organizadores
    private final EventoService eventoService = new EventoService();
    private final OrganizadorService organizadorService = new OrganizadorService();

    // mapeia requisições POST para o caminho "/organizadores/{email}/eventos"
    @PostMapping(path = "/organizadores/{email}/eventos")
    public ResponseEntity<EventoDTO> cadastrar(
            // extrai o valor da variável "email" do caminho da URL
            @PathVariable(parameter = "email") String email,
            // extrai o corpo da requisição e converte para EventoRequestDTO
            @RequestBody EventoRequestDTO request
    ) {
        try {
            // busca o organizador pelo email para verificar se existe
            Organizador organizador = organizadorService.buscarPorEmail(email);
            // chama o serviço para cadastrar um novo evento
            Evento novoEvento = eventoService.cadastrarEvento(email, request, organizador);
            // retorna status 201 (Created) com o DTO do evento criado
            return ResponseEntity.status(201, EventoMapper.toDTO(novoEvento));
        } catch (IllegalArgumentException e) {
            // captura exceção de organizador não encontrado ou dados inválidos
            // retorna status 400 (Bad Request) com corpo vazio
            return ResponseEntity.status(400, null);
        }
    }

    // mapeia requisições PUT para o caminho "/organizadores/{email}/eventos/{id}"
    @PutMapping(path = "/organizadores/{email}/eventos/{id}")
    public ResponseEntity<EventoDTO> alterar(
            // extrai o valor da variável "email" do caminho da URL
            @PathVariable(parameter = "email") String email,
            // extrai o valor da variável "id" do caminho da URL
            @PathVariable(parameter = "id") int id,
            // extrai o corpo da requisição com os dados atualizados
            @RequestBody EventoRequestDTO request
    ) {
        try {
            // chama o serviço para alterar o evento
            Evento eventoAtualizado = eventoService.alterarEvento(email, id, request);
            // retorna status 200 (OK) com o DTO do evento alterado
            return ResponseEntity.ok(EventoMapper.toDTO(eventoAtualizado));
        } catch (IllegalArgumentException e) {
            // captura exceções de:
            // - organizador não encontrado
            // - evento não encontrado
            // - evento não pertence ao organizador
            // - evento inativo
            // retorna status 404 (Not Found) com corpo vazio
            return ResponseEntity.status(404, null);
        }
    }

    // mapeia requisições PATCH para o caminho "/organizadores/{email}/eventos/{id}/{status}"
    @PatchMapping(path = "/organizadores/{email}/eventos/{id}/{status}")
    public ResponseEntity<String> alterarStatus(
            // extrai o valor da variável "email" do caminho da URL
            @PathVariable(parameter = "email") String email,
            // extrai o valor da variável "id" do caminho da URL
            @PathVariable(parameter = "id") int id,
            // extrai o valor da variável "status" do caminho da URL (ativar/desativar)
            @PathVariable(parameter = "status") String status
    ) {
        try {
            // chama o serviço para alterar o status do evento
            eventoService.alterarStatusEvento(id, status);
            // retorna status 200 (OK) com mensagem de sucesso
            return ResponseEntity.ok("Status alterado com sucesso");
        } catch (IllegalArgumentException e) {
            // captura exceções de evento não encontrado ou status inválido
            // retorna status 400 (Bad Request) com a mensagem de erro
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    // mapeia requisições GET para o caminho "/eventos/feed"
    @GetMapping(path = "/eventos/feed")
    public ResponseEntity<List<EventoDTO>> feed() {
        // obtém todos os eventos ativos do serviço
        List<EventoDTO> eventos = eventoService.listarEventosAtivos().stream()
                .map(EventoMapper::toDTO) // converte cada evento para DTO
                .collect(Collectors.toList()); // coleta os DTOs em uma lista
        // retorna status 200 (OK) com a lista de eventos ativos
        return ResponseEntity.ok(eventos);
    }

    // mapeia requisições GET para o caminho "/organizadores/{email}/eventos"
    @GetMapping(path = "/organizadores/{email}/eventos")
    public ResponseEntity<List<EventoDTO>> listarPorOrganizador(
            // extrai o valor da variável "email" do caminho da URL
            @PathVariable(parameter = "email") String email
    ) {
        try {
            // obtém os eventos de um organizador específico
            List<EventoDTO> eventos = eventoService.listarEventosPorOrganizador(email).stream()
                    .map(EventoMapper::toDTO) // converte cada evento para DTO
                    .collect(Collectors.toList()); // coleta os DTOs em uma lista
            // retorna status 200 (OK) com a lista de eventos do organizador
            return ResponseEntity.ok(eventos);
        } catch (IllegalArgumentException e) {
            // captura exceção de organizador não encontrado
            // retorna status 404 (Not Found) com corpo vazio
            return ResponseEntity.status(404, null);
        }
    }
}