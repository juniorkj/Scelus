package br.jus.tjma.scelus.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuração do DataSource de autenticação: <strong>sentinelaDS</strong> (Oracle).
 *
 * <p>Utilizado exclusivamente pela {@code infra-spring} para validação de tokens
 * via procedure {@code dba_sentinela.PKG_VALIDACAO.sp_login}.
 *
 * <p>As entidades gerenciadas aqui pertencem ao schema {@code DBA_SENTINELA} e são
 * de responsabilidade da infra — <strong>não criar entidades de domínio neste PU</strong>.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "br.jus.tjma.infraspring.seguranca.data",
        entityManagerFactoryRef = "sentinelaEntityManagerFactory",
        transactionManagerRef  = "sentinelaTransactionManager"
)
public class SentinelaDatasourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.sentinela")
    public DataSourceProperties sentinelaDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.sentinela.hikari")
    public DataSource sentinelaDataSource(
            @Qualifier("sentinelaDataSourceProperties") DataSourceProperties props) {
        return props.initializeDataSourceBuilder()
                    .type(HikariDataSource.class)
                    .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean sentinelaEntityManagerFactory(
            @Qualifier("sentinelaDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder) {
        Map<String, Object> jpaProps = new HashMap<>();
        jpaProps.put("hibernate.dialect",        "org.hibernate.dialect.OracleDialect");
        jpaProps.put("hibernate.default_schema", "DBA_SENTINELA");
        jpaProps.put("hibernate.show_sql",       "false");
        return builder
                .dataSource(dataSource)
                .packages("br.jus.tjma.infraspring.seguranca.data")
                .persistenceUnit("sentinelaPU")
                .properties(jpaProps)
                .build();
    }

    @Bean
    public PlatformTransactionManager sentinelaTransactionManager(
            @Qualifier("sentinelaEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
