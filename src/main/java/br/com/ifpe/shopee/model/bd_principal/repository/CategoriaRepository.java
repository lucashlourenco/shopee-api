package br.com.ifpe.shopee.model.bd_principal.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.ifpe.shopee.model.bd_principal.entity.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    // --- HIERARQUIA (Menu de Navegação) ---

    // 1. Busca Categorias Raiz (Aquelas que não têm Pai)
    // Útil para listar o menu principal do site
    List<Categoria> findByPaiIsNullAndHabilitadoTrue();

    // 2. Busca Subcategorias (Filhas de uma categoria específica)
    // Útil quando o usuário clica em "Eletrônicos" e você quer mostrar "Celulares", "TVs"...
    List<Categoria> findByPaiIdAndHabilitadoTrue(UUID idPai);


    // --- BUSCA E PESQUISA ---

    // 3. Busca por parte do nome (Autocomplete ou Barra de Busca)
    List<Categoria> findByNomeContainingIgnoreCaseAndHabilitadoTrue(String nome);
    
    // 4. Busca apenas categorias ATIVAS (além de habilitadas)
    // Se você usa o flag 'estaAtiva' para esconder categorias temporariamente (Ex: "Natal")
    List<Categoria> findByEstaAtivaTrueAndHabilitadoTrue();


    // --- VALIDAÇÕES (Para evitar duplicidade) ---

    // 5. Verifica se já existe uma categoria com esse nome NO MESMO NÍVEL (mesmo pai)
    // Evita ter "Celulares" e "Celulares" dentro de Eletrônicos.
    boolean existsByNomeAndPai(String nome, Categoria pai);

    // 6. Verifica se existe categoria com esse nome sendo Raiz (sem pai)
    boolean existsByNomeAndPaiIsNull(String nome);
    
    
    // --- QUERY AVANÇADA (Opcional) ---
    // Buscar toda a árvore de categorias de uma vez (requer query nativa recursiva ou lógica no Java)
    // Por enquanto, os métodos acima resolvem 99% dos casos.
}