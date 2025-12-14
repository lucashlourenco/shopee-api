// src/main/java/br/com/ifpe/shopee/ShopeeApiApplication.java

package br.com.ifpe.shopee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {
	DataSourceAutoConfiguration.class,      // Exclui a auto-configuração do DataSource
    MongoAutoConfiguration.class,           // Exclui a auto-configuração da Conexão Mongo
    MongoDataAutoConfiguration.class,       // Exclui a auto-configuração do Spring Data Mongo
	HibernateJpaAutoConfiguration.class		// Exclui a autoconfiguração do JPA/Hibernate
})
@EnableScheduling
@EnableJpaAuditing
public class ShopeeApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopeeApiApplication.class, args);
	}
}
