package br.com.fiap.mais_agua.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_reservatorio_dispositivo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservatorioSensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_reservatorio_dispositivo;

    @Column(name = "data_instalacao", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime data_instalacao= LocalDateTime.now();

    private LocalDate data_remocao;

    @ManyToOne
    @JoinColumn(name = "id_reservatorio", nullable = false)
    private Reservatorio reservatorio;

    @ManyToOne
    @JoinColumn(name = "id_dispositivo", nullable = false)
    private Dispositivo dispositivo;
}
