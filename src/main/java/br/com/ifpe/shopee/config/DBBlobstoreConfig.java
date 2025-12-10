// src/main/java/br/com/ifpe/shopee/config/DBBlobstoreConfig.java

package br.com.ifpe.shopee.config;

import java.util.HashMap;
import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
    basePackages = "br.com.ifpe.shopee.model.bd_blobstore.repository",
    entityManagerFactoryRef = "blobstoreEntityManagerFactory",
    transactionManagerRef = "blobstoreTransactionManager"
)
public class DBBlobstoreConfig {

    // Pacote onde o Spring deve escanear as classes de Entidade (@Entity) da blobstore
    private static final String PACOTE_ENTITY = "br.com.ifpe.shopee.model.bd_blobstore.entity";

    // Conexão Física com o Banco de Dados
    @Bean(name = "blobstoreDataSource")
    @ConfigurationProperties(prefix = "bd-blobstore.datasource")
    public DataSource blobstoreDataSource() {
        return DataSourceBuilder.create().build();
    }

    // Configurando o EntityManagerFactory (Mapeamento JPA/Hibernate)
    @Bean(name = "blobstoreEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean blobstoreEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();

        factory.setDataSource(blobstoreDataSource());
        factory.setPackagesToScan(PACOTE_ENTITY);
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        HashMap<String, Object> properties = new HashMap<>();
        
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.database-platform", "org.hibernate.dialect.PostgreSQL9Dialect");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.temp.use_jdbc_metadata_defaults", "false"); 

        factory.setJpaPropertyMap(properties);
        return factory;
    }

    // Configurando o TransactionManager (Gerenciamento de Transações)
    @Bean(name = "blobstoreTransactionManager")
    public PlatformTransactionManager blobstoreTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(blobstoreEntityManagerFactory().getObject());
        return transactionManager;
    }
}