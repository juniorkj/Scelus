package br.jus.tjma.scelus.dominio.repository;

import br.jus.tjma.scelus.dominio.model.Cep;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * javadoc Interface de repositório para a entidade Cep.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@Repository
public interface CepRepository extends JpaRepository<Cep, Long> {

    /**
     * Pesquisa CEPs pelo prefixo informado (consulta simples para autocomplete).
     */
    List<Cep> findTop20ByCepStartingWithOrderByCep(String cep);

    /**
     * Localiza um CEP pelo valor exato formatado (ex.: 65010-000).
     */
    Optional<Cep> findFirstByCep(String cep);
}
