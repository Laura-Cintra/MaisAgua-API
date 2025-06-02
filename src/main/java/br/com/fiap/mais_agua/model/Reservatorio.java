package br.com.fiap.mais_agua.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_reservatorio")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Reservatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_reservatorio;

    @NotBlank(message = "Campo obrigatório")
    private String nome;

    @NotNull(message = "Campo obrigatório")
    private Integer capacidade_total_litros;

    @Column(name = "data_instalacao", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime data_instalacao= LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "id_unidade", referencedColumnName = "id_unidade", nullable = false)
    private Unidade unidade;

}
