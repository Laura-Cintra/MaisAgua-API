package br.com.fiap.mais_agua.controller;
import br.com.fiap.mais_agua.model.Dispositivo;
import br.com.fiap.mais_agua.model.StatusReservatorio;
import br.com.fiap.mais_agua.repository.StatusReservatorioRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/status-reservatorio")
@Slf4j
public class StatusReservatorioController {

    @Autowired
    private StatusReservatorioRepository repository;

    @GetMapping
    public List<StatusReservatorio> index() {
        return repository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StatusReservatorio create(@RequestBody @Valid StatusReservatorio status) {
        log.info("Cadastrando status: " + status.getStatus());
        return repository.save(status);
    }

    @GetMapping("{id}")
    public ResponseEntity<StatusReservatorio> get(@PathVariable Integer id) {
        var status = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status não encontrado"));
        return ResponseEntity.ok(getStatus(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> destroy(@PathVariable Integer id) {
        repository.delete(getStatus(id));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id,
                                         @RequestBody @Valid StatusReservatorio status) {
        var statusDB = getStatus(id);

        BeanUtils.copyProperties(status, statusDB, "id");

        repository.save(statusDB);

        return ResponseEntity.ok(statusDB);
    }

    private StatusReservatorio getStatus(Integer id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Status não encontrado"));
    }

}
