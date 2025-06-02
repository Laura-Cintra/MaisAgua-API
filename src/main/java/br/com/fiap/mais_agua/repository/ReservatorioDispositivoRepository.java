package br.com.fiap.mais_agua.repository;

import br.com.fiap.mais_agua.model.Dispositivo;
import br.com.fiap.mais_agua.model.ReservatorioDispositivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservatorioDispositivoRepository extends JpaRepository<ReservatorioDispositivo, Integer> {
    List<ReservatorioDispositivo> findByDispositivo(Dispositivo dispositivo);
}
