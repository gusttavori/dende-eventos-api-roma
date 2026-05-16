package br.com.softhouse.dende.services;

import br.com.dende.softhouse.annotations.Component;
import br.com.softhouse.dende.dto.OrganizadorDTO;
import br.com.softhouse.dende.exceptions.EntidadeNaoEncontradaException;
import br.com.softhouse.dende.exceptions.RegraDeNegocioException;
import br.com.softhouse.dende.mappers.OrganizadorMapper;
import br.com.softhouse.dende.model.Organizador;
import br.com.softhouse.dende.repositories.EventoRepository;
import br.com.softhouse.dende.repositories.OrganizadorRepository;

@Component
public class OrganizadorService {

    private final OrganizadorRepository organizadorRepository;
    private final EventoRepository eventoRepository;

    public OrganizadorService(OrganizadorRepository organizadorRepository, EventoRepository eventoRepository) {
        this.organizadorRepository = organizadorRepository;
        this.eventoRepository = eventoRepository;
    }

    public OrganizadorDTO cadastrar(OrganizadorDTO dto) {
        // Validações de campos obrigatórios
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

        // Verificar se email já existe
        if (organizadorRepository.emailExiste(dto.getEmail())) {
            throw new RegraDeNegocioException("Email já está em uso");
        }

        // Se informou CNPJ, verificar se já existe
        if (dto.getCnpj() != null && !dto.getCnpj().isEmpty()) {
            if (organizadorRepository.cnpjExiste(dto.getCnpj())) {
                throw new RegraDeNegocioException("CNPJ já está em uso");
            }
        }

        Organizador organizador = OrganizadorMapper.toEntity(dto);
        organizador = organizadorRepository.salvar(organizador);
        return OrganizadorMapper.toDTO(organizador);
    }

    public OrganizadorDTO buscarPorId(Long id) {
        Organizador organizador = organizadorRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Organizador não encontrado"));
        return OrganizadorMapper.toDTO(organizador);
    }

    public Organizador buscarEntidadePorId(Long id) {
        return organizadorRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Organizador não encontrado"));
    }

    public Organizador buscarEntidadePorEmail(String email) {
        return organizadorRepository.buscarPorEmail(email)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Organizador não encontrado"));
    }

    public OrganizadorDTO atualizar(Long id, OrganizadorDTO dto) {
        if (id == null) {
            throw new RegraDeNegocioException("ID do organizador é obrigatório");
        }

        Organizador existente = buscarEntidadePorId(id);

        if (dto.getEmail() != null && !dto.getEmail().equals(existente.getEmail())) {
            throw new RegraDeNegocioException("Não é permitido alterar o email");
        }

        if (dto.getCnpj() != null && !dto.getCnpj().equals(existente.getCnpj())) {
            if (organizadorRepository.cnpjExiste(dto.getCnpj())) {
                throw new RegraDeNegocioException("CNPJ já está em uso");
            }
        }

        Organizador organizadorAtualizado = OrganizadorMapper.updateEntity(existente, dto);
        organizadorRepository.atualizar(organizadorAtualizado);
        return OrganizadorMapper.toDTO(organizadorAtualizado);
    }

    public OrganizadorDTO ativarComSenha(Long id, String senha) {
        Organizador organizador = buscarEntidadePorId(id);

        if (!organizador.getSenha().equals(senha)) {
            throw new RegraDeNegocioException("Senha incorreta");
        }

        if (organizador.getAtivo()) {
            throw new RegraDeNegocioException("Organizador já está ativo");
        }

        organizador.setAtivo(true);
        organizadorRepository.atualizar(organizador);
        return OrganizadorMapper.toDTO(organizador);
    }

    public OrganizadorDTO desativarComSenha(Long id, String senha) {
        Organizador organizador = buscarEntidadePorId(id);

        if (!organizador.getSenha().equals(senha)) {
            throw new RegraDeNegocioException("Senha incorreta");
        }

        if (!organizador.getAtivo()) {
            throw new RegraDeNegocioException("Organizador já está inativo");
        }

        if (eventoRepository.organizadorTemEventosAtivosOuEmExecucao(id)) {
            throw new RegraDeNegocioException("Não é possível desativar: organizador possui eventos ativos ou em execução");
        }

        organizador.setAtivo(false);
        organizadorRepository.atualizar(organizador);
        return OrganizadorMapper.toDTO(organizador);
    }

    public boolean emailExiste(String email) {
        return organizadorRepository.emailExiste(email);
    }

    public boolean cnpjExiste(String cnpj) {
        return organizadorRepository.cnpjExiste(cnpj);
    }
}