package br.com.softhouse.dende.service;

import br.com.softhouse.dende.dto.request.UsuarioRequestDTO;
import br.com.softhouse.dende.exceptions.BusinessRuleException;
import br.com.softhouse.dende.exceptions.EntityNotFoundException;
import br.com.softhouse.dende.mappers.UsuarioMapper;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.UsuarioComum;
import br.com.softhouse.dende.repositories.UsuarioRepository;

import java.time.LocalDate;
import java.util.List;

public class UsuarioService {

    private final UsuarioRepository usuarioRepository = new UsuarioRepository();

    public UsuarioComum cadastrarUsuarioComum(
            String nome,
            LocalDate dataNascimento,
            String sexo,
            String email,
            String senha
    ) {
        // Verifica se já existe um usuário com o email
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new BusinessRuleException("E-mail já cadastrado");
        }

        // Cria o usuário (ID será gerado automaticamente)
        UsuarioComum usuario = new UsuarioComum(
                null,
                nome,
                dataNascimento,
                sexo,
                email,
                senha
        );

        return (UsuarioComum) usuarioRepository.save(usuario);
    }

    public Usuario buscarPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário", id));
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    public void atualizarUsuario(String email, UsuarioRequestDTO dados) {
        Usuario usuario = buscarPorEmail(email);

        if (!(usuario instanceof UsuarioComum)) {
            throw new BusinessRuleException("Usuário não é do tipo comum");
        }

        UsuarioMapper.updateEntityFromDTO(dados, usuario);
        usuarioRepository.update(usuario);
    }

    public void alterarStatus(Integer id, String status) {
        Usuario usuario = buscarPorId(id);

        if ("ativar".equalsIgnoreCase(status)) {
            usuario.ativar();
        } else if ("desativar".equalsIgnoreCase(status)) {
            usuario.desativar();
        } else {
            throw new BusinessRuleException("Status inválido. Use 'ativar' ou 'desativar'");
        }

        usuarioRepository.update(usuario);
    }

    public void reativar(String email, String senha) {
        Usuario usuario = buscarPorEmail(email);

        if (usuario.isAtivo()) {
            throw new BusinessRuleException("Usuário já está ativo");
        }

        boolean resultado = usuario.reativar(senha);

        if (!resultado) {
            throw new BusinessRuleException("Senha incorreta");
        }

        usuarioRepository.update(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }
}