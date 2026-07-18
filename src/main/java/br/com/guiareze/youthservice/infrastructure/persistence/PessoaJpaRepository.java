package br.com.guiareze.youthservice.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PessoaJpaRepository extends JpaRepository<PessoaEntity, UUID> {

    boolean existsByNome(String nome);
}
