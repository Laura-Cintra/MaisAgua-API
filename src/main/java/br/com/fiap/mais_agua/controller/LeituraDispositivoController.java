package br.com.fiap.mais_agua.controller;

import br.com.fiap.mais_agua.model.Dispositivo;
import br.com.fiap.mais_agua.model.LeituraDispositivo;
import br.com.fiap.mais_agua.model.Usuario;
import br.com.fiap.mais_agua.repository.DispositivoRepository;
import br.com.fiap.mais_agua.repository.LeituraDispositivoRepository;
import br.com.fiap.mais_agua.repository.ReservatorioDispositivoRepository;
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
@RequestMapping("/leitura-dispositivo")
@Slf4j
public class LeituraDispositivoController {

    @Autowired
    private LeituraDispositivoRepository leituraRepository;

    @Autowired
    private DispositivoRepository dispositivoRepository;

    @Autowired
    private ReservatorioDispositivoRepository reservatorioDispositivoRepository;

    @GetMapping
    public List<LeituraDispositivo> index(@AuthenticationPrincipal Usuario usuario) {
        return leituraRepository.findAll().stream()
                .filter(leitura ->
                        pertenceAoUsuario(leitura.getDispositivo(), usuario)
                ).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeituraDispositivo create(@RequestBody @Valid LeituraDispositivo leitura,
                                     @AuthenticationPrincipal Usuario usuario) {
        log.info("Cadastrando leitura de dispositivo");

        Dispositivo dispositivo = getDispositivoDoUsuario(leitura.getDispositivo().getIdDispositivo(), usuario);

        leitura.setDispositivo(dispositivo);

        return leituraRepository.save(leitura);
    }

    @GetMapping("{id}")
    public ResponseEntity<LeituraDispositivo> get(@PathVariable Integer id,
                                                  @AuthenticationPrincipal Usuario usuario) {
        var leitura = getLeituraDoUsuario(id, usuario);

        return ResponseEntity.ok(leitura);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> destroy(@PathVariable Integer id,
                                          @AuthenticationPrincipal Usuario usuario) {
        var leitura = getLeituraDoUsuario(id, usuario);

        leituraRepository.delete(leitura);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Object> update(@PathVariable Integer id,
                                         @RequestBody @Valid LeituraDispositivo leitura,
                                         @AuthenticationPrincipal Usuario usuario) {
        var leituraExistente = getLeituraDoUsuario(id, usuario);

        Dispositivo dispositivo = getDispositivoDoUsuario(leitura.getDispositivo().getIdDispositivo(), usuario);

        leitura.setDispositivo(dispositivo);

        BeanUtils.copyProperties(leitura, leituraExistente, "id_leitura", "dataHora");

        leituraRepository.save(leituraExistente);

        return ResponseEntity.ok(leituraExistente);
    }

    private LeituraDispositivo getLeituraDoUsuario(Integer id, Usuario usuario) {
        var leitura = leituraRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Leitura não encontrada"));

        if (!pertenceAoUsuario(leitura.getDispositivo(), usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem acesso a essa leitura");
        }

        return leitura;
    }

    private Dispositivo getDispositivoDoUsuario(Integer id, Usuario usuario) {
        var dispositivo = dispositivoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dispositivo não encontrado"));

        if (!pertenceAoUsuario(dispositivo, usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem acesso a esse dispositivo");
        }

        return dispositivo;
    }

    private boolean pertenceAoUsuario(Dispositivo dispositivo, Usuario usuario) {
        return reservatorioDispositivoRepository.findByDispositivo(dispositivo).stream()
                .anyMatch(reservatorioDispositivo ->
                        reservatorioDispositivo.getReservatorio()
                                .getUnidade()
                                .getUsuario()
                                .equals(usuario)
                );
    }
}
