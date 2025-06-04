package br.com.fiap.mais_agua.service;

// import br.com.fiap.mais_agua.model.dto.CadastroCompletoDTO;
import br.com.fiap.mais_agua.model.*;
import br.com.fiap.mais_agua.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.*;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CadastroCompletoService {

    private final UsuarioRepository usuarioRepository;
    private final UnidadeRepository unidadeRepository;
    private final EnderecoRepository enderecoRepository;
    private final CidadeRepository cidadeRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void cadastrar(br.com.fiap.mais_agua.model.DTO.CadastroCompletoDTO dto) {
        // 1 - Cadastrar Usuário
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new ResponseStatusException(BAD_REQUEST, "E-mail já cadastrado");
        }

        Usuario usuario = Usuario.builder()
                .nome(dto.nomeUsuario())
                .email(dto.email())
                .senha(passwordEncoder.encode(dto.senha()))
                .build();
        usuario = usuarioRepository.save(usuario);

        // 2 - Cadastrar Unidade
        Unidade unidade = Unidade.builder()
                .nome(dto.nomeUnidade())
                .capacidade_total_litros(dto.capacidade_total_litros())
                .usuario(usuario)
                .data_cadastro(LocalDateTime.now())
                .build();
        unidade = unidadeRepository.save(unidade);

        // 3 - Buscar cidade
        Cidade cidade = cidadeRepository.findById(dto.idCidade())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Cidade não encontrada"));

        // 4 - Cadastrar Endereço
        Endereco endereco = Endereco.builder()
                .logradouro(dto.logradouro())
                .numero(dto.numero())
                .complemento(dto.complemento())
                .cep(dto.cep())
                .cidade(cidade)
                .unidade(unidade)
                .build();
        enderecoRepository.save(endereco);
    }
}