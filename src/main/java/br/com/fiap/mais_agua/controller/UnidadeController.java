package br.com.fiap.mais_agua.controller;

import br.com.fiap.mais_agua.model.DTO.UnidadeReadDTO;
import br.com.fiap.mais_agua.model.DTO.UnidadeResponseDTO;
import br.com.fiap.mais_agua.model.DTO.UsuarioResponseDTO;
import br.com.fiap.mais_agua.model.Unidade;
import br.com.fiap.mais_agua.model.Usuario;
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
@RequestMapping("/unidade")
@Slf4j
public class UnidadeController {
    @Autowired
    private UnidadeRepository unidadeRepository;


    @GetMapping
    public List<UnidadeReadDTO> index(@AuthenticationPrincipal Usuario usuario) {
        return unidadeRepository.findByUsuario(usuario)
                .stream()
                .map(unidade -> {
                    UsuarioResponseDTO usuarioDTO = new UsuarioResponseDTO(
                            unidade.getUsuario().getIdUsuario(),
                            unidade.getUsuario().getNome(),
                            unidade.getUsuario().getEmail()
                    );

                    return new UnidadeReadDTO(
                            unidade.getIdUnidade(),
                            unidade.getNome(),
                            unidade.getCapacidadeTotalLitros(),
                            unidade.getDataCadastro().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            usuarioDTO
                    );
                })
                .toList();
    }



    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public UnidadeResponseDTO create(@RequestBody @Valid Unidade unidade, @AuthenticationPrincipal Usuario usuario) {
        log.info("Cadastrando unidade " + unidade.getNome());

        unidade.setUsuario(usuario);

        Unidade unidadeSalva = unidadeRepository.save(unidade);

        UnidadeResponseDTO responseDTO = new UnidadeResponseDTO(
                unidadeSalva.getNome(),
                unidadeSalva.getCapacidadeTotalLitros(),
                unidadeSalva.getUsuario().getIdUsuario());

        return responseDTO;
    }

    @GetMapping("{id}")
    public ResponseEntity<UnidadeReadDTO> get(@PathVariable Integer id, @AuthenticationPrincipal Usuario usuario) {
        log.info("Buscando unidade " + id);

        Unidade unidade = getUnidade(id, usuario);

        UsuarioResponseDTO usuarioDTO = new UsuarioResponseDTO(
                unidade.getUsuario().getIdUsuario(),
                unidade.getUsuario().getNome(),
                unidade.getUsuario().getEmail()
        );

        UnidadeReadDTO dto = new UnidadeReadDTO(
                unidade.getIdUnidade(),
                unidade.getNome(),
                unidade.getCapacidadeTotalLitros(),
                unidade.getDataCadastro().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                usuarioDTO
        );

        return ResponseEntity.ok(dto);
    }



    @DeleteMapping("{id}")
    public ResponseEntity<Object> destroy (@PathVariable Integer id, @AuthenticationPrincipal Usuario usuario){
        log.info("Excluindo unidade " + id);
        unidadeRepository.delete(getUnidade(id, usuario));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<UnidadeReadDTO> update(@PathVariable Integer id, @RequestBody @Valid Unidade unidade, @AuthenticationPrincipal Usuario usuario
    ) {
        log.info("Atualizando unidade " + id + " com " + unidade);

        var oldUnidade = getUnidade(id, usuario);

        BeanUtils.copyProperties(unidade, oldUnidade, "idUnidade", "usuario", "dataCadastro");
        unidadeRepository.save(oldUnidade);

        UsuarioResponseDTO usuarioDTO = new UsuarioResponseDTO(
                oldUnidade.getUsuario().getIdUsuario(),
                oldUnidade.getUsuario().getNome(),
                oldUnidade.getUsuario().getEmail()
        );

        UnidadeReadDTO dto = new UnidadeReadDTO(
                oldUnidade.getIdUnidade(),
                oldUnidade.getNome(),
                oldUnidade.getCapacidadeTotalLitros(),
                oldUnidade.getDataCadastro().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                usuarioDTO
        );

        return ResponseEntity.ok(dto);
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
