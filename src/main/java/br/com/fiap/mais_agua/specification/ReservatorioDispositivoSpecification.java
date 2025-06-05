package br.com.fiap.mais_agua.specification;

import br.com.fiap.mais_agua.controller.ReservatorioDispositivoController.ReservatorioDispositivoFilters;
import br.com.fiap.mais_agua.model.ReservatorioDispositivo;
import br.com.fiap.mais_agua.model.Usuario;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class ReservatorioDispositivoSpecification {

    // Recebe os filtros e transforma em uma Specification para o BD
    public static Specification<ReservatorioDispositivo> withFilters(ReservatorioDispositivoFilters filters, Usuario usuario) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();

            // Filtro por ID do reservatório
            if (filters.idReservatorio() != null) {
                predicates.add(cb.equal(root.get("reservatorio").get("idReservatorio"), filters.idReservatorio()));
            }

            // Filtro por ID do dispositivo
            if (filters.idDispositivo() != null) {
                predicates.add(cb.equal(root.get("dispositivo").get("idDispositivo"), filters.idDispositivo()));
            }

            // Filtro por data de instalação
            if (filters.dataInstalacao() != null) {
                predicates.add(cb.equal(root.get("dataInstalacao"), filters.dataInstalacao()));
            }

            // Filtro por usuário autenticado (somente registros do usuário)
            predicates.add(cb.equal(root.get("reservatorio").get("unidade").get("usuario"), usuario));

            // Combina todos os predicados com AND
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
