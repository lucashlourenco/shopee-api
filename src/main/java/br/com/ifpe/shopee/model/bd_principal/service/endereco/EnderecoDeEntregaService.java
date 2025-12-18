// src/main/java/br.com.ifpe.shopee.model/bd_principal/service/endereco/EnderecoDeEntregaService.java

package br.com.ifpe.shopee.model.bd_principal.service.endereco;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifpe.shopee.model.bd_principal.entity.Cliente;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeEntrega;
import br.com.ifpe.shopee.model.bd_principal.repository.TipoDeUsuarioRepository;
import br.com.ifpe.shopee.model.bd_principal.repository.endereco.EnderecoDeEntregaRepository;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;
import jakarta.transaction.Transactional;

@Service
public class EnderecoDeEntregaService {

    @Autowired
    private EnderecoDeEntregaRepository repository;
   
    @Autowired
    private TipoDeUsuarioRepository tipoDeUsuarioRepository;

    /**
     * Adiciona um novo endereço de entrega ao cliente.
     * @param novoEndereco A entidade EnderecoDeEntrega.
     * @param idCliente O ID do cliente proprietário.
     * @return O EnderecoDeEntrega recém-criado.
     */
    @Transactional
    public EnderecoDeEntrega adicionarEnderecoDeEntrega(UUID idCliente, EnderecoDeEntrega novoEndereco) {
        // Busca direta pelo repositório quebra o ciclo de serviços
        Cliente cliente = (Cliente) tipoDeUsuarioRepository.findById(idCliente)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));
        
        novoEndereco.setCliente(cliente);
        novoEndereco.setHabilitado(Boolean.TRUE);
        
        return repository.save(novoEndereco);
    }
   
    /**
     * Obtém um endereço de entrega pelo ID.
     * @param id O ID do endereço.
     * @return O EnderecoDeEntrega encontrado.
     */
    public EnderecoDeEntrega obterPorID(UUID id) {
        return repository.findById(id)
                         .orElseThrow(() -> new RecursoNaoEncontradoException("Endereço de Entrega não encontrado com ID: " + id + "."));
    }
   

    /**
     * Busca todos os endereços de entrega associados a um Cliente específico.
     * @param idCliente O ID do Cliente.
     * @return Lista de EnderecoDeEntrega do Cliente.
     */
    public List<EnderecoDeEntrega> listarPorCliente(UUID idCliente) {
        return repository.findByClienteId(idCliente);
    }

    /**
     * Altera um endereço de entrega existente.
     * @param idEndereco O ID do endereço a ser alterado.
     * @param enderecoAlterado A Entidade EnderecoDeEntrega com os novos dados.
     * @return O EnderecoDeEntrega alterado.
     */
    @Transactional
    public EnderecoDeEntrega alterarEndereco(UUID idEndereco, EnderecoDeEntrega enderecoAlterado) {
       
        EnderecoDeEntrega enderecoOriginal = obterPorID(idEndereco);

        // Altera os dados do endereço.
        enderecoOriginal.setHabilitado(Boolean.TRUE);
        enderecoOriginal.setCep(enderecoAlterado.getCep());
        enderecoOriginal.setRua(enderecoAlterado.getRua());
        enderecoOriginal.setBairro(enderecoAlterado.getBairro());
        enderecoOriginal.setCidade(enderecoAlterado.getCidade());
        enderecoOriginal.setEstado(enderecoAlterado.getEstado());
        enderecoOriginal.setNumero(enderecoAlterado.getNumero());
        enderecoOriginal.setComplemento(enderecoAlterado.getComplemento());
        enderecoOriginal.setReferencia(enderecoAlterado.getReferencia());
        enderecoOriginal.setNome(enderecoAlterado.getNome());
        enderecoOriginal.setRecebedor(enderecoAlterado.getRecebedor());

        // Salva o endereço alterado.
        return repository.save(enderecoOriginal);
    }

    /**
     * Apaga um endereço de entrega (exclusão lógica).
     * @param id O ID do endereço.
     */
    @Transactional
    public EnderecoDeEntrega apagarEndereco(UUID id) {
        EnderecoDeEntrega endereco = obterPorID(id);
        endereco.setHabilitado(Boolean.FALSE);
        return repository.save(endereco);
    }

    /**
     * ATENÇÂO: Use com cautela. Apaga um endereço de entrega permanentemente.
     * @param id O ID do endereço.
     */
    @Transactional
    public void deletarPermanentemente(UUID id) {
        EnderecoDeEntrega endereco = obterPorID(id);
        repository.delete(endereco);
    }
}
