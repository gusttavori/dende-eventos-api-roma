package br.com.softhouse.dende.services;

import br.com.dende.softhouse.annotations.Component;
import br.com.softhouse.dende.dto.UsuarioDTO;
import br.com.softhouse.dende.exceptions.EntidadeNaoEncontradaException;
import br.com.softhouse.dende.exceptions.RegraDeNegocioException;
import br.com.softhouse.dende.mappers.UsuarioMapper;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.repositories.UsuarioRepository;

/**
 * SERVICE DE USUÁRIOS
 *
 * Esta classe é responsável por implementar TODAS as regras de negócio relacionadas a usuários.
 * Ela atua como uma camada intermediária entre o Controller (camada de apresentação) e o
 * Repositório (camada de dados).
 *
 * Princípios aplicados:
 * - Single Responsibility: Cada metodo tem uma responsabilidade única
 * - Validações: Todas as regras de negócio são validadas aqui
 * - Tratamento de exceções: Lança exceções com mensagens claras para o controller
 */
@Component
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // Injeção de dependência via construtor
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Metodo para cadastrar um novo usuário
    public UsuarioDTO cadastrar(UsuarioDTO dto) {
        // Validar campos obrigatórios
        if (dto.getNome() == null || dto.getNome().trim().isEmpty()) {
            throw new RegraDeNegocioException("Nome é obrigatório");
        }
        if (dto.getDataNascimento() == null) {
            throw new RegraDeNegocioException("Data de nascimento é obrigatória");
        }
        if (dto.getSexo() == null) {
            throw new RegraDeNegocioException("Sexo é obrigatório");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new RegraDeNegocioException("Email é obrigatório");
        }
        if (dto.getSenha() == null || dto.getSenha().trim().isEmpty()) {
            throw new RegraDeNegocioException("Senha é obrigatória");
        }

        // Verificar se email já existe (US1 - emails únicos)
        if (usuarioRepository.emailExiste(dto.getEmail())) {
            throw new RegraDeNegocioException("Email já está em uso");
        }

        // Converter DTO para entidade
        Usuario usuario = UsuarioMapper.toEntity(dto);

        // Salvar no repositório
        usuario = usuarioRepository.salvar(usuario);

        // Retornar DTO de resposta
        return UsuarioMapper.toDTO(usuario);
    }

    public UsuarioDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));
        return UsuarioMapper.toDTO(usuario);
    }

    public Usuario buscarEntidadePorId(Long id) {
        return usuarioRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));
    }

    public Usuario buscarEntidadePorEmail(String email) {
        return usuarioRepository.buscarPorEmail(email)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));
    }

    // Metodo para atualizar um usuário existente
    public UsuarioDTO atualizar(Long id, UsuarioDTO dto) {
        if (id == null) {
            throw new RegraDeNegocioException("ID do usuário é obrigatório");
        }

        // Buscar usuário existente
        Usuario existente = buscarEntidadePorId(id);

        // Validar se email foi alterado (US3 - não permitido)
        if (dto.getEmail() != null && !dto.getEmail().equals(existente.getEmail())) {
            throw new RegraDeNegocioException("Não é permitido alterar o email");
        }

        // Atualizar campos (o mapper já valida o email)
        Usuario usuarioAtualizado = UsuarioMapper.updateEntity(existente, dto);

        // Salvar alterações
        usuarioRepository.atualizar(usuarioAtualizado);

        // Retornar DTO de resposta
        return UsuarioMapper.toDTO(usuarioAtualizado);
    }

    public UsuarioDTO ativarComSenha(Long id, String senha) {
        Usuario usuario = buscarEntidadePorId(id);

        if (!usuario.getSenha().equals(senha)) {
            throw new RegraDeNegocioException("Senha incorreta");
        }

        if (usuario.getAtivo()) {
            throw new RegraDeNegocioException("Usuário já está ativo");
        }

        usuario.setAtivo(true);
        usuarioRepository.atualizar(usuario);

        return UsuarioMapper.toDTO(usuario);
    }

    public UsuarioDTO desativarComSenha(Long id, String senha) {
        Usuario usuario = buscarEntidadePorId(id);

        // Validar senha (segurança)
        if (!usuario.getSenha().equals(senha)) {
            throw new RegraDeNegocioException("Senha incorreta");
        }

        if (!usuario.getAtivo()) {
            throw new RegraDeNegocioException("Usuário já está inativo");
        }

        usuario.setAtivo(false);
        usuarioRepository.atualizar(usuario);

        return UsuarioMapper.toDTO(usuario);
    }

    public boolean emailExiste(String email) {
        return usuarioRepository.emailExiste(email);
    }
}