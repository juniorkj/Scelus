package br.jus.tjma.scelus.dominio.repository;

import br.jus.tjma.scelus.dominio.model.ConfiguracaoFamiliar;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * javadoc Interface de repositório para a entidade ConfiguracaoFamiliar.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
@Repository
public interface ConfiguracaoFamiliarRepository extends JpaRepository<ConfiguracaoFamiliar, Long> {

    List<ConfiguracaoFamiliar> findByIdVitima(Long idVitima);
}
