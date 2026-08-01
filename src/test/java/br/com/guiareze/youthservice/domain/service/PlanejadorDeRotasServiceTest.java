package br.com.guiareze.youthservice.domain.service;

import br.com.guiareze.youthservice.domain.model.Endereco;
import br.com.guiareze.youthservice.domain.model.GrupoDeRota;
import br.com.guiareze.youthservice.domain.model.PlanoDeRotas;
import br.com.guiareze.youthservice.domain.model.PontoDeVisita;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PlanejadorDeRotasServiceTest {

    private final PlanejadorDeRotasService service = new PlanejadorDeRotasService();

    private static Endereco enderecoEm(double latitude, double longitude) {
        return Endereco.criar("01310100", "100", "Rua Teste", "Bairro Teste", "Cidade Teste", "SP", latitude, longitude);
    }

    private static PontoDeVisita pontoEm(String nome, double latitude, double longitude) {
        return new PontoDeVisita(nome, enderecoEm(latitude, longitude));
    }

    @Test
    void deveDividirEmGruposDeTamanhoIgualQuandoDivisaoEExata() {
        Endereco partida = enderecoEm(0, 0);
        List<PontoDeVisita> pontos = List.of(
                pontoEm("P1", 0.01, 0.01), pontoEm("P2", 0.02, 0.02),
                pontoEm("P3", 0.01, -0.01), pontoEm("P4", 0.02, -0.02),
                pontoEm("P5", -0.01, -0.01), pontoEm("P6", -0.02, -0.02),
                pontoEm("P7", -0.01, 0.01), pontoEm("P8", -0.02, 0.02));

        PlanoDeRotas plano = service.planejar(partida, pontos, 4);

        assertThat(plano.getGrupos()).hasSize(4);
        assertThat(plano.getGrupos()).allSatisfy(grupo -> assertThat(grupo.getPontosDeVisita()).hasSize(2));
    }

    @Test
    void deveDistribuirRestoQuandoDivisaoNaoEExata() {
        Endereco partida = enderecoEm(0, 0);
        List<PontoDeVisita> pontos = List.of(
                pontoEm("P1", 0.01, 0.01), pontoEm("P2", 0.02, 0.02), pontoEm("P3", 0.03, 0.03),
                pontoEm("P4", 0.01, -0.01), pontoEm("P5", 0.02, -0.02), pontoEm("P6", 0.03, -0.03),
                pontoEm("P7", -0.01, -0.01), pontoEm("P8", -0.02, -0.02),
                pontoEm("P9", -0.01, 0.01), pontoEm("P10", -0.02, 0.02));

        PlanoDeRotas plano = service.planejar(partida, pontos, 4);

        assertThat(plano.getGrupos()).hasSize(4);
        int total = plano.getGrupos().stream().mapToInt(g -> g.getPontosDeVisita().size()).sum();
        assertThat(total).isEqualTo(10);
        assertThat(plano.getGrupos()).allSatisfy(grupo ->
                assertThat(grupo.getPontosDeVisita().size()).isBetween(2, 3));
    }

    @Test
    void deveAgruparPorProximidadeRealMesmoQuandoAngulosEmRelacaoAPartidaSaoParecidos() {
        // Coordenadas reais de um caso em produção: dois pares de endereços claramente próximos entre
        // si (Nakamura+MorroDoIndio a 1,4km; Abdon+Cassia a 1,3km), mas com ângulos parecidos em relação
        // a um ponto de partida distante — um sweep por ângulo os agrupa errado (mistura os pares).
        Endereco partida = enderecoEm(-23.5726102, -46.8100187);
        PontoDeVisita nakamura = pontoEm("Nakamura", -23.7008494, -46.7701908);
        PontoDeVisita morroDoIndio = pontoEm("MorroDoIndio", -23.6894715, -46.7760747);
        PontoDeVisita abdon = pontoEm("Abdon", -23.6815034, -46.7550870);
        PontoDeVisita cassia = pontoEm("Cassia", -23.6750962, -46.7444707);

        PlanoDeRotas plano = service.planejar(partida, List.of(nakamura, morroDoIndio, abdon, cassia), 2);

        assertThat(plano.getGrupos()).hasSize(2);
        Set<String> grupo1 = Set.copyOf(plano.getGrupos().get(0).getPontosDeVisita().stream()
                .map(PontoDeVisita::getNomeMorador).toList());
        Set<String> grupo2 = Set.copyOf(plano.getGrupos().get(1).getPontosDeVisita().stream()
                .map(PontoDeVisita::getNomeMorador).toList());

        Set<String> parNakamuraMorroDoIndio = Set.of("Nakamura", "MorroDoIndio");
        Set<String> parAbdonCassia = Set.of("Abdon", "Cassia");
        assertThat(Set.of(grupo1, grupo2)).containsExactlyInAnyOrder(parNakamuraMorroDoIndio, parAbdonCassia);
    }

    @Test
    void deveRetornarUmUnicoGrupoComTodosOsPontosQuandoQuantidadeDeGruposForUm() {
        Endereco partida = enderecoEm(0, 0);
        List<PontoDeVisita> pontos = List.of(
                pontoEm("Perto", 0.01, 0.01),
                pontoEm("Longe", 0.05, 0.05),
                pontoEm("Meio", 0.03, 0.03));

        PlanoDeRotas plano = service.planejar(partida, pontos, 1);

        assertThat(plano.getGrupos()).hasSize(1);
        GrupoDeRota grupo = plano.getGrupos().get(0);
        assertThat(grupo.getPontosDeVisita()).hasSize(3);
        assertThat(grupo.getPontosDeVisita().stream().map(PontoDeVisita::getNomeMorador).toList())
                .containsExactly("Longe", "Meio", "Perto");
    }

    @Test
    void deveCriarUmGrupoParaCadaEnderecoQuandoQuantidadeDeGruposIgualaQuantidadeDeEnderecos() {
        Endereco partida = enderecoEm(0, 0);
        List<PontoDeVisita> pontos = List.of(
                pontoEm("P1", 0.01, 0.01), pontoEm("P2", 0.02, -0.02), pontoEm("P3", -0.01, 0.02));

        PlanoDeRotas plano = service.planejar(partida, pontos, 3);

        assertThat(plano.getGrupos()).hasSize(3);
        assertThat(plano.getGrupos()).allSatisfy(grupo -> assertThat(grupo.getPontosDeVisita()).hasSize(1));
    }

    @Test
    void deveOrdenarRotaDoMaisDistanteParaOMaisProximoDoPontoDePartida() {
        Endereco partida = enderecoEm(0, 0);
        List<PontoDeVisita> pontos = List.of(
                pontoEm("A", 0.01, 0.01),
                pontoEm("B", 0.04, 0.04),
                pontoEm("C", 0.02, 0.02));

        PlanoDeRotas plano = service.planejar(partida, pontos, 1);

        List<String> ordemVisita = plano.getGrupos().get(0).getPontosDeVisita().stream()
                .map(PontoDeVisita::getNomeMorador)
                .toList();
        assertThat(ordemVisita).containsExactly("B", "C", "A");
    }
}
