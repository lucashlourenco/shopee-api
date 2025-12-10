// src/main/java/br/com/ifpe/shopee/config/DBPrincipalConfig.java

package br.com.ifpe.shopee.config;

import java.util.HashMap;
import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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

    // Pacote onde o Spring deve escanear as classes de Entidade (@Entity) do BD principal da aplicação
    private static final String PACOTE_ENTITY = "br.com.ifpe.shopee.model.bd_principal.entity";

    // Conexão Física com o Banco de Dados
    @Primary
    @Bean(name = "principalDataSource")
    @ConfigurationProperties(prefix = "bd-principal.datasource")
    public DataSource principalDataSource() {
        // Usa o Spring Boot's DataSourceBuilder para criar o DataSource a partir das
        // propriedades
        return DataSourceBuilder.create().build();
    }

    // Configurando o EntityManagerFactory (Mapeamento JPA/Hibernate)
    @Primary
    @Bean(name = "principalEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean principalEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();

        factory.setDataSource(principalDataSource());
        factory.setPackagesToScan(PACOTE_ENTITY);
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        // Configurações JPA/Hibernate (lidas do application.properties, mas adicionadas realmente aqui)
        HashMap<String, Object> properties = new HashMap<>();
        
        // hibernate.ddl-auto
        properties.put("hibernate.hbm2ddl.auto", "none");
        // hibernate.dialect
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.database-platform", "org.hibernate.dialect.PostgreSQL9Dialect");
        // show-sql
        properties.put("hibernate.show_sql", "true"); 
        // temp.use_jdbc_metadata_defaults
        properties.put("hibernate.temp.use_jdbc_metadata_defaults", "false");

        factory.setJpaPropertyMap(properties);
        return factory;
    }

    // Configurando o TransactionManager (Gerenciamento de Transações)
    @Primary
    @Bean(name = "principalTransactionManager")
    public PlatformTransactionManager principalTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(principalEntityManagerFactory().getObject());
        return transactionManager;
    }
}
