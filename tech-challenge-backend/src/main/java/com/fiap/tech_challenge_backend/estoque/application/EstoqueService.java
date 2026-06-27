package com.fiap.tech_challenge_backend.estoque.application;

import com.fiap.tech_challenge_backend.estoque.application.dto.EntradaEstoqueRequestDTO;
import com.fiap.tech_challenge_backend.estoque.application.dto.MovimentacaoResponseDTO;
import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoRequestDTO;
import com.fiap.tech_challenge_backend.estoque.application.dto.PecaInsumoResponseDTO;
import com.fiap.tech_challenge_backend.estoque.domain.entities.MovimentacaoEstoque;
import com.fiap.tech_challenge_backend.estoque.domain.entities.PecaInsumo;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoMovimentacao;
import com.fiap.tech_challenge_backend.estoque.domain.enums.TipoPecaInsumo;
import com.fiap.tech_challenge_backend.estoque.infrastructure.MovimentacaoRepository;
import com.fiap.tech_challenge_backend.estoque.infrastructure.PecaInsumoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

/**
 * Serviço responsável pelo gerenciamento do estoque de peças e insumos.
 * Contexto Delimitado: estoque
 * Camada: Application
 */
@Service
@Transactional
public class EstoqueService {

    private final PecaInsumoRepository pecaInsumoRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    /**
     * Dá entrada no estoque de forma unificada: se {@code request.id()} apontar para
     * uma peça/insumo existente, incrementa o estoque (reposição); caso contrário,
     * cadastra uma nova peça/insumo usando a quantidade como estoque inicial.
     * Em ambos os casos gera uma movimentação do tipo ENTRADA.
     */
    public PecaInsumoResponseDTO darEntrada(EntradaEstoqueRequestDTO request) {
        PecaInsumo peca = request.id() != null
                ? pecaInsumoRepository.findById(request.id()).orElse(null)
                : null;

        if (peca != null) {
            peca.entrada(request.quantidade());
        } else {
            if (request.nome() == null || request.nome().isBlank()) {
                throw new IllegalArgumentException("O nome é obrigatório para cadastrar uma nova peça/insumo");
            }
            if (request.precoVenda() == null || request.precoCompra() == null) {
                throw new IllegalArgumentException("Preço de venda e de compra são obrigatórios para cadastrar uma nova peça/insumo");
            }
            if (request.tipo() == null) {
                throw new IllegalArgumentException("O tipo (PECA ou INSUMO) é obrigatório para cadastrar uma nova peça/insumo");
            }
            peca = PecaInsumo.builder()
                    .nome(request.nome())
                    .descricao(request.descricao())
                    .precoVenda(request.precoVenda())
                    .precoCompra(request.precoCompra())
                    .quantidadePorUnidade(request.quantidadePorUnidade())
                    .quantidadeEstoque(request.quantidade())
                    .quantidadeMinima(request.quantidadeMinima() != null ? request.quantidadeMinima() : 0)
                    .tipo(request.tipo())
                    .build();
        }

        var salva = pecaInsumoRepository.save(peca);
        movimentacaoRepository.save(MovimentacaoEstoque.builder()
                .pecaInsumo(salva)
                .tipoMovimentacao(TipoMovimentacao.ENTRADA)
                .quantidade(request.quantidade())
                .observacao(request.observacao())
                .build());
        return PecaInsumoResponseDTO.from(salva);
    }

    public EstoqueService(PecaInsumoRepository pecaInsumoRepository,
                          MovimentacaoRepository movimentacaoRepository) {
        this.pecaInsumoRepository = pecaInsumoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

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
        int estoqueAnterior = peca.getQuantidadeEstoque();
        peca.setNome(request.nome());
        peca.setDescricao(request.descricao());
        peca.setPrecoVenda(request.precoVenda());
        peca.setPrecoCompra(request.precoCompra());
        peca.setQuantidadePorUnidade(request.quantidadePorUnidade());
        peca.setQuantidadeEstoque(request.quantidadeEstoque());
        peca.setQuantidadeMinima(request.quantidadeMinima());
        peca.setTipo(request.tipo());
        var salva = pecaInsumoRepository.save(peca);
        int diferenca = Math.abs(request.quantidadeEstoque() - estoqueAnterior);
        if (diferenca > 0) {
            movimentacaoRepository.save(MovimentacaoEstoque.builder()
                    .pecaInsumo(salva)
                    .tipoMovimentacao(TipoMovimentacao.AJUSTE)
                    .quantidade(diferenca)
                    .observacao("Ajuste via atualização cadastral")
                    .build());
        }
        return PecaInsumoResponseDTO.from(salva);
    }

    @Transactional(readOnly = true)
    public PecaInsumoResponseDTO buscarPorId(UUID id) {
        return PecaInsumoResponseDTO.from(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<PecaInsumoResponseDTO> listarTodos(TipoPecaInsumo tipo) {
        List<PecaInsumo> itens = tipo != null
                ? pecaInsumoRepository.findByTipo(tipo)
                : pecaInsumoRepository.findAll();
        return itens.stream().map(PecaInsumoResponseDTO::from).toList();
    }

    @Transactional(readOnly = true)
    public List<PecaInsumoResponseDTO> listarAbaixoDoMinimo() {
        return pecaInsumoRepository.findAbaixoDoMinimo().stream()
                .map(PecaInsumoResponseDTO::from)
                .toList();
    }

    public void remover(UUID id) {
        var peca = buscarEntidade(id);

        long referenciaCount = pecaInsumoRepository.countByPecaInUso(id);
        if (referenciaCount > 0) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Não é possível deletar a peça/insumo '" + peca.getNome() +
                "' pois está sendo utilizada em " + referenciaCount + " ordem(ns) de serviço(s)"
            );
        }

        pecaInsumoRepository.deleteById(id);
    }

    public void registrarEntrada(UUID id, Integer quantidade, String observacao) {
        var peca = buscarEntidade(id);
        peca.entrada(quantidade);
        pecaInsumoRepository.save(peca);
        var movimentacao = new MovimentacaoEstoque();
        movimentacao.setPecaInsumo(peca);
        movimentacao.setTipoMovimentacao(TipoMovimentacao.ENTRADA);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setObservacao(observacao);
        movimentacaoRepository.save(movimentacao);
    }

    public void registrarSaida(UUID id, Integer quantidade, String observacao) {
        var peca = buscarEntidade(id);
        peca.saida(quantidade);
        pecaInsumoRepository.save(peca);
        var movimentacao = new MovimentacaoEstoque();
        movimentacao.setPecaInsumo(peca);
        movimentacao.setTipoMovimentacao(TipoMovimentacao.SAIDA);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setObservacao(observacao);
        movimentacaoRepository.save(movimentacao);
    }

    public void registrarVenda(UUID id, Integer quantidade, String observacao) {
        var peca = buscarEntidade(id);
        peca.saida(quantidade);
        pecaInsumoRepository.save(peca);
        movimentacaoRepository.save(MovimentacaoEstoque.builder()
                .pecaInsumo(peca)
                .tipoMovimentacao(TipoMovimentacao.VENDA)
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

    public void cancelarReserva(UUID id, Integer quantidade, String observacao) {
        var peca = buscarEntidade(id);
        peca.entrada(quantidade);
        pecaInsumoRepository.save(peca);
        movimentacaoRepository.save(MovimentacaoEstoque.builder()
                .pecaInsumo(peca)
                .tipoMovimentacao(TipoMovimentacao.AJUSTE)
                .quantidade(quantidade)
                .observacao(observacao)
                .build());
    }

    private PecaInsumo buscarEntidade(UUID id) {
        return pecaInsumoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Peça/insumo não encontrado: " + id));
    }
}
