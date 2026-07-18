package br.com.guiareze.youthservice.infrastructure.persistence;

import br.com.guiareze.youthservice.application.port.PessoaRepository;
import br.com.guiareze.youthservice.domain.model.Pessoa;
import br.com.guiareze.youthservice.infrastructure.mapper.PessoaMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PessoaRepositoryAdapter implements PessoaRepository {

    private final PessoaJpaRepository jpaRepository;
    private final PessoaMapper mapper;

    public PessoaRepositoryAdapter(PessoaJpaRepository jpaRepository, PessoaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Pessoa salvar(Pessoa pessoa) {
        PessoaEntity entity = mapper.toEntity(pessoa);
        PessoaEntity salvo = jpaRepository.save(entity);
        return mapper.toDomain(salvo);
    }

    @Override
    public Optional<Pessoa> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Pessoa> listarTodas() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existePorNome(String nome) {
        return jpaRepository.existsByNome(nome);
    }
}
