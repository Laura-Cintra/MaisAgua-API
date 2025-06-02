package br.com.fiap.mais_agua.controller;

import br.com.fiap.mais_agua.model.Cidade;
import br.com.fiap.mais_agua.repository.CidadeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cidades")
public class CidadeController {

    private final CidadeRepository repository;

    public CidadeController(CidadeRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Cidade> listar() {
        return repository.findAll();
    }

    @GetMapping("/por-estado/{idEstado}")
    public List<Cidade> listarPorEstado(@PathVariable Integer idEstado) {
        return repository.findByEstadoId(idEstado);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cidade create(@RequestBody Cidade cidade){
        return repository.save(cidade);
    }
}
