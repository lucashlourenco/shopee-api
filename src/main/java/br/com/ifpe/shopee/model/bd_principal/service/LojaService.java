// src/main/java/br/com/ifpe.shopee.model/bd_principal/service/LojaService.java

package br.com.ifpe.shopee.model.bd_principal.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifpe.shopee.model.bd_principal.entity.Loja;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoComercial;
import br.com.ifpe.shopee.model.bd_principal.repository.LojaRepository;
import br.com.ifpe.shopee.model.bd_principal.service.endereco.EnderecoComercialService;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;
import jakarta.transaction.Transactional;

@Service
public class LojaService {
    
    @Autowired
    private LojaRepository repository;
    
    @Autowired 
    private EnderecoComercialService enderecoComercialService; 
    
    /**
     * Busca uma Loja pelo seu ID.
     * @param id O ID da Loja.
     * @return  A Loja encontrada.
     */
    public Loja obterPorID(UUID id) {
        return repository.findById(id)
                         .orElseThrow(() -> new RecursoNaoEncontradoException("Loja com ID: '" + id + "' não encontrada."));
    }
    
    /**
     * Adiciona uma nova Loja.
     * @param novaLoja A loja a ser adicionada.
     * @param enderecoComercial O endereço da loja.
     * @return
     */
    @Transactional
    public Loja adicionarLoja(Loja novaLoja, EnderecoComercial enderecoComercial) {
        
        // 1. Gerencia o Endereço Comercial (cria ou reutiliza).
        EnderecoComercial endereco = enderecoComercialService.adicionarOuEncontarEnderecoComercial(enderecoComercial);
        
        // 2. Associa a Loja ao endereço gerenciado
        novaLoja.setHabilitado(Boolean.TRUE);
        novaLoja.setEndereco(endereco);
        
        // 3. Salva e retorna a Loja
        return repository.save(novaLoja);
    }
    
    /**
     * Apaga uma Loja (exclusão lógica).
     * @param id O ID da Loja.
     * @return A Loja apagada.
     */
    @Transactional
    public Loja apagarLoja(UUID id) {
        Loja loja = obterPorID(id);
        loja.setHabilitado(Boolean.FALSE);
        return repository.save(loja);
    }
}