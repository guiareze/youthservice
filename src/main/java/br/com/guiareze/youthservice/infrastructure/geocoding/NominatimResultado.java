package br.com.guiareze.youthservice.infrastructure.geocoding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NominatimResultado(String lat, String lon, NominatimEndereco address) {
}
