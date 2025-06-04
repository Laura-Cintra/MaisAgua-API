package br.com.fiap.mais_agua.repository;

import br.com.fiap.mais_agua.model.HistoricoReservatorio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HistoricoReservatorioRepository extends JpaRepository<HistoricoReservatorio, Integer> {

    Optional<HistoricoReservatorio> findTopByReservatorioIdReservatorioOrderByDataHoraDesc(Integer idReservatorio);

}
