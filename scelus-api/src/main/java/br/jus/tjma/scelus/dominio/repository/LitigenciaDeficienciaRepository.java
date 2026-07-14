package br.jus.tjma.scelus.dominio.repository;

import br.jus.tjma.scelus.dominio.model.LitigenciaDeficiencia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * javadoc Interface de repositório para a entidade LitigenciaDeficiencia.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
@Repository
public interface LitigenciaDeficienciaRepository extends JpaRepository<LitigenciaDeficiencia, Long> {

    List<LitigenciaDeficiencia> findByIdLitigancia(Long idLitigancia);
}
