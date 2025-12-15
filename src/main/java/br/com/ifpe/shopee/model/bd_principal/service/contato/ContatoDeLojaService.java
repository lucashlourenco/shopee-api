// src/main/java/br/com/ifpe/shopee/model/bd_principal/service/contato/ContatoDeLojaService.java

package br.com.ifpe.shopee.model.bd_principal.service.contato;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ifpe.shopee.model.bd_principal.entity.Loja;
import br.com.ifpe.shopee.model.bd_principal.entity.contato.ContatoDeLoja;
import br.com.ifpe.shopee.model.bd_principal.repository.contato.ContatoDeLojaRepository;
import br.com.ifpe.shopee.model.bd_principal.service.LojaService;
import br.com.ifpe.shopee.util.exception.EntidadeDuplicadaException;
import br.com.ifpe.shopee.util.exception.RecursoNaoEncontradoException;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityNotFoundException; // Para quando a Loja não for encontrada

@Service
public class ContatoDeLojaService {

    @Autowired
    private ContatoDeLojaRepository repository;

    @Autowired
    private LojaService lojaService; // Usado para buscar a Loja principal

    /**
     * Serviço para obter um contato de Loja pelo ID.
     * 
     * @param id O ID do contato.
     * @return O ContatoDeLoja encontrado.
     */
    public ContatoDeLoja obterPorID(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Contato de loja não encontrado com ID: " + id));
    }

    /**
     * Serviço para adicionar um novo contato a uma Loja existente.
     * Implementa a regra de unicidade de domínio: (Loja + Valor + Tipo) deve ser
     * único.
     *
     * @param idLoja      O ID da Loja onde o contato será adicionado.
     * @param novoContato A Entidade ContatoDeLoja (convertida a partir do Request).
     * @return O ContatoDeLoja salvo.
     */
    @Transactional
    public ContatoDeLoja adicionarContatoDeLoja(UUID idLoja, ContatoDeLoja novoContato) {
        // Verificar e obter a Loja
        // TODO: LojaService.obterPorID lança RecursoNaoEncontradoException
        Loja loja = lojaService.obterPorID(idLoja);

        // Verificar unicidade (Regra: Loja + Valor + Tipo deve ser único)
        ContatoDeLoja contatoExistente = repository.findByLojaIdAndValorAndTipo(
                idLoja,
                novoContato.getValor(),
                novoContato.getTipo());

        if (contatoExistente != null) {
            throw new EntidadeDuplicadaException("Já existe um contato '"
                                                    + novoContato.getTipo()
                                                    + "' com o valor '"
                                                    + novoContato.getValor()
                                                    + "' registrado para esta loja.");
        }

        // Finalizar a entidade e salvar
        novoContato.setHabilitado(Boolean.TRUE);
        novoContato.setLoja(loja);

        return repository.save(novoContato);
    }

    /**
     * Altera um contato de Loja existente.
     * 
     * @param id              O ID do contato a ser alterado.
     * @param contatoAlterado A Entidade ContatoDeLoja (convertida a partir do
     *                        Request).
     * @return O ContatoDeLoja alterado.
     */
    @Transactional
    public ContatoDeLoja alterarContato(UUID id, ContatoDeLoja contatoAlterado) {

        ContatoDeLoja contatoOriginal = obterPorID(id);

        // Verificar se o novo Valor/Tipo já existe na mesmo Loja,
        // mas apenas se o valor ou tipo foram alterados.

        boolean valorOuTipoMudou = !contatoOriginal.getValor().equals(contatoAlterado.getValor())
                || !contatoOriginal.getTipo().equals(contatoAlterado.getTipo());

        if (valorOuTipoMudou) {
            ContatoDeLoja contatoDuplicado = repository.findByLojaIdAndValorAndTipo(
                    contatoOriginal.getLoja().getId(),
                    contatoAlterado.getValor(),
                    contatoAlterado.getTipo());

            // Se for encontrado um contato duplicado, e esse contato não for o original que
            // estamos alterando...
            if (contatoDuplicado != null && !contatoDuplicado.getId().equals(contatoOriginal.getId())) {
                throw new EntidadeDuplicadaException("O novo valor/tipo de contato já existe para esta loja.");
            }
        }

        // Aplicação das alterações
        contatoOriginal.setValor(contatoAlterado.getValor());
        contatoOriginal.setTipo(contatoAlterado.getTipo());
        contatoOriginal.setNome(contatoAlterado.getNome());

        return repository.save(contatoOriginal);
    }

    /**
     * Listar todos os contatos de uma Loja.
     * 
     * @param idLoja O ID da Loja onde os contatos serão buscados.
     * @return A lista de contatos.
     */
    public List<ContatoDeLoja> listarContatos(UUID idLoja) {
        return repository.findByLojaId(idLoja);
    }

    /**
     * Apaga um contato de Loja (exclusão lógica).
     * 
     * @param id O ID do contato.
     * @return O ContatoDeLoja apagado.
     */
    @Transactional
    public ContatoDeLoja apagarContato(UUID id) {
        ContatoDeLoja contato = obterPorID(id);
        contato.setHabilitado(Boolean.FALSE);
        return repository.save(contato);
    }

    /**
     * ATENÇÂO: Use com cautela. Apaga um contato de Loja permanentemente.
     * 
     * @param id O ID do contato.
     */
    @Transactional
    public void deletarPermanentemente(UUID id) {
        ContatoDeLoja contato = obterPorID(id);
        repository.delete(contato);
    }
}
