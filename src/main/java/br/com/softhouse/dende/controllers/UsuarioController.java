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

    private final UsuarioService usuarioService = new UsuarioService();

    @PostMapping(path = "/usuarios")
    // [ITEM 9] O retorno 400 para senha nula está correto (Bad Request).
    // Porém, quando o service lança IllegalArgumentException (e-mail duplicado),
    // o controller retorna 201 via exceção não capturada — adicione um try/catch
    // para retornar 409 (Conflict) em caso de e-mail já cadastrado, pois indica
    // conflito de recurso existente, não uma requisição inválida.
    // [ITEM 4] O objeto UsuarioComum recebido no corpo da requisição é a entidade de domínio
    // completa. Considere criar um DTO de entrada (CadastrarUsuarioRequest) com apenas os
    // campos necessários: nome, dataNascimento, sexo, email, senha.
    // [ITEM 5] Um Mapper (ex: UsuarioMapper) seria a boa prática para converter o DTO em entidade.
    // Não era obrigatório nesta avaliação, mas seria o ideal em produção.
    // [ITEM 4] O objeto UsuarioComum retornado expõe a SENHA do usuário na resposta.
    // Nunca retorne a senha em uma resposta HTTP. Crie um DTO de saída (UsuarioResponse)
    // sem o campo senha.
    public ResponseEntity<UsuarioComum> cadastrar(@RequestBody UsuarioComum usuario) {
        if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
            return ResponseEntity.status(400, null);
        }

        UsuarioComum novo = usuarioService.cadastrarUsuarioComum(
                usuario.getNome(),
                usuario.getDataNascimento(),
                usuario.getSexo(),
                usuario.getEmail(),
                usuario.getSenha()
        );
        return ResponseEntity.status(201, novo);
    }

    @PutMapping(path = "/usuarios/{email}")
    // [ITEM 9] Quando a atualização ocorre com sucesso, retorna 200 (OK) — correto.
    // Porém, ao buscar o usuário após a atualização (buscarPorEmail) uma segunda chamada
    // ao service é feita desnecessariamente. O método atualizarUsuario() poderia retornar
    // o próprio usuário atualizado para evitar a chamada dupla.
    // [ITEM 4] O corpo da requisição é UsuarioComum completo. Crie um DTO de atualização
    // sem campos sensíveis como email e id.
    // [ITEM 9] Qualquer IllegalArgumentException retorna 404. Mas se a exceção for
    // "Usuário não é do tipo comum", o status correto seria 400 (Bad Request), não 404.
    public ResponseEntity<Usuario> alterar(
            @PathVariable(parameter = "email") String email,
            @RequestBody UsuarioComum usuario
    ) {
        try {
            usuarioService.atualizarUsuario(email, usuario);
            return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404, null);
        }
    }

    @GetMapping(path = "/usuarios/{id}")
    // [ITEM 4] O retorno é o objeto Usuario completo, que inclui a senha.
    // Nunca retorne a senha em uma resposta HTTP. Use um DTO de saída sem o campo senha.
    // [ITEM — US4] A US4 exige que a idade seja exibida no formato "Y anos, M meses e D dias".
    // O objeto Usuario retornado expõe os dados brutos. Seria necessário um DTO que inclua
    // o campo idadeFormatada calculado pelo método getIdade().
    public ResponseEntity<Usuario> visualizar(@PathVariable(parameter = "id") Integer id) {
        try {
            return ResponseEntity.ok(usuarioService.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404, null);
        }
    }

    @PatchMapping(path = "/usuarios/{id}/{status}")
    // [ITEM 9] O endpoint de desativação retorna 200 (OK) com corpo vazio para sucesso — aceitável.
    // Porém, tanto "não encontrado" quanto "status inválido" retornam 404.
    // "Status inválido" deveria retornar 400 (Bad Request).
    // [ITEM — US5] Este endpoint de desativação para UsuarioComum não verifica se o usuário
    // tem eventos ativos — essa verificação existe apenas para o Organizador no OrganizadorService.
    // Para UsuarioComum, a US5 não impõe restrições, então está correto, mas
    // seria bom um comentário explicando a diferença.
    // [ITEM 1] O nome do método alterarStatus() no controller é genérico. Sugestão:
    // renomeie para alterarStatusUsuario() para alinhar com a sugestão do service.
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
    // [ITEM 9] Retornar 401 (Unauthorized) para senha incorreta é aceitável.
    // Porém, quando o usuário não é encontrado, também retorna 401 — o correto seria 404.
    // Separe os casos de erro: 404 para usuário não encontrado e 401 para senha incorreta.
    // [ITEM — US6] O fluxo de reativação está correto: recebe email e senha, valida e reativa.
    // [ITEM 4] O ReativacaoRequest contém apenas email e senha, o que está adequado para a US6.
    public ResponseEntity<String> reativar(@RequestBody ReativacaoRequest request) {
        try {
            usuarioService.reativar(request.getEmail(), request.getSenha());
            return ResponseEntity.ok("Usuário reativado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401, e.getMessage());
        }
    }
}