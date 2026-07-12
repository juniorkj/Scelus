package br.jus.tjma.scelus.dominio.repository;

import br.jus.tjma.scelus.dominio.model.Vitima;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * javadoc Interface de repositório para a entidade Vitima.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 10/07/2026
 */
@Repository
public interface VitimaRepository extends JpaRepository<Vitima, Long> {}
