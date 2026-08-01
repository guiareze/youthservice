package br.com.guiareze.youthservice.infrastructure.mapper;

import br.com.guiareze.youthservice.application.usecase.EnderecoEntrada;
import br.com.guiareze.youthservice.application.usecase.PontoDeVisitaEntrada;
import br.com.guiareze.youthservice.domain.model.Endereco;
import br.com.guiareze.youthservice.domain.model.GrupoDeRota;
import br.com.guiareze.youthservice.domain.model.PlanoDeRotas;
import br.com.guiareze.youthservice.domain.model.PontoDeVisita;
import br.com.guiareze.youthservice.presentation.dto.EnderecoPartidaRequest;
import br.com.guiareze.youthservice.presentation.dto.EnderecoVisitaRequest;
import br.com.guiareze.youthservice.presentation.dto.GrupoDeRotaResponse;
import br.com.guiareze.youthservice.presentation.dto.PlanoDeRotasResponse;
import br.com.guiareze.youthservice.presentation.dto.PontoDeVisitaResponse;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PlanoDeRotasMapper {

    EnderecoEntrada toEntrada(EnderecoPartidaRequest request);

    PontoDeVisitaEntrada toEntrada(EnderecoVisitaRequest request);

    List<PontoDeVisitaEntrada> toEntrada(List<EnderecoVisitaRequest> requests);

    default PlanoDeRotasResponse toResponse(PlanoDeRotas planoDeRotas) {
        List<GrupoDeRotaResponse> grupos = planoDeRotas.getGrupos().stream()
                .map(this::toResponse)
                .toList();
        return new PlanoDeRotasResponse(grupos);
    }

    default GrupoDeRotaResponse toResponse(GrupoDeRota grupo) {
        List<PontoDeVisita> pontos = grupo.getPontosDeVisita();
        List<PontoDeVisitaResponse> enderecos = new ArrayList<>();
        for (int i = 0; i < pontos.size(); i++) {
            enderecos.add(toResponse(pontos.get(i), i + 1));
        }
        return new GrupoDeRotaResponse(grupo.getNumero(), enderecos);
    }

    default PontoDeVisitaResponse toResponse(PontoDeVisita ponto, int ordemVisita) {
        Endereco endereco = ponto.getEndereco();
        String enderecoCompleto = "%s, %s - %s, %s/%s, CEP %s".formatted(
                endereco.getLogradouro(), endereco.getNumero(), endereco.getBairro(),
                endereco.getCidade(), endereco.getUf(), formatarCep(endereco.getCep()));
        return new PontoDeVisitaResponse(ponto.getNomeMorador(), enderecoCompleto, ordemVisita);
    }

    private String formatarCep(String cep) {
        return cep.substring(0, 5) + "-" + cep.substring(5);
    }
}
