package br.com.fiap.mais_agua.controller;

import br.com.fiap.mais_agua.model.Dispositivo;
import br.com.fiap.mais_agua.model.Reservatorio;
import br.com.fiap.mais_agua.model.ReservatorioDispositivo;
import br.com.fiap.mais_agua.model.Usuario;
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

import java.util.List;

@RestController
@RequestMapping("/reservatorio-sensor")
@Slf4j
public class ReservatorioSensorController {

    @Autowired
    private ReservatorioDispositivoRepository reservatorioSensorRepository;

    @Autowired
    private ReservatorioRepository reservatorioRepository;

    @Autowired
    private DispositivoRepository dispositivoRepository;

    @GetMapping
    public List<ReservatorioDispositivo> index(@AuthenticationPrincipal Usuario usuario) {
        return reservatorioSensorRepository.findAll()
                .stream()
                .filter(rs -> rs.getReservatorio().getUnidade().getUsuario().equals(usuario))
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservatorioDispositivo create(@RequestBody @Valid ReservatorioDispositivo reservatorioSensor,
                                          @AuthenticationPrincipal Usuario usuario) {
        log.info("Cadastrando ReservatorioSensor");

        Reservatorio reservatorio = getReservatorio(reservatorioSensor.getReservatorio().getId_reservatorio(), usuario);

        Dispositivo dispositivo = getDispositivo(reservatorioSensor.getDispositivo().getId_dispositivo());

        reservatorioSensor.setReservatorio(reservatorio);
        reservatorioSensor.setDispositivo(dispositivo);

        return reservatorioSensorRepository.save(reservatorioSensor);
    }

    @GetMapping("{id}")
    public ResponseEntity<ReservatorioDispositivo> get(@PathVariable Integer id,
                                                       @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(getReservatorioSensor(id, usuario));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> destroy(@PathVariable Integer id,
                                          @AuthenticationPrincipal Usuario usuario) {
        var reservatorioSensor = getReservatorioSensor(id, usuario);
        reservatorioSensorRepository.delete(reservatorioSensor);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id,
                                         @RequestBody @Valid ReservatorioDispositivo reservatorioSensor,
                                         @AuthenticationPrincipal Usuario usuario) {
        var oldRS = getReservatorioSensor(id, usuario);

        Reservatorio reservatorio = getReservatorio(reservatorioSensor.getReservatorio().getId_reservatorio(), usuario);

        Dispositivo dispositivo = getDispositivo(reservatorioSensor.getDispositivo().getId_dispositivo());

        reservatorioSensor.setReservatorio(reservatorio);
        reservatorioSensor.setDispositivo(dispositivo);

        BeanUtils.copyProperties(reservatorioSensor, oldRS, "id_reservatorio_dispositivo");
        reservatorioSensorRepository.save(oldRS);

        return ResponseEntity.ok(oldRS);
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
        var d = dispositivoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dispositivo não encontrado"));

        return d;
    }
}
