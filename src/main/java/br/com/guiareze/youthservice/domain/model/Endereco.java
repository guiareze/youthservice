package br.com.guiareze.youthservice.domain.model;

public final class Endereco {

    private final String cep;
    private final String numero;
    private final String logradouro;
    private final String bairro;
    private final String cidade;
    private final String uf;
    private final double latitude;
    private final double longitude;

    private Endereco(String cep, String numero, String logradouro, String bairro,
                      String cidade, String uf, double latitude, double longitude) {
        validarCep(cep);
        validarObrigatorio(numero, "número");
        validarObrigatorio(logradouro, "logradouro");
        validarObrigatorio(bairro, "bairro");
        validarObrigatorio(cidade, "cidade");
        validarObrigatorio(uf, "UF");
        validarCoordenadas(latitude, longitude);
        this.cep = cep;
        this.numero = numero;
        this.logradouro = logradouro;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static Endereco criar(String cepNormalizado, String numero, String logradouro, String bairro,
                                  String cidade, String uf, double latitude, double longitude) {
        return new Endereco(cepNormalizado, numero, logradouro, bairro, cidade, uf, latitude, longitude);
    }

    private static void validarCep(String cep) {
        if (cep == null || !cep.matches("\\d{8}")) {
            throw new IllegalArgumentException("O CEP deve conter 8 dígitos numéricos");
        }
    }

    private static void validarObrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("O campo '%s' é obrigatório".formatted(campo));
        }
    }

    private static void validarCoordenadas(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Coordenadas geográficas inválidas");
        }
    }

    public String getCep() {
        return cep;
    }

    public String getNumero() {
        return numero;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public String getUf() {
        return uf;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
