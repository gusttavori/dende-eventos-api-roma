package br.com.softhouse.dende.service;

import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.UsuarioComum;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDate;
import java.util.List;

public class UsuarioService {

    private final Repositorio repositorio = Repositorio.getInstance();

    // Cadastra um novo usuário comum
    public UsuarioComum cadastrarUsuarioComum(
            String nome,
            LocalDate dataNascimento,
            String sexo,
            String email,
            String senha
    ) {
        // [ITEM 3] A verificação "!= null" pode ser substituída por Objects.nonNull():
        // if (Objects.nonNull(repositorio.buscarUsuarioPorEmail(email))) { ... }
        // Ou ainda usando o Optional retornado se o repositório for refatorado:
        // repositorio.buscarUsuarioPorEmail(email).ifPresent(u -> { throw new ...; });
        if (repositorio.buscarUsuarioPorEmail(email) != null) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }

        // [ITEM 6] A geração do ID e a atribuição ao usuário estão sendo feitas aqui no Service.
        // O ideal seria o Repositório ser responsável por gerar e atribuir IDs ao persistir,
        // mantendo essa responsabilidade na camada de persistência.
        UsuarioComum usuario = new UsuarioComum(
                repositorio.gerarId(),
                nome,
                dataNascimento,
                sexo,
                email,
                senha
        );

        repositorio.salvarUsuario(usuario);
        return usuario;
    }

    // Busca um usuário pelo ID
    // [ITEM 8] O método retorna null (via exceção) quando não encontrado, mas poderia retornar
    // Optional<Usuario> para tornar a ausência explícita no contrato do método.
    // Sugestão: public Optional<Usuario> buscarPorId(Integer id)
    public Usuario buscarPorId(Integer id) {
        Usuario usuario = repositorio.buscarUsuarioPorId(id);
        // [ITEM 3] A verificação "== null" pode ser substituída por:
        // if (Objects.isNull(usuario)) { throw new IllegalArgumentException("Usuário não encontrado"); }
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        return usuario;
    }

    // Busca um usuário pelo email
    // [ITEM 8] Mesmo comentário de buscarPorId: prefira Optional<Usuario>.
    public Usuario buscarPorEmail(String email) {
        Usuario usuario = repositorio.buscarUsuarioPorEmail(email);
        // [ITEM 3] Substitua "== null" por Objects.isNull(usuario)
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        return usuario;
    }

    // Atualiza dados de um usuário (versão com ID)
    // [ITEM 4] O parâmetro "dados" é um objeto Usuario completo sendo trafegado apenas para
    // atualizar nome, dataNascimento e sexo. Isso expõe campos sensíveis como senha e email
    // no payload de entrada. Considere criar um DTO de atualização (AtualizarUsuarioRequest)
    // com apenas os campos que podem ser modificados (nome, dataNascimento, sexo, senha).
    // [ITEM 5] Um Mapper (ex: UsuarioMapper) seria uma boa prática para converter entre
    // a entidade Usuario e os DTOs de entrada/saída. Não era obrigatório nesta avaliação,
    // mas seria o ideal para separar as responsabilidades.
    public void atualizarUsuario(Integer id, Usuario dados) {
        Usuario usuario = buscarPorId(id);
        usuario.alterarPerfil(
                dados.getNome(),
                dados.getDataNascimento(),
                dados.getSexo()
        );

        // [ITEM 3] A verificação "!= null" pode ser substituída por Objects.nonNull(dados.getSenha())
        if (dados.getSenha() != null && !dados.getSenha().isEmpty()) {
            usuario.setSenha(dados.getSenha());
        }
    }

    // Altera o status de um usuário (ativar/desativar)
    // [ITEM 1] O nome "alterarStatus" não deixa claro que este método serve tanto para ativar quanto para desativar.
    // Sugestão: separe em dois métodos específicos — ativarUsuario(Integer id) e desativarUsuario(Integer id) —
    // ou renomeie para alterarStatusUsuario(Integer id, String status) para ficar mais descritivo.
    // [ITEM 9] Este método pode lançar IllegalArgumentException tanto para "usuário não encontrado"
    // quanto para "status inválido". No controller, ambos retornam 404, mas o status inválido deveria
    // retornar 400 (Bad Request).
    public void alterarStatus(Integer id, String status) {
        Usuario usuario = buscarPorId(id);

        if ("ativar".equalsIgnoreCase(status)) {
            usuario.ativar();
        } else if ("desativar".equalsIgnoreCase(status)) {
            usuario.desativar();
        } else {
            throw new IllegalArgumentException("Status inválido. Use 'ativar' ou 'desativar'");
        }
    }

    // Reativa um usuário após validação de senha
    // [ITEM 7] Este método lança IllegalArgumentException quando o usuário já está ativo.
    // Isso fere a idempotência: chamar reativar() para um usuário já ativo é uma operação
    // que não deveria resultar em erro — o estado desejado (ativo) já está atingido.
    // Sugestão: substitua a exceção por um retorno silencioso ou uma mensagem informativa.
    public void reativar(String email, String senha) {
        Usuario usuario = buscarPorEmail(email);

        // [ITEM 7] Lançar exceção aqui fere a idempotência do endpoint de reativação.
        if (usuario.isAtivo()) {
            System.out.println("8. ERRO: Usuário já está ativo!");
            throw new IllegalArgumentException("Usuário já está ativo");
        }

        boolean resultado = usuario.reativar(senha);

        if (!resultado) {
            throw new IllegalArgumentException("Senha incorreta");
        }
    }

    // Lista todos os usuários
    public List<Usuario> listarUsuarios() {
        return repositorio.listarUsuarios();
    }

    // Atualiza dados de um usuário (versão com email)
    // [ITEM 1] Existem dois métodos chamados atualizarUsuario() com assinaturas diferentes.
    // Isso causa confusão — o método por ID recebe Usuario, o por email recebe UsuarioComum.
    // Sugestão: renomeie para atualizarUsuarioComumPorEmail(String email, UsuarioComum dados)
    // para deixar claro o propósito e evitar sobrecarga confusa.
    // [ITEM 4] Mesmo comentário do método acima: use um DTO de atualização.
    public void atualizarUsuario(String email, UsuarioComum dados) {
        Usuario usuario = buscarPorEmail(email);

        if (!(usuario instanceof UsuarioComum)) {
            throw new IllegalArgumentException("Usuário não é do tipo comum");
        }

        usuario.alterarPerfil(dados.getNome(), dados.getDataNascimento(), dados.getSexo());

        // [ITEM 3] A verificação "!= null" pode ser substituída por Objects.nonNull(dados.getSenha())
        if (dados.getSenha() != null && !dados.getSenha().isEmpty()) {
            usuario.setSenha(dados.getSenha());
        }
    }
}