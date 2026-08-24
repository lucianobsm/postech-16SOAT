package com.fiap.tech_challenge_backend.cadastro.application.usecases;

import com.fiap.tech_challenge_backend.cadastro.application.dtos.BuscarVeiculoResponse;
import com.fiap.tech_challenge_backend.cadastro.application.ports.VeiculoRepository;
import com.fiap.tech_challenge_backend.cadastro.application.ports.in.ListarVeiculosInputPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarVeiculosUseCase implements ListarVeiculosInputPort {

    private final VeiculoRepository veiculoRepository;

    public ListarVeiculosUseCase(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    @Override
    public List<BuscarVeiculoResponse> execute() {
        return veiculoRepository.listar().stream()
                .map(v -> new BuscarVeiculoResponse(
                        v.getId(),
                        v.getPlaca().valor(),
                        v.getMarca(),
                        v.getModelo(),
                        v.getAno(),
                        v.getCor()))
                .toList();
    }
}
