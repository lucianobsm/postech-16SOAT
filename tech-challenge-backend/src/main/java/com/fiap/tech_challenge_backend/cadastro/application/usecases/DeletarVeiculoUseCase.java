package com.fiap.tech_challenge_backend.cadastro.application.usecases;

import com.fiap.tech_challenge_backend.cadastro.application.exceptions.VeiculoNaoEncontradoException;
import com.fiap.tech_challenge_backend.cadastro.application.ports.ClienteVeiculoRepository;
import com.fiap.tech_challenge_backend.cadastro.application.ports.VeiculoRepository;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DeletarVeiculoUseCase {

    private final VeiculoRepository veiculoRepository;
    private final ClienteVeiculoRepository clienteVeiculoRepository;

    public DeletarVeiculoUseCase(
            VeiculoRepository veiculoRepository,
            ClienteVeiculoRepository clienteVeiculoRepository
    ) {
        this.veiculoRepository = veiculoRepository;
        this.clienteVeiculoRepository = clienteVeiculoRepository;
    }

    @Transactional
    public void execute(String placa) {
        Veiculo veiculo = veiculoRepository.buscarPorPlaca(new Placa(placa))
                .orElseThrow(() -> new VeiculoNaoEncontradoException(placa));

        clienteVeiculoRepository.deletarPorVeiculoId(veiculo.getId());
        veiculoRepository.deletar(veiculo.getId());
    }
}
