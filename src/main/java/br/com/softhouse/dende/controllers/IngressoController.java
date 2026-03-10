package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.dto.IngressoDTO;
import br.com.softhouse.dende.mappers.IngressoMapper;
import br.com.softhouse.dende.service.IngressoService;

import java.util.List;
import java.util.stream.Collectors;

// indica que esta classe é um controller (componente que recebe requisições HTTP)
@Controller
// define o caminho base para todas as rotas deste controller (vazio = raiz)
@RequestMapping
public class IngressoController {

    // cria uma instância do serviço de ingresso para delegar as operações
    private final IngressoService service = new IngressoService();

    // mapeia requisições POST para o caminho "/usuarios/{email}/eventos/{eventoId}/ingressos"
    @PostMapping(path = "/usuarios/{email}/eventos/{eventoId}/ingressos")
    public ResponseEntity<List<IngressoDTO>> comprar(
            // extrai o valor da variável "email" do caminho da URL
            @PathVariable(parameter = "email") String email,
            // extrai o valor da variável "eventoId" do caminho da URL
            @PathVariable(parameter = "eventoId") int eventoId
    ) {
        try {
            // chama o serviço para comprar ingresso e obtém a lista de ingressos comprados
            List<IngressoDTO> ingressos = service.comprarIngresso(email, eventoId).stream()
                    .map(IngressoMapper::toDTO) // converte cada ingresso para DTO
                    .collect(Collectors.toList()); // coleta os DTOs em uma lista
            // retorna status 201 (Created) com a lista de ingressos no corpo
            return ResponseEntity.status(201, ingressos);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // captura exceções de:
            // - usuário não encontrado
            // - evento não encontrado
            // - evento inativo
            // - capacidade esgotada
            // - evento já realizado
            // retorna status 400 (Bad Request) com corpo vazio
            return ResponseEntity.status(400, null);
        }
    }

    // mapeia requisições POST para o caminho "/ingressos/{id}/cancelar"
    @PostMapping(path = "/ingressos/{id}/cancelar")
    public ResponseEntity<String> cancelar(
            // extrai o valor da variável "id" do caminho da URL
            @PathVariable(parameter = "id") int id
    ) {
        try {
            // chama o serviço para cancelar o ingresso pelo ID
            service.cancelarIngresso(id);
            // retorna status 200 (OK) com mensagem de sucesso
            return ResponseEntity.ok("Ingresso cancelado com sucesso");
        } catch (IllegalArgumentException | IllegalStateException e) {
            // captura exceções de:
            // - ingresso não encontrado
            // - evento já realizado
            // - ingresso já cancelado
            // retorna status 400 (Bad Request) com a mensagem de erro
            return ResponseEntity.status(400, e.getMessage());
        }
    }

    // mapeia requisições GET para o caminho "/usuarios/{email}/ingressos"
    @GetMapping(path = "/usuarios/{email}/ingressos")
    public ResponseEntity<List<IngressoDTO>> listar(
            // extrai o valor da variável "email" do caminho da URL
            @PathVariable(parameter = "email") String email
    ) {
        try {
            // chama o serviço para listar ingressos do usuário
            List<IngressoDTO> ingressos = service.listarIngressosUsuario(email).stream()
                    .map(IngressoMapper::toDTO) // converte cada ingresso para DTO
                    .collect(Collectors.toList()); // coleta os DTOs em uma lista
            // retorna status 200 (OK) com a lista de ingressos
            return ResponseEntity.ok(ingressos);
        } catch (IllegalArgumentException e) {
            // captura exceção de usuário não encontrado
            // retorna status 404 (Not Found) com corpo vazio
            return ResponseEntity.status(404, null);
        }
    }
}