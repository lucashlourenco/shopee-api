// src/main/java/br/com/ifpe/shopee/config/DBPrincipalConfig.java

package br.com.ifpe.shopee.config;

import java.util.HashMap;
import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
    basePackages = "br.com.ifpe.shopee.model.bd_principal.repository",
    entityManagerFactoryRef = "principalEntityManagerFactory",
    transactionManagerRef = "principalTransactionManager"
)
public class DBPrincipalConfig {

    // Pacote onde o Spring deve escanear as classes de Entidade (@Entity) do BD
    // principal da aplicação
    private static final String PACOTE_ENTITY = "br.com.ifpe.shopee.model.bd_principal.entity";
    // private static final String PACOTE_BLOBSTORE = "br.com.ifpe.shopee.model.bd_blobstore";
    // private static final String PACOTE_UTIL = "br.com.ifpe.shopee.util.entity.bd_relacional";
    // private static final String PACOTE_ABSTRATO = "br.com.ifpe.shopee.model.abstrato";

    // Injetando o valor explicitamente do application.properties
    @Value("${bd-principal.datasource.url}")
    private String dbUrl;

    @Value("${bd-principal.datasource.username}")
    private String dbUsername;

    @Value("${bd-principal.datasource.password}")
    private String dbPassword;

    @Value("${bd-principal.datasource.driver-class-name}")
    private String dbDriverClassName;

    @Value("${bd-principal.jpa.hibernate.ddl-auto}")
    private String hibernateDdlAuto;

    @Value("${bd-principal.jpa.properties.hibernate.dialect}")
    private String hibernateDialect;

    // Injetando o nome do Pool
    @Value("${bd-principal.datasource.pool-name}")
    private String dbPoolName;

    // Conexão Física com o Banco de Dados
    @Bean(name = "principalDataSource")
    public DataSource principalDataSource() {

        // Cria a configuração do Hikari
        HikariConfig config = new HikariConfig();

        // Define o nome do Pool
        config.setPoolName(dbPoolName);

        // Define as propriedades do DataSource
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setDriverClassName(dbDriverClassName);

        // Retorna o HikariDataSource
        return new HikariDataSource(config);
    }

    // Configurando o EntityManagerFactory (Mapeamento JPA/Hibernate)
    @Bean(name = "principalEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean principalEntityManagerFactory(
            @Qualifier("principalDataSource") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();

        factory.setDataSource(dataSource);
        factory.setPackagesToScan(PACOTE_ENTITY);
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        // Define um nome explícito para a Unidade de Persistência
        factory.setPersistenceUnitName("principalPersistenceUnit");

        // Configurações JPA/Hibernate (lidas do application.properties, mas adicionadas
        // realmente aqui)
        HashMap<String, Object> properties = new HashMap<>();

        // hibernate.ddl-auto
        properties.put("hibernate.hbm2ddl.auto", hibernateDdlAuto);

        properties.put("jakarta.persistence.schema-generation.database.action", hibernateDdlAuto);

        // hibernate.dialect
        properties.put("hibernate.dialect", hibernateDialect);
        properties.put("hibernate.database-platform", "org.hibernate.dialect.PostgreSQL9Dialect");
        // show-sql
        properties.put("hibernate.show_sql", "true");
        // temp.use_jdbc_metadata_defaults
        properties.put("hibernate.temp.use_jdbc_metadata_defaults", "false");

        properties.put("hibernate.boot.allow_jdbc_metadata_access", "false");

        properties.put("jakarta.persistence.jdbc.url", dbUrl);
        properties.put("jakarta.persistence.jdbc.user", dbUsername);
        properties.put("jakarta.persistence.jdbc.password", dbPassword);
        properties.put("jakarta.persistence.jdbc.driver", dbDriverClassName);

        factory.setJpaPropertyMap(properties);
        return factory;
    }

    // Configurando o TransactionManager (Gerenciamento de Transações)
    @Bean(name = "principalTransactionManager")
    public PlatformTransactionManager principalTransactionManager(
            @Qualifier("principalEntityManagerFactory") LocalContainerEntityManagerFactoryBean principalEntityManagerFactory) {
        return new JpaTransactionManager(principalEntityManagerFactory.getObject());
    }
}
