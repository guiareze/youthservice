package br.com.guiareze.youthservice.infrastructure.mapper;

import br.com.guiareze.youthservice.domain.model.Pessoa;
import br.com.guiareze.youthservice.infrastructure.persistence.PessoaEntity;
import br.com.guiareze.youthservice.presentation.dto.CadastrarPessoaRequest;
import br.com.guiareze.youthservice.presentation.dto.PessoaResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PessoaMapperTest {

    private final PessoaMapper mapper = new PessoaMapperImpl();

    @Test
    void deveConverterPessoaParaEntity() {
        Pessoa pessoa = Pessoa.criar("Maria da Silva", 30, "+5511952526969");

        PessoaEntity entity = mapper.toEntity(pessoa);

        assertThat(entity.getId()).isEqualTo(pessoa.getId());
        assertThat(entity.getNome()).isEqualTo("Maria da Silva");
        assertThat(entity.getIdade()).isEqualTo(30);
        assertThat(entity.getTelefone()).isEqualTo("5511952526969");
        assertThat(entity.getCriadoEm()).isEqualTo(pessoa.getCriadoEm());
    }

    @Test
    void deveConverterEntityParaPessoa() {
        var id = java.util.UUID.randomUUID();
        var criadoEm = java.time.Instant.now();
        PessoaEntity entity = new PessoaEntity(id, "Maria da Silva", 30, "5511952526969", criadoEm);

        Pessoa pessoa = mapper.toDomain(entity);

        assertThat(pessoa.getId()).isEqualTo(id);
        assertThat(pessoa.getNome()).isEqualTo("Maria da Silva");
        assertThat(pessoa.getTelefone()).isEqualTo("5511952526969");
    }

    @Test
    void deveConverterRequestParaPessoa() {
        CadastrarPessoaRequest request = new CadastrarPessoaRequest("Maria da Silva", 30, "+5511952526969");

        Pessoa pessoa = mapper.toDomain(request);

        assertThat(pessoa.getNome()).isEqualTo("Maria da Silva");
        assertThat(pessoa.getTelefone()).isEqualTo("5511952526969");
    }

    @Test
    void deveConverterPessoaParaResponse() {
        Pessoa pessoa = Pessoa.criar("Maria da Silva", 30, "+5511952526969");

        PessoaResponse response = mapper.toResponse(pessoa);

        assertThat(response.id()).isEqualTo(pessoa.getId());
        assertThat(response.nome()).isEqualTo("Maria da Silva");
        assertThat(response.idade()).isEqualTo(30);
        assertThat(response.telefone()).isEqualTo("5511952526969");
    }
}
