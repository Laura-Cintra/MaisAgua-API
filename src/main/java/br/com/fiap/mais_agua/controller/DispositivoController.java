package br.com.fiap.mais_agua.controller;

import br.com.fiap.mais_agua.model.Dispositivo;
import br.com.fiap.mais_agua.repository.DispositivoRepository;
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
@RequestMapping("/dispositivo")
@Slf4j
public class DispositivoController {

    @Autowired
    private DispositivoRepository dispositivoRepository;

    @GetMapping
    public List<Dispositivo> index() {
        return dispositivoRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Dispositivo create(@RequestBody @Valid Dispositivo dispositivo) {
        log.info("Cadastrando dispositivo");
        // data_instalacao será setada pelo banco (default CURRENT_TIMESTAMP)
        return dispositivoRepository.save(dispositivo);
    }

    @GetMapping("{id}")
    public ResponseEntity<Dispositivo> get(@PathVariable Integer id) {
        log.info("Buscando dispositivo com o id " + id);

        return ResponseEntity.ok(getDispositivo(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> destroy(@PathVariable Integer id) {
        log.info("Deletando dispositivo com o id " + id);

        dispositivoRepository.delete(getDispositivo(id));
        return ResponseEntity.noContent().build();
    }

    private Dispositivo getDispositivo(Integer id) {
        return dispositivoRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Dispositivo não encontrado"));
    }
}
