package br.com.fiap.mais_agua.service;

import br.com.fiap.mais_agua.model.DTO.PerfilDTO;
import br.com.fiap.mais_agua.model.Endereco;
import br.com.fiap.mais_agua.model.HistoricoReservatorio;
import br.com.fiap.mais_agua.model.LeituraDispositivo;
import br.com.fiap.mais_agua.model.ReservatorioDispositivo;
import br.com.fiap.mais_agua.model.Reservatorio;
import br.com.fiap.mais_agua.model.Dispositivo;
import br.com.fiap.mais_agua.model.Usuario;
import br.com.fiap.mais_agua.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PerfilService {

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private ReservatorioDispositivoRepository reservatorioDispositivoRepository;

    @Autowired
    private HistoricoReservatorioRepository historicoReservatorioRepository;

    @Autowired
    private LeituraDispositivoRepository leituraDispositivoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    public PerfilDTO getPerfil(Integer idReservatorio, Usuario usuario) {
        List<Endereco> enderecos = enderecoRepository.findByUnidadeUsuario(usuario);

        Endereco endereco = enderecos.stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

        ReservatorioDispositivo reservatorioDispositivo = reservatorioDispositivoRepository
                .findByReservatorioIdReservatorio(idReservatorio)
                .orElseThrow(() -> new RuntimeException("ReservatórioDispositivo não encontrado"));

        Reservatorio reservatorio = reservatorioDispositivo.getReservatorio();
        Dispositivo dispositivo = reservatorioDispositivo.getDispositivo();

        HistoricoReservatorio historicoReservatorio = historicoReservatorioRepository
                .findTopByReservatorioIdReservatorioOrderByDataHoraDesc(reservatorio.getIdReservatorio())
                .orElse(null);

        LeituraDispositivo leituraDispositivo = leituraDispositivoRepository
                .findTopByDispositivoIdDispositivoOrderByDataHoraDesc(dispositivo.getIdDispositivo())
                .orElse(null);

        Optional<Usuario> usuarioRepoOptional = usuarioRepository.findByIdUsuario(usuario.getIdUsuario());

        if (usuarioRepoOptional.isPresent()) {
            Usuario usuarioRepo = usuarioRepoOptional.get();

            return PerfilDTO.builder()
                    .nome(usuarioRepo.getNome())
                    .logradouro(endereco.getLogradouro())
                    .numero(endereco.getNumero())
                    .nivelLitros(historicoReservatorio != null ? historicoReservatorio.getNivelLitros() : 0)
                    .ph(leituraDispositivo != null ? leituraDispositivo.getPhInt() : BigDecimal.ZERO)
                    .nivelPct(leituraDispositivo != null ? leituraDispositivo.getNivelPct() : 0)
                    .build();
        } else {
            throw new RuntimeException("Usuário não encontrado com o id: " + usuario.getIdUsuario());
        }
    }
}
