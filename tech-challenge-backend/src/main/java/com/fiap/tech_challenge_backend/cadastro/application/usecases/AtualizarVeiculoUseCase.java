package com.fiap.tech_challenge_backend.cadastro.application.usecases;

import com.fiap.tech_challenge_backend.cadastro.application.dtos.AtualizarVeiculoRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dtos.BuscarVeiculoResponse;
import com.fiap.tech_challenge_backend.cadastro.application.exceptions.VeiculoNaoEncontradoException;
import com.fiap.tech_challenge_backend.cadastro.application.ports.VeiculoRepository;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AtualizarVeiculoUseCase {

    private final VeiculoRepository veiculoRepository;

    public AtualizarVeiculoUseCase(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    @Transactional
    public BuscarVeiculoResponse execute(String placa, AtualizarVeiculoRequest request) {
        Veiculo veiculo = veiculoRepository.buscarPorPlaca(new Placa(placa))
                .orElseThrow(() -> new VeiculoNaoEncontradoException(placa));

        veiculo.setModelo(request.modelo());

        Veiculo veiculoAtualizado = veiculoRepository.salvar(veiculo);

        return new BuscarVeiculoResponse(
                veiculoAtualizado.getId(),
                veiculoAtualizado.getPlaca().valor(),
                veiculoAtualizado.getMarca(),
                veiculoAtualizado.getModelo(),
                veiculoAtualizado.getAno(),
                veiculoAtualizado.getCor()
        );
    }
}
