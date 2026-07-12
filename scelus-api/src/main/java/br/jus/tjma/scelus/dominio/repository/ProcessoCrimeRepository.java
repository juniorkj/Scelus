package br.jus.tjma.scelus.dominio.repository;

import br.jus.tjma.scelus.dominio.model.ProcessoCrime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * javadoc Interface de repositório para a entidade ProcessoCrime.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 10/07/2026
 */
@Repository
public interface ProcessoCrimeRepository extends JpaRepository<ProcessoCrime, Long> {

    /**
     * Localiza um processo de crime pelo número único (CNJ).
     */
    Optional<ProcessoCrime> findFirstByNumeroUnico(String numeroUnico);
}
