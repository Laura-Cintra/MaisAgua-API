package br.com.fiap.mais_agua.controller;

import br.com.fiap.mais_agua.model.Reservatorio;
import br.com.fiap.mais_agua.model.Unidade;
import br.com.fiap.mais_agua.model.Usuario;
import br.com.fiap.mais_agua.repository.ReservatorioRepository;
import br.com.fiap.mais_agua.repository.UnidadeRepository;
import br.com.fiap.mais_agua.service.ReservatorioService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Cache;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/reservatorio")
@Slf4j
public class ReservatorioController {

    @Autowired
    private ReservatorioRepository reservatorioRepository;

    @Autowired
    private UnidadeRepository unidadeRepository;

    @Autowired
    private ReservatorioService service;

    @GetMapping
    public List<Reservatorio> index(@AuthenticationPrincipal Usuario usuario) {
        return reservatorioRepository.findByUnidadeUsuario(usuario);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Reservatorio create(@RequestBody @Valid Reservatorio reservatorio,
                               @AuthenticationPrincipal Usuario usuario) {
        return service.criarReservatorio(reservatorio, usuario);
    }

    @GetMapping("{id}")
    public ResponseEntity<Reservatorio> get(@PathVariable Integer id, @AuthenticationPrincipal Usuario usuario) {
        log.info("Buscando reservatório " + id);
        return ResponseEntity.ok(getReservatorio(id, usuario));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> destroy(@PathVariable Integer id, @AuthenticationPrincipal Usuario usuario) {
        log.info("Excluindo reservatório " + id);
        reservatorioRepository.delete(getReservatorio(id, usuario));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id,
                                         @RequestBody @Valid Reservatorio reservatorio,
                                         @AuthenticationPrincipal Usuario usuario) {
        log.info("Atualizando reservatório " + id + " com " + reservatorio);

        Reservatorio oldReservatorio = getReservatorio(id, usuario);

        BeanUtils.copyProperties(reservatorio, oldReservatorio, "id_reservatorio", "unidade");
        reservatorioRepository.save(oldReservatorio);

        return ResponseEntity.ok(oldReservatorio);
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
