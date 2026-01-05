// src/main/java/br/com/ifpe/shopee/model/bd_blobstore/service/MidiaService.java

package br.com.ifpe.shopee.model.bd_blobstore.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.com.ifpe.shopee.model.bd_blobstore.entity.Midia;
import br.com.ifpe.shopee.model.bd_blobstore.repository.MidiaRepository;
import br.com.ifpe.shopee.model.bd_principal.entity.Usuario;
import br.com.ifpe.shopee.util.MidiaStorageUtil;
import br.com.ifpe.shopee.util.exception.AdvertenciaException;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;

@Service
public class MidiaService {

    @Autowired
    private MidiaRepository repository;
    
    @Autowired
    private MidiaStorageUtil storageUtil;

    /**
     * Realiza o upload de um arquivo.
     * Salva no disco e cria o registro no banco com 0 referências.
     * 
     * @param arquivo Arquivo a ser salvo
     * @param usuarioLogado Usuário logado no momento
     * @param classeOrigem Classe que originou o arquivo
     * @return Midia
     */
    @Transactional("blobstoreTransactionManager")
    public Midia uploadMidia(MultipartFile arquivo, Usuario usuarioLogado, String classeOrigem) {

        // Valida o tipo de arquivo a ser salvo
        String contentType = arquivo.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new AdvertenciaException("Apenas arquivos de imagem (JPG, PNG, WEBP) são permitidos.");
        }
        
        try {
            // 1. Prepara a entidade
            Midia midia = new Midia();
            midia.setTipoMime(arquivo.getContentType());
            midia.setTamanhoBytes(arquivo.getSize());
            midia.setIdUsuarioDeOrigem(usuarioLogado.getId());
            midia.setClasseDeOrigem(classeOrigem);
            midia.setReferencias(0); // Nasce órfã até ser associada a uma entidade (Loja/Produto)
            
            // 2. Salva metadados para gerar o ID (UUID)
            midia = repository.save(midia); 
            
            // 3. Salva o arquivo físico usando o ID gerado
            String extensao = extrairExtensao(arquivo.getOriginalFilename());
            String caminho = storageUtil.salvarArquivoFisico(
                usuarioLogado.getId(), 
                classeOrigem, 
                midia.getId(), 
                extensao, 
                arquivo.getBytes()
            );
            
            // 4. Atualiza caminho e URL pública
            midia.setCaminhoStorage(caminho);
            // Define a rota pública onde a imagem estará acessível
            midia.setUrlAcessoPublico("/api/v1/publico/midia/" + midia.getId());
            
            return repository.save(midia);
        }
        catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo de mídia: ", e);
        }
    }

    /**
     * Incrementa o contador de referências de uma mídia.
     * Deve ser chamado quando uma instância usa a imagem.
     * Aceita a URL completa ou apenas o UUID (String).
     * 
     * @param urlOuId URL completa ou UUID
     */
    @Transactional("blobstoreTransactionManager")
    public void associarMidia(String urlOuId) {
        if (urlOuId == null || urlOuId.isEmpty()) return;

        UUID id = extrairIdDaUrl(urlOuId);
        Midia midia = repository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Mídia não encontrada: " + id));
        
        midia.setReferencias(midia.getReferencias() + 1);
        repository.save(midia);
    }

    /**
     * Decrementa o contador de referências.
     * Deve ser chamado quando uma instância deixa de usar a imagem.
     * 
     * @param urlOuId URL completa ou UUID
     */
    @Transactional("blobstoreTransactionManager")
    public void desassociarMidia(String urlOuId) {
        if (urlOuId == null || urlOuId.isEmpty()) return;
        
        try {
            UUID id = extrairIdDaUrl(urlOuId);
            repository.findById(id).ifPresent(midia -> {
                if (midia.getReferencias() > 0) {
                    midia.setReferencias(midia.getReferencias() - 1);
                    repository.save(midia);
                }
            });
        }
        
        catch (IllegalArgumentException e) {
            // Se o ID for inválido ou antigo, ignoramos para não quebrar o fluxo de update
        }
    }

    /**
     * Apaga uma mídia (exclusão lógica).
     * Deve ser usada quando a instância da entidade detentora é apagada.
     * NÃO altera o contador de referências (o arquivo físico é preservado).
     * 
     * @param urlOuId URL completa ou UUID da mídia.
     * @param habilitado
     */
    @Transactional("blobstoreTransactionManager")
    public void apagarMidia(String urlOuId) {
        if (urlOuId == null || urlOuId.isEmpty()) return;

        UUID id = extrairIdDaUrl(urlOuId);

        repository.findById(id).ifPresent(midia -> {
            midia.setHabilitado(Boolean.FALSE);
            repository.save(midia);
        });
    }

    /**
     * Retorna o Resource para o arquivo fisico.
     * 
     * @param id ID da mídia.
     * @return Resource do arquivo.
     */
    public Resource carregarMidiaPeloId(UUID id) {
        Midia midia = repository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Mídia não encontrada"));
        
        return storageUtil.carregarArquivo(midia.getCaminhoStorage());
    }
    
    /**
     * Retorna a entidade pelo ID.
     * 
     * @param id ID da mídia.
     * @return Midia.
     */
    public Midia obterPorId(UUID id) {
        return repository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Mídia não encontrada"));
    }

    // --- Métodos Auxiliares ---

    private String extrairExtensao(String nomeArquivo) {
        if (nomeArquivo != null && nomeArquivo.contains(".")) {
            return nomeArquivo.substring(nomeArquivo.lastIndexOf(".") + 1);
        }
        return "bin"; // Default
    }

    private UUID extrairIdDaUrl(String urlOuId) {
        // Se vier a URL completa: /api/v1/publico/midia/uuid-aqui
        // Pegamos a última parte. Se for só o UUID, usamos ele.
        String uuidString = urlOuId;
        if (urlOuId.contains("/")) {
            uuidString = urlOuId.substring(urlOuId.lastIndexOf("/") + 1);
        }
        return UUID.fromString(uuidString);
    }
}