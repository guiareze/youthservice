package br.com.guiareze.youthservice.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pessoas")
public class PessoaEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private Integer idade;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    protected PessoaEntity() {
    }

    public PessoaEntity(UUID id, String nome, Integer idade, String telefone, Instant criadoEm) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.telefone = telefone;
        this.criadoEm = criadoEm;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Instant criadoEm) {
        this.criadoEm = criadoEm;
    }
}
