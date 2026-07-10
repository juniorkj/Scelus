package br.jus.tjma.scelus.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuração do DataSource principal: <strong>postgresDS</strong> (PostgreSQL).
 *
 * <p>Este datasource é o primário da aplicação e gerencia todas as entidades do domínio
 * do Scelus no schema {@code dba_scelus}.
 *
 * <p>Nos Controllers/Services, use {@code @Qualifier("postgresEntityManagerFactory")} para
 * garantir a utilização deste datasource.
 *
 * <p>Ao criar um novo módulo de domínio, adicione o pacote em {@code basePackages} abaixo
 * (aqui e no bean {@code postgresEntityManagerFactory}) para que os repositórios JPA sejam
 * registrados neste persistence unit.
 */
@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties(JpaProperties.class)
@EnableJpaRepositories(
        basePackages = {
                "br.jus.tjma.scelus.comum"
        },
        entityManagerFactoryRef = "postgresEntityManagerFactory",
        transactionManagerRef  = "postgresTransactionManager"
)
public class PostgresDatasourceConfig {

    @Bean
    @Primary
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder(JpaProperties jpaProperties) {
        return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), ds -> jpaProperties.getProperties(), null);
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.postgres")
    public DataSourceProperties postgresDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.postgres.hikari")
    public DataSource postgresDataSource(
            @Qualifier("postgresDataSourceProperties") DataSourceProperties props) {
        return props.initializeDataSourceBuilder()
                    .type(HikariDataSource.class)
                    .build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean postgresEntityManagerFactory(
            @Qualifier("postgresDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder) {
        Map<String, Object> jpaProps = new HashMap<>();
        jpaProps.put("hibernate.dialect",                   "org.hibernate.dialect.PostgreSQLDialect");
        jpaProps.put("hibernate.default_schema",            "dba_scelus");
        jpaProps.put("hibernate.show_sql",                  "false");
        jpaProps.put("hibernate.format_sql",                "true");
        jpaProps.put("hibernate.physical_naming_strategy",
                "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");

        return builder
                .dataSource(dataSource)
                .packages(
                        "br.jus.tjma.scelus.comum"
                )
                .persistenceUnit("postgresPU")
                .properties(jpaProps)
                .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager postgresTransactionManager(
            @Qualifier("postgresEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
