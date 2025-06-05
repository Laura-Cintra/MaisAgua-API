package br.com.fiap.mais_agua.controller;

import br.com.fiap.mais_agua.model.Dispositivo;
import br.com.fiap.mais_agua.model.Reservatorio;
import br.com.fiap.mais_agua.model.ReservatorioDispositivo;
import br.com.fiap.mais_agua.model.Usuario;
import br.com.fiap.mais_agua.model.DTO.ReservatorioBasicoDTO;
import br.com.fiap.mais_agua.model.DTO.ReservatorioDispositivoDTO;
import br.com.fiap.mais_agua.model.DTO.UnidadeReadDTO;
import br.com.fiap.mais_agua.model.DTO.UsuarioResponseDTO;
import br.com.fiap.mais_agua.repository.DispositivoRepository;
import br.com.fiap.mais_agua.repository.ReservatorioRepository;
import br.com.fiap.mais_agua.repository.ReservatorioDispositivoRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservatorio-dispositivo")
@Slf4j
public class ReservatorioDispositivoController {

    public record ReservatorioDispositivoFilters(
            Integer idReservatorio,   // Filtro para o reservatório
            Integer idDispositivo,    // Filtro para o dispositivo
            LocalDate dataInstalacao  // Filtro para a data de instalação
    ) {}

    @Autowired
    private ReservatorioDispositivoRepository reservatorioSensorRepository;

    @Autowired
    private ReservatorioRepository reservatorioRepository;

    @Autowired
    private DispositivoRepository dispositivoRepository;

    @GetMapping
    public List<ReservatorioDispositivoDTO> index(@AuthenticationPrincipal Usuario usuario) {
        return reservatorioSensorRepository.findAll().stream()
                .filter(rs -> rs.getReservatorio().getUnidade().getUsuario().equals(usuario))
                .map(this::toDTO)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservatorioDispositivoDTO create(@RequestBody @Valid ReservatorioDispositivo reservatorioSensor,
                                             @AuthenticationPrincipal Usuario usuario) {
        log.info("Cadastrando ReservatorioSensor");

        Reservatorio reservatorio = getReservatorio(reservatorioSensor.getReservatorio().getIdReservatorio(), usuario);
        Dispositivo dispositivo = getDispositivo(reservatorioSensor.getDispositivo().getIdDispositivo());

        reservatorioSensor.setReservatorio(reservatorio);
        reservatorioSensor.setDispositivo(dispositivo);

        var saved = reservatorioSensorRepository.save(reservatorioSensor);
        return toDTO(saved);
    }

    @GetMapping("{id}")
    public ResponseEntity<ReservatorioDispositivoDTO> get(@PathVariable Integer id,
                                                          @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(toDTO(getReservatorioSensor(id, usuario)));
    }


    @DeleteMapping("{id}")
    public ResponseEntity<Object> destroy(@PathVariable Integer id,
                                          @AuthenticationPrincipal Usuario usuario) {
        var reservatorioSensor = getReservatorioSensor(id, usuario);
        reservatorioSensorRepository.delete(reservatorioSensor);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("{id}")
    public ResponseEntity<ReservatorioDispositivoDTO> update(@PathVariable Integer id,
                                                             @RequestBody @Valid ReservatorioDispositivo reservatorioSensor,
                                                             @AuthenticationPrincipal Usuario usuario) {
        var oldRS = getReservatorioSensor(id, usuario);

        Reservatorio reservatorio = getReservatorio(reservatorioSensor.getReservatorio().getIdReservatorio(), usuario);
        Dispositivo dispositivo = getDispositivo(reservatorioSensor.getDispositivo().getIdDispositivo());

        reservatorioSensor.setReservatorio(reservatorio);
        reservatorioSensor.setDispositivo(dispositivo);

        BeanUtils.copyProperties(reservatorioSensor, oldRS, "idReservatorioDispositivo");
        reservatorioSensorRepository.save(oldRS);

        return ResponseEntity.ok(toDTO(oldRS));
    }


    // Método auxiliar para conversão de entidade para DTO
    private ReservatorioDispositivoDTO toDTO(ReservatorioDispositivo entity) {
        var unidade = entity.getReservatorio().getUnidade();
        var usuario = unidade.getUsuario();

        return new ReservatorioDispositivoDTO(
                entity.getIdReservatorioDispositivo(),
                entity.getDataInstalacao() != null ? entity.getDataInstalacao().toString() : null,
                new ReservatorioBasicoDTO(
                        entity.getReservatorio().getIdReservatorio(),
                        entity.getReservatorio().getNome(),
                        entity.getReservatorio().getCapacidadeTotalLitros()
                ),
                UnidadeReadDTO.builder()
                        .idUnidade(unidade.getIdUnidade())
                        .nomeUnidade(unidade.getNome())
                        .capacidadeTotalLitros(unidade.getCapacidadeTotalLitros())
                        .dataCadastro(unidade.getDataCadastro().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                        .usuario(new UsuarioResponseDTO(
                                usuario.getIdUsuario(),
                                usuario.getNome(),
                                usuario.getEmail()
                        ))
                        .build()
        );
    }

    private ReservatorioDispositivo getReservatorioSensor(Integer id, Usuario usuario) {
        var rs = reservatorioSensorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservatorio-Sensor não encontrado"));

        if (!rs.getReservatorio().getUnidade().getUsuario().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar este sensor");
        }
        return rs;
    }


    private Reservatorio getReservatorio(Integer id, Usuario usuario) {
        var r = reservatorioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservatorio não encontrado"));

        if (!r.getUnidade().getUsuario().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para alterar este reservatório");
        }
        return r;
    }


    private Dispositivo getDispositivo(Integer id) {
        return dispositivoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dispositivo não encontrado"));
    }
}
