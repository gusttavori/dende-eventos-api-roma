package br.com.softhouse.dende.services;

import br.com.dende.softhouse.annotations.Component;
import br.com.softhouse.dende.dto.CancelamentoResponseDTO;
import br.com.softhouse.dende.dto.CompraRequestDTO;
import br.com.softhouse.dende.dto.CompraResponseDTO;
import br.com.softhouse.dende.dto.IngressoDTO;
import br.com.softhouse.dende.exceptions.EntidadeNaoEncontradaException;
import br.com.softhouse.dende.exceptions.RegraDeNegocioException;
import br.com.softhouse.dende.mappers.IngressoMapper;
import br.com.softhouse.dende.model.Evento;
import br.com.softhouse.dende.model.Ingresso;
import br.com.softhouse.dende.model.Usuario;
import br.com.softhouse.dende.model.enums.StatusIngresso;
import br.com.softhouse.dende.repositories.EventoRepository;
import br.com.softhouse.dende.repositories.IngressoRepository;
import br.com.softhouse.dende.repositories.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class IngressoService {

    private final IngressoRepository ingressoRepository;
    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;

    public IngressoService(IngressoRepository ingressoRepository,
                           EventoRepository eventoRepository,
                           UsuarioRepository usuarioRepository) {
        this.ingressoRepository = ingressoRepository;
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public CompraResponseDTO comprar(Long organizadorId, Long eventoId, CompraRequestDTO request) {
        Usuario usuario = usuarioRepository.buscarPorEmail(request.getUsuarioEmail())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));

        if (!usuario.getAtivo()) {
            throw new RegraDeNegocioException("Usuário inativo não pode comprar ingressos");
        }

        Evento evento = eventoRepository.buscarPorId(eventoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Evento não encontrado"));

        if (!evento.getOrganizadorId().equals(organizadorId)) {
            throw new RegraDeNegocioException("Evento não pertence a este organizador");
        }

        if (!evento.getAtivo()) {
            throw new RegraDeNegocioException("Evento inativo não está vendendo ingressos");
        }

        if (evento.eventoJaAconteceu()) {
            throw new RegraDeNegocioException("Evento já aconteceu");
        }

        if (!evento.temIngressosDisponiveis()) {
            throw new RegraDeNegocioException("Ingressos esgotados");
        }

        if (ingressoRepository.existeIngressoAtivo(usuario.getId(), eventoId)) {
            throw new RegraDeNegocioException("Você já possui um ingresso ativo para este evento");
        }

        double valorEsperado = evento.getPrecoIngresso();
        Evento principal = null;

        if (evento.getEventoPrincipalId() != null) {
            principal = eventoRepository.buscarPorId(evento.getEventoPrincipalId()).orElse(null);
            if (principal != null && principal.getAtivo()) {
                if (!principal.temIngressosDisponiveis()) {
                    throw new RegraDeNegocioException("Evento principal não tem ingressos disponíveis");
                }
                valorEsperado += principal.getPrecoIngresso();
            }
        }

        if (request.getTotalPago() != null && Math.abs(request.getTotalPago() - valorEsperado) > 0.01) {
            throw new RegraDeNegocioException(
                    String.format("Valor pago (R$ %.2f) não corresponde ao preço do ingresso (R$ %.2f)",
                            request.getTotalPago(), valorEsperado)
            );
        }

        double valorTotal = evento.getPrecoIngresso();
        List<String> codigos = new ArrayList<>();

        Ingresso ingressoPrincipal = IngressoMapper.createIngresso(usuario.getId(), eventoId, evento.getPrecoIngresso());
        ingressoRepository.salvar(ingressoPrincipal);
        evento.venderIngresso();
        eventoRepository.atualizar(evento);
        codigos.add(ingressoPrincipal.getCodigo());

        if (principal != null && principal.getAtivo() && principal.temIngressosDisponiveis()) {
            Ingresso ingressoVinculado = IngressoMapper.createIngressoVinculado(
                    usuario.getId(), principal.getId(), eventoId, principal.getPrecoIngresso());
            ingressoRepository.salvar(ingressoVinculado);
            principal.venderIngresso();
            eventoRepository.atualizar(principal);
            valorTotal += principal.getPrecoIngresso();
            codigos.add(ingressoVinculado.getCodigo());
        }

        String mensagem = codigos.size() > 1
                ? "Compra realizada com sucesso! Ingressos gerados: " + String.join(", ", codigos)
                : "Compra realizada com sucesso! Ingresso gerado: " + codigos.get(0);

        return new CompraResponseDTO(mensagem, codigos, valorTotal, "AGUARDANDO_PAGAMENTO");
    }

    public CancelamentoResponseDTO cancelar(Long usuarioId, Long ingressoId) {
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));

        Ingresso ingresso = ingressoRepository.buscarPorId(ingressoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Ingresso não encontrado"));

        if (!ingresso.getUsuarioId().equals(usuarioId)) {
            throw new RegraDeNegocioException("Ingresso não pertence a este usuário");
        }

        if (!ingresso.podeSerCancelado()) {
            throw new RegraDeNegocioException("Este ingresso não pode ser cancelado");
        }

        Evento evento = eventoRepository.buscarPorId(ingresso.getEventoId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Evento associado ao ingresso não encontrado"));

        if (evento.eventoJaAconteceu()) {
            throw new RegraDeNegocioException("Não é possível cancelar ingresso de evento já realizado");
        }

        double valorReembolso = evento.calcularReembolso(ingresso.getValorPago());

        ingresso.cancelar();
        ingressoRepository.atualizar(ingresso);

        evento.cancelarIngresso();
        eventoRepository.atualizar(evento);

        String mensagem;
        if (evento.getEstornaCancelamento()) {
            mensagem = String.format(
                    "Ingresso cancelado. Código: %s, Valor pago: R$ %.2f, Reembolso: R$ %.2f (taxa de %.1f%%)",
                    ingresso.getCodigo(),
                    ingresso.getValorPago(),
                    valorReembolso,
                    evento.getTaxaEstorno()
            );
        } else {
            mensagem = String.format(
                    "Ingresso cancelado. Código: %s, Valor pago: R$ %.2f, Este evento não oferece reembolso.",
                    ingresso.getCodigo(),
                    ingresso.getValorPago()
            );
        }

        return new CancelamentoResponseDTO(
                mensagem,
                ingresso.getValorPago(),
                valorReembolso,
                ingresso.getCodigo()
        );
    }

    public double calcularReembolso(Long ingressoId) {
        Ingresso ingresso = ingressoRepository.buscarPorId(ingressoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Ingresso não encontrado"));

        Evento evento = eventoRepository.buscarPorId(ingresso.getEventoId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Evento não encontrado"));

        return evento.calcularReembolso(ingresso.getValorPago());
    }

    public List<IngressoDTO> listarPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Usuário não encontrado"));

        List<Ingresso> ingressos = ingressoRepository.buscarPorUsuarioId(usuarioId);

        ingressos.sort((i1, i2) -> {
            Evento e1 = eventoRepository.buscarPorId(i1.getEventoId()).orElse(null);
            Evento e2 = eventoRepository.buscarPorId(i2.getEventoId()).orElse(null);

            if (e1 == null || e2 == null) return 0;

            boolean i1Ativo = i1.getStatus() == StatusIngresso.ATIVO && !e1.eventoJaAconteceu();
            boolean i2Ativo = i2.getStatus() == StatusIngresso.ATIVO && !e2.eventoJaAconteceu();

            if (i1Ativo && !i2Ativo) return -1;
            if (!i1Ativo && i2Ativo) return 1;

            return e1.getDataInicio().compareTo(e2.getDataInicio());
        });

        return ingressos.stream()
                .map(i -> {
                    Evento e = eventoRepository.buscarPorId(i.getEventoId()).orElse(null);
                    return IngressoMapper.toDTO(i, e);
                })
                .collect(Collectors.toList());
    }
}