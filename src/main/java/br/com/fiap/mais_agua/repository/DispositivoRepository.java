package br.com.fiap.mais_agua.repository;

import br.com.fiap.mais_agua.model.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DispositivoRepository extends JpaRepository<Dispositivo, Integer> {
}
