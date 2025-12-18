// src/main/java/br/com/ifpe/shopee.model/bd_principal/service/PessoaService.java

package br.com.ifpe.shopee.model.bd_principal.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifpe.shopee.model.bd_principal.entity.Pessoa;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeCadastro;
import br.com.ifpe.shopee.model.bd_principal.repository.PessoaRepository;
import br.com.ifpe.shopee.model.bd_principal.service.endereco.EnderecoDeCadastroService;
import br.com.ifpe.shopee.util.exception.AdvertenciaException;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException; 
import jakarta.transaction.Transactional;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository repository;

    @Autowired
    private EnderecoDeCadastroService enderecoDeCadastroService;

    /**
     * Cria e persiste uma nova Pessoa, garantindo que o CPF é único e o endereço é gerenciado.
     * @param novaPessoa A entidade Pessoa com os dados civis.
     * @param enderecoCadastro O endereço de cadastro que será persistido/reutilizado.
     * @return A Pessoa salva.
     */
    @Transactional
    public Pessoa adicionarPessoa(Pessoa novaPessoa, EnderecoDeCadastro enderecoCadastro) {
        
        // 1. Verifica se o CPF ja existe
        if (repository.existsByCpf(novaPessoa.getCpf())) {
            throw new AdvertenciaException("O CPF " + novaPessoa.getCpf() + " já está cadastrado no sistema.");
        }
        
        // 2. Gerencia o Endereço de Cadastro
        EnderecoDeCadastro endereco = enderecoDeCadastroService.adicionarOuEncontarEnderecoDeCadastro(enderecoCadastro);
        
        // 3. Associa a Pessoa ao endereço gerenciado
        novaPessoa.setHabilitado(Boolean.TRUE);
        novaPessoa.setEndereco(endereco);
        
        // 4. Salva a Pessoa
        return repository.save(novaPessoa);
    }
    
    /**
     * Busca uma Pessoa pelo seu ID.
     * @param id O ID da Pessoa.
     * @return A Pessoa encontrada.
     */
    public Pessoa obterPorID(UUID id) {
        return repository.findById(id)
                         .orElseThrow(() -> new RecursoNaoEncontradoException("Pessoa com ID: " + id + " nao encontrada."));
    }

    /**
     * Busca uma Pessoa pelo seu CPF.
     * @param cpf O CPF da Pessoa.
     * @return A Pessoa encontrada.
     */
    public Pessoa obterPorCPF(String cpf) {
        return repository.findByCpf(cpf)
                         .orElseThrow(() -> new RecursoNaoEncontradoException("Pessoa com CPF: " + cpf + " não encontrada."));
    }

    /**
     * Altera os dados de uma Pessoa existente.
     * @param id O ID da Pessoa.
     * @return A Pessoa alterada.
     */
    public Pessoa alterarPessoa(UUID id, Pessoa novaPessoa) {
        Pessoa pessoa = obterPorID(id);
        
        pessoa.setNomeCompleto(novaPessoa.getNomeCompleto());
        pessoa.setNacionalidade(novaPessoa.getNacionalidade());
        pessoa.setDataNascimento(novaPessoa.getDataNascimento());

        return repository.save(pessoa);
    }
    
    /**
     * Apaga uma Pessoa (exclusão lógica).
     * @param id O ID da Pessoa.
     * @return A Pessoa apagada.
     */
    @Transactional
    public Pessoa apagarPessoa(UUID id) {
        Pessoa pessoa = obterPorID(id);
        pessoa.setHabilitado(Boolean.FALSE);
        return repository.save(pessoa);
    }

    /**
     * Conta o número de Usuários ativos vinculados a uma Pessoa.
     * Usado na orquestração de exclusão de conta.
     * @param pessoaId O ID da Pessoa.
     * @return O número de Usuários ativos.
     */
    public long countUsuariosAtivosByPessoaId(UUID pessoaId) {
        return repository.countUsuariosAtivosByPessoaId(pessoaId);
    }

    /**
     * Altera o endereço de cadastro de uma pessoa, gerenciando a complexidade
     * de endereços compartilhados e reaproveitamento de registros.
     */
    @Transactional
    public Pessoa alterarEndereco(UUID idPessoa, EnderecoDeCadastro novoEnderecoDados) {
        
        // 1. Busca a pessoa
        Pessoa pessoa = obterPorID(idPessoa);
        
        // 2. Pega o ID do endereço que ela usa atualmente
        UUID idEnderecoAtual = pessoa.getEndereco().getId();

        // 3. Chama o serviço de endereço para processar a mudança.
        // O EnderecoDeCadastroService vai decidir se altera o original 
        // ou se retorna um novo/existente baseado na semântica.
        EnderecoDeCadastro enderecoResultante = enderecoDeCadastroService.alterarEndereco(idEnderecoAtual, novoEnderecoDados);

        // 4. Atualiza o endereço da pessoa
        // Se o serviço de endereço nos devolveu um objeto com ID diferente do atual,
        // significa que a Pessoa agora aponta para outro registro no banco.
        if (!idEnderecoAtual.equals(enderecoResultante.getId())) {
            pessoa.setEndereco(enderecoResultante);
            repository.save(pessoa);
            
            // TODO Opcional: Aqui o PessoaService poderia verificar se o idEnderecoAtual 
            // ficou "órfão" (sem nenhuma pessoa usando) e deletá-lo.
        }

        return pessoa;
    }
}