package br.com.fiap.mais_agua.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_historico_reservatorio")
@Data
public class HistoricoReservatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historico")
    private Integer id;

    @NotNull
    @Column(name = "nivel_litros")
    private Integer nivelLitros;

    @Column(name = "data_hora")
    private LocalDateTime dataHora = LocalDateTime.now();

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_reservatorio")
    private Reservatorio reservatorio;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_status_reservatorio")
    private StatusReservatorio status;
}
