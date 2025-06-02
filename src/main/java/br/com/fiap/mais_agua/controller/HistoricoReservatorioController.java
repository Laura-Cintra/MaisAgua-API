package br.com.fiap.mais_agua.controller;

import br.com.fiap.mais_agua.model.HistoricoReservatorio;
import br.com.fiap.mais_agua.model.Reservatorio;
import br.com.fiap.mais_agua.model.StatusReservatorio;
import br.com.fiap.mais_agua.model.Usuario;
import br.com.fiap.mais_agua.repository.HistoricoReservatorioRepository;
import br.com.fiap.mais_agua.repository.ReservatorioRepository;
import br.com.fiap.mais_agua.repository.StatusReservatorioRepository;
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
@RequestMapping("/historico-reservatorio")
@Slf4j
public class HistoricoReservatorioController {

    @Autowired
    private HistoricoReservatorioRepository historicoRepository;

    @Autowired
    private ReservatorioRepository reservatorioRepository;

    @Autowired
    private StatusReservatorioRepository statusRepository;

    @GetMapping
    public List<HistoricoReservatorio> index(@AuthenticationPrincipal Usuario usuario) {
        return historicoRepository.findAll().stream()
                .filter(h -> h.getReservatorio().getUnidade().getUsuario().equals(usuario))
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HistoricoReservatorio create(@RequestBody @Valid HistoricoReservatorio historico,
                                        @AuthenticationPrincipal Usuario usuario) {
        log.info("Cadastrando histórico");

        Reservatorio reservatorio = getReservatorio(historico.getReservatorio().getId_reservatorio(), usuario);

        StatusReservatorio status = getStatus(historico.getStatus().getId());

        historico.setReservatorio(reservatorio);
        historico.setStatus(status);

        return historicoRepository.save(historico);
    }

    @GetMapping("{id}")
    public ResponseEntity<HistoricoReservatorio> get(@PathVariable Integer id,
                                                     @AuthenticationPrincipal Usuario usuario) {
        var historico = getHistorico(id, usuario);
        return ResponseEntity.ok(historico);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> destroy(@PathVariable Integer id,
                                          @AuthenticationPrincipal Usuario usuario) {
        var historico = getHistorico(id, usuario);
        historicoRepository.delete(historico);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id,
                                         @RequestBody @Valid HistoricoReservatorio historico,
                                         @AuthenticationPrincipal Usuario usuario) {
        var historicoDB = getHistorico(id, usuario);

        Reservatorio reservatorio = getReservatorio(historico.getReservatorio().getId_reservatorio(), usuario);

        StatusReservatorio status = getStatus(historico.getStatus().getId());

        historico.setReservatorio(reservatorio);
        historico.setStatus(status);

        BeanUtils.copyProperties(historico, historicoDB, "id");

        historicoRepository.save(historicoDB);

        return ResponseEntity.ok(historicoDB);
    }

    private HistoricoReservatorio getHistorico(Integer id, Usuario usuario) {
        var historico = historicoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Histórico não encontrado"));

        if (!historico.getReservatorio().getUnidade().getUsuario().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar este histórico");
        }

        return historico;
    }

    private Reservatorio getReservatorio(Integer id, Usuario usuario) {
        var reservatorio = reservatorioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservatório não encontrado"));

        if (!reservatorio.getUnidade().getUsuario().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar este reservatório");
        }

        return reservatorio;
    }

    private StatusReservatorio getStatus(Integer id) {
        return statusRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Status não encontrado"));
    }
}
