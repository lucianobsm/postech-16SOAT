package com.fiap.tech_challenge_backend.cadastro.application.usecases;

import com.fiap.tech_challenge_backend.cadastro.application.dtos.BuscarVeiculoResponse;
import com.fiap.tech_challenge_backend.cadastro.application.exceptions.VeiculoNaoEncontradoException;
import com.fiap.tech_challenge_backend.cadastro.application.ports.VeiculoRepository;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import org.springframework.stereotype.Service;

@Service
public class BuscarVeiculoUseCase {

    private final VeiculoRepository veiculoRepository;

    public BuscarVeiculoUseCase(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    public BuscarVeiculoResponse execute(String placa) {
        return veiculoRepository.buscarPorPlaca(new Placa(placa))
                .map(v -> new BuscarVeiculoResponse(v.getId(), v.getPlaca().valor(), v.getModelo()))
                .orElseThrow(() -> new VeiculoNaoEncontradoException(placa));
    }
}
