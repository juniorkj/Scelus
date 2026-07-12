package br.jus.tjma.scelus.dominio.repository;

import br.jus.tjma.scelus.dominio.model.Vinculo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * javadoc Interface de repositório para a entidade Vinculo.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 10/07/2026
 */
@Repository
public interface VinculoRepository extends JpaRepository<Vinculo, Long> {

    /**
     * Localiza o vínculo existente entre uma vítima e um acusado.
     */
    Optional<Vinculo> findFirstByIdVitimaAndIdAcusado(Long idVitima, Long idAcusado);
}
