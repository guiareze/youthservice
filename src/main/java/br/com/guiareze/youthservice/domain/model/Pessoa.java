package br.com.guiareze.youthservice.domain.model;

import br.com.guiareze.youthservice.domain.exception.PessoaInvalidaException;

import java.time.Instant;
import java.util.UUID;
import java.util.regex.Pattern;

public final class Pessoa {

    private static final Pattern TELEFONE_E164 = Pattern.compile("^\\+[1-9]\\d{1,14}$");

    private final UUID id;
    private final String nome;
    private final Integer idade;
    private final String telefone;
    private final Instant criadoEm;

    private Pessoa(UUID id, String nome, Integer idade, String telefone, Instant criadoEm) {
        validarNome(nome);
        validarIdade(idade);
        validarTelefoneNormalizado(telefone);
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.telefone = telefone;
        this.criadoEm = criadoEm;
    }

    public static Pessoa criar(String nome, Integer idade, String telefoneE164) {
        validarFormatoE164(telefoneE164);
        String telefoneNormalizado = normalizarTelefone(telefoneE164);
        return new Pessoa(UUID.randomUUID(), nome, idade, telefoneNormalizado, Instant.now());
    }

    public static Pessoa reconstituir(UUID id, String nome, Integer idade, String telefoneNormalizado, Instant criadoEm) {
        return new Pessoa(id, nome, idade, telefoneNormalizado, criadoEm);
    }

    private static void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new PessoaInvalidaException("O nome é obrigatório");
        }
    }

    private static void validarIdade(Integer idade) {
        if (idade == null) {
            throw new PessoaInvalidaException("A idade é obrigatória");
        }
        if (idade == 0) {
            throw new PessoaInvalidaException("A idade não pode ser zero");
        }
    }

    private static void validarFormatoE164(String telefone) {
        if (telefone == null || !TELEFONE_E164.matcher(telefone).matches()) {
            throw new PessoaInvalidaException("O telefone deve seguir o formato internacional E.164, ex: +5511952526969");
        }
    }

    private static void validarTelefoneNormalizado(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            throw new PessoaInvalidaException("O telefone é obrigatório");
        }
    }

    private static String normalizarTelefone(String telefoneE164) {
        return telefoneE164.substring(1);
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Integer getIdade() {
        return idade;
    }

    public String getTelefone() {
        return telefone;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
