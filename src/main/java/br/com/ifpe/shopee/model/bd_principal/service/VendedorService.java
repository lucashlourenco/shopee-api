// src/main/java/br/com/ifpe.shopee.model/bd_principal/service/VendedorService.java

package br.com.ifpe.shopee.model.bd_principal.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifpe.shopee.model.bd_principal.entity.Vendedor;
import br.com.ifpe.shopee.model.bd_principal.entity.Loja;
import br.com.ifpe.shopee.model.bd_principal.entity.Pessoa;
import br.com.ifpe.shopee.model.bd_principal.entity.Usuario;
import br.com.ifpe.shopee.model.bd_principal.repository.TipoDeUsuarioRepository;
import br.com.ifpe.shopee.model.bd_principal.request.CadastroVendedorRequest;
import br.com.ifpe.shopee.model.bd_principal.request.TipoDeUsuarioRequest;
import br.com.ifpe.shopee.util.exception.AdvertenciaException;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;
import br.com.ifpe.shopee.util.seguranca.ValidadorDeAcesso;
import jakarta.transaction.Transactional;

@Service
public class VendedorService {

    @Autowired
    private PessoaService pessoaService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private LojaService lojaService;
    
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
     * Orquestra a criação completa de um novo Vendedor (Pessoa, Usuario, Vendedor e Loja).
     * 
     * @param request O DTO de Orquestração.
     * @return O Vendedor recém-criado.
     */
    @Transactional
    public Vendedor adicionarVendedor(CadastroVendedorRequest request) {
        
        Pessoa pessoa;
        
        // 1. Gerenciar a Pessoa e Validar o Papel
        try {
            pessoa = pessoaService.obterPorCPF(request.getPessoa().getCpf());
            
            if (tipoDeUsuarioRepository.existsVendedorByPessoaId(pessoa.getId())) {
                throw new AdvertenciaException("Esta Pessoa já possui um perfil de Vendedor ativo.");
            }
        }
        catch (RecursoNaoEncontradoException e) {
            Pessoa novaPessoa = request.getPessoa().build();
            pessoa = pessoaService.adicionarPessoa(novaPessoa, request.getPessoa().getEnderecoDeCadastro().build());
        }

        // 2. Criar a Conta (Usuário)
        Usuario usuario = usuarioService.adicionarUsuario(
            pessoa, 
            request.getConta().getSenha(), 
            request.getConta().getLogin().build()
        );
        
        // 3. Criar e Salvar a Loja (Se o DTO a contiver)
        Loja lojaSalva = null;
        if (request.getLoja() != null) {
            Loja novaLoja = request.getLoja().build();
            lojaSalva = lojaService.adicionarLoja(
                novaLoja, 
                request.getLoja().getEnderecoComercial().build()
            );
        }
    
        // 4. Criar o Papel (Vendedor)
        Vendedor vendedor = new Vendedor();
        vendedor.setHabilitado(Boolean.TRUE);
        vendedor.setDataDeCadastro(LocalDateTime.now());
        vendedor.setUsuario(usuario);
        vendedor.setCnpj(request.getCnpj()); // Pode ser nulo
        vendedor.setLoja(lojaSalva);         // Pode ser nulo
        vendedor.setNome(extrairPrimeiroNome(pessoa.getNomeCompleto()));
        
        Vendedor vendedorSalvo = (Vendedor) tipoDeUsuarioRepository.save(vendedor);
        
        return vendedorSalvo;
    }

    /**
     * Busca um Vendedor pelo ID.
     * 
     * @param id O ID do Vendedor.
     * @return O Vendedor encontrado.
     */
    public Vendedor obterPorID(UUID id) {
        // Usa o JpaRepository de TipoDeUsuario para buscar o Vendedor específico
        return (Vendedor) tipoDeUsuarioRepository.findById(id)
                         .orElseThrow(() -> new RecursoNaoEncontradoException("Vendedor com ID: '" + id + "' não encontrado."));
    }

    /**
     * Atualiza um Vendedor apartir do ID. Como o único dado atualizável de um Vendedor é o nome,
     * este serviço apenas atualiza o nome.
     *
     * @param id O ID do Vendedor.
     * @param nome O novo nome do Vendedor.
     * @param usuarioLogado O usuário que está logado.
     * @return O Vendedor atualizado.
     */
    
    public Vendedor atualizarVendedor(UUID idVendedor, TipoDeUsuarioRequest request, Usuario usuarioLogado) {
        Vendedor vendedor = obterPorID(idVendedor);

        // Valida se o papel (vendedor) pertence ao usuário
        validador.validarPosse(vendedor.getUsuario().getId(), usuarioLogado);
        vendedor.setNome(request.getNome());
        
        return (Vendedor) tipoDeUsuarioRepository.save(vendedor);
    }
    
    /**
     * Apaga um Vendedor (exclusão lógica) e orquestra a exclusão do Usuario, Pessoa e Loja
     * se não houverem mais papéis ativos.
     * 
     * @param id O ID do Vendedor.
     * @param usuarioLogado O usuário que está logado.
     * @return O Vendedor desabilitado.
     */
    @Transactional
    public Vendedor apagarVendedor(UUID id, Usuario usuarioLogado) {
        Vendedor vendedor = obterPorID(id);

        // Valida se o papel (vendedor) pertence ao usuário
        validador.validarPosse(vendedor.getUsuario().getId(), usuarioLogado);
        
        Usuario usuario = vendedor.getUsuario();
        Pessoa pessoa = usuario.getPessoa();
        Loja loja = vendedor.getLoja();
        
        // 1. Exclusão Lógica do Papel (Vendedor)
        vendedor.setHabilitado(Boolean.FALSE);
        // vendedor.setLoja(null); // Optando por não desvincula a Loja do Vendedor
        Vendedor vendedorDesabilitado = (Vendedor) tipoDeUsuarioRepository.save(vendedor);

        // 2. Orquestração da Exclusão do Usuário.
        // Contamos quantos papéis ativos restam para este Usuário APÓS a exclusão deste Vendedor.
        long papeisAtivos = tipoDeUsuarioRepository.countTiposAtivosByUsuarioId(usuario.getId());

        // 3. Se nenhum outro papel estiver ativo, apagamos o Usuário e a Pessoa
        if (papeisAtivos == 0) {
            usuarioService.apagarUsuario(usuario.getId(), usuarioLogado);
            pessoaService.apagarPessoa(pessoa.getId(), usuarioLogado);
        }
        
        // 4. Apagar a Loja.
        if (loja != null) {
            lojaService.apagarLoja(loja.getId(), usuarioLogado);
        }
        
        return vendedorDesabilitado;
    }

    /**
     * ATENÇÂO: Use com cautela. Apaga um Vendedor permanentemente.
     * 
     * @param id O ID do endereço.
     */
    @Transactional
    public void deletarPermanentemente(UUID id) {
        Vendedor vendedor = obterPorID(id);
        tipoDeUsuarioRepository.delete(vendedor);
    }
}