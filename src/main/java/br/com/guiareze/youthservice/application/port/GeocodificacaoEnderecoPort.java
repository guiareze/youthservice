package br.com.guiareze.youthservice.application.port;

import br.com.guiareze.youthservice.domain.exception.EnderecoNaoGeocodificadoException;
import br.com.guiareze.youthservice.domain.model.Endereco;

public interface GeocodificacaoEnderecoPort {

    /**
     * @param identificador rótulo usado para identificar o endereço em mensagens de erro
     *                       (ex: nome do morador, ou "Ponto de partida")
     * @throws EnderecoNaoGeocodificadoException se o CEP for inválido ou o endereço não puder ser localizado
     */
    Endereco geocodificar(String cep, String numero, String identificador);
}
