package br.com.guiareze.youthservice.application.usecase;

import br.com.guiareze.youthservice.application.port.GeocodificacaoEnderecoPort;
import br.com.guiareze.youthservice.domain.exception.EnderecoNaoGeocodificadoException;
import br.com.guiareze.youthservice.domain.exception.QuantidadeGruposInvalidaException;
import br.com.guiareze.youthservice.domain.model.Endereco;
import br.com.guiareze.youthservice.domain.model.PlanoDeRotas;
import br.com.guiareze.youthservice.domain.model.PontoDeVisita;
import br.com.guiareze.youthservice.domain.service.PlanejadorDeRotasService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlanejarRotasUseCase {

    private final GeocodificacaoEnderecoPort geocodificacaoEnderecoPort;
    private final PlanejadorDeRotasService planejadorDeRotasService = new PlanejadorDeRotasService();

    public PlanejarRotasUseCase(GeocodificacaoEnderecoPort geocodificacaoEnderecoPort) {
        this.geocodificacaoEnderecoPort = geocodificacaoEnderecoPort;
    }

    public PlanoDeRotas executar(EnderecoEntrada partida, List<PontoDeVisitaEntrada> enderecosVisitar, int quantidadeGrupos) {
        validarQuantidadeGrupos(quantidadeGrupos, enderecosVisitar.size());

        List<String> falhas = new ArrayList<>();
        Endereco enderecoPartida = geocodificar(partida.cep(), partida.numero(), "Ponto de partida", falhas);

        List<PontoDeVisita> pontosDeVisita = new ArrayList<>();
        for (PontoDeVisitaEntrada entrada : enderecosVisitar) {
            Endereco endereco = geocodificar(entrada.cep(), entrada.numero(), entrada.nomeMorador(), falhas);
            if (endereco != null) {
                pontosDeVisita.add(new PontoDeVisita(entrada.nomeMorador(), endereco));
            }
        }

        if (!falhas.isEmpty()) {
            throw new EnderecoNaoGeocodificadoException(falhas);
        }

        return planejadorDeRotasService.planejar(enderecoPartida, pontosDeVisita, quantidadeGrupos);
    }

    private Endereco geocodificar(String cep, String numero, String identificador, List<String> falhas) {
        try {
            return geocodificacaoEnderecoPort.geocodificar(cep, numero, identificador);
        } catch (EnderecoNaoGeocodificadoException ex) {
            falhas.addAll(ex.getEnderecosComFalha());
            return null;
        }
    }

    private void validarQuantidadeGrupos(int quantidadeGrupos, int quantidadeEnderecos) {
        if (quantidadeEnderecos == 0) {
            throw QuantidadeGruposInvalidaException.listaEnderecosVazia();
        }
        if (quantidadeGrupos <= 0) {
            throw QuantidadeGruposInvalidaException.quantidadeNaoPositiva();
        }
        if (quantidadeGrupos > quantidadeEnderecos) {
            throw QuantidadeGruposInvalidaException.excedeQuantidadeEnderecos(quantidadeGrupos, quantidadeEnderecos);
        }
    }
}
