package br.jus.tjma.scelus.changelog;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * javadoc Serviço que monta o histórico de alterações (changelog) em formato
 * consumível pelo drawer do TjAuthTemplate, no mesmo padrão do frottas-new.
 *
 * @author Fco Antonio S. Júnior
 * @email fjunior.pdcase@gmail.com
 * @empresa Pd case
 * @version 1.0
 * @since 11/07/2026
 */
@Service
public class ChangeLogService {

    private final ChangeLogRepository repository;

    public ChangeLogService(ChangeLogRepository repository) {
        this.repository = repository;
    }

    /**
     * Lista todo o histórico de alterações agrupado por release, no formato
     * esperado pelo componente de changelog da @tjma/angular.
     */
    @Transactional(readOnly = true)
    public String listarMarkdown() {
        List<ChangeLog> logs = repository.findAllByOrderByVersaoDescIdDesc();

        StringBuilder sb = new StringBuilder();
        sb.append("# Histórico de Alterações\n\n");

        // Agrupa por título da release (str_release)
        Map<String, List<ChangeLog>> agrupadoPorRelease = logs.stream()
                .collect(Collectors.groupingBy(ChangeLog::getRelease, LinkedHashMap::new, Collectors.toList()));

        for (Map.Entry<String, List<ChangeLog>> entry : agrupadoPorRelease.entrySet()) {
            sb.append("<div>\n");
            sb.append("  <h2>").append(entry.getKey()).append("</h2>\n");
            sb.append("  <p>Alterações</p>\n");
            sb.append("  <ul>\n");
            for (ChangeLog log : entry.getValue()) {
                sb.append("    <li>").append(log.getDescricaoItem()).append("</li>\n");
            }
            sb.append("  </ul>\n");
            sb.append("</div>\n\n");
        }

        return sb.toString();
    }
}
