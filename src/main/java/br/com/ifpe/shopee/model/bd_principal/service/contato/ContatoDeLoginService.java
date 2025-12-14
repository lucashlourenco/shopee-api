// src/main/java/br/com/ifpe/shopee/model/bd_principal/service/contato/ContatoDeLoginService.java

package br.com.ifpe.shopee.model.bd_principal.service.contato;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeLogin;
import br.com.ifpe.shopee.model.bd_principal.repository.contato.ContatoDeLoginRepository;
import br.com.ifpe.shopee.util.exception.EntidadeDuplicadaException; 
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException; 
import jakarta.transaction.Transactional;

@Service
public class ContatoDeLoginService {

    @Autowired
    private ContatoDeLoginRepository repository;

    /**
     * Serviço para salvar um novo Contato de Login.
     * Implementa a regra de unicidade (valor deve ser único no sistema).
     *
     * @param novoContato A Entidade ContatoDeLogin a ser salva.
     * @return O ContatoDeLogin salvo.
     */
    @Transactional
    public ContatoDeLogin salvar(ContatoDeLogin novoContato) {
        
        // Verificar unicidade global
        if (repository.findByValor(novoContato.getValor()).isPresent()) {
            throw new EntidadeDuplicadaException("O valor de login '" 
                                                 + novoContato.getValor() 
                                                 + "' já está em uso por outro usuário.");
        }

        // Persistência
        novoContato.setHabilitado(Boolean.TRUE);
        
        return repository.save(novoContato);
    }
    
    /**
     * Serviço para buscar um ContatoDeLogin pelo valor. 
     * Usado principalmente por serviços de Autenticação.
     *
     * @param valor O valor do login (email/telefone).
     * @return O ContatoDeLogin encontrado.
     */
    public ContatoDeLogin buscarPorValor(String valor) {
        return repository.findByValor(valor)
                         .orElseThrow(() -> new RecursoNaoEncontradoException("Login não encontrado para o valor: " + valor));
    }

    /**
     * Apaga um contato de login (exclusão lógica).
     * @param valor O valor (email/telefone) do contato a ser desabilitado..
     * @return O ContatoDeLogin desabilitado.
     */
    @Transactional
    public ContatoDeLogin apagarContato(String valor) {
        ContatoDeLogin contato = buscarPorValor(valor);
        contato.setHabilitado(Boolean.FALSE);
        return repository.save(contato);
    }
}