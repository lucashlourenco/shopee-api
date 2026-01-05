// src/main/java/br/com/ifpe/shopee.model/bd_principal/service/PessoaService.java

package br.com.ifpe.shopee.model.bd_principal.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifpe.shopee.model.bd_principal.entity.Pessoa;
import br.com.ifpe.shopee.model.bd_principal.entity.Usuario;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeCadastro;
import br.com.ifpe.shopee.model.bd_principal.repository.PessoaRepository;
import br.com.ifpe.shopee.model.bd_principal.service.endereco.EnderecoDeCadastroService;
import br.com.ifpe.shopee.util.exception.AdvertenciaException;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;
import br.com.ifpe.shopee.util.seguranca.ValidadorDeAcesso;
import jakarta.transaction.Transactional;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository repository;

    @Autowired
    private EnderecoDeCadastroService enderecoDeCadastroService;

    @Autowired
    private ValidadorDeAcesso validador;

    /**
     * Cria e persiste uma nova Pessoa, garantindo que o CPF é único e o endereço é gerenciado.
     * 
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
                         .orElseThrow(() -> new RecursoNaoEncontradoException("Pessoa com ID: " + id + " não encontrada."));
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
     * Verifica se uma Pessoa possui algum usuário ativo.
     * 
     * @param pessoaId O ID da Pessoa.
     * @return true se a Pessoa possui um usuário ativo.
     */
    public boolean existeUsuarioAtivo(UUID pessoaId) {
        return repository.existeUsuarioAtivoParaPessoa(pessoaId);
    }

    /**
     * Altera os dados de uma Pessoa existente.
     * 
     * @param id O ID da Pessoa.
     * @param novaPessoa Pessoa com os novos dados.
     * @param usuarioLogado O usuário logado.
     * @return A Pessoa alterada.
     */
    public Pessoa alterarPessoa(UUID id, Pessoa novaPessoa, Usuario usuarioLogado) {
        Pessoa pessoa = obterPorID(id);
        
        // Valida se a Pessoa pertence ao Usuário Logado
        validador.validarPosse(pessoa.getUsuario().getId(), usuarioLogado);
        pessoa.setNomeCompleto(novaPessoa.getNomeCompleto());
        pessoa.setNacionalidade(novaPessoa.getNacionalidade());
        pessoa.setDataNascimento(novaPessoa.getDataNascimento());

        return repository.save(pessoa);
    }

    /**
     * Apaga uma Pessoa (exclusão lógica).
     * 
     * @param id O ID da Pessoa.
     * @param usuarioLogado O usuário logado.
     * @return A Pessoa apagada.
     */
    @Transactional
    public Pessoa apagarPessoa(UUID id, Usuario usuarioLogado) {
        Pessoa pessoa = obterPorID(id);

        validador.validarPosse(pessoa.getUsuario().getId(), usuarioLogado);
        pessoa.setHabilitado(Boolean.FALSE);

        return repository.save(pessoa);
    }

    /**
     * ATENÇÂO: Use com cautela. Apaga um endereço comercial permanentemente.
     * 
     * @param id O ID do endereço.
     */
    @Transactional
    public void deletarPermanentemente(UUID id) {
        Pessoa endereco = obterPorID(id);
        repository.delete(endereco);
    }

    // -------------------------------------------------
    // Endereço de Cadastro
    // -------------------------------------------------

    /**
     * Altera o endereço de cadastro de uma pessoa, gerenciando a complexidade
     * de endereços compartilhados e reaproveitamento de registros.
     * 
     * @param idPessoa O ID da Pessoa.
     * @param novoEnderecoDados Os dados do endereço de cadastro.
     * @return A Pessoa com o endereço alterado.
     */
    @Transactional
    public Pessoa alterarEndereco(UUID idPessoa, EnderecoDeCadastro novoEnderecoDados, Usuario usuarioLogado) {

        // 1. Busca a pessoa
        Pessoa pessoa = obterPorID(idPessoa);
        // Validação de Posse
        validador.validarPosse(pessoa.getUsuario().getId(), usuarioLogado);
        
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
            pessoa = repository.save(pessoa);
            
            // 5. Limpeza: O endereço anterior pode ter ficado órfão
            // Se o endereço anterior era compartilhado, ele continua lá, mas.
            // se era o último a usar, o limparSeOrfao fará a exclusão lógica.
            enderecoDeCadastroService.limparSeOrfao(idEnderecoAtual);
        }

        return pessoa;
    }
}