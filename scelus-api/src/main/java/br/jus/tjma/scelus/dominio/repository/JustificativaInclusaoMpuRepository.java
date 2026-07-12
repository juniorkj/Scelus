package br.jus.tjma.scelus.dominio.repository;

import br.jus.tjma.scelus.dominio.model.JustificativaInclusaoMpu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * javadoc Interface de repositório para a entidade JustificativaInclusaoMpu.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@Repository
public interface JustificativaInclusaoMpuRepository extends JpaRepository<JustificativaInclusaoMpu, Long> {}
