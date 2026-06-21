package com.fiap.tech_challenge_backend.cadastro.application.usecases;

import com.fiap.tech_challenge_backend.cadastro.application.dtos.BuscarVeiculoResponse;
import com.fiap.tech_challenge_backend.cadastro.application.ports.VeiculoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarVeiculosUseCase {

    private final VeiculoRepository veiculoRepository;

    public ListarVeiculosUseCase(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    public List<BuscarVeiculoResponse> execute() {
        return veiculoRepository.listar().stream()
                .map(v -> new BuscarVeiculoResponse(v.getId(), v.getPlaca().valor(), v.getModelo()))
                .toList();
    }
}
