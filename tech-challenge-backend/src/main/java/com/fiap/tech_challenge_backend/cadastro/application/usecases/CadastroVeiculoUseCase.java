package com.fiap.tech_challenge_backend.cadastro.application.usecases;

import com.fiap.tech_challenge_backend.cadastro.application.dtos.CadastroVeiculoRequest;
import com.fiap.tech_challenge_backend.cadastro.application.dtos.CadastroVeiculoResponse;
import com.fiap.tech_challenge_backend.cadastro.application.exceptions.ClienteNaoEncontradoException;
import com.fiap.tech_challenge_backend.cadastro.application.exceptions.VeiculoJaCadastradoException;
import com.fiap.tech_challenge_backend.cadastro.application.ports.ClienteRepository;
import com.fiap.tech_challenge_backend.cadastro.application.ports.ClienteVeiculoRepository;
import com.fiap.tech_challenge_backend.cadastro.application.ports.VeiculoRepository;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Cliente;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.ClienteVeiculo;
import com.fiap.tech_challenge_backend.cadastro.domain.entities.Veiculo;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.CpfCnpj;
import com.fiap.tech_challenge_backend.shared.domain.valueobjects.Placa;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * Use Case responsável pelo cadastro de veículos.
 * Contexto Delimitado: cadastro
 * Camada: Application
 */
@Service
public class CadastroVeiculoUseCase {

    private final VeiculoRepository veiculoRepository;
    private final ClienteRepository clienteRepository;
    private final ClienteVeiculoRepository clienteVeiculoRepository;

    public CadastroVeiculoUseCase(
            VeiculoRepository veiculoRepository,
            ClienteRepository clienteRepository,
            ClienteVeiculoRepository clienteVeiculoRepository
    ) {
        this.veiculoRepository = veiculoRepository;
        this.clienteRepository = clienteRepository;
        this.clienteVeiculoRepository = clienteVeiculoRepository;
    }

    @Transactional
    public CadastroVeiculoResponse execute(CadastroVeiculoRequest request) {
        CpfCnpj cpfCnpj = new CpfCnpj(request.cpfCnpj());
        Placa placa = new Placa(request.placa());
        validarVeiculoExistente(placa);

        Cliente cliente = buscarClientePeloCpfCnpj(cpfCnpj);

        Veiculo veiculo = Veiculo.builder()
                .placa(placa)
                .marca(request.marca())
                .modelo(request.modelo())
                .ano(request.ano())
                .cor(request.cor())
                .build();

        Veiculo veiculoCadastrado = veiculoRepository.salvar(veiculo);

        ClienteVeiculo clienteVeiculo = ClienteVeiculo.builder()
                .clienteId(cliente.getId())
                .veiculoId(veiculoCadastrado.getId())
                .ativo(true)
                .build();

        clienteVeiculoRepository.salvar(clienteVeiculo);

        return new CadastroVeiculoResponse(
                veiculoCadastrado.getId(),
                veiculoCadastrado.getPlaca().valor(),
                veiculoCadastrado.getMarca(),
                veiculoCadastrado.getModelo(),
                veiculoCadastrado.getAno(),
                veiculoCadastrado.getCor(),
                cliente.getCpfCnpj().valor()
        );
    }

    private void validarVeiculoExistente(Placa placa) {
        if (veiculoRepository.existePorPlaca(placa)) {
            throw new VeiculoJaCadastradoException(placa.valor());
        }
    }

    private Cliente buscarClientePeloCpfCnpj(CpfCnpj cpfCnpj) {
        return clienteRepository.buscarPorCpfCnpj(cpfCnpj)
                .orElseThrow(() -> new ClienteNaoEncontradoException(cpfCnpj.valor()));

    }

}
