package br.com.fiap.mais_agua.controller;

import br.com.fiap.mais_agua.model.Estado;
import br.com.fiap.mais_agua.repository.EstadoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estados")
public class EstadoController {

    private final EstadoRepository repository;

    public EstadoController(EstadoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Estado> listar() {
        return repository.findAll();
    }

    @GetMapping("/por-pais/{idPais}")
    public List<Estado> listarPorPais(@PathVariable Integer idPais) {
        return repository.findByPaisId(idPais);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Estado create(@RequestBody Estado estado){
        return repository.save(estado);
    }
}
