package br.com.fiap.mais_agua.repository;

import br.com.fiap.mais_agua.model.ReservatorioDispositivo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservatorioSensorRepository extends JpaRepository<ReservatorioDispositivo, Integer> {
}
