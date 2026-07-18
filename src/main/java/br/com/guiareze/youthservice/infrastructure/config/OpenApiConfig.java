package br.com.guiareze.youthservice.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Youth Service API",
                version = "1.0.0",
                description = "API de cadastro de pessoas"
        )
)
public class OpenApiConfig {
}
