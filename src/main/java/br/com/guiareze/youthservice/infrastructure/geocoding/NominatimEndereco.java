package br.com.guiareze.youthservice.infrastructure.geocoding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NominatimEndereco(String city, String town, String municipality) {

    public String cidade() {
        if (city != null) {
            return city;
        }
        if (town != null) {
            return town;
        }
        return municipality;
    }
}
