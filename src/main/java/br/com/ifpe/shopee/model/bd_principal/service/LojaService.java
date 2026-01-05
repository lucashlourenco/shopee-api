// src/main/java/br/com/ifpe.shopee.model/bd_principal/service/LojaService.java

package br.com.ifpe.shopee.model.bd_principal.service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifpe.shopee.model.bd_principal.entity.Loja;
import br.com.ifpe.shopee.model.bd_principal.entity.Usuario;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoComercial;
import br.com.ifpe.shopee.model.bd_principal.entity.endereco.EnderecoDeEstoque;
import br.com.ifpe.shopee.model.bd_principal.repository.LojaRepository;
import br.com.ifpe.shopee.model.bd_principal.service.endereco.EnderecoComercialService;
import br.com.ifpe.shopee.model.bd_principal.service.endereco.EnderecoDeEstoqueService;
import br.com.ifpe.shopee.model.bd_blobstore.service.MidiaService;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;
import br.com.ifpe.shopee.util.seguranca.ValidadorDeAcesso;
import jakarta.transaction.Transactional;

@Service
public class LojaService {
    
    @Autowired
    private LojaRepository repository;
    
    @Autowired
    private EnderecoComercialService enderecoComercialService;

    @Autowired
    private EnderecoDeEstoqueService enderecoDeEstoqueService;

    @Autowired
    private MidiaService midiaService;

    @Autowired
    private ValidadorDeAcesso validador;

    // Método para fazer a validação de posse da Loja.
    private void validarPosseDaLoja(Loja loja, Usuario usuarioLogado) {
        validador.validarPosse(loja.getVendedor().getUsuario().getId(), usuarioLogado);
    }

    // -------------------------------------------------
    // Gestão de Loja
    // -------------------------------------------------

    /**
     * Adiciona uma nova Loja. Não é chamada por usuarios, já que só é possível ter 1 loja por pessoa.
     * Se for chamada por um usuario, deve validar a posse dele.
     * 
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

        // 3. Quando a loja for ciada com uma logo, associa a mesma.
        if (novaLoja.getLogo() != null && !novaLoja.getLogo().isEmpty()) {
            midiaService.associarMidia(novaLoja.getLogo());
        }
        
        // 4. Salva e retorna a Loja
        return repository.save(novaLoja);
    }

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
     * Atualiza os dados de uma Loja (Nome, Descrição, Logo).
     * Gerencia a troca de imagens na Blobstore.
     * 
     * @param idLoja O ID da Loja a ser atualizada.
     * @param lojaAlterada A Loja com os dados alterados.
     * @param usuarioLogado O usuário logado.
     * @return A Loja atualizada.
     */
    @Transactional
    public Loja atualizarLoja(UUID idLoja, Loja lojaAlterada, Usuario usuarioLogado) {
        Loja lojaOriginal = obterPorID(idLoja);

        // 1. Verifica se a loja pertence ao usuário
        validarPosseDaLoja(lojaOriginal, usuarioLogado);

        // 2. Atualização de Campos Simples
        lojaOriginal.setNome(lojaAlterada.getNome());
        lojaOriginal.setDescricao(lojaAlterada.getDescricao());

        // 3. Gestão da Logo
        String logoAntiga = lojaOriginal.getLogo();
        String logoNova = lojaAlterada.getLogo();

        // Veifica se e a logo mudou
        boolean logoMudou = (logoAntiga == null && logoNova != null) || 
                            (logoAntiga != null && !Objects.equals(logoAntiga, logoNova));

        // Se a logo mudou...
        if (logoMudou) {
            // A. Decrementa referência da logo antiga, se existia
            if (logoAntiga != null) {
                midiaService.desassociarMidia(logoAntiga);
            }

            // B. Incrementa referência da nova logo, se existe
            if (logoNova != null) {
                midiaService.associarMidia(logoNova);
            }

            // C. Atualiza o campo na entidade
            lojaOriginal.setLogo(logoNova);
        }

        // 4. Lógica de Gestão de Imagem (Logo)
        // Se a logo mudou (a String da URL/ID é diferente)
        if (lojaAlterada.getLogo() != null && !lojaAlterada.getLogo().equals(lojaOriginal.getLogo())) {
            
            // A. Decrementa referência da logo antiga (se existia)
            if (lojaOriginal.getLogo() != null) {
                midiaService.desassociarMidia(lojaOriginal.getLogo());
            }

            // B. Incrementa referência da nova logo
            midiaService.associarMidia(lojaAlterada.getLogo());

            // C. Atualiza o campo
            lojaOriginal.setLogo(lojaAlterada.getLogo());
        }

        return repository.save(lojaOriginal);
    }
    
    /**
     * Apaga uma Loja (exclusão lógica). Não é chamada por usuarios.
     * Se for chamada por um usuario, deve validar a posse dele.
     * 
     * @param id O ID da Loja.
     * @param usuarioLogado O usuário que estálogado.
     * @return A Loja apagada.
     */
    @Transactional
    public Loja apagarLoja(UUID id, Usuario usuarioLogado) {
        Loja loja = obterPorID(id);

        // Verifica se a loja pertence ao usuário
        validarPosseDaLoja(loja, usuarioLogado);
        loja.setHabilitado(Boolean.FALSE);
        // 2. Desabilita a logo da loja
        // Isso impede acesso público via URL, mas mantém o arquivo físico
        if (loja.getLogo() != null) {
            midiaService.apagarMidia(loja.getLogo());
        }

        return repository.save(loja);
    }

    /**
     * ATENÇÃO: Use com cautela. Apaga uma Loja permanentemente.
     * @param id O ID da Loja.
     */
    @Transactional
    public void deletarPermanentemente(UUID id) {
        Loja loja = obterPorID(id);

        // Se a loja tinha uma logo, desassocia a mesma da loja.
        if (loja.getLogo() != null) {
            midiaService.desassociarMidia(loja.getLogo());
        }
        repository.delete(loja);
    }

    // -------------------------------------------------
    // Gestão de Endereço Comercial
    // -------------------------------------------------
    /**
     * Faz a gestão de alteração do endereço comercial da loja.
     * 
     * @param idLoja O ID da Loja.
     * @param enderecoAlterado Os dados do novo endereço.
     * @param usuarioLogado O usuário que está logado.
     * @return A Loja com o endereço alterado.
     */
    @Transactional
    public Loja atualizarEnderecoComercial(UUID idLoja, EnderecoComercial enderecoAlterado, Usuario usuarioLogado) {
        // 1. Busca a Loja
        Loja loja = obterPorID(idLoja);

        // Validar a posse da loja
        validarPosseDaLoja(loja, usuarioLogado);
        
        // 2. Pega o ID do endereço atual
        UUID idEnderecoAntigo = loja.getEndereco().getId();

        // 3. Chama o serviço de endereço para processar a mudança (Copy-on-Write)
        // Ele decide se altera o original ou retorna um novo/existente.
        EnderecoComercial enderecoResultante = enderecoComercialService.alterarEndereco(idEnderecoAntigo, enderecoAlterado);

        // 4. Atualiza a Loja se o ID mudou
        if (!idEnderecoAntigo.equals(enderecoResultante.getId())) {
            loja.setEndereco(enderecoResultante);
            repository.save(loja);

            // 5. Limpeza: Verifica se o endereço antigo ficou órfão e o remove
            enderecoComercialService.limparSeOrfao(idEnderecoAntigo);
        }

        return loja;
    }

    // -------------------------------------------------
    // Gestão de Endereços de Estoque
    // -------------------------------------------------

    /**
     * Faz a gestão de adição de um novo endereço de estoque para a loja.
     * 
     * @param idLoja O ID da Loja.
     * @param novoEstoque Os dados do novo endereço de estoque.
     * @param usuarioLogado O usuário que está logado.
     * @return O endereço de estoque adicionado.
     */
    @Transactional
    public EnderecoDeEstoque adicionarEnderecoDeEstoque(UUID idLoja, EnderecoDeEstoque enderecoDeEstoque, Usuario usuarioLogado) {
        Loja loja = obterPorID(idLoja);

        // Validar a posse da loja
        validarPosseDaLoja(loja, usuarioLogado);

        // 1. Cria ou recupera o endereço
        EnderecoDeEstoque deposito = enderecoDeEstoqueService.adicionarOuEncontarEnderecoDeEstoque(enderecoDeEstoque);

        // 2. Vincula e Salva
        loja.getDepositos().add(deposito);
        repository.save(loja);

        return deposito;
    }

    /**
     * Listar os endereços de estoque de uma Loja.
     * 
     * @param idLoja O ID da Loja.
     * @return Uma lista de endereços de estoque.
     */
    public List<EnderecoDeEstoque> listarEstoques(UUID idLoja) {
        return enderecoDeEstoqueService.listarPorLojaId(idLoja);
    }

    @Transactional
    public EnderecoDeEstoque atualizarEnderecoDeEstoque(UUID idLoja, UUID idEndereco, EnderecoDeEstoque enderecoAlterado) {
        Loja loja = obterPorID(idLoja);
        
        // Verifica se a loja realmente possui esse estoque vinculado
        boolean possuiEstoque = loja.getDepositos().stream()
                .anyMatch(e -> e.getId().equals(idEndereco));
        
        if (!possuiEstoque) {
            throw new RecursoNaoEncontradoException("Este estoque não está vinculado à loja.");
        }
    
        // Chama o service de endereço para processar a mudança
        EnderecoDeEstoque estoqueResultante = enderecoDeEstoqueService.alterarEndereco(idEndereco, enderecoAlterado);
    
        // Se o ID mudou (foi criado um novo ou trocado), atualiza a lista da Loja
        if (!idEndereco.equals(estoqueResultante.getId())) {
            // Remove o antigo da lista da loja
            loja.getDepositos().removeIf(e -> e.getId().equals(idEndereco));
            // Adiciona o novo
            loja.getDepositos().add(estoqueResultante);
            repository.save(loja);
    
            // Limpa o antigo se ficou órfão
            enderecoDeEstoqueService.limparSeOrfao(idEndereco);
        }
    
        return estoqueResultante;
    }

    /**
     * Faz a gestão de remoção de um endereço de estoque da Loja.
     * 
     * @param idLoja O ID da Loja.
     * @param idDeposito O ID do endereço de estoque.
     * @param usuarioLogado O usuário que está logado.
     */
    @Transactional
    public void removerEnderecoDeEstoque(UUID idLoja, UUID idDeposito, Usuario usuarioLogado) {
        Loja loja = obterPorID(idLoja);

        // Validar a posse da loja
        validarPosseDaLoja(loja, usuarioLogado);

        // 1. Remove da lista da loja
        boolean removeu = loja.getDepositos().removeIf(e -> e.getId().equals(idDeposito));

        if (removeu) {
            repository.save(loja); // Atualiza a tabela de junção

            // 2. Tenta limpar o endereço se ficou órfão (ninguém mais usa)
            enderecoDeEstoqueService.limparSeOrfao(idDeposito);
        } else {
            throw new RecursoNaoEncontradoException("Estoque não vinculado a esta loja.");
        }
    }
}