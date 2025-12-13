// src/main/java/br/com/ifpe/shopee/config/DBBlobstoreConfig.java

package br.com.ifpe.shopee.config;

import java.util.HashMap;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(basePackages = "br.com.ifpe.shopee.model.bd_blobstore.repository", entityManagerFactoryRef = "blobstoreEntityManagerFactory", transactionManagerRef = "blobstoreTransactionManager")
public class DBBlobstoreConfig {

    // Pacote onde o Spring deve escanear as classes de Entidade (@Entity) da
    // blobstore
    private static final String PACOTE_ENTITY = "br.com.ifpe.shopee.model.bd_blobstore.entity";

    // Injetando valores explicitamente do application.properties
    @Value("${bd-blobstore.datasource.url}")
    private String dbUrl;

    @Value("${bd-blobstore.datasource.username}")
    private String dbUsername;

    @Value("${bd-blobstore.datasource.password}")
    private String dbPassword;

    @Value("${bd-blobstore.datasource.driver-class-name}")
    private String dbDriverClassName;

    @Value("${bd-blobstore.jpa.hibernate.ddl-auto}")
    private String hibernateDdlAuto;

    @Value("${bd-blobstore.jpa.properties.hibernate.dialect}")
    private String hibernateDialect;

    // Injetando o nome do Pool
    @Value("${bd-blobstore.datasource.pool-name}")
    private String dbPoolName;

    // Conexão Física com o Banco de Dados
    @Bean(name = "blobstoreDataSource")
    // REMOVER @ConfigurationProperties(prefix = "bd-blobstore.datasource")
    public DataSource blobstoreDataSource() {

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
    @Bean(name = "blobstoreEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean blobstoreEntityManagerFactory(
            @Qualifier("blobstoreDataSource") DataSource dataSource) { // INJEÇÃO EXPLÍCITA DO DS

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();

        factory.setDataSource(dataSource);
        factory.setPackagesToScan(PACOTE_ENTITY);
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        // Define um nome explícito e DIFERENTE para a Unidade de Persistência
        factory.setPersistenceUnitName("blobstorePersistenceUnit");

        HashMap<String, Object> properties = new HashMap<>();

        properties.put("hibernate.hbm2ddl.auto", hibernateDdlAuto);
        properties.put("hibernate.dialect", hibernateDialect);
        properties.put("hibernate.database-platform", "org.hibernate.dialect.PostgreSQL9Dialect");
        properties.put("hibernate.show_sql", "true");
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
    @Bean(name = "blobstoreTransactionManager")
    public PlatformTransactionManager blobstoreTransactionManager(
            @Qualifier("blobstoreEntityManagerFactory") LocalContainerEntityManagerFactoryBean blobstoreEntityManagerFactory) {
        // O .getObject() deve ser chamado no TransactionManager, não na assinatura do
        // método
        return new JpaTransactionManager(blobstoreEntityManagerFactory.getObject());
    }
}