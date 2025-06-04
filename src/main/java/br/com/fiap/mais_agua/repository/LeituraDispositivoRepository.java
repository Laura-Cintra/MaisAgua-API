package br.com.fiap.mais_agua.repository;

import br.com.fiap.mais_agua.model.LeituraDispositivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LeituraDispositivoRepository extends JpaRepository<LeituraDispositivo, Integer> {
    Optional<LeituraDispositivo> findTopByDispositivoIdDispositivoOrderByDataHoraDesc(Integer idDispositivo);

}
