package br.com.fiap.mais_agua.repository;

import br.com.fiap.mais_agua.model.Endereco;
import br.com.fiap.mais_agua.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnderecoRepository extends JpaRepository<Endereco, Integer> {
    List<Endereco> findByUnidadeUsuario(Usuario usuario);
}
