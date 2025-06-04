package br.com.fiap.mais_agua.controller;

import br.com.fiap.mais_agua.model.DTO.ReservatorioReadDTO;
import br.com.fiap.mais_agua.model.DTO.UnidadeReadDTO;
import br.com.fiap.mais_agua.model.DTO.UsuarioResponseDTO;
import br.com.fiap.mais_agua.model.Reservatorio;
import br.com.fiap.mais_agua.model.Usuario;
import br.com.fiap.mais_agua.repository.ReservatorioRepository;
import br.com.fiap.mais_agua.service.ReservatorioService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/reservatorio")
@Slf4j
public class ReservatorioController {

    @Autowired
    private ReservatorioRepository reservatorioRepository;


    @Autowired
    private ReservatorioService service;

    @GetMapping
    public List<ReservatorioReadDTO> index(@AuthenticationPrincipal Usuario usuario) {
        List<Reservatorio> lista = reservatorioRepository.findByUnidadeUsuario(usuario);
        List<ReservatorioReadDTO> dtoList = new ArrayList<>();

        for (Reservatorio reservatorio : lista) {
            dtoList.add(new ReservatorioReadDTO(
                    reservatorio.getIdReservatorio(),
                    reservatorio.getNome(),
                    reservatorio.getCapacidadeTotalLitros(),
                    reservatorio.getDataInstalacao().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    new UnidadeReadDTO(
                            reservatorio.getUnidade().getIdUnidade(),
                            reservatorio.getUnidade().getNome(),
                            reservatorio.getUnidade().getCapacidadeTotalLitros(),
                            reservatorio.getUnidade().getDataCadastro().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            new UsuarioResponseDTO(
                                    reservatorio.getUnidade().getUsuario().getIdUsuario(),
                                    reservatorio.getUnidade().getUsuario().getNome(),
                                    reservatorio.getUnidade().getUsuario().getEmail()
                            )
                    )
            ));
        }

        return dtoList;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservatorioReadDTO create(@RequestBody @Valid Reservatorio reservatorio,
                                      @AuthenticationPrincipal Usuario usuario) {
        var created = service.criarReservatorio(reservatorio, usuario);

        return new ReservatorioReadDTO(
                created.getIdReservatorio(),
                created.getNome(),
                created.getCapacidadeTotalLitros(),
                created.getDataInstalacao().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                new UnidadeReadDTO(
                        created.getUnidade().getIdUnidade(),
                        created.getUnidade().getNome(),
                        created.getUnidade().getCapacidadeTotalLitros(),
                        created.getUnidade().getDataCadastro().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        new UsuarioResponseDTO(
                                created.getUnidade().getUsuario().getIdUsuario(),
                                created.getUnidade().getUsuario().getNome(),
                                created.getUnidade().getUsuario().getEmail()
                        )
                )
        );
    }

    @GetMapping("{id}")
    public ResponseEntity<ReservatorioReadDTO> get(@PathVariable Integer id,
                                                   @AuthenticationPrincipal Usuario usuario) {
        log.info("Buscando reservatório " + id);

        Reservatorio reservatorio = getReservatorio(id, usuario);

        ReservatorioReadDTO dto = new ReservatorioReadDTO(
                reservatorio.getIdReservatorio(),
                reservatorio.getNome(),
                reservatorio.getCapacidadeTotalLitros(),
                reservatorio.getDataInstalacao().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                new UnidadeReadDTO(
                        reservatorio.getUnidade().getIdUnidade(),
                        reservatorio.getUnidade().getNome(),
                        reservatorio.getUnidade().getCapacidadeTotalLitros(),
                        reservatorio.getUnidade().getDataCadastro().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        new UsuarioResponseDTO(
                                reservatorio.getUnidade().getUsuario().getIdUsuario(),
                                reservatorio.getUnidade().getUsuario().getNome(),
                                reservatorio.getUnidade().getUsuario().getEmail()
                        )
                )
        );

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> destroy(@PathVariable Integer id, @AuthenticationPrincipal Usuario usuario) {
        log.info("Excluindo reservatório " + id);
        service.deletarReservatorio(id, usuario);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("{id}")
    public ResponseEntity<ReservatorioReadDTO> update(@PathVariable Integer id,
                                                      @RequestBody @Valid Reservatorio reservatorio,
                                                      @AuthenticationPrincipal Usuario usuario) {
        log.info("Atualizando reservatório " + id + " com " + reservatorio);

        Reservatorio oldReservatorio = getReservatorio(id, usuario);

        BeanUtils.copyProperties(reservatorio, oldReservatorio, "idReservatorio", "unidade");
        reservatorioRepository.save(oldReservatorio);

        ReservatorioReadDTO dto = new ReservatorioReadDTO(
                oldReservatorio.getIdReservatorio(),
                oldReservatorio.getNome(),
                oldReservatorio.getCapacidadeTotalLitros(),
                oldReservatorio.getDataInstalacao().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                new UnidadeReadDTO(
                        oldReservatorio.getUnidade().getIdUnidade(),
                        oldReservatorio.getUnidade().getNome(),
                        oldReservatorio.getUnidade().getCapacidadeTotalLitros(),
                        oldReservatorio.getUnidade().getDataCadastro().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        new UsuarioResponseDTO(
                                oldReservatorio.getUnidade().getUsuario().getIdUsuario(),
                                oldReservatorio.getUnidade().getUsuario().getNome(),
                                oldReservatorio.getUnidade().getUsuario().getEmail()
                        )
                )
        );

        return ResponseEntity.ok(dto);
    }

    private Reservatorio getReservatorio(Integer id, Usuario usuario) {
        var reservatorio = reservatorioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservatório não encontrado"));

        if (!reservatorio.getUnidade().getUsuario().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar este reservatório");
        }

        return reservatorio;
    }
}
