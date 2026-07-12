package br.jus.tjma.scelus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Ponto de entrada da API REST do Sistema Scelus do TJMA.
 *
 * <p>Autenticação centralizada no <strong>Sentinela</strong>
 * — gerenciado automaticamente pela {@code infra-spring} via {@code SentinelaServletFilter}.
 *
 * <p>Datasources configurados:
 * <ul>
 *   <li>postgresDS  — PostgreSQL (dados de domínio do Scelus, schema dba_scelus)</li>
 *   <li>sentinelaDS — Oracle (autenticação / grupos)</li>
 * </ul>
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
// infra-spring: scan seguranca.* excluindo WebConfig (depende de RestRequestFilter não disponível)
@ComponentScan(
        basePackages = {"br.jus.tjma.scelus", "br.jus.tjma.infraspring.seguranca"},
        excludeFilters = {
            @ComponentScan.Filter(
                    type = FilterType.ASSIGNABLE_TYPE,
                    classes = br.jus.tjma.infraspring.seguranca.WebConfig.class),
            @ComponentScan.Filter(
                    type = FilterType.ASSIGNABLE_TYPE,
                    classes = br.jus.tjma.infraspring.seguranca.inject.UsuarioContextProducer.class)
        })
// Importa explicitamente os beans do infra-spring que estão fora do pacote seguranca:
//   - ServletConfig: registra LoginSentinelaServlet
//   - SentinelaConfig: registra o SeguranaServletFilter
//   - UsuarioContextService: valida tokens (usa seguranca.authz.* internamente)
//   - ClienteSentinela: cliente HTTP do Sentinela SSO
@Import({
    br.jus.tjma.infraspring.config.ServletConfig.class,
    br.jus.tjma.infraspring.sentinela.SentinelaConfig.class,
    br.jus.tjma.infraspring.sentinela.authz.UsuarioContextService.class,
    br.jus.tjma.infraspring.sentinela.authz.Ambiente.class,
    br.jus.tjma.infraspring.sentinela.authz.SentinelaInterceptor.class,
    br.jus.tjma.infraspring.sentinela.impl.ClienteSentinela.class
})
public class ScelusApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScelusApiApplication.class, args);
    }
}
