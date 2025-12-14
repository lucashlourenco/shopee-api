// src/main/java/br/com/ifpe/shopee.config/DBSecundarioConfig.java

package br.com.ifpe.shopee.config;

import java.net.URLEncoder; 
import java.nio.charset.StandardCharsets; 
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
    basePackages = "br.com.ifpe.shopee.model.bd_secundario.repository",
    mongoTemplateRef = "secundarioDataTemplate"
)
public class DBSecundarioConfig {

    // Carrega as propriedades do Mongo
    @Bean
    @ConfigurationProperties(prefix = "bd-secundario.data.mongodb")
    public MongoProperties secundarioDataProperties() {
        return new MongoProperties();
    }

    // Cria o cliente Mongo de forma segura, montando a URI ou ConnectionString
    @Bean(name = "secundarioDataClient")
    public MongoClient secundarioDataClient() {
        MongoProperties props = secundarioDataProperties();

        // Conversão segura do char[] para String
        String username = new String(props.getUsername());
        String password = new String(props.getPassword());

        try {
            // Codifica o usuário e a senha para garantir que caracteres especiais funcionem
            String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8.toString());
            String encodedPassword = URLEncoder.encode(password, StandardCharsets.UTF_8.toString());
            
            // 1. Constrói a ConnectionString usando os componentes codificados
            String connectionString = "mongodb://" 
                + encodedUsername + ":" 
                + encodedPassword + "@" 
                + props.getHost() + ":" 
                + props.getPort() + "/" 
                + props.getDatabase();
            
            // 2. Cria o MongoClient a partir da String de Conexão
            return MongoClients.create(connectionString);

        } catch (Exception e) {
            // Em caso de falha na codificação ou problema de conexão.
            throw new RuntimeException("Falha ao construir a string de conexão do MongoDB Secundário. Verifique as credenciais no .env.", e);
        }
    }
    
    // Cria o Mongo Template (o principal ponto de acesso ao BD)
    @Bean(name = "secundarioDataTemplate")
    public MongoTemplate secundarioDataTemplate() throws Exception {
        return new MongoTemplate(secundarioDataClient(), secundarioDataProperties().getDatabase());
    }
}