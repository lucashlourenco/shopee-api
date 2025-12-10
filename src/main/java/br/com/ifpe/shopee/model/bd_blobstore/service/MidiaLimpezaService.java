// src/main/java/br/com/ifpe/shopee/model/bd_blobstore/service/MidiaLimpezaService.java

package br.com.ifpe.shopee.model.bd_blobstore.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import br.com.ifpe.shopee.model.bd_blobstore.repository.MidiaLimpezaRepository;
import br.com.ifpe.shopee.util.MidiaStorageUtil; // Utilitário para exclusão física

@Service
public class MidiaLimpezaService {

    private final MidiaLimpezaRepository midiaRepository;
    private final MidiaStorageUtil midiaStorageUtil;

    public MidiaLimpezaService(
        MidiaLimpezaRepository midiaRepository,
        MidiaStorageUtil midiaStorageUtil
    ){
        this.midiaRepository = midiaRepository;
        this.midiaStorageUtil = midiaStorageUtil;
    }

    /**
     * Limpeza da Midia: Deleta metadados e arquivos físicos se 'referencias' for 0.
     * O cron expression abaixo executa a cada 1 hora (0 0 * * * *).
     * SINTAXE CRON: segundo minuto hora dia-do-mês mês dia-da-semana
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional("blobstoreTransactionManager")
    public void executarLimpezaDeMidia() {
        System.out.println("Iniciando Limpeza de Mídia Agendada...");

        // 1. Busca todos os registros de Mídia com 0 referências (JPA)
        midiaRepository.findByReferencias(0).forEach(midia -> {
            try {
                // 2. Tenta deletar o arquivo físico do disco/S3
                midiaStorageUtil.deletarArquivoFisico(midia.getCaminhoStorage());

                // 3. Se a exclusão física for bem-sucedida, deleta o registro no BD
                midiaRepository.delete(midia);
                System.out.println("Mídia deletada com sucesso: " + midia.getId());

            } catch (Exception e) {
                // Se a exclusão falhar (ex: arquivo não encontrado), apenas logamos
                // e deixamos o registro no BD para uma próxima tentativa.
                System.err.println("Falha ao deletar mídia " + midia.getId() + ": " + e.getMessage());
            }
        });
    }
}