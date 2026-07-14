package br.jus.tjma.scelus.dominio.repository;

import br.jus.tjma.scelus.dominio.model.Comunicante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * javadoc Interface de repositório para a entidade Comunicante.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 12/07/2026
 */
@Repository
public interface ComunicanteRepository extends JpaRepository<Comunicante, Long> {}
