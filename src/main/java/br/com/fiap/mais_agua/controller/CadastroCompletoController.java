package br.com.fiap.mais_agua.controller;

import br.com.fiap.mais_agua.model.DTO.CadastroCompletoDTO;
import br.com.fiap.mais_agua.service.CadastroCompletoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cadastro-completo")
@RequiredArgsConstructor
public class CadastroCompletoController {

    private final CadastroCompletoService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void cadastrar(@RequestBody @Valid CadastroCompletoDTO dto) {
        service.cadastrar(dto);
    }
}
