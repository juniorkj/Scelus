package br.jus.tjma.scelus.dominio.repository;

import br.jus.tjma.scelus.dominio.model.LitigenciaDroga;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * javadoc Interface de repositório para a entidade LitigenciaDroga.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
@Repository
public interface LitigenciaDrogaRepository extends JpaRepository<LitigenciaDroga, Long> {

    List<LitigenciaDroga> findByIdLitigancia(Long idLitigancia);
}
