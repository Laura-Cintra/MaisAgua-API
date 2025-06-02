package br.com.fiap.mais_agua.controller;

import br.com.fiap.mais_agua.model.Pais;
import br.com.fiap.mais_agua.repository.PaisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/paises")
public class PaisController {

    @Autowired
    private PaisRepository repository;

    @GetMapping
    public List<Pais> index() {
        return repository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pais create(@RequestBody Pais pais){
        return repository.save(pais);
    }
}
