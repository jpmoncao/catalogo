package br.com.fatec.catalogo.repositories;

import br.com.fatec.catalogo.models.ProdutoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface ProdutoRepository extends JpaRepository<ProdutoModel, Long> {
    List<ProdutoModel> findByNomeContainingIgnoreCase(String nome);
    boolean existsByNome(String nome);

    List<ProdutoModel> findByCategoriaIdCategoria(Long idCategoria);

    List<ProdutoModel> findAllByOrderByDataCadastroDesc();
}
