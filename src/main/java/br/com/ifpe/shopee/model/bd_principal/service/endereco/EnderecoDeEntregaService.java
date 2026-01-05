// src/main/java/br.com.ifpe.shopee.model/bd_principal/service/endereco/EnderecoDeEntregaService.java

package br.com.ifpe.shopee.model.bd_principal.service.endereco;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifpe.shopee.model.bd_principal.entity.Cliente;
import br.com.ifpe.shopee.model.bd_principal.entity.Usuario;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeEntrega;
import br.com.ifpe.shopee.model.bd_principal.repository.TipoDeUsuarioRepository;
import br.com.ifpe.shopee.model.bd_principal.repository.endereco.EnderecoDeEntregaRepository;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;
import br.com.ifpe.shopee.util.seguranca.ValidadorDeAcesso;
import jakarta.transaction.Transactional;

@Service
public class EnderecoDeEntregaService {

    @Autowired
    private EnderecoDeEntregaRepository repository;
   
    @Autowired
    private TipoDeUsuarioRepository tipoDeUsuarioRepository;

    @Autowired
    private ValidadorDeAcesso validador;

    // Método auxiliar para evitar repetição de setters (Clean Code)
    private void copiarDados(EnderecoDeEntrega origem, EnderecoDeEntrega destino) {
        destino.setCep(origem.getCep());
        destino.setRua(origem.getRua());
        destino.setBairro(origem.getBairro());
        destino.setCidade(origem.getCidade());
        destino.setEstado(origem.getEstado());
        destino.setNumero(origem.getNumero());
        destino.setComplemento(origem.getComplemento());
        destino.setReferencia(origem.getReferencia());
        destino.setNome(origem.getNome());
        destino.setRecebedor(origem.getRecebedor());
    }

    /**
     * Adiciona um novo endereço de entrega ao cliente.
     * 
     * @param idCliente O ID do cliente proprietário.
     * @param novoEndereco A entidade EnderecoDeEntrega.
     * @param usuarioLogado O usuário que está logado.
     * @return O EnderecoDeEntrega recém-criado.
     */
    @Transactional
    public EnderecoDeEntrega adicionarEnderecoDeEntrega(UUID idCliente, EnderecoDeEntrega novoEndereco, Usuario usuarioLogado) {
        // Busca o cliente (usando o repositório genérico de TipoDeUsuario)
        Cliente cliente = (Cliente) tipoDeUsuarioRepository.findById(idCliente)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente com ID: " + idCliente + " não encontrado."));
        
        // Valida se está adicionando um endereço no próprio Cliente
        validador.validarPosse(cliente.getUsuario().getId(), usuarioLogado);
        novoEndereco.setCliente(cliente);
        novoEndereco.setHabilitado(Boolean.TRUE);
        
        return repository.save(novoEndereco);
    }
   
    /**
     * Obtém um endereço de entrega pelo ID.
     * 
     * @param id O ID do endereço.
     * @return O EnderecoDeEntrega encontrado.
     */
    public EnderecoDeEntrega obterPorID(UUID id) {
        return repository.findById(id)
                         .orElseThrow(() -> new RecursoNaoEncontradoException("Endereço de Entrega com ID: " + id + " não encontrado."));
    }
   

    /**
     * Busca todos os endereços de entrega associados a um Cliente específico.
     * 
     * @param idCliente O ID do Cliente.
     * @param usuarioLogado O usuário que está logado.
     * @return Lista de EnderecoDeEntrega do Cliente.
     */
    public List<EnderecoDeEntrega> listarPorCliente(UUID idCliente, Usuario usuarioLogado) {
        Cliente cliente = (Cliente) tipoDeUsuarioRepository.findById(idCliente).orElseThrow(/*...*/);
        
        // Valida se tenho permissão para ver estes endereços
        validador.validarPosse(cliente.getUsuario().getId(), usuarioLogado);

        return repository.findByClienteId(idCliente);
    }

    /**
     * Altera um endereço de entrega existente. Conseidera que o endereço de entrega
     * é exclusivo do Cliente.
     * 
     * @param idEndereco O ID do endereço a ser alterado.
     * @param enderecoAlterado A Entidade EnderecoDeEntrega com os novos dados.
     * @param usuarioLogado O usuário que está logado.
     * @return O EnderecoDeEntrega alterado.
     */
    @Transactional
    public EnderecoDeEntrega alterarEndereco(UUID idEndereco, EnderecoDeEntrega enderecoAlterado, Usuario usuarioLogado) {
        EnderecoDeEntrega enderecoOriginal = obterPorID(idEndereco);

        // Valida se o endereço pertence ao usuário
        validador.validarPosse(enderecoOriginal.getCliente().getUsuario().getId(), usuarioLogado);
        enderecoOriginal.setHabilitado(Boolean.TRUE);
        copiarDados(enderecoAlterado, enderecoOriginal);

        return repository.save(enderecoOriginal);
    }

    /**
     * Apaga um endereço de entrega (exclusão lógica).
     * 
     * @param id O ID do endereço.
     * @param usuarioLogado O usuário que está logado.
     * @return O EnderecoDeEntrega apagado.
     */
    @Transactional
    public EnderecoDeEntrega apagarEndereco(UUID id, Usuario usuarioLogado) {
        EnderecoDeEntrega endereco = obterPorID(id);

        // Valida se o endereço pertence ao usuário
        validador.validarPosse(endereco.getCliente().getUsuario().getId(), usuarioLogado);
        endereco.setHabilitado(Boolean.FALSE);

        return repository.save(endereco);
    }

    /**
     * ATENÇÂO: Use com cautela. Apaga um endereço de entrega permanentemente.
     * 
     * @param id O ID do endereço.
     */
    @Transactional
    public void deletarPermanentemente(UUID id) {
        EnderecoDeEntrega endereco = obterPorID(id);
        repository.delete(endereco);
    }
}
