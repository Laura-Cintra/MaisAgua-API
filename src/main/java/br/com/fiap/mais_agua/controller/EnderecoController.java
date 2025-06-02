package br.com.fiap.mais_agua.controller;

import br.com.fiap.mais_agua.model.Endereco;
import br.com.fiap.mais_agua.model.Unidade;
import br.com.fiap.mais_agua.model.Usuario;
import br.com.fiap.mais_agua.repository.CidadeRepository;
import br.com.fiap.mais_agua.repository.EnderecoRepository;
import br.com.fiap.mais_agua.repository.UnidadeRepository;
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
@RequestMapping("/endereco")
@Slf4j
public class EnderecoController {

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private UnidadeRepository unidadeRepository;

    @Autowired
    private CidadeRepository cidadeRepository;

    @GetMapping
    public List<Endereco> index(@AuthenticationPrincipal Usuario usuario) {
        return enderecoRepository.findByUnidadeUsuario(usuario);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Endereco create(@RequestBody @Valid Endereco endereco, @AuthenticationPrincipal Usuario usuario) {
        log.info("Cadastrando endereço: " + endereco.getLogradouro());

        Unidade unidade = unidadeRepository.findById(endereco.getUnidade().getId_unidade())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidade não encontrada"));

        if (!unidade.getUsuario().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar esta unidade");
        }

        endereco.setUnidade(unidade);

        return enderecoRepository.save(endereco);
    }

    @GetMapping("{id}")
    public ResponseEntity<Endereco> get(@PathVariable Integer id, @AuthenticationPrincipal Usuario usuario) {
        log.info("Buscando endereço " + id);
        return ResponseEntity.ok(getEndereco(id, usuario));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> destroy(@PathVariable Integer id, @AuthenticationPrincipal Usuario usuario) {
        log.info("Excluindo endereço " + id);
        enderecoRepository.delete(getEndereco(id, usuario));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id,
                                         @RequestBody @Valid Endereco endereco,
                                         @AuthenticationPrincipal Usuario usuario) {
        log.info("Atualizando endereço " + id + " com " + endereco);

        Endereco oldEndereco = getEndereco(id, usuario);

        BeanUtils.copyProperties(endereco, oldEndereco, "id", "unidade");
        enderecoRepository.save(oldEndereco);

        return ResponseEntity.ok(oldEndereco);
    }

    private Endereco getEndereco(Integer id, Usuario usuario) {
        var endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado"));

        if (!endereco.getUnidade().getUsuario().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar este endereço");
        }

        return endereco;
    }
}
