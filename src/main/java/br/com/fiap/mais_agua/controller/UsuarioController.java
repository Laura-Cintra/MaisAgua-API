package br.com.fiap.mais_agua.controller;

import br.com.fiap.mais_agua.model.DTO.CadastroDTO;
import br.com.fiap.mais_agua.model.DTO.Credentials;
import br.com.fiap.mais_agua.model.DTO.PerfilDTO;
import br.com.fiap.mais_agua.model.DTO.UsuarioResponseDTO;
import br.com.fiap.mais_agua.model.Token;
import br.com.fiap.mais_agua.model.Usuario;
import br.com.fiap.mais_agua.repository.ReservatorioRepository;
import br.com.fiap.mais_agua.repository.UsuarioRepository;
import br.com.fiap.mais_agua.service.PerfilService;
import br.com.fiap.mais_agua.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UsuarioController {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ReservatorioRepository reservatorioRepository;
    @Autowired
    private PerfilService perfilService;

    @PostMapping("/login")
    public ResponseEntity<Token> login(@RequestBody Credentials credentials) {
        try {
            var auth = new UsernamePasswordAuthenticationToken(credentials.email(), credentials.senha());
            var user = (Usuario) authenticationManager.authenticate(auth).getPrincipal();

            Token token = tokenService.createToken(user);

            return ResponseEntity.ok(token);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha incorretos!");
        }
    }

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponseDTO> register(@RequestBody @Valid CadastroDTO dto) {
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-mail já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha())); // Codificando a senha

        usuario = usuarioRepository.save(usuario);

        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO(usuario.getIdUsuario(), usuario.getNome(), usuario.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/perfil/{idReservatorio}")
    public ResponseEntity<PerfilDTO> readPerfil(@PathVariable Integer idReservatorio,
                                                @AuthenticationPrincipal Usuario usuario) {
        PerfilDTO perfilDTO = perfilService.getPerfil(idReservatorio, usuario);
        return ResponseEntity.ok(perfilDTO);
    }

}
