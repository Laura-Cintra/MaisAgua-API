package br.com.fiap.mais_agua.service;

import br.com.fiap.mais_agua.model.*;
import br.com.fiap.mais_agua.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservatorioService {
    @Autowired
    private ReservatorioRepository reservatorioRepository;
    @Autowired
    private UnidadeRepository unidadeRepository;
    @Autowired

    private DispositivoRepository dispositivoRepository;
    @Autowired

    private ReservatorioDispositivoRepository reservatorioDispositivoRepository;

    // executar um conjunto de operações no bd em uma unica transação
    @Transactional
    public Reservatorio criarReservatorio(Reservatorio reservatorio, Usuario usuario) {
        log.info("Cadastrando reservatório: {}", reservatorio.getNome());

        Unidade unidade = unidadeRepository.findById(reservatorio.getUnidade().getId_unidade())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidade não encontrada"));

        if (!unidade.getUsuario().getId_usuario().equals(usuario.getId_usuario())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar essa unidade");
        }

        if(reservatorio.getCapacidade_total_litros() > unidade.getCapacidade_total_litros()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O reservátorio não pode ultrapassar a capacidade total de litros da unidade, que é " + unidade.getCapacidade_total_litros());
        }

        validarCapacidadeReservatorios(unidade, reservatorio.getCapacidade_total_litros());


        reservatorio.setUnidade(unidade);
        Reservatorio novoReservatorio = reservatorioRepository.save(reservatorio);

        // Cria dispositivo
        Dispositivo dispositivo = Dispositivo.builder()
                .data_instalacao(LocalDateTime.now())
                .build();
        dispositivo = dispositivoRepository.save(dispositivo);

        // Cria vínculo reservatorio-dispositivo
        ReservatorioDispositivo vinculo = ReservatorioDispositivo.builder()
                .data_instalacao(LocalDateTime.now())
                .reservatorio(novoReservatorio)
                .dispositivo(dispositivo)
                .build();
        reservatorioDispositivoRepository.save(vinculo);

        return novoReservatorio;
    }

    private void validarCapacidadeReservatorios(Unidade unidade, Integer capacidadeNovoReservatorio) {
        // Soma dos reservatórios já existentes
        Integer capacidadeTotalReservatorios = reservatorioRepository.findByUnidade(unidade)
                .stream()
                .mapToInt(Reservatorio::getCapacidade_total_litros)
                .sum();

        // Verifica se a soma atual + novo reservatório ultrapassa a capacidade da unidade
        if ((capacidadeTotalReservatorios + capacidadeNovoReservatorio) > unidade.getCapacidade_total_litros()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A capacidade total dos reservatórios excede a capacidade da unidade. " +
                            "Capacidade da unidade: " + unidade.getCapacidade_total_litros() + " litros. " +
                            "Capacidade já utilizada: " + capacidadeTotalReservatorios + " litros. " +
                            "Tentando adicionar mais: " + capacidadeNovoReservatorio + " litros.");
        }
    }



}
