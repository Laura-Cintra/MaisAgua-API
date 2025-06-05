package br.com.fiap.mais_agua.service;

import br.com.fiap.mais_agua.model.DTO.EnderecoDTO;
import br.com.fiap.mais_agua.model.Endereco;
import br.com.fiap.mais_agua.model.Unidade;
import br.com.fiap.mais_agua.model.Usuario;
import br.com.fiap.mais_agua.model.DTO.UnidadeReadDTO;
import br.com.fiap.mais_agua.model.DTO.UsuarioResponseDTO;
import br.com.fiap.mais_agua.repository.EnderecoRepository;
import br.com.fiap.mais_agua.repository.UnidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;
    private final UnidadeRepository unidadeRepository;

    @Autowired
    public EnderecoService(EnderecoRepository enderecoRepository, UnidadeRepository unidadeRepository) {
        this.enderecoRepository = enderecoRepository;
        this.unidadeRepository = unidadeRepository;
    }

    // Buscar todos os endereços para um usuário
    public List<EnderecoDTO> findByUsuario(Usuario usuario) {
        List<Endereco> enderecos = enderecoRepository.findByUnidadeUsuario(usuario);
        return enderecos.stream()
                .map(this::mapToEnderecoDTO)
                .toList();
    }

    // Buscar endereço por ID com relacionamentos
    @Transactional
    public EnderecoDTO getEnderecoComRelacionamentos(Integer id) {
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado"));

        return mapToEnderecoDTO(endereco);
    }

    // Criar novo endereço
    public EnderecoDTO createEndereco(Endereco endereco, Usuario usuario) {
        Unidade unidade = unidadeRepository.findById(endereco.getUnidade().getIdUnidade())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidade não encontrada"));

        if (!unidade.getUsuario().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para acessar esta unidade");
        }

        endereco.setUnidade(unidade);
        Endereco savedEndereco = enderecoRepository.save(endereco);
        return mapToEnderecoDTO(savedEndereco);
    }

    // Deletar endereço
    public void deleteEndereco(Integer id, Usuario usuario) {
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado"));

        if (!endereco.getUnidade().getUsuario().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para excluir este endereço");
        }

        enderecoRepository.delete(endereco);
    }

    // Atualizar endereço
    public EnderecoDTO updateEndereco(Integer id, Endereco endereco, Usuario usuario) {
        Endereco oldEndereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado"));

        if (!oldEndereco.getUnidade().getUsuario().equals(usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para atualizar este endereço");
        }

        oldEndereco.setLogradouro(endereco.getLogradouro());
        oldEndereco.setNumero(endereco.getNumero());
        oldEndereco.setComplemento(endereco.getComplemento());
        oldEndereco.setCep(endereco.getCep());

        Endereco savedEndereco = enderecoRepository.save(oldEndereco);
        return mapToEnderecoDTO(savedEndereco);
    }

    // Método auxiliar para mapear o Endereco para EnderecoDTO
    private EnderecoDTO mapToEnderecoDTO(Endereco endereco) {
        String pais = endereco.getCidade() != null && endereco.getCidade().getEstado() != null
                ? endereco.getCidade().getEstado().getPais().getNome()
                : null;

        String estado = endereco.getCidade() != null && endereco.getCidade().getEstado() != null
                ? endereco.getCidade().getEstado().getNome()
                : null;

        String cidade = endereco.getCidade() != null ? endereco.getCidade().getNome() : null;

        UnidadeReadDTO unidadeReadDTO = new UnidadeReadDTO(
                endereco.getUnidade().getIdUnidade(),
                endereco.getUnidade().getNome(),
                endereco.getUnidade().getCapacidadeTotalLitros(),
                endereco.getUnidade().getDataCadastro().toString(),
                new UsuarioResponseDTO(
                        endereco.getUnidade().getUsuario().getIdUsuario(),
                        endereco.getUnidade().getUsuario().getNome(),
                        endereco.getUnidade().getUsuario().getEmail()
                )
        );

        return new EnderecoDTO(
                endereco.getId(),
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getCep(),
                pais,
                estado,
                cidade,
                unidadeReadDTO
        );
    }
}
