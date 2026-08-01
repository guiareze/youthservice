package br.com.guiareze.youthservice.infrastructure.config;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestClient;

@Configuration
public class GeocodingConfig {

    // Prototype: cada cliente HTTP recebe seu próprio builder, evitando estado mutável compartilhado
    // (baseUrl/headers) entre ViaCepClient e NominatimClient.
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
