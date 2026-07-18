package br.com.guiareze.youthservice.application.port;

import br.com.guiareze.youthservice.domain.model.Pessoa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PessoaRepository {

    Pessoa salvar(Pessoa pessoa);

    Optional<Pessoa> buscarPorId(UUID id);

    List<Pessoa> listarTodas();

    boolean existePorNome(String nome);
}
