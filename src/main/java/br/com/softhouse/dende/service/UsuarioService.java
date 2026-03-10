package br.com.softhouse.dende.service;

import br.com.softhouse.dende.dto.request.UsuarioRequestDTO;
import br.com.softhouse.dende.mappers.UsuarioMapper;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.UsuarioComum;
import br.com.softhouse.dende.repositories.Repositorio;

import java.time.LocalDate;
import java.util.List;

public class UsuarioService {

    // obtém a instância única do repositório (singleton)
    private final Repositorio repositorio = Repositorio.getInstance();

    // metodo para cadastrar um novo usuário comum
    public UsuarioComum cadastrarUsuarioComum(
            String nome,
            LocalDate dataNascimento,
            String sexo,
            String email,
            String senha
    ) {
        // verifica se já existe um usuário com o email informado
        if (repositorio.buscarUsuarioPorEmail(email) != null) {
            // lança exceção se o email já estiver cadastrado
            throw new IllegalArgumentException("E-mail já cadastrado");
        }

        // cria um novo objeto UsuarioComum com os dados fornecidos
        UsuarioComum usuario = new UsuarioComum(
                repositorio.gerarId(),  // gera um ID automático para o usuário
                nome,
                dataNascimento,
                sexo,
                email,
                senha
        );

        // salva o usuário no repositório
        repositorio.salvarUsuario(usuario);
        // retorna o usuário cadastrado
        return usuario;
    }

    // metodo para buscar um usuário pelo ID
    public Usuario buscarPorId(Integer id) {
        // tenta encontrar o usuário no repositório pelo ID
        Usuario usuario = repositorio.buscarUsuarioPorId(id);
        // verifica se o usuário não foi encontrado
        if (usuario == null) {
            // lança exceção se o usuário não existir
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        // retorna o usuário encontrado
        return usuario;
    }

    // metodo para buscar um usuário pelo email
    public Usuario buscarPorEmail(String email) {
        // tenta encontrar o usuário no repositório pelo email
        Usuario usuario = repositorio.buscarUsuarioPorEmail(email);
        // verifica se o usuário não foi encontrado
        if (usuario == null) {
            // lança exceção se o usuário não existir
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        // retorna o usuário encontrado
        return usuario;
    }

    // metodo para atualizar os dados de um usuário
    public void atualizarUsuario(String email, UsuarioRequestDTO dados) {
        // busca o usuário pelo email
        Usuario usuario = buscarPorEmail(email);

        // verifica se o usuário é do tipo UsuarioComum
        if (!(usuario instanceof UsuarioComum)) {
            // lança exceção se não for um usuário comum
            throw new IllegalArgumentException("Usuário não é do tipo comum");
        }

        // atualiza os dados do usuário usando o mapper
        UsuarioMapper.updateEntityFromDTO(dados, usuario);
    }

    // metodo para ativar ou desativar um usuário
    public void alterarStatus(Integer id, String status) {
        // busca o usuário pelo ID
        Usuario usuario = buscarPorId(id);

        // verifica se o status é "ativar" (ignorando maiúsculas/minúsculas)
        if ("ativar".equalsIgnoreCase(status)) {
            // ativa o usuário
            usuario.ativar();
            // verifica se o status é "desativar" (ignorando maiúsculas/minúsculas)
        } else if ("desativar".equalsIgnoreCase(status)) {
            // desativa o usuário
            usuario.desativar();
        } else {
            // lança exceção se o status não for válido
            throw new IllegalArgumentException("Status inválido. Use 'ativar' ou 'desativar'");
        }
    }

    // metodo para reativar um usuário inativo
    public void reativar(String email, String senha) {
        // busca o usuário pelo email
        Usuario usuario = buscarPorEmail(email);

        // verifica se o usuário já está ativo
        if (usuario.isAtivo()) {
            // lança exceção se já estiver ativo
            throw new IllegalArgumentException("Usuário já está ativo");
        }

        // tenta reativar o usuário com a senha fornecida
        boolean resultado = usuario.reativar(senha);

        // verifica se a reativação falhou (senha incorreta)
        if (!resultado) {
            // lança exceção se a senha estiver incorreta
            throw new IllegalArgumentException("Senha incorreta");
        }
    }

    // metodo para listar todos os usuários
    public List<Usuario> listarUsuarios() {
        // retorna a lista de usuários do repositório
        return repositorio.listarUsuarios();
    }
}