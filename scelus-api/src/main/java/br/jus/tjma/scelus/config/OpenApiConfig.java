package br.jus.tjma.scelus.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Swagger/OpenAPI 3 para o scelus-api.
 *
 * <p>Autenticação via header {@code Seguranca-Token} (token Sentinela).
 * Para testar no Swagger UI, obtenha um token válido em:
 * {@code https://sistemasd.tjma.jus.br/sentinela/}
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "SentinelaToken";

    @Bean
    public OpenAPI scelusOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Scelus API — TJMA")
                        .description("""
                                API REST do Sistema Scelus do Tribunal de Justiça do Maranhão.

                                **Autenticação:** Centralizada no Sentinela (SSO TJMA).
                                Todas as requisições devem enviar o header `Seguranca-Token` com o token válido.

                                Obtenha um token em: https://sistemasd.tjma.jus.br/sentinela/
                                """)
                        .version("1.0.0-SNAPSHOT")
                        .contact(new Contact()
                                .name("DTI — TJMA")
                                .email("dti@tjma.jus.br")
                                .url("https://www.tjma.jus.br"))
                        .license(new License()
                                .name("Uso interno TJMA")
                                .url("https://www.tjma.jus.br")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name("Seguranca-Token")
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .description("Token de autenticação do Sentinela TJMA")));
    }
}
