// src/main/java/br/com/ifpe/shopee.util/MidiaStorageUtil.java

package br.com.ifpe.shopee.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MidiaStorageUtil {

    // Injeta o caminho raiz a partir do application.properties
    @Value("${midia.storage.root}")
    private String localRaizStorage;

    /**
     * Deleta o arquivo físico do disco.
     * 
     * @param caminhoStorage O caminho relativo (ex: usuario123/produto/UUID.jpg)
     * @throws IOException Se a exclusão falhar
     */
    public void deletarArquivoFisico(String caminhoStorage) throws IOException {
        if (caminhoStorage == null || caminhoStorage.trim().isEmpty()) {
            throw new IllegalArgumentException("Caminho do arquivo não pode ser nulo.");
        }

        // Constrói o caminho completo (raiz + caminho relativo)
        Path caminhoCompleto = Paths.get(this.localRaizStorage, caminhoStorage);

        if (Files.exists(caminhoCompleto)) {
            // Tenta deletar o arquivo
            boolean deleted = Files.deleteIfExists(caminhoCompleto);

            if (!deleted) {
                // Lança exceção se, por algum motivo (permissão, bloqueio), o arquivo existir,
                // mas não puder ser deletado
                throw new IOException("Arquivo encontrado, mas não pôde ser deletado: " + caminhoCompleto);
            }
        } else {
            // Tratamento suave: Se o registro está no BD, mas o arquivo sumiu (problema prévio),
            // o GC deve deletar o registro do BD mesmo assim.
            System.out.println("Aviso: Arquivo físico não encontrado em " + caminhoCompleto
                    + ". Deletando apenas registro do BD.");
        }
    }

    /**
     * Método auxiliar para salvar o arquivo e retornar o caminho relativo (caminhoStorage).
     * 
     * @param idUsuario    UUID do usuário
     * @param classeOrigem String da classe (Produto, Loja, etc.)
     * @param midiaId      UUID da Midia
     * @param file         Bytes do arquivo
     * @return O caminho relativo que será salvo em Midia.caminhoStorage
     */
    public String salvarArquivoFisico(
            UUID idUsuario,
            String classeOrigem,
            UUID midiaId,
            String extensao,
            byte[] file) throws IOException {
        // Padrão interno: idUsuario/classeOrigem/midiaId.extensao
        String diretorioRelativo = idUsuario.toString() + "/" + classeOrigem.toLowerCase();
        String nomeArquivo = midiaId.toString() + "." + extensao.toLowerCase();

        // 1. Cria o diretório (se não existir)
        Path diretorioAbsoluto = Paths.get(localRaizStorage, diretorioRelativo);
        Files.createDirectories(diretorioAbsoluto);

        // 2. Salva o arquivo
        Path caminhoCompletoArquivo = diretorioAbsoluto.resolve(nomeArquivo);
        Files.write(caminhoCompletoArquivo, file);

        // 3. Retorna o caminho RELATIVO para salvar no BD
        return diretorioRelativo + "/" + nomeArquivo;
    }
}