package br.com.fiap.mais_agua.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_dispositivo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_dispositivo;

    @Column(name = "data_instalacao", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime data_instalacao= LocalDateTime.now();
}
