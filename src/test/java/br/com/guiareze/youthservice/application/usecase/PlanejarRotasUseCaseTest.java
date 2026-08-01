package br.com.guiareze.youthservice.application.usecase;

import br.com.guiareze.youthservice.application.port.GeocodificacaoEnderecoPort;
import br.com.guiareze.youthservice.domain.exception.EnderecoNaoGeocodificadoException;
import br.com.guiareze.youthservice.domain.exception.QuantidadeGruposInvalidaException;
import br.com.guiareze.youthservice.domain.model.Endereco;
import br.com.guiareze.youthservice.domain.model.PlanoDeRotas;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanejarRotasUseCaseTest {

    @Mock
    private GeocodificacaoEnderecoPort geocodificacaoEnderecoPort;

    private static Endereco enderecoEm(double latitude, double longitude) {
        return Endereco.criar("01310100", "100", "Rua Teste", "Bairro Teste", "Cidade Teste", "SP", latitude, longitude);
    }

    @Test
    void devePlanejarRotasComSucesso() {
        PlanejarRotasUseCase useCase = new PlanejarRotasUseCase(geocodificacaoEnderecoPort);
        EnderecoEntrada partida = new EnderecoEntrada("01310100", "1000");
        List<PontoDeVisitaEntrada> enderecos = List.of(
                new PontoDeVisitaEntrada("Maria", "01310100", "100"),
                new PontoDeVisitaEntrada("João", "01310100", "200"));

        when(geocodificacaoEnderecoPort.geocodificar(eq("01310100"), eq("1000"), anyString())).thenReturn(enderecoEm(0, 0));
        when(geocodificacaoEnderecoPort.geocodificar(eq("01310100"), eq("100"), anyString())).thenReturn(enderecoEm(0.01, 0.01));
        when(geocodificacaoEnderecoPort.geocodificar(eq("01310100"), eq("200"), anyString())).thenReturn(enderecoEm(0.02, 0.02));

        PlanoDeRotas plano = useCase.executar(partida, enderecos, 1);

        assertThat(plano.getGrupos()).hasSize(1);
        assertThat(plano.getGrupos().get(0).getPontosDeVisita()).hasSize(2);
    }

    @Test
    void deveRejeitarQuandoQuantidadeDeGruposForZeroOuNegativa() {
        PlanejarRotasUseCase useCase = new PlanejarRotasUseCase(geocodificacaoEnderecoPort);
        EnderecoEntrada partida = new EnderecoEntrada("01310100", "1000");
        List<PontoDeVisitaEntrada> enderecos = List.of(new PontoDeVisitaEntrada("Maria", "01310100", "100"));

        assertThatThrownBy(() -> useCase.executar(partida, enderecos, 0))
                .isInstanceOf(QuantidadeGruposInvalidaException.class);
    }

    @Test
    void deveRejeitarQuandoQuantidadeDeGruposExcederQuantidadeDeEnderecos() {
        PlanejarRotasUseCase useCase = new PlanejarRotasUseCase(geocodificacaoEnderecoPort);
        EnderecoEntrada partida = new EnderecoEntrada("01310100", "1000");
        List<PontoDeVisitaEntrada> enderecos = List.of(new PontoDeVisitaEntrada("Maria", "01310100", "100"));

        assertThatThrownBy(() -> useCase.executar(partida, enderecos, 2))
                .isInstanceOf(QuantidadeGruposInvalidaException.class);
    }

    @Test
    void deveRejeitarQuandoListaDeEnderecosForVazia() {
        PlanejarRotasUseCase useCase = new PlanejarRotasUseCase(geocodificacaoEnderecoPort);
        EnderecoEntrada partida = new EnderecoEntrada("01310100", "1000");

        assertThatThrownBy(() -> useCase.executar(partida, List.of(), 1))
                .isInstanceOf(QuantidadeGruposInvalidaException.class);
    }

    @Test
    void deveAgregarFalhaDeGeocodificacaoDeUmUnicoEndereco() {
        PlanejarRotasUseCase useCase = new PlanejarRotasUseCase(geocodificacaoEnderecoPort);
        EnderecoEntrada partida = new EnderecoEntrada("01310100", "1000");
        List<PontoDeVisitaEntrada> enderecos = List.of(new PontoDeVisitaEntrada("Maria", "00000000", "100"));

        when(geocodificacaoEnderecoPort.geocodificar(eq("01310100"), eq("1000"), anyString())).thenReturn(enderecoEm(0, 0));
        when(geocodificacaoEnderecoPort.geocodificar(eq("00000000"), eq("100"), anyString()))
                .thenThrow(new EnderecoNaoGeocodificadoException("Maria (CEP 00000000, nº 100)"));

        assertThatThrownBy(() -> useCase.executar(partida, enderecos, 1))
                .isInstanceOf(EnderecoNaoGeocodificadoException.class);
    }

    @Test
    void deveAgregarFalhasDeGeocodificacaoDeMultiplosEnderecos() {
        PlanejarRotasUseCase useCase = new PlanejarRotasUseCase(geocodificacaoEnderecoPort);
        EnderecoEntrada partida = new EnderecoEntrada("00000000", "1000");
        List<PontoDeVisitaEntrada> enderecos = List.of(
                new PontoDeVisitaEntrada("Maria", "00000001", "100"),
                new PontoDeVisitaEntrada("João", "01310100", "200"));

        when(geocodificacaoEnderecoPort.geocodificar(eq("00000000"), eq("1000"), anyString()))
                .thenThrow(new EnderecoNaoGeocodificadoException("Ponto de partida (CEP 00000000, nº 1000)"));
        when(geocodificacaoEnderecoPort.geocodificar(eq("00000001"), eq("100"), anyString()))
                .thenThrow(new EnderecoNaoGeocodificadoException("Maria (CEP 00000001, nº 100)"));
        when(geocodificacaoEnderecoPort.geocodificar(eq("01310100"), eq("200"), anyString())).thenReturn(enderecoEm(0.01, 0.01));

        assertThatThrownBy(() -> useCase.executar(partida, enderecos, 1))
                .isInstanceOf(EnderecoNaoGeocodificadoException.class)
                .satisfies(ex -> assertThat(((EnderecoNaoGeocodificadoException) ex).getEnderecosComFalha()).hasSize(2));
    }
}
