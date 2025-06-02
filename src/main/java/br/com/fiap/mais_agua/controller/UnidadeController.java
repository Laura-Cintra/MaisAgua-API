package br.com.fiap.mais_agua.controller;

import br.com.fiap.mais_agua.model.Unidade;
import br.com.fiap.mais_agua.model.Usuario;
import br.com.fiap.mais_agua.repository.UnidadeRepository;
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
@RequestMapping("/unidade")
@Slf4j
public class UnidadeController {
    @Autowired
    private UnidadeRepository unidadeRepository;


    @GetMapping
    public List<Unidade> index(@AuthenticationPrincipal Usuario usuario){
        return unidadeRepository.findByUsuario(usuario);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Unidade create(@RequestBody @Valid Unidade unidade, @AuthenticationPrincipal Usuario usuario){
        log.info("Cadastrando unidade " + unidade.getNome());
        unidade.setUsuario(usuario);
        return unidadeRepository.save(unidade);
    }

    @GetMapping("{id}")
    public ResponseEntity<Unidade> get(@PathVariable Integer id, @AuthenticationPrincipal Usuario usuario){
        log.info("Buscando unidade " + id);
        return ResponseEntity.ok(getUnidade(id, usuario));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> destroy (@PathVariable Integer id, @AuthenticationPrincipal Usuario usuario){
        log.info("Excluindo unidade " + id);
        unidadeRepository.delete(getUnidade(id, usuario));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id, @RequestBody @Valid Unidade unidade, @AuthenticationPrincipal Usuario usuario){
        log.info("Atualizando categoria " + id + " com " + unidade);
        var oldUnidade = getUnidade(id, usuario);
        BeanUtils.copyProperties(unidade, oldUnidade,"id_unidade", "usuario");
        unidadeRepository.save(oldUnidade);
        return ResponseEntity.ok(oldUnidade);
    }

    private Unidade getUnidade(Integer id, Usuario usuario){
        var unidadeFind = unidadeRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidade não encontrada")
                );

        if(!unidadeFind.getUsuario().equals(usuario)){
            throw new ResponseStatusException((HttpStatus.FORBIDDEN));
        }
        return unidadeFind;
    }
}
