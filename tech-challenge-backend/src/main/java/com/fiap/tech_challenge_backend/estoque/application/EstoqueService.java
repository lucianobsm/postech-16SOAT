package com.fiap.tech_challenge_backend.estoque.application;

import com.fiap.tech_challenge_backend.estoque.application.dto.MovimentacaoResponseDTO;
import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoRequestDTO;
import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoResponseDTO;
import com.fiap.tech_challenge_backend.estoque.domain.entities.MovimentacaoEstoque;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoMovimentacao;
import com.fiap.tech_challenge_backend.estoque.infrastructure.MovimentacaoRepository;
import com.fiap.tech_challenge_backend.estoque.infrastructure.PecaInsumoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Serviço responsável pelo gerenciamento do estoque de peças e insumos.
 * Contexto Delimitado: estoque
 * Camada: Application
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EstoqueService {

    private final PecaInsumoRepository pecaInsumoRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public PecaInsumoResponseDTO cadastrar(PecaInsumoRequestDTO request) {
        var peca = PecaInsumo.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .precoVenda(request.precoVenda())
                .precoCompra(request.precoCompra())
                .quantidadePorUnidade(request.quantidadePorUnidade())
                .quantidadeEstoque(request.quantidadeEstoque())
                .quantidadeMinima(request.quantidadeMinima())
                .build();
        return PecaInsumoResponseDTO.from(pecaInsumoRepository.save(peca));
    }

    public PecaInsumoResponseDTO atualizar(UUID id, PecaInsumoRequestDTO request) {
        var peca = buscarEntidade(id);
        peca.setNome(request.nome());
        peca.setDescricao(request.descricao());
        peca.setPrecoVenda(request.precoVenda());
        peca.setPrecoCompra(request.precoCompra());
        peca.setQuantidadePorUnidade(request.quantidadePorUnidade());
        peca.setQuantidadeMinima(request.quantidadeMinima());
        return PecaInsumoResponseDTO.from(pecaInsumoRepository.save(peca));
    }

    @Transactional(readOnly = true)
    public PecaInsumoResponseDTO buscarPorId(UUID id) {
        return PecaInsumoResponseDTO.from(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<PecaInsumoResponseDTO> listarTodos() {
        return pecaInsumoRepository.findAll().stream()
                .map(PecaInsumoResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PecaInsumoResponseDTO> listarAbaixoDoMinimo() {
        return pecaInsumoRepository.findAbaixoDoMinimo().stream()
                .map(PecaInsumoResponseDTO::from)
                .toList();
    }

    public void remover(UUID id) {
        buscarEntidade(id);
        pecaInsumoRepository.deleteById(id);
    }

    public void registrarEntrada(UUID id, Integer quantidade, String observacao) {
        var peca = buscarEntidade(id);
        peca.entrada(quantidade);
        pecaInsumoRepository.save(peca);
        movimentacaoRepository.save(MovimentacaoEstoque.builder()
                .pecaInsumo(peca)
                .tipoMovimentacao(TipoMovimentacao.ENTRADA)
                .quantidade(quantidade)
                .observacao(observacao)
                .build());
    }

    public void registrarSaida(UUID id, Integer quantidade, String observacao) {
        var peca = buscarEntidade(id);
        peca.saida(quantidade);
        pecaInsumoRepository.save(peca);
        movimentacaoRepository.save(MovimentacaoEstoque.builder()
                .pecaInsumo(peca)
                .tipoMovimentacao(TipoMovimentacao.SAIDA)
                .quantidade(quantidade)
                .observacao(observacao)
                .build());
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoResponseDTO> listarMovimentacoes(UUID pecaInsumoId) {
        var peca = buscarEntidade(pecaInsumoId);
        return movimentacaoRepository.findByPecaInsumoOrderByCriadoEmDesc(peca).stream()
                .map(MovimentacaoResponseDTO::from)
                .toList();
    }

    private PecaInsumo buscarEntidade(UUID id) {
        return pecaInsumoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Peça/insumo não encontrado: " + id));
    }
}
