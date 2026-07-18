package br.com.guiareze.youthservice.infrastructure.mapper;

import br.com.guiareze.youthservice.domain.model.Pessoa;
import br.com.guiareze.youthservice.infrastructure.persistence.PessoaEntity;
import br.com.guiareze.youthservice.presentation.dto.CadastrarPessoaRequest;
import br.com.guiareze.youthservice.presentation.dto.PessoaResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PessoaMapper {

    PessoaEntity toEntity(Pessoa pessoa);

    PessoaResponse toResponse(Pessoa pessoa);

    default Pessoa toDomain(PessoaEntity entity) {
        return Pessoa.reconstituir(entity.getId(), entity.getNome(), entity.getIdade(), entity.getTelefone(), entity.getCriadoEm());
    }

    default Pessoa toDomain(CadastrarPessoaRequest request) {
        return Pessoa.criar(request.nome(), request.idade(), request.telefone());
    }
}
