package br.com.guiareze.youthservice.infrastructure.geocoding;

import br.com.guiareze.youthservice.application.port.GeocodificacaoEnderecoPort;
import br.com.guiareze.youthservice.domain.exception.EnderecoNaoGeocodificadoException;
import br.com.guiareze.youthservice.domain.model.Endereco;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GeocodificacaoEnderecoAdapter implements GeocodificacaoEnderecoPort {

    private static final Logger log = LoggerFactory.getLogger(GeocodificacaoEnderecoAdapter.class);

    private final ViaCepClient viaCepClient;
    private final NominatimClient nominatimClient;

    public GeocodificacaoEnderecoAdapter(ViaCepClient viaCepClient, NominatimClient nominatimClient) {
        this.viaCepClient = viaCepClient;
        this.nominatimClient = nominatimClient;
    }

    @Override
    public Endereco geocodificar(String cep, String numero, String identificador) {
        String descritor = "%s (CEP %s, nº %s)".formatted(identificador, cep, numero);

        ViaCepResponse enderecoViaCep = viaCepClient.consultar(cep, descritor);
        Coordenadas coordenadas = geocodificarComFallback(enderecoViaCep, numero, descritor);

        return Endereco.criar(normalizarCep(cep), numero, enderecoViaCep.logradouro(), enderecoViaCep.bairro(),
                enderecoViaCep.localidade(), enderecoViaCep.uf(), coordenadas.latitude(), coordenadas.longitude());
    }

    /**
     * Estratégia de busca em duas tentativas, cada uma validada contra a cidade do ViaCEP:
     * 1) rua + número + cidade (sem bairro): mais precisa, mas para nomes de rua genéricos/repetidos
     *    em mais de um município pode retornar um homônimo errado — por isso o resultado é validado
     *    contra a cidade esperada, e rejeitado (não apenas aceito silenciosamente) se não bater.
     * 2) rua + bairro + cidade (sem número): usada quando (1) falha ou é rejeitada. Incluir o bairro
     *    junto com o número costuma não retornar nenhum resultado no Nominatim, mas sem o número
     *    costuma funcionar e ajuda a desambiguar ruas homônimas (foi o que resolveu um caso real em
     *    que a tentativa (1) pegou uma rua de mesmo nome em outro município).
     */
    private Coordenadas geocodificarComFallback(ViaCepResponse enderecoViaCep, String numero, String descritor) {
        String cidade = enderecoViaCep.localidade();
        String enderecoComNumero = "%s, %s, %s, Brasil".formatted(enderecoViaCep.logradouro(), numero, cidade);
        try {
            return nominatimClient.geocodificar(enderecoComNumero, cidade, descritor);
        } catch (EnderecoNaoGeocodificadoException ex) {
            log.warn("Endereço com número não localizado (ou em cidade diferente) para '{}'; tentando com bairro, sem o número", descritor);
            String enderecoComBairro = "%s, %s, %s, Brasil".formatted(
                    enderecoViaCep.logradouro(), enderecoViaCep.bairro(), cidade);
            return nominatimClient.geocodificar(enderecoComBairro, cidade, descritor);
        }
    }

    private String normalizarCep(String cep) {
        return cep.replaceAll("\\D", "");
    }
}
