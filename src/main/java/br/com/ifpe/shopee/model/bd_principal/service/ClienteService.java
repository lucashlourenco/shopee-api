// src/main/java/br/com/ifpe.shopee.model/bd_principal/service/ClienteService.java

package br.com.ifpe.shopee.model.bd_principal.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifpe.shopee.model.bd_principal.entity.Cliente;
import br.com.ifpe.shopee.model.bd_principal.entity.Pessoa;
import br.com.ifpe.shopee.model.bd_principal.entity.Usuario;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeEntrega;
import br.com.ifpe.shopee.model.bd_principal.repository.TipoDeUsuarioRepository;
import br.com.ifpe.shopee.model.bd_principal.request.CadastroClienteRequest;
import br.com.ifpe.shopee.model.bd_principal.request.TipoDeUsuarioRequest;
import br.com.ifpe.shopee.model.bd_principal.service.endereco.EnderecoDeEntregaService;
import br.com.ifpe.shopee.util.exception.AdvertenciaException;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;
import br.com.ifpe.shopee.util.seguranca.ValidadorDeAcesso;
import jakarta.transaction.Transactional;

@Service
public class ClienteService {

    @Autowired
    private PessoaService pessoaService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private EnderecoDeEntregaService enderecoDeEntregaService;
    
    @Autowired
    private TipoDeUsuarioRepository tipoDeUsuarioRepository;

    @Autowired
    private ValidadorDeAcesso validador;

    // Método auxiliar para extrair o primeiro nome de um nome completo
    private String extrairPrimeiroNome(String nomeCompleto) {
        // Dada a regra de não poder ser vazio ou nulo, lá na frente o erro correto vai ser levantado
        if (nomeCompleto == null) return ""; 
        
        return nomeCompleto.trim().split("\\s+")[0]; // Pega a primeira palavra
    }

    /**
     * Orquestra a criação completa de um novo Cliente (Pessoa, Usuario, Cliente e Endereços).
     * @param request O DTO de Orquestração.
     * @return O Cliente recém-criado.
     */
    @Transactional
    public Cliente adicionarCliente(CadastroClienteRequest request) {
        Pessoa pessoa;

        try {
            // Tenta buscar a Pessoa pelo CPF.
            pessoa = pessoaService.obterPorCPF(request.getPessoa().getCpf());
    
            // Se a Pessoa existir, verifica se ela possui um papel de Cliente ativo.
            if (tipoDeUsuarioRepository.existsClienteByPessoaId(pessoa.getId())) {
                throw new AdvertenciaException("Esta Pessoa já possui um perfil de Cliente ativo.");
            }
        }

        // Se a Pessoa não existir, cria uma nova.
        catch (RecursoNaoEncontradoException e) {
            // Se a Pessoa não existe, ela será criada. Não há papel Cliente ativo, logo, segue o fluxo.
            Pessoa novaPessoa = request.getPessoa().build();
            
            // O Endereço de Cadastro só é necessário se a Pessoa for nova.
            pessoa = pessoaService.adicionarPessoa(novaPessoa, request.getPessoa().getEnderecoDeCadastro().build());
        }

        // Criar a Conta (Usuário)
        Usuario usuario = usuarioService.adicionarUsuario(
            pessoa, 
            request.getConta().getSenha(), 
            request.getConta().getLogin().build()
        );
    
        // Criar o Papel (Cliente)
        Cliente cliente = new Cliente();
        cliente.setHabilitado(Boolean.TRUE);
        cliente.setDataDeCadastro(LocalDateTime.now());
        cliente.setUsuario(usuario);
        cliente.setNome(extrairPrimeiroNome(pessoa.getNomeCompleto()));
    
        Cliente clienteSalvo = (Cliente) tipoDeUsuarioRepository.save(cliente);
        
        // Adicionar Endereço de Entrega Inicial (de forma opcional)
        if (request.getEnderecoDeEntregaInicial() != null) {
            EnderecoDeEntrega novoEndereco = request.getEnderecoDeEntregaInicial().build();
            enderecoDeEntregaService.adicionarEnderecoDeEntrega(clienteSalvo.getId(), novoEndereco, usuario);
        }
        
        return clienteSalvo;
    }
    
    /**
     * Busca um Cliente pelo ID.
     * @param id O ID do Cliente.
     * @return O Cliente encontrado.
     */
    public Cliente obterPorID(UUID id) {
        // Usa o JpaRepository de TipoDeUsuario para buscar o Cliente específico
        return (Cliente) tipoDeUsuarioRepository.findById(id)
                         .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente com ID: '" + id + "' não encontrado."));
    }

    /**
     * Atualiza um Cliente apartir do ID. Como o único dado atualizável de um Cliente é o nome,
     * este serviço apenas atualiza o nome.
     * 
     * @param id O ID do Cliente.
     * @param nome O novo nome do Cliente.
     * @param usuarioLogado O usuário que está logado.
     * @return O Cliente atualizado.
     */
    public Cliente atualizarCliente(UUID idCliente, TipoDeUsuarioRequest request, Usuario usuarioLogado) {
        Cliente cliente = obterPorID(idCliente);

        // Valida se o Cliente pertence ao Usuário Logado
        validador.validarPosse(cliente.getUsuario().getId(), usuarioLogado);
        cliente.setNome(request.getNome());

        return (Cliente) tipoDeUsuarioRepository.save(cliente);
    }

    /**
     * Apaga um Cliente (exclusão lógica) e orquestra a exclusão do Usuario e Pessoa
     * se não houverem mais papéis ativos.
     * 
     * @param id O ID do Cliente.
     * @param usuarioLogado O usuário que está logado.
     * @return O Cliente desabilitado.
     */
    @Transactional
    public Cliente apagarCliente(UUID id, Usuario usuarioLogado) {
        Cliente cliente = obterPorID(id);

        // Validação de Posse
        validador.validarPosse(cliente.getUsuario().getId(), usuarioLogado);

        Usuario usuario = cliente.getUsuario();
        Pessoa pessoa = usuario.getPessoa();
        
        // 1. Exclusão Lógica do Papel (Cliente)
        cliente.setHabilitado(Boolean.FALSE);
        Cliente clienteDesabilitado = (Cliente) tipoDeUsuarioRepository.save(cliente);

        // 2. Orquestração da Exclusão do Usuário.
        // Contamos quantos papéis ativos restam para este Usuário APÓS a exclusão deste Cliente.
        long papeisAtivos = tipoDeUsuarioRepository.countTiposAtivosByUsuarioId(usuario.getId());

        // 3. Se nenhum outro papel estiver ativo, apagamos o Usuário e a Pessoa
        if (papeisAtivos == 0) {
            usuarioService.apagarUsuario(usuario.getId(), usuarioLogado);
            pessoaService.apagarPessoa(pessoa.getId(), usuarioLogado);
        }
        
        return clienteDesabilitado;
    }

    /**
     * ATENÇÃO: Use com cautela. Apaga um Cliente permanentemente.
     * 
     * @param id O ID do endereço.
     */
    @Transactional
    public void deletarPermanentemente(UUID id) {
        Cliente cliente = obterPorID(id);
        tipoDeUsuarioRepository.delete(cliente);
    }
}