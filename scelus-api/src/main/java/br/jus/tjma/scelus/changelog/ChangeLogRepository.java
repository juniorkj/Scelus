package br.jus.tjma.scelus.changelog;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * javadoc Interface de repositório para a entidade ChangeLog.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@Repository
public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

    List<ChangeLog> findAllByOrderByVersaoDescIdDesc();
}
