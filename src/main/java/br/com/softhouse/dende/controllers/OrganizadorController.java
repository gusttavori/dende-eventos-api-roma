package br.com.softhouse.dende.controllers;

import br.com.dende.softhouse.annotations.Controller;
import br.com.dende.softhouse.annotations.request.*;
import br.com.dende.softhouse.process.route.ResponseEntity;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.service.OrganizadorService;

import java.util.List;

@Controller
@RequestMapping
public class OrganizadorController {

    private final OrganizadorService service = new OrganizadorService();

    @PostMapping(path = "/organizadores")
    // [ITEM 9] Para e-mail duplicado (IllegalArgumentException do service), o retorno é 400.
    // O mais semântico seria 409 (Conflict), pois o recurso já existe.
    // [ITEM 4] O objeto Organizador recebido é a entidade completa. Crie um DTO de entrada
    // (CadastrarOrganizadorRequest) com apenas os campos necessários para o cadastro.
    // [ITEM 4] O Organizador retornado na resposta expõe a SENHA. Nunca retorne a senha.
    // Crie um DTO de saída (OrganizadorResponse) sem o campo senha.
    // [ITEM — US2] O cadastro de dados da empresa (CNPJ, Razão Social, Nome Fantasia) está
    // disponível via o atributo empresa dentro do Organizador. Correto em OO.
    public ResponseEntity<Organizador> cadastrar(@RequestBody Organizador organizador) {
        try {
            if (organizador.getSenha() == null || organizador.getSenha().trim().isEmpty()) {
                return ResponseEntity.status(400, null);
            }
            return ResponseEntity.status(201, service.cadastrar(organizador));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400, null);
        }
    }

    @PutMapping(path = "/organizadores/{email}")
    // [ITEM 9] Retornar 200 (OK) para atualização bem-sucedida está correto.
    // Porém, quando o usuário não é um Organizador, retorna 404 — seria mais preciso 400 (Bad Request),
    // pois o recurso existe mas não é do tipo correto.
    // [ITEM 4] O Organizador retornado inclui a senha — use um DTO de saída sem a senha.
    public ResponseEntity<Organizador> alterar(
            @PathVariable(parameter = "email") String email,
            @RequestBody Organizador organizador
    ) {
        try {
            return ResponseEntity.ok(service.atualizar(email, organizador));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404, null);
        }
    }

    @GetMapping(path = "/organizadores")
    // [ITEM 4] A lista de Organizadores retornada expõe a senha de todos os organizadores.
    // Use um DTO de saída (OrganizadorResponse) sem o campo senha.
    // [ITEM — US11] Esta rota não foi mapeada para a US11 — ela lista TODOS os organizadores,
    // o que não é uma funcionalidade prevista nas User Stories. A US11 é para listar
    // os EVENTOS de um organizador específico, não os organizadores em si.
    public ResponseEntity<List<Organizador>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping(path = "/organizadores/{email}")
    // [ITEM 4] O Organizador retornado inclui a senha — use um DTO de saída sem a senha.
    // [ITEM — US4] Para visualizar o perfil do organizador, a US4 exige a exibição da idade
    // no formato "Y anos, M meses e D dias" e os dados da empresa. A entidade retornada tem
    // esses dados, mas a formatação da idade depende do método getIdade() que não é serializado
    // automaticamente. Verifique se esse campo está sendo incluído na resposta.
    public ResponseEntity<Organizador> visualizar(@PathVariable(parameter = "email") String email) {
        try {
            Organizador organizador = service.buscarPorEmail(email);
            return ResponseEntity.ok(organizador);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404, null);
        }
    }

    @PatchMapping(path = "/organizadores/{email}/{status}")
    // [ITEM 9] Para desativação bem-sucedida, retorna 200 (OK) — correto.
    // Para erros, retorna 400 tanto para "organizador não encontrado" (deveria ser 404)
    // quanto para "possui eventos ativos" (400 é aceitável aqui) e "status inválido" (400 correto).
    // Sugestão: diferencie os casos retornando 404 quando o organizador não for encontrado.
    // [ITEM — US5] A verificação de eventos ativos antes de desativar está implementada
    // no OrganizadorService, o que está correto para a US5.
    public ResponseEntity<String> alterarStatus(
            @PathVariable(parameter = "email") String email,
            @PathVariable(parameter = "status") String status
    ) {
        try {
            service.alterarStatus(email, status);
            return ResponseEntity.ok("Status alterado com sucesso");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(400, e.getMessage());
        }
    }
}