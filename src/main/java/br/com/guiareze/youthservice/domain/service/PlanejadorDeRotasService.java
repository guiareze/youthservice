package br.com.guiareze.youthservice.domain.service;

import br.com.guiareze.youthservice.domain.model.Endereco;
import br.com.guiareze.youthservice.domain.model.GrupoDeRota;
import br.com.guiareze.youthservice.domain.model.PlanoDeRotas;
import br.com.guiareze.youthservice.domain.model.PontoDeVisita;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlanejadorDeRotasService {

    private static final double RAIO_TERRA_KM = 6371.0;

    public PlanoDeRotas planejar(Endereco partida, List<PontoDeVisita> pontosDeVisita, int quantidadeGrupos) {
        List<List<PontoDeVisita>> agrupamentos = agruparPorProximidade(pontosDeVisita, quantidadeGrupos);

        List<GrupoDeRota> grupos = new ArrayList<>();
        for (int i = 0; i < agrupamentos.size(); i++) {
            List<PontoDeVisita> rotaOrdenada = agrupamentos.get(i).stream()
                    .sorted(Comparator.comparingDouble((PontoDeVisita p) -> distanciaHaversine(partida, p.getEndereco())).reversed())
                    .toList();
            grupos.add(new GrupoDeRota(i + 1, rotaOrdenada));
        }
        return new PlanoDeRotas(grupos);
    }

    /**
     * Forma grupos de tamanho balanceado maximizando a proximidade real entre os endereços de um
     * mesmo grupo: cada grupo começa por uma "semente" (o ponto restante mais distante do centroide
     * dos pontos ainda não alocados, para espalhar os grupos entre si) e cresce sempre incorporando o
     * ponto restante mais próximo de algum ponto já pertencente ao grupo, até atingir o tamanho-alvo.
     * Agrupar apenas pelo ângulo em relação à partida (sweep) não garante isso: dois endereços podem
     * ter ângulos parecidos em relação a um ponto de partida distante e ainda assim estar a
     * quilômetros um do outro.
     */
    private List<List<PontoDeVisita>> agruparPorProximidade(List<PontoDeVisita> pontosDeVisita, int quantidadeGrupos) {
        List<PontoDeVisita> restantes = new ArrayList<>(pontosDeVisita);
        int total = restantes.size();
        int tamanhoBase = total / quantidadeGrupos;
        int resto = total % quantidadeGrupos;

        List<List<PontoDeVisita>> grupos = new ArrayList<>();
        for (int i = 0; i < quantidadeGrupos; i++) {
            int tamanhoGrupo = tamanhoBase + (i < resto ? 1 : 0);
            List<PontoDeVisita> grupo = new ArrayList<>();

            PontoDeVisita semente = pontoMaisDistanteDoCentroide(restantes);
            grupo.add(semente);
            restantes.remove(semente);

            while (grupo.size() < tamanhoGrupo && !restantes.isEmpty()) {
                PontoDeVisita maisProximo = pontoMaisProximoDoGrupo(grupo, restantes);
                grupo.add(maisProximo);
                restantes.remove(maisProximo);
            }
            grupos.add(grupo);
        }
        return grupos;
    }

    private PontoDeVisita pontoMaisDistanteDoCentroide(List<PontoDeVisita> pontos) {
        double centroideLat = pontos.stream().mapToDouble(p -> p.getEndereco().getLatitude()).average().orElseThrow();
        double centroideLon = pontos.stream().mapToDouble(p -> p.getEndereco().getLongitude()).average().orElseThrow();

        return pontos.stream()
                .max(Comparator.comparingDouble(p -> distanciaHaversine(
                        centroideLat, centroideLon, p.getEndereco().getLatitude(), p.getEndereco().getLongitude())))
                .orElseThrow();
    }

    private PontoDeVisita pontoMaisProximoDoGrupo(List<PontoDeVisita> grupo, List<PontoDeVisita> candidatos) {
        return candidatos.stream()
                .min(Comparator.comparingDouble(candidato -> distanciaMinimaAoGrupo(grupo, candidato)))
                .orElseThrow();
    }

    private double distanciaMinimaAoGrupo(List<PontoDeVisita> grupo, PontoDeVisita candidato) {
        return grupo.stream()
                .mapToDouble(p -> distanciaHaversine(p.getEndereco(), candidato.getEndereco()))
                .min()
                .orElseThrow();
    }

    private double distanciaHaversine(Endereco origem, Endereco destino) {
        return distanciaHaversine(origem.getLatitude(), origem.getLongitude(), destino.getLatitude(), destino.getLongitude());
    }

    private double distanciaHaversine(double lat1Graus, double lon1Graus, double lat2Graus, double lon2Graus) {
        double lat1 = Math.toRadians(lat1Graus);
        double lat2 = Math.toRadians(lat2Graus);
        double deltaLat = Math.toRadians(lat2Graus - lat1Graus);
        double deltaLon = Math.toRadians(lon2Graus - lon1Graus);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RAIO_TERRA_KM * c;
    }
}
